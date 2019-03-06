package server.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import server.engine.ai.AIAction;
import server.engine.ai.EnemyAI;
import server.engine.state.GameState;
import server.engine.state.entity.Entity;
import server.engine.state.entity.ItemDrop;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.enemy.Drop;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.player.Player;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.Item;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.map.GameMap;
import server.engine.state.map.MapReader;
import server.engine.state.map.Zone;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import server.engine.state.physics.HasPhysics;
import server.engine.state.physics.Physics;
import server.engine.state.physics.Velocity;
import shared.Location;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.AmmoList;
import shared.lists.ItemType;
import shared.lists.MapList;
import shared.lists.Status;
import shared.lists.Teams;
import shared.lists.TileState;
import shared.request.ClientRequests;
import shared.request.Request;
import shared.view.GameView;
import shared.view.ItemView;
import shared.view.TileView;
import shared.view.entity.EnemyView;
import shared.view.entity.ItemDropView;
import shared.view.entity.PlayerView;
import shared.view.entity.ProjectileView;

public class ProcessGameState extends Thread {
    private static final int MIN_TIME_DIFFERENCE = 17; // number of milliseconds between each process (approx 60th of a second).

    private final HasEngine handler;

    private GameState gameState;
    private GameView view;
    private ClientRequests clientRequests;
    private boolean handlerClosing;

    public ProcessGameState(HasEngine handler, MapList mapName, String hostName, Teams hostTeam) {
        this.handler = handler;
        LinkedHashMap<Integer, Player> players = new LinkedHashMap<>();
        Player hostPlayer = new Player(hostTeam, hostName);
        players.put(hostPlayer.getID(), hostPlayer);
        this.gameState = new GameState(MapReader.readMap(mapName), players);
        this.handlerClosing = false;

        // setup GameView
        GameMap map = this.gameState.getCurrentMap();
        int xDim = map.getXDim();
        int yDim = map.getYDim();
        TileView[][] tileMapView = new TileView[xDim][yDim];
        Tile[][] tileMap = map.getTileMap();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Tile tile = tileMap[x][y];
                tileMapView[x][y] = new TileView(tile.getType(), tile.getState());
            }
        }

        LinkedHashSet<PlayerView> playerViews = new LinkedHashSet<>();
        playerViews.add(toPlayerView(hostPlayer));

        view = new GameView(playerViews, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView);
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    public void handlerClosing() {
        this.handlerClosing = true;
        this.interrupt();
    }

    public void addPlayer(String playerName, Teams team) {
        gameState.addPlayer(new Player(team, playerName));
    }

    @Override
    public void run() {
        handler.updateGameView(view);
        long lastProcessTime = System.currentTimeMillis();
        long currentTimeDifference = 0;

        // performance checking variables
        long totalTimeProcessing = 0;
        long numOfProcesses = -1;
        long longestTimeProcessing = 0;

        // Zones
        LinkedHashMap<Integer, Zone> inactiveZones = gameState.getCurrentMap().getZones();
        LinkedHashMap<Integer, Zone> activeZones = new LinkedHashMap<>();

        while (!handlerClosing) {
            currentTimeDifference = System.currentTimeMillis() - lastProcessTime;

            // performance checks
            numOfProcesses++;
            if (numOfProcesses != 0) {
                totalTimeProcessing += currentTimeDifference;
                if (currentTimeDifference > longestTimeProcessing)
                    longestTimeProcessing = currentTimeDifference;
                //if (numOfProcesses % 3600 == 0) printPerformanceInfo(totalTimeProcessing, numOfProcesses, longestTimeProcessing); //uncomment for regular performance info
            }

            long timeDiff = MIN_TIME_DIFFERENCE - currentTimeDifference;
            if (timeDiff > 0) {
                try {
                    Thread.sleep(timeDiff);
                } catch (InterruptedException e1) {
                    continue; // Most likely that the handler is closing.
                }
                if (handlerClosing)
                    break;
                currentTimeDifference = MIN_TIME_DIFFERENCE;
            } else {
                System.out.println("Can't keep up!");
            }
            lastProcessTime = System.currentTimeMillis();

            handler.requestClientRequests();
            if (clientRequests == null)
                continue; // waits until clients start doing something.

            // extract/setup necessary data
            GameMap currentMap = gameState.getCurrentMap();
            Tile[][] tileMap = currentMap.getTileMap();
            LinkedHashSet<Projectile> newProjectiles = new LinkedHashSet<>(); // TODO change projectiles to use hashmap with id which may improve performance?
            LinkedHashMap<Integer, ItemDrop> items = gameState.getItems();

            TileView[][] tileMapView = view.getTileMap();
            LinkedHashSet<ProjectileView> projectilesView = new LinkedHashSet<>();

            // process player requests
            LinkedHashMap<Integer, Request> playerRequests = clientRequests.getPlayerRequests();
            LinkedHashMap<Integer, Player> players = gameState.getPlayers();
            clientRequests = new ClientRequests(players.size()); // clears requests

            for (Map.Entry<Integer, Request> playerRequest : playerRequests.entrySet()) {
                int playerID = playerRequest.getKey();
                Player currentPlayer = players.get(playerID);

                if (currentPlayer == null) {
                    System.out.println("WARNING: Request from non-existent player. Was the player added/removed properly in handler?");
                    continue;
                }

                Request request = playerRequest.getValue();

                if (request.getLeave()) {
                    players.remove(playerID);
                    handler.removePlayer(playerID);
                    continue;
                }

                // reset player values
                currentPlayer.setMoving(false);
                currentPlayer.setTakenDamage(false);
                ActionList lastAction = currentPlayer.getCurrentAction();
                
                if (lastAction == ActionList.DEAD)
                    continue;

                if (currentPlayer.getHealth() <= 0) {
                    currentPlayer.setStatus(Status.DEAD);
                    currentPlayer.setCurrentAction(ActionList.DEAD);
                    LinkedHashSet<int[]> playerTilesOn = tilesOn(currentPlayer);
                    for (int[] playerTileCords : playerTilesOn) {
                        tileMap[playerTileCords[0]][playerTileCords[1]].removePlayer(playerID);
                    }
                    continue; // TODO find proper way of dealing with player death?
                }
                
                if (lastAction == ActionList.ATTACKING || lastAction == ActionList.THROW || lastAction == ActionList.ITEM_SWITCH)
                    currentPlayer.setCurrentAction(ActionList.NONE);


                Pose playerPose = currentPlayer.getPose();
                Item currentItem = currentPlayer.getCurrentItem();

                // TODO process status (Make method for this?)

                if (currentItem instanceof Gun) {
                    Gun currentGun = ((Gun) currentItem);
                    if (currentGun.isReloading()) {
                        AmmoList ammoType = currentGun.getAmmoType();
                        int amountTaken = currentGun.reload(currentPlayer.getAmmo(ammoType));
                        if (amountTaken > 0) {
                            currentPlayer.setAmmo(ammoType, currentPlayer.getAmmo(ammoType) - amountTaken);
                        }
                        if (!currentGun.isReloading())
                            currentPlayer.setCurrentAction(ActionList.NONE);
                    }
                }

                if (request.getShoot()) {
                    if (currentItem.getItemType() == ItemType.GUN) {
                        currentPlayer.setCurrentAction(ActionList.ATTACKING);
                        Gun currentGun = (Gun) currentItem;
                        if (currentGun.shoot(currentPlayer.getAmmo(currentGun.getAmmoType()))) {
                            LinkedList<Projectile> shotProjectiles = currentGun.getShotProjectiles(playerPose, currentPlayer.getTeam());
                            for (Projectile p : shotProjectiles) {
                                newProjectiles.add(p);
                                projectilesView.add(new ProjectileView(p.getPose(), 1, p.getEntityListName(), p.isCloaked(), p.getStatus()));
                            }
                        }
                    }
                }

                if (request.getDrop() && currentPlayer.getItems().size() > 1) {
                    currentPlayer.setCurrentAction(ActionList.THROW);
                    ItemDrop itemDropped = new ItemDrop(currentItem, playerPose);
                    // TODO add force & add damage if melee weapon
                    items.put(itemDropped.getID(), itemDropped);

                    LinkedHashSet<int[]> tilesOn = tilesOn(itemDropped);
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].addItemDrop(itemDropped.getID());
                    }
                    currentPlayer.removeItem(currentPlayer.getCurrentItemIndex());

                } else if (request.getReload()) {
                    if (currentItem instanceof Gun) {
                        Gun currentGun = ((Gun) currentItem);
                        if (currentGun.attemptReload(currentPlayer.getAmmo(currentGun.getAmmoType())))
                            currentPlayer.setCurrentAction(ActionList.RELOADING);
                    }
                }

                if (request.facingExists() || request.movementExists()) {
                    int facingDirection = playerPose.getDirection();

                    if (request.facingExists()) {
                        facingDirection = request.getFacing();
                        currentPlayer.setPose(new Pose(playerPose, facingDirection));
                    }

                    if (request.movementExists()) {
                        currentPlayer.setMoving(true);
                        currentPlayer.addNewForce(Physics.getForce(currentPlayer.getAcceleration(), request.getMovementDirection(), currentPlayer.getMass()));
                    }

                }

                currentPlayer.setCurrentItem(currentItem); // sets item changes before switching

                if (request.selectItemAtExists()) {
                    currentPlayer.setCurrentItemIndex(request.getSelectItemAt());
                    currentPlayer.setCurrentAction(ActionList.ITEM_SWITCH);
                }

                players.put(playerID, currentPlayer);

            }

            // process item drops
            LinkedHashSet<ItemDropView> itemDropsView = new LinkedHashSet<>();
            LinkedList<Integer> itemsToRemove = new LinkedList<>();
            for (ItemDrop i : items.values()) {
                if ((lastProcessTime - i.getDropTime()) > ItemDrop.DECAY_LENGTH) {
                    itemsToRemove.add(i.getID());
                    LinkedHashSet<int[]> tilesOn = tilesOn(i);
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].removeItemDrop(i.getID());
                    }
                    // TODO itemdrop decay status here
                } else {
                    itemDropsView.add(new ItemDropView(i.getPose(), i.getSize(), i.getEntityListName(), i.isCloaked(), i.getStatus()));
                }
            }
            itemsToRemove.stream().forEach((i) -> items.remove(i));
            itemsToRemove = new LinkedList<>();
            // process enemies
            LinkedHashMap<Integer, Enemy> enemies = gameState.getEnemies();
            LinkedHashSet<EnemyView> enemiesView = new LinkedHashSet<>();

            HashSet<Pose> playerPoses = new HashSet<>();
            players.values().stream().forEach((p) -> playerPoses.add(p.getPose()));

            for (Enemy e : enemies.values()) {
                Enemy currentEnemy = e;

                // reset values
                currentEnemy.setMoving(false);
                currentEnemy.setTakenDamage(false);

                // TODO process status (Make method for this?)

                int enemyID = currentEnemy.getID();
                double maxDistanceMoved = getDistanceMoved(currentTimeDifference, currentEnemy.getMoveSpeed());
                EnemyAI ai = currentEnemy.getAI();

                if (!ai.isProcessing())
                    ai.setInfo(currentEnemy, playerPoses, tileMap);

                AIAction enemyAction = ai.getAction();
                currentEnemy.setCurrentAction(ai.getActionState());

                switch (enemyAction) {
                case ATTACK:
                    LinkedList<Attack> attacks = ai.getAttacks();

                    for (Attack a : attacks) {
                        switch (a.getAttackType()) {
                        case AOE:
                            AoeAttack aoeAttack = (AoeAttack) a;
                            LinkedHashSet<int[]> tilesOn = tilesOn(aoeAttack);

                            for (int[] tileCords : tilesOn) {
                                Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                                LinkedHashSet<Integer> playersOnTile = tileOn.getPlayersOnTile();

                                for (Integer playerID : playersOnTile) {
                                    Player playerBeingChecked = players.get(playerID);
                                    if (haveCollided(aoeAttack, playerBeingChecked)) {
                                        playerBeingChecked.damage(aoeAttack.getDamage());
                                        playerBeingChecked.setTakenDamage(true);
                                        players.put(playerID, playerBeingChecked);
                                    }
                                }
                            }
                            break;
                        case PROJECTILE:
                            ProjectileAttack projectileAttack = (ProjectileAttack) a;
                            for (Projectile p : projectileAttack.getProjectiles()) {
                                newProjectiles.add(p);
                                projectilesView.add(new ProjectileView(p.getPose(), 1, p.getEntityListName(), p.isCloaked(), p.getStatus()));
                            }
                            break;
                        }
                    }

                    break;
                case MOVE:
                    currentEnemy.setMoving(true);
                    LinkedHashSet<int[]> tilesOn = tilesOn(currentEnemy);

                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].removeEnemy(enemyID);
                    }

                    currentEnemy.setPose(ai.getNewPose(maxDistanceMoved));

                    tilesOn = tilesOn(currentEnemy);
                    for (int[] tileCords : tilesOn) {
                        try {
                            tileMap[tileCords[0]][tileCords[1]].addEnemy(enemyID);
                        }catch(ArrayIndexOutOfBoundsException ex){
                            System.out.println("WARNING: Enemy tried to move out of map.");
                        }
                    }
                    break;
                case WAIT:
                    break;
                default:
                    System.out.println("AIAction " + enemyAction.toString() + " not known!");
                    break;
                }

                enemies.put(enemyID, currentEnemy);
                enemiesView.add(new EnemyView(currentEnemy.getPose(), currentEnemy.getSize(),
                        currentEnemy.getEntityListName(), currentEnemy.isCloaked(), currentEnemy.getStatus(),
                        currentEnemy.getCurrentAction(), currentEnemy.hasTakenDamage(), currentEnemy.isMoving(),
                        currentEnemy.getHealth(), currentEnemy.getMaxHealth(), currentEnemy.getID()));
            }

            // process projectiles
            LinkedHashSet<Projectile> projectiles = gameState.getProjectiles();

            for (Projectile p : projectiles) {
                boolean removed = false;
                Projectile currentProjectile = p;
                double distanceMoved = getDistanceMoved(currentTimeDifference, currentProjectile.getSpeed());

                if (currentProjectile.maxRangeReached(distanceMoved)) {
                    removed = true;
                } else {
                    Location newLocation = Location.calculateNewLocation(currentProjectile.getLocation(), currentProjectile.getPose().getDirection(),
                            distanceMoved);
                    currentProjectile.setLocation(newLocation);
                    LinkedHashSet<int[]> tilesOn = tilesOn(currentProjectile);
                    for (int[] tileCords : tilesOn) {
                        Tile tileOn = null;
                        try {
                            tileOn = tileMap[tileCords[0]][tileCords[1]];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            removed = true;
                            System.out.println("WARNING: Projectile went out of bounds. Is the map complete?");
                            break;
                        }
                        if (tileOn.getState() == TileState.SOLID) {
                            removed = true;
                            tileMapView[tileCords[0]][tileCords[1]] = new TileView(tileOn.getType(), tileOn.getState(), true); // Tile hit
                            break;
                        }
                    }

                    if (!removed) {
                        for (int[] tileCords : tilesOn) {
                            Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                            LinkedHashSet<Integer> enemiesOnTile = tileOn.getEnemiesOnTile();

                            for (Integer enemyID : enemiesOnTile) {
                                Enemy enemyBeingChecked = enemies.get(enemyID);
                                Location enemyLocation = enemyBeingChecked.getLocation();

                                if (currentProjectile.getTeam() != Teams.ENEMY && haveCollided(currentProjectile, enemyBeingChecked)) {
                                    enemyBeingChecked.addNewForce(currentProjectile.getImpactForce());
                                    removed = true;

                                    if (enemyBeingChecked.damage(currentProjectile.getDamage())) {
                                        // TODO enemy death status here
                                        enemies.remove(enemyID);
                                        activeZones.get(enemyBeingChecked.getZoneID()).entityRemoved();

                                        LinkedHashSet<int[]> enemyTilesOn = tilesOn(enemyBeingChecked);
                                        for (int[] enemyTileCords : enemyTilesOn) {
                                            tileMap[enemyTileCords[0]][enemyTileCords[1]].removeEnemy(enemyID);
                                        }

                                        Player.changeScore(currentProjectile.getTeam(), enemyBeingChecked.getScoreOnKill());

                                        LinkedHashSet<Drop> drops = enemyBeingChecked.getDrops();
                                        for (Drop d : drops) {
                                            int dropAmount = d.getDrop();
                                            if (dropAmount != 0) {
                                                Item itemToDrop = d.getItem();
                                                // TODO have itemdrops of the same type stack?
                                                ItemDrop newDrop = new ItemDrop(itemToDrop, enemyLocation, dropAmount);
                                                items.put(newDrop.getID(), newDrop);
                                                // TODO spawned itemdrop status? is this needed?
                                                itemDropsView.add(new ItemDropView(newDrop.getPose(), newDrop.getSize(), newDrop.getEntityListName(),
                                                        newDrop.isCloaked(), newDrop.getStatus()));

                                                LinkedHashSet<int[]> itemTilesOn = tilesOn(newDrop);
                                                for (int[] itemTileCords : itemTilesOn) {
                                                    tileMap[itemTileCords[0]][itemTileCords[1]].addItemDrop(newDrop.getID());
                                                }

                                            }
                                        }
                                    } else {
                                        enemyBeingChecked.setTakenDamage(true);
                                        enemies.put(enemyID, enemyBeingChecked);
                                    }
                                    break; // bullet was removed no need to check other enemies
                                }
                            }

                            if (!removed) {
                                LinkedHashSet<Integer> playersOnTile = tileOn.getPlayersOnTile();

                                for (Integer playerID : playersOnTile) {
                                    Player playerBeingChecked = players.get(playerID);

                                    if (currentProjectile.getTeam() != playerBeingChecked.getTeam() && haveCollided(currentProjectile, playerBeingChecked)) {
                                        playerBeingChecked.addNewForce(currentProjectile.getImpactForce());
                                        removed = true;
                                        playerBeingChecked.setTakenDamage(true);
                                        playerBeingChecked.damage(currentProjectile.getDamage());
                                        break; // bullet was removed no need to check other players
                                    }
                                }

                            }

                            if (removed)
                                break; // Projectile is already gone.
                        }
                    }

                }
                if (removed) {
                    // TODO removed projectile process
                } else {
                    newProjectiles.add(currentProjectile);
                    projectilesView.add(new ProjectileView(currentProjectile.getPose(), currentProjectile.getSize(), currentProjectile.getEntityListName(),
                            currentProjectile.isCloaked(), currentProjectile.getStatus()));
                }
            }

            // physics processing
            // TODO refactor somehow?

            for (Player p : players.values()) {
                Player currentPlayer = p;
                LinkedHashSet<int[]> tilesOn = tilesOn(currentPlayer);
                int playerID = currentPlayer.getID();
                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].removePlayer(playerID);
                }
                
                currentPlayer = (Player) doPhysics(currentPlayer, tileMap, currentTimeDifference);

                tilesOn = tilesOn(currentPlayer);
                LinkedHashSet<Integer> playersOnTile = new LinkedHashSet<>();
                LinkedHashSet<Integer> enemiesOnTile = new LinkedHashSet<>();

                for (int[] tileCords : tilesOn) {
                    Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                    playersOnTile.addAll(tileOn.getPlayersOnTile());
                    enemiesOnTile.addAll(tileOn.getEnemiesOnTile());
                }
                
                double closestThing = Double.MAX_VALUE;
                int closestID = -1;
                boolean isPlayer = false;

                for (int playerCheckedID : playersOnTile) {
                    Player playerBeingChecked = players.get(playerCheckedID);
                    if (playerID != playerCheckedID && haveCollided(currentPlayer, playerBeingChecked)) {
                        double dist = getDistSqrd(currentPlayer.getLocation(), playerBeingChecked.getLocation());
                        if (dist < closestThing) {
                            closestThing = dist;
                            isPlayer = true;
                            closestID = playerCheckedID;
                        }
                    }
                }

                for (int enemyCheckedID : enemiesOnTile) {
                    Enemy enemyBeingChecked = enemies.get(enemyCheckedID);
                    if (haveCollided(currentPlayer, enemyBeingChecked)) {
                        double dist = getDistSqrd(currentPlayer.getLocation(), enemyBeingChecked.getLocation());
                        if (dist < closestThing) {
                            closestThing = dist;
                            isPlayer = false;
                            closestID = enemyCheckedID;
                        }
                    }
                }

                if (closestID != -1) {
                    HasPhysics e2;
                    if (isPlayer) {
                        e2 = players.get(closestID);
                        tilesOn = tilesOn((Entity) e2);
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].removePlayer(closestID);
                        }
                    } else {
                        e2 = enemies.get(closestID);
                        tilesOn = tilesOn((Entity) e2);
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].removeEnemy(closestID);
                        }
                    }

                    HasPhysics result[] = Physics.objectCollision(currentPlayer, e2, tileMap);
                    currentPlayer = (Player) result[0];
                    e2 = result[1];

                    if (isPlayer) {
                        tilesOn = tilesOn((Entity) e2);
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].addPlayer(closestID);
                        }
                        players.put(closestID, (Player) e2);
                    } else {
                        tilesOn = tilesOn((Entity) e2);
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].addEnemy(closestID);
                        }
                        enemies.put(closestID, (Enemy) e2);
                    }
                }

                tilesOn = tilesOn(currentPlayer);
                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].addPlayer(playerID);
                }

                players.put(playerID, currentPlayer);
            }

            for (Enemy e : enemies.values()) {
                Enemy currentEnemy = e;
                LinkedHashSet<int[]> tilesOn = tilesOn(currentEnemy);
                int enemyID = currentEnemy.getID();
                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].removeEnemy(enemyID);
                }

                currentEnemy = (Enemy) doPhysics(currentEnemy, tileMap, currentTimeDifference);

                tilesOn = tilesOn(currentEnemy);
                LinkedHashSet<Integer> enemiesOnTile = new LinkedHashSet<>();

                for (int[] tileCords : tilesOn) {
                    Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                    enemiesOnTile.addAll(tileOn.getEnemiesOnTile());
                }

                double closestThing = Double.MAX_VALUE;
                int closestID = -1;

                for (int enemyCheckedID : enemiesOnTile) {
                    Enemy enemyBeingChecked = enemies.get(enemyCheckedID);
                    if (enemyID != enemyCheckedID && haveCollided(currentEnemy, enemyBeingChecked)) {
                        double dist = getDistSqrd(currentEnemy.getLocation(), enemyBeingChecked.getLocation());
                        if (dist < closestThing) {
                            closestThing = dist;
                            closestID = enemyCheckedID;
                        }
                    }
                }

                if (closestID != -1) {
                    Enemy e2 = enemies.get(closestID);
                    tilesOn = tilesOn(e2);
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].removeEnemy(closestID);
                    }

                    HasPhysics result[] = Physics.objectCollision(currentEnemy, e2, tileMap);
                    currentEnemy = (Enemy) result[0];
                    e2 = (Enemy) result[1];

                    tilesOn = tilesOn(e2);
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].addEnemy(closestID);
                    }

                    enemies.put(closestID, e2);
                }

                tilesOn = tilesOn(currentEnemy);
                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].addEnemy(enemyID);
                }

                enemies.put(enemyID, currentEnemy);
            }

            // final player processing
            for (Player p : players.values()) {
                Player currentPlayer = p;
                LinkedHashSet<int[]> tilesOn = tilesOn(currentPlayer);
                int playerID = currentPlayer.getID();

                for (int[] tileCords : tilesOn) {
                    Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                    // check for triggers
                    if (tileMap[tileCords[0]][tileCords[1]].hasTriggers()) {
                        LinkedHashSet<Integer> zonesTriggered = tileMap[tileCords[0]][tileCords[1]].triggered();
                        for (int zoneId : zonesTriggered) {
                            Zone zoneActivated = inactiveZones.remove(zoneId);
                            zoneActivated.activate();
                            activeZones.put(zoneId, zoneActivated);

                            LinkedHashSet<int[]> triggersToRemove = zoneActivated.getTriggers();
                            for (int[] trigger : triggersToRemove) {
                                tileMap[trigger[0]][trigger[1]].removeTrigger(zoneId);
                            }
                        }
                    }

                    // check for item drops
                    LinkedHashSet<Integer> itemsOnTile = tileOn.getItemDropsOnTile();
                    LinkedList<Integer> dropsToRemove = new LinkedList<>();

                    for (Integer itemDropID : itemsOnTile) {
                        ItemDrop currentItemDrop = items.get(itemDropID);

                        if (haveCollided(currentPlayer, currentItemDrop)) {
                            boolean removed = false;
                            int dropQuantity = currentItemDrop.getQuantity();
                            ArrayList<Item> playerItems = currentPlayer.getItems();

                            switch (currentItemDrop.getItemType()) {
                            case AMMO:
                                AmmoList ammoType = currentItemDrop.getItemName().toAmmoList();
                                dropQuantity -= currentPlayer.addAmmo(ammoType, dropQuantity);

                                if (dropQuantity != 0) {
                                    currentItemDrop.setQuantity(dropQuantity);
                                    items.put(itemDropID, currentItemDrop);
                                } else {
                                    removed = true;
                                }
                                break;
                            case GUN: // TODO change case to include melee as well
                                if (playerItems.stream().anyMatch((i) -> i.getItemListName() == currentItemDrop.getItemName())) {
                                    // player already has that item TODO take some ammo from it
                                } else if (playerItems.size() < currentPlayer.getMaxItems()
                                        && (lastProcessTime - currentItemDrop.getDropTime()) > ItemDrop.DROP_FREEZE) {
                                    playerItems.add(currentItemDrop.getItem());
                                    dropQuantity -= 1;

                                    if (dropQuantity != 0) {
                                        currentItemDrop.setQuantity(dropQuantity);
                                        items.put(itemDropID, currentItemDrop);
                                    } else {
                                        removed = true;
                                    }
                                }
                                break;
                            }

                            if (removed) {
                                dropsToRemove.add(itemDropID);
                            }
                        }
                    }

                    for (Integer dropID : dropsToRemove) {
                        ItemDrop dropToRemove = items.get(dropID);
                        LinkedHashSet<int[]> itemTilesOn = tilesOn(dropToRemove);
                        for (int[] itemTileCords : itemTilesOn) {
                            tileMap[itemTileCords[0]][itemTileCords[1]].removeItemDrop(dropID);
                        }
                        items.remove(dropID);
                    }

                }
                players.put(playerID, currentPlayer);
            }

            // TODO process tiles?

            for (Zone z : activeZones.values()) {
                for (Entity e : z.getEntitysToSpawn()) {
                    if (e instanceof Enemy) {
                        Enemy enemyToSpawn = (Enemy) e;
                        enemies.put(enemyToSpawn.getID(), enemyToSpawn);
                    }
                }

                for (Map.Entry<int[], Tile> tileChanged : z.getTileChanges().entrySet()) {
                    int[] cords = tileChanged.getKey();
                    Tile newTile = tileChanged.getValue();
                    tileMap[cords[0]][cords[1]] = newTile;
                    tileMapView[cords[0]][cords[1]] = new TileView(newTile.getType(), newTile.getState());
                }
            }


            gameState.setPlayers(players);
            gameState.setProjectiles(newProjectiles);
            gameState.setEnemies(enemies);
            gameState.setItems(items);
            gameState.setTileMap(tileMap);

            // turn players to player view
            LinkedHashSet<PlayerView> playersView = new LinkedHashSet<>();
            for (Player p : players.values()) {
                playersView.add(toPlayerView(p));
            }

            GameView view = new GameView(playersView, enemiesView, projectilesView, itemDropsView, tileMapView);
            handler.updateGameView(view);
        }
        printPerformanceInfo(totalTimeProcessing, numOfProcesses, longestTimeProcessing);
    }

    private void printPerformanceInfo(long totalTimeProcessing, long numOfProcesses, long longestTimeProcessing) {
        System.out.println("LongestTimeProcessing: " + longestTimeProcessing);
        double avgTimeProcessing = (double) totalTimeProcessing / numOfProcesses;
        System.out.println("TimeProcessing: " + totalTimeProcessing);
        System.out.println("NumOfProcesses: " + numOfProcesses);
        System.out.println("AverageTimeProcessing: " + avgTimeProcessing);
        System.out.println("ProjectileCount: " + gameState.getProjectiles().size());
        System.out.println("EnemyCount: " + gameState.getEnemies().size());
    }

    private static double getDistanceMoved(long timeDiff, double speed) {
        double distMoved = Physics.normaliseTime(timeDiff) * speed; // time in millis
        if (distMoved >= Tile.TILE_SIZE)
            System.out.println("WARNING: Entity moving too fast!");
        return distMoved;
    }

    private static boolean haveCollided(Entity e1, Entity e2) {
        Location e1_loc = e1.getLocation();
        int e1_radius = e1.getSize();
        double e1_x = e1_loc.getX();
        double e1_y = e1_loc.getY();

        Location e2_loc = e2.getLocation();
        int e2_radius = e2.getSize();
        double e2_x = e2_loc.getX();
        double e2_y = e2_loc.getY();
        
        double dist_between_squared = Math.pow(Math.abs(e1_x - e2_x), 2) + Math.pow(Math.abs(e1_y - e2_y), 2);

        return (dist_between_squared <= Math.pow(e1_radius + e2_radius, 2));
    }

    private static LinkedHashSet<int[]> tilesOn(Entity e) { // TODO prevent tilesOn out of the map
        Location loc = e.getLocation();
        int radius = e.getSize();
        double x = loc.getX();
        double y = loc.getY();

        int[] max_loc = Tile.locationToTile(new Location(x + radius, y + radius));
        int[] min_loc = Tile.locationToTile(new Location(x - radius, y - radius));

        LinkedHashSet<int[]> tilesOn = new LinkedHashSet<>();

        for (int t_x = min_loc[0]; t_x <= max_loc[0]; t_x++) {
            for (int t_y = min_loc[1]; t_y <= max_loc[1]; t_y++) {
                tilesOn.add(new int[] { t_x, t_y });
            }
        }

        return tilesOn;
    }

    private static PlayerView toPlayerView(Player p) {
        ArrayList<ItemView> playerItems = new ArrayList<>();
        for (Item i : p.getItems()) {
            if (i instanceof Gun) {
                Gun g = (Gun) i;
                playerItems.add(new ItemView(g.getItemListName(), g.getAmmoType(), g.getClipSize(), g.getAmmoInClip(), g.isAutoFire(), g.getReloadTime()));
            } else {
                playerItems.add(new ItemView(i.getItemListName(), AmmoList.NONE, 0, 0, false, 0));
            }
        }
        return new PlayerView(p.getPose(), p.getSize(), p.getHealth(), p.getMaxHealth(), playerItems, p.getCurrentItemIndex(), p.getScore(), p.getName(),
                p.getAmmoList(), p.getID(), p.getTeam(), p.isCloaked(), p.getStatus(), p.getCurrentAction(), p.hasTakenDamage(),
                p.isMoving());
    }

    private static int[] getMostSignificatTile(Location l, LinkedHashSet<int[]> tilesOn, Tile[][] tileMap) {
        int[] mostSigTileCords = { -1, -1 };
        double mostSigDist = Double.MAX_VALUE;
        for (int[] tileCords : tilesOn) {
            Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
            if (tileOn.getState() == TileState.SOLID) {
                Location tileLoc = Tile.tileToLocation(tileCords[0], tileCords[1]);
                double xDiff = tileLoc.getX() - l.getX();
                double yDiff = tileLoc.getY() - l.getY();
                double distSqrd = Math.pow(xDiff, 2) + Math.pow(yDiff, 2);

                if (distSqrd < mostSigDist) {
                    mostSigTileCords = tileCords;
                    mostSigDist = distSqrd;
                }
            }
        }
        return mostSigTileCords;
    }

    private static HasPhysics doPhysics(HasPhysics e, Tile[][] tileMap, long timeDiff) {
        Force resultantForce = e.getResultantForce();
        Velocity currentVelocity = e.getVelocity();

        if (currentVelocity.getSpeed() == 0 && resultantForce.getForce() == 0)
            return e;

        double mass = e.getMass();
        LinkedHashSet<int[]> tilesOn = tilesOn((Entity) e);

        double frictionCoefficient = 0;
        double density = 0;

        for (int[] tileCords : tilesOn) {
            Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
            if (tileOn.getState() != TileState.SOLID) {
                frictionCoefficient += tileOn.getFrictionCoefficient();
                density += tileOn.getDensity();
            } else {
                // System.out.println("INFO: Object clipped in tile.");
            }
        }

        int numOfTilesOn = tilesOn.size();
        frictionCoefficient = frictionCoefficient / numOfTilesOn;
        density = density / numOfTilesOn;

        Force frictionForce = Physics.getFrictionalForce(frictionCoefficient, mass, currentVelocity.getDirection());

        if (resultantForce.getForce() == 0
                && Physics.getAcceleration(frictionForce, mass) * Physics.normaliseTime(timeDiff) > currentVelocity.getSpeed()) {
            e.setVelocity(new Velocity());
            return e;
        }

        if (currentVelocity.getSpeed() != 0) {
            e.addNewForce(frictionForce);
            e.addNewForce(Physics.getDragForce(density, currentVelocity, e.getSize()));
            resultantForce = e.getResultantForce();
        } else if (resultantForce.getForce() > frictionForce.getForce()) {
            resultantForce = new Force(resultantForce.getDirection(), resultantForce.getForce() - frictionForce.getForce());
        } else {
            return e; // force not great enough
        }

        double acceleration = Physics.getAcceleration(resultantForce, mass);
        currentVelocity = Physics.getNewVelocity(acceleration, currentVelocity, resultantForce.getDirection(), timeDiff);

        e.setVelocity(currentVelocity);

        double distanceMoved = getDistanceMoved(timeDiff, currentVelocity.getSpeed() * Tile.TILE_SIZE);
        Location newLocation = Location.calculateNewLocation(e.getLocation(), currentVelocity.getDirection(), distanceMoved);
        e.setLocation(newLocation);

        // tile collisions
        tilesOn = tilesOn((Entity)e);
        int[] mostSigTileCords = getMostSignificatTile(newLocation, tilesOn, tileMap);
        if (mostSigTileCords[0] != -1) {
            // TODO add hit sound to tile if velocity is high
            Location mostSigTileLoc = Tile.tileToLocation(mostSigTileCords[0], mostSigTileCords[1]);
            e = Physics.tileCollision(e, mostSigTileLoc, tileMap[mostSigTileCords[0]][mostSigTileCords[1]].getBounceCoefficient());
        }

        return e;
    }
    
    private static double getDistSqrd(Location e1Loc, Location e2Loc) {
        double xDiff = e1Loc.getX() - e2Loc.getX();
        double yDiff = e1Loc.getY() - e2Loc.getY();
        double distSqrd = Math.pow(xDiff, 2) + Math.pow(yDiff, 2);

        return distSqrd;
    }

}
