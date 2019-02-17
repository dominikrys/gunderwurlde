package server.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import server.engine.ai.AIAction;
import server.engine.ai.EnemyAI;
import server.engine.state.GameState;
import server.engine.state.entity.Entity;
import server.engine.state.entity.ItemDrop;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.enemy.Drop;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.player.Player;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.Item;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.map.GameMap;
import server.engine.state.map.MapReader;
import server.engine.state.map.Round;
import server.engine.state.map.Wave;
import server.engine.state.map.tile.Tile;
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
        Random random = new Random();
        long lastProcessTime = System.currentTimeMillis();
        long currentTimeDifference = 0;

        // TODO update this depending on areas (Stop spawning in empty areas)
        Iterator<Round> roundIterator = gameState.getCurrentMap().getRounds().iterator();
        Round currentRound = roundIterator.next();
        LinkedHashSet<Wave> currentWaves = new LinkedHashSet<>();
        Iterator<Location> enemySpawnIterator = gameState.getCurrentMap().getEnemySpawns().iterator();

        // performance checking variables
        long totalTimeProcessing = 0;
        long numOfProcesses = -1;
        long longestTimeProcessing = 0;

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
                if (currentPlayer.getCurrentAction() == ActionList.ATTACKING)
                    currentPlayer.setCurrentAction(ActionList.NONE);

                if (currentPlayer.getHealth() <= 0) {
                    currentPlayer.setStatus(Status.DEAD);
                    LinkedHashSet<int[]> playerTilesOn = tilesOn(currentPlayer);
                    for (int[] playerTileCords : playerTilesOn) {
                        tileMap[playerTileCords[0]][playerTileCords[1]].removePlayer(playerID);
                    }
                    continue; // TODO find proper way of dealing with player death?
                }


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
                        currentPlayer.setCurrentAction(ActionList.NONE);
                    }
                }

                if (request.getShoot()) {
                    if (currentItem.getItemType() == ItemType.GUN) {
                        currentPlayer.setCurrentAction(ActionList.ATTACKING);
                        Gun currentGun = (Gun) currentItem;
                        if (currentGun.shoot(currentPlayer.getAmmo(currentGun.getAmmoType()))) {
                            int numOfBullets = currentGun.getProjectilesPerShot();
                            int spread = currentGun.getSpread();
                            int bulletSpacing = 0;
                            if (numOfBullets > 1)
                                bulletSpacing = (2 * spread) / (numOfBullets - 1);

                            LinkedList<Pose> bulletPoses = new LinkedList<>();
                            Pose gunPose = playerPose; // TODO change pose to include gunlength/position
                            int accuracy = currentGun.getAccuracy();

                            int nextDirection = gunPose.getDirection() - spread;
                            for (int i = 0; i < numOfBullets; i++) {
                                int direction = nextDirection;
                                if (accuracy != 0)
                                    direction += (random.nextInt(accuracy) - (accuracy / 2));
                                bulletPoses.add(new Pose(gunPose, direction));
                                nextDirection += bulletSpacing;
                            }

                            Projectile templateProjectile = currentGun.getProjectile();
                            for (Pose p : bulletPoses) {
                                Projectile proj = templateProjectile.createFor(p, currentPlayer.getTeam());
                                newProjectiles.add(proj);
                                projectilesView.add(new ProjectileView(p, proj.getSize(), proj.getEntityListName(), proj.isCloaked(), proj.getStatus()));
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
                    Location newLocation = playerPose;

                    if (request.facingExists()) {
                        facingDirection = request.getFacing();
                        currentPlayer.setPose(new Pose(playerPose, facingDirection));
                    }

                    if (request.movementExists()) {
                        currentPlayer.setMoving(true);
                        double distanceMoved = getDistanceMoved(currentTimeDifference, currentPlayer.getMoveSpeed());
                        newLocation = Location.calculateNewLocation(playerPose, request.getMovementDirection(), distanceMoved);

                        LinkedHashSet<int[]> tilesOn = tilesOn(currentPlayer);
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].removePlayer(playerID);
                        }

                        currentPlayer.setLocation(newLocation);
                        boolean pushedBack = false;

                        tilesOn = tilesOn(currentPlayer);
                        for (int[] tileCords : tilesOn) {
                            Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                            if (tileOn.getState() == TileState.SOLID) {
                                currentPlayer.setLocation(playerPose);
                                pushedBack = true;
                                break;
                            }
                        }

                        if (!pushedBack) {
                            for (int[] tileCords : tilesOn) {
                                Tile tileOn = tileMap[tileCords[0]][tileCords[1]];

                                // TODO include knock-back of player/enemies depending on some factor e.g. size.

                                HashSet<Integer> itemsOnTile = tileOn.getItemDropsOnTile();
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
                                            currentPlayer.setAmmo(ammoType, currentPlayer.getAmmo(ammoType) + dropQuantity);
                                            // As there is no max ammo player takes it all and itemdrop is removed
                                            removed = true;
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
                        }
                        tilesOn = tilesOn(currentPlayer);
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].addPlayer(playerID);
                        }

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
                Pose enemyPose = currentEnemy.getPose(); // don't change
                double maxDistanceMoved = getDistanceMoved(currentTimeDifference, currentEnemy.getMoveSpeed());
                EnemyAI ai = currentEnemy.getAI();

                if (!ai.isProcessing())
                    ai.setInfo(enemyPose, currentEnemy.getSize(), playerPoses, tileMap);

                AIAction enemyAction = ai.getAction();
                currentEnemy.setCurrentAction(ai.getActionState());

                switch (enemyAction) {
                case ATTACK:
                    LinkedList<Attack> attacks = ai.getAttacks();

                    for (Attack a : attacks) {
                        switch (a.getAttackType()) {
                        case AOE:
                            AoeAttack attack = (AoeAttack) a;
                            LinkedHashSet<int[]> tilesOn = tilesOn(attack);

                            for (int[] tileCords : tilesOn) {
                                Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                                HashSet<Integer> playersOnTile = tileOn.getPlayersOnTile();

                                for (Integer playerID : playersOnTile) {
                                    Player playerBeingChecked = players.get(playerID);
                                    if (haveCollided(attack, playerBeingChecked)) {
                                        playerBeingChecked.damage(attack.getDamage());
                                        playerBeingChecked.setTakenDamage(true);
                                        players.put(playerID, playerBeingChecked);
                                    }
                                }
                            }
                            break;
                        case PROJECTILE:
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
                        tileMap[tileCords[0]][tileCords[1]].addEnemy(enemyID);
                    }
                    // TODO include knock-back of player/enemies depending on some factor e.g. size.
                    break;
                case WAIT:
                    break;
                default:
                    System.out.println("AIAction " + enemyAction.toString() + " not known!");
                    break;
                }

                enemies.put(enemyID, currentEnemy);
                enemiesView.add(new EnemyView(currentEnemy.getPose(), currentEnemy.getSize(), currentEnemy.getEntityListName(), currentEnemy.isCloaked(),
                        currentEnemy.getStatus(), currentEnemy.getCurrentAction(), currentEnemy.hasTakenDamage(), currentEnemy.isMoving()));
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
                            HashSet<Integer> enemiesOnTile = tileOn.getEnemiesOnTile();

                            for (Integer enemyID : enemiesOnTile) {
                                Enemy enemyBeingChecked = enemies.get(enemyID);
                                Location enemyLocation = enemyBeingChecked.getLocation();

                                if (haveCollided(currentProjectile, enemyBeingChecked)) {
                                    // TODO add force to enemy
                                    removed = true;
                                    if (enemyBeingChecked.damage(currentProjectile.getDamage())) {
                                        // TODO enemy death status here
                                        enemies.remove(enemyID);

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
                                HashSet<Integer> playersOnTile = tileOn.getPlayersOnTile();

                                for (Integer playerID : playersOnTile) {
                                    Player playerBeingChecked = players.get(playerID);

                                    if (currentProjectile.getTeam() != playerBeingChecked.getTeam() && haveCollided(currentProjectile, playerBeingChecked)) {
                                        // TODO add force to player
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

            // TODO process tiles?

            LinkedHashSet<Wave> newWaves = new LinkedHashSet<>();

            if (currentRound.hasWavesLeft()) {
                while (currentRound.isWaveReady()) {
                    currentWaves.add(currentRound.getNextWave());
                }
            }

            if (!currentWaves.isEmpty()) {
                for (Wave wave : currentWaves) {
                    if (!wave.isDone()) {
                        Wave currentWave = wave;
                        if (currentWave.readyToSpawn()) {
                            Enemy templateEnemyToSpawn = currentWave.getEnemyToSpawn();
                            int amountToSpawn = currentWave.getSpawn();

                            for (int i = 0; i < amountToSpawn; i++) {
                                if (!enemySpawnIterator.hasNext())
                                    enemySpawnIterator = currentMap.getEnemySpawns().iterator();
                                Enemy enemyToSpawn = templateEnemyToSpawn.makeCopy();
                                enemyToSpawn.setPose(new Pose(enemySpawnIterator.next()));
                                // TODO spawning status & add to gameview
                                enemies.put(enemyToSpawn.getID(), enemyToSpawn);
                            }
                        }
                        newWaves.add(currentWave);
                    }
                }
            } else if (enemies.isEmpty()) {
                if (roundIterator.hasNext()) {
                    currentRound = roundIterator.next();
                } else {
                    // TODO no more rounds left send win message if co-op
                }
            }
            currentWaves = newWaves;

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

    private static double getDistanceMoved(long timeDiff, int speed) {
        return (timeDiff / 1000.0) * speed; // time in millis
    }

    private static boolean haveCollided(Entity e1, Entity e2) {
        Location e1_loc = e1.getLocation();
        int e1_radius = e1.getSize() / 2;
        double e1_x = e1_loc.getX();
        double e1_y = e1_loc.getY();

        Location e2_loc = e2.getLocation();
        int e2_radius = e2.getSize() / 2;
        double e2_x = e2_loc.getX();
        double e2_y = e2_loc.getY();

        return ((e1_x - e1_radius) <= (e2_x + e2_radius) && (e1_x + e1_radius) >= (e2_x - e2_radius) && (e1_y - e1_radius) <= (e2_y + e2_radius)
                && (e1_y + e1_radius) >= (e2_y - e2_radius));
    }

    private static LinkedHashSet<int[]> tilesOn(Entity e) {
        Location loc = e.getLocation();
        int size = e.getSize();
        int radius = size / 2;
        double x = loc.getX();
        double max_x = x + radius;
        double min_x = x - radius;
        double y = loc.getY();
        double max_y = y + radius;
        double min_y = y - radius;

        LinkedHashSet<int[]> tilesOn = new LinkedHashSet<>();

        double check_x = min_x;
        double check_y = min_y;
        while (check_x < max_x) {
            while (check_y < max_y) {
                tilesOn.add(Tile.locationToTile(new Location(check_x, check_y)));
                check_y += Tile.TILE_SIZE;
            }
            tilesOn.add(Tile.locationToTile(new Location(check_x, max_y)));
            check_x += Tile.TILE_SIZE;
        }
        tilesOn.add(Tile.locationToTile(new Location(max_x, max_y)));

        return tilesOn;
    }

    private static PlayerView toPlayerView(Player p) {
        ArrayList<ItemView> playerItems = new ArrayList<>();
        for (Item i : p.getItems()) {
            if (i instanceof Gun) {
                Gun g = (Gun) i;
                playerItems.add(new ItemView(g.getItemListName(), g.getAmmoType(), g.getClipSize(), g.getAmmoInClip(), g.isAutoFire()));
            } else {
                playerItems.add(new ItemView(i.getItemListName(), AmmoList.NONE, 0, 0, false));
            }
        }
        return new PlayerView(p.getPose(), p.getSize(), p.getHealth(), p.getMaxHealth(), playerItems, p.getCurrentItemIndex(), p.getScore(), p.getName(),
                p.getAmmoList(), p.getID(), p.getTeam(), p.getMoveSpeed(), p.isCloaked(), p.getStatus(), p.getCurrentAction(), p.hasTakenDamage(),
                p.isMoving());
    }

}
