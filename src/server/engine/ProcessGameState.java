package server.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.engine.ai.AIAction;
import server.engine.ai.enemyAI.EnemyAI;
import server.engine.state.GameState;
import server.engine.state.entity.Entity;
import server.engine.state.entity.ItemDrop;
import server.engine.state.entity.LivingEntity;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.player.Player;
import server.engine.state.entity.projectile.CrystalBullet;
import server.engine.state.entity.projectile.HasEffect;
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
import shared.lists.EntityList;
import shared.lists.EntityStatus;
import shared.lists.ItemType;
import shared.lists.MapList;
import shared.lists.Team;
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
    private static final Logger LOGGER = Logger.getLogger(ProcessGameState.class.getName());
    private static final int MIN_TIME_DIFFERENCE = 17; // number of milliseconds between each process (approx 60th of a second).
    private static final int TICKS_TILL_INFO = 3600;

    static {
        LOGGER.setLevel(Level.WARNING);
    }

    private static Random random = new Random(); // TODO remove if unused

    private final HasEngine handler;

    private GameState gameState;
    private GameView view;
    private ClientRequests clientRequests;
    private boolean handlerClosing;

    public ProcessGameState(HasEngine handler, MapList mapName, String hostName, Team hostTeam) {
        this.handler = handler;
        LinkedHashMap<Integer, LivingEntity> players = new LinkedHashMap<>();
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

        view = new GameView(playerViews, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView, Team.NONE);
        LOGGER.info("Engine set up.");
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    public void handlerClosing() {
        LOGGER.info("Stopping engine.");
        this.handlerClosing = true;
        this.interrupt();
    }

    public void addPlayer(String playerName, Team team) { // TODO remove if not used
        gameState.addPlayer(new Player(team, playerName));
    }

    @Override
    public void run() {
        LOGGER.info("Starting engine.");
        handler.updateGameView(view);
        long lastProcessTime = System.currentTimeMillis();
        long currentTimeDifference = 0;

        // performance checking variables
        long totalTimeProcessing = 0;
        long numOfProcesses = -1;
        long longestTimeProcessing = 0;
        int ticksLeft = TICKS_TILL_INFO;

        // Zones
        LinkedHashMap<Integer, Zone> inactiveZones = gameState.getCurrentMap().getZones();
        LinkedHashMap<Integer, Zone> activeZones = new LinkedHashMap<>();

        // pause tracking variables
        boolean paused = false;
        int numPaused = 0;

        while (!handlerClosing) {
            currentTimeDifference = System.currentTimeMillis() - lastProcessTime;

            // performance checks
            if (!paused) {
                numOfProcesses++;
                if (numOfProcesses != 0) {
                    totalTimeProcessing += currentTimeDifference;
                    if (currentTimeDifference > longestTimeProcessing)
                        longestTimeProcessing = currentTimeDifference;
                    if (--ticksLeft == 0) {
                        printPerformanceInfo(totalTimeProcessing, numOfProcesses, longestTimeProcessing);
                        ticksLeft = TICKS_TILL_INFO;
                    }
                }
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
                LOGGER.warning("Engine can't keep up!");
            }
            lastProcessTime = System.currentTimeMillis();

            handler.requestClientRequests();
            if (clientRequests == null)
                continue; // waits until clients start doing something.

            // extract/setup necessary data
            GameMap currentMap = gameState.getCurrentMap();
            Tile[][] tileMap = currentMap.getTileMap();
            LinkedHashSet<Projectile> newProjectiles = new LinkedHashSet<>();
            LinkedHashMap<Integer, ItemDrop> items = gameState.getItems();
            LinkedHashMap<Integer, LivingEntity> livingEntities = gameState.getLivingEntities();

            // views for the handler
            TileView[][] tileMapView = view.getTileMap();
            LinkedHashSet<ProjectileView> projectilesView = new LinkedHashSet<>();
            LinkedHashSet<ItemDropView> itemDropsView = new LinkedHashSet<>();

            // process player requests
            LinkedHashMap<Integer, Request> playerRequests = clientRequests.getPlayerRequests();
            LinkedHashSet<Integer> playerIDs = gameState.getPlayerIDs();
            clientRequests = new ClientRequests(playerIDs.size()); // clears requests

            for (Map.Entry<Integer, Request> playerRequest : playerRequests.entrySet()) {
                int playerID = playerRequest.getKey();
                Player currentPlayer = (Player) livingEntities.get(playerID);

                if (currentPlayer == null) {
                    LOGGER.warning("Request from non-existent player. Was the player added/removed properly in handler?");
                    continue;
                }

                // handle player request
                Request request = playerRequest.getValue();

                if (request.getLeave()) {
                    LOGGER.info("Removing player: " + playerID);
                    playerIDs.remove(playerID);
                    livingEntities.remove(playerID);
                    handler.removePlayer(playerID);
                    continue;
                }

                // pause checking
                if (request.getPause() && !currentPlayer.isPaused()) {
                    numPaused++;
                    currentPlayer.setPaused(true);
                } else if (request.getResume() && currentPlayer.isPaused()) {
                    numPaused--;
                    currentPlayer.setPaused(false);
                }

                if (numPaused == playerIDs.size()) {
                    paused = true;
                    break;
                } else {
                    paused = false;
                }

                // reset player values
                currentPlayer.setMoving(false);
                currentPlayer.setTakenDamage(false);
                ActionList lastAction = currentPlayer.getCurrentAction();

                // prevent dead players from doing things
                if (lastAction == ActionList.DEAD)
                    continue;

                // check if player is dead and process
                if (currentPlayer.getHealth() <= 0) {
                    LOGGER.info("Player: " + playerID + " has died.");
                    LinkedHashSet<int[]> playerTilesOn = tilesOn(currentPlayer);
                    for (int[] playerTileCords : playerTilesOn) {
                        tileMap[playerTileCords[0]][playerTileCords[1]].removePlayer(playerID);
                    }

                    LinkedList<ItemDrop> drops = currentPlayer.getDrops();
                    for (ItemDrop newDrop : drops) {
                        items.put(newDrop.getID(), newDrop);
                        // TODO spawned itemdrop status? is this needed?
                        itemDropsView.add(new ItemDropView(newDrop.getPose(), newDrop.getSize(), newDrop.getEntityListName(), newDrop.isCloaked(),
                                newDrop.getStatus(), (int) ItemDrop.DECAY_LENGTH));

                        LinkedHashSet<int[]> itemTilesOn = tilesOn(newDrop);
                        for (int[] itemTileCords : itemTilesOn) {
                            tileMap[itemTileCords[0]][itemTileCords[1]].addItemDrop(newDrop.getID());
                        }
                    }

                    currentPlayer.setCurrentAction(ActionList.DEAD);
                    continue;
                }

                // reset single tick actions
                if (lastAction == ActionList.ATTACKING || lastAction == ActionList.THROW || lastAction == ActionList.ITEM_SWITCH)
                    currentPlayer.setCurrentAction(ActionList.NONE);

                Pose playerPose = currentPlayer.getPose();
                Item currentItem = currentPlayer.getCurrentItem();

                // apply status effects to player
                if (currentPlayer.hasEffect())
                    currentPlayer = (Player) currentPlayer.getEffect().applyEffect(currentPlayer);

                // gun reload processing
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
                        Gun currentGun = (Gun) currentItem;
                        if (currentGun.shoot(currentPlayer.getAmmo(currentGun.getAmmoType()))) {
                            currentPlayer.setCurrentAction(ActionList.ATTACKING);
                            LinkedList<Projectile> shotProjectiles = currentGun.getShotProjectiles(playerPose, currentPlayer.getTeam());
                            for (Projectile p : shotProjectiles) {
                                newProjectiles.add(p);
                                projectilesView.add(new ProjectileView(p.getPose(), p.getSize(), p.getEntityListName(), p.isCloaked(), p.getStatus()));
                            }
                        }
                    }
                }

                if (request.getDrop() && currentPlayer.getItems().size() > 1) {
                    if (currentPlayer.removeItem(currentPlayer.getCurrentItemIndex())) {
                        LOGGER.info("Player: " + playerID + " dropped item.");
                        currentPlayer.setCurrentAction(ActionList.THROW);
                        Velocity dropVelocity = new Velocity(playerPose.getDirection(), 30);
                        dropVelocity.add(currentPlayer.getVelocity());
                        ItemDrop itemDropped = new ItemDrop(currentItem, playerPose, dropVelocity);
                        // TODO turn into projectile if melee weapon
                        items.put(itemDropped.getID(), itemDropped);

                        LinkedHashSet<int[]> tilesOn = tilesOn(itemDropped);
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].addItemDrop(itemDropped.getID());
                        }
                    } else {
                        LOGGER.warning("Player: " + playerID + "Failed to drop item.");
                    }

                } else if (request.getReload()) {
                    if (currentItem instanceof Gun) {
                        Gun currentGun = ((Gun) currentItem);
                        if (currentGun.attemptReload(currentPlayer.getAmmo(currentGun.getAmmoType())))
                            currentPlayer.setCurrentAction(ActionList.RELOADING);
                        currentPlayer.setCurrentItem(currentItem);
                    }
                }

                if (request.facingExists()) {
                    currentPlayer.setPose(new Pose(playerPose, request.getFacing()));
                }

                if (request.movementExists()) {
                    currentPlayer.setMoving(true);
                    currentPlayer.addNewForce(new Force(request.getMovementDirection(), currentPlayer.getMovementForce()));
                } else if (currentPlayer.getVelocity().getSpeed() > 1) {
                    int slowDirection = currentPlayer.getVelocity().getDirection() - 180;
                    if (slowDirection < 0)
                        slowDirection += 360;
                    currentPlayer.addNewForce(new Force(slowDirection, currentPlayer.getMovementForce()));
                }

                if (request.selectItemAtExists()) {
                    currentPlayer.setCurrentItemIndex(request.getSelectItemAt());
                    currentPlayer.setCurrentAction(ActionList.ITEM_SWITCH);
                }

                livingEntities.put(playerID, currentPlayer);

            }

            // prevent any processing if paused
            if (paused)
                continue;

            // process item drops
            LinkedList<Integer> itemsToRemove = new LinkedList<>();
            for (ItemDrop i : items.values()) {
                LinkedHashSet<int[]> tilesOn = tilesOn(i);
                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].removeItemDrop(i.getID());
                }

                int timeLeft = (int) (ItemDrop.DECAY_LENGTH - (lastProcessTime - i.getDropTime()));
                if (timeLeft <= 0) {
                    itemsToRemove.add(i.getID());
                } else {
                    i = (ItemDrop) doPhysics(i, tileMap, currentTimeDifference);
                    tilesOn = tilesOn(i);
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].addItemDrop(i.getID());
                    }
                    itemDropsView.add(new ItemDropView(i.getPose(), i.getSize(), i.getEntityListName(), i.isCloaked(), i.getStatus(), timeLeft));
                }
            }
            itemsToRemove.stream().forEach((i) -> items.remove(i));
            itemsToRemove = new LinkedList<>();

            // process enemies
            LinkedHashSet<EnemyView> enemiesView = new LinkedHashSet<>();
            LinkedHashSet<Integer> enemyIDs = gameState.getEnemyIDs();
            LinkedList<Integer> enemiesToRemove = new LinkedList<>();

            HashSet<Pose> playerPoses = new HashSet<>();
            for (Integer p : playerIDs) {
                LivingEntity currentPlayer = livingEntities.get(p);
                //if (currentPlayer.getStatus() != EntityStatus.DEAD)
                    playerPoses.add(currentPlayer.getPose());
            }

            for (Integer e : enemyIDs) {
                Enemy currentEnemy = (Enemy) livingEntities.get(e);

                // reset values
                currentEnemy.setMoving(false);
                currentEnemy.setTakenDamage(false);

                if (currentEnemy.hasEffect())
                    currentEnemy = (Enemy) currentEnemy.getEffect().applyEffect(currentEnemy);

                int enemyID = currentEnemy.getID();
                double maxMovementForce = currentEnemy.getMovementForce();
                EnemyAI ai = currentEnemy.getAI();

                if (!ai.isProcessing())
                    ai.setInfo(currentEnemy, playerPoses, tileMap);

                // handle enemyAI action
                AIAction enemyAction = ai.getAction();

                switch (enemyAction) {
                case ATTACK:
                    LinkedList<Attack> attacks = ai.getAttacks();
                    Force movementForce = ai.getForceFromAttack(maxMovementForce);
                    currentEnemy.addNewForce(movementForce);
                    currentEnemy.setPose(new Pose(currentEnemy.getLocation(), movementForce.getDirection()));
                    for (Attack a : attacks) {
                        switch (a.getAttackType()) {
                        case AOE:
                            AoeAttack aoeAttack = (AoeAttack) a;
                            LinkedHashSet<int[]> tilesOn = tilesOn(aoeAttack);
                            LinkedHashSet<Integer> affectedPlayers = new LinkedHashSet<>();

                            for (int[] tileCords : tilesOn) {
                                Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                                LinkedHashSet<Integer> playersOnTile = tileOn.getPlayersOnTile();

                                for (Integer playerID : playersOnTile) {
                                    if (affectedPlayers.contains(playerID))
                                        continue;
                                    Player playerBeingChecked = (Player) livingEntities.get(playerID);
                                    if (haveCollided(aoeAttack, playerBeingChecked)) {
                                        affectedPlayers.add(playerID);
                                        playerBeingChecked.damage(aoeAttack.getDamage());
                                        playerBeingChecked.setTakenDamage(true);
                                        playerBeingChecked.addNewForce(aoeAttack.getForce(playerBeingChecked.getPose(), currentEnemy.getLocation()));
                                        livingEntities.put(playerID, playerBeingChecked);
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
                    movementForce = ai.getMovementForce(maxMovementForce);
                    currentEnemy.addNewForce(movementForce);
                    currentEnemy.setPose(new Pose(currentEnemy.getLocation(), movementForce.getDirection()));
                    break;
                case WAIT:
                    break;
                case UPDATE:
                    currentEnemy = ai.getUpdatedEnemy();
                    break;
                default:
                    LOGGER.severe("AIAction " + enemyAction.toString() + " not known!");
                    break;
                }

                currentEnemy.setCurrentAction(ai.getActionState());

                if (currentEnemy.getStatus() != EntityStatus.DEAD) {
                    livingEntities.put(enemyID, currentEnemy);
                } else {
                    // TODO check if current action should be set to dead
                    enemiesToRemove.add(enemyID);
                }

                enemiesView.add(new EnemyView(currentEnemy.getPose(), currentEnemy.getSize(), currentEnemy.getEntityListName(), currentEnemy.isCloaked(),
                        currentEnemy.getStatus(), currentEnemy.getCurrentAction(), currentEnemy.hasTakenDamage(), currentEnemy.isMoving(),
                        currentEnemy.getHealth(), currentEnemy.getMaxHealth(), currentEnemy.getID()));
            }

            // process and remove dead enemies
            for (Integer enemyID : enemiesToRemove) {
                LivingEntity enemyToRemove = livingEntities.get(enemyID);
                LinkedHashSet<int[]> enemyTilesOn = tilesOn(enemyToRemove);
                for (int[] enemyTileCords : enemyTilesOn) {
                    tileMap[enemyTileCords[0]][enemyTileCords[1]].removeEnemy(enemyID);
                }

                LinkedList<ItemDrop> drops = enemyToRemove.getDrops();
                for (ItemDrop newDrop : drops) {
                    items.put(newDrop.getID(), newDrop);
                    // TODO spawned itemdrop status? is this needed?
                    itemDropsView.add(new ItemDropView(newDrop.getPose(), newDrop.getSize(), newDrop.getEntityListName(), newDrop.isCloaked(),
                            newDrop.getStatus(), (int) ItemDrop.DECAY_LENGTH));

                    LinkedHashSet<int[]> itemTilesOn = tilesOn(newDrop);
                    for (int[] itemTileCords : itemTilesOn) {
                        tileMap[itemTileCords[0]][itemTileCords[1]].addItemDrop(newDrop.getID());
                    }
                }

                livingEntities.remove(enemyID);
                enemyIDs.remove(enemyID);
                activeZones.get(enemyToRemove.getZoneID()).entityRemoved();
            }

            // process projectiles
            LinkedHashSet<Projectile> projectiles = gameState.getProjectiles();

            for (Projectile p : projectiles) {
                boolean removed = false;
                Projectile currentProjectile = p;
                double distanceMoved = getDistanceMoved(currentTimeDifference, currentProjectile.getSpeed());

                if (currentProjectile.maxRangeReached(distanceMoved)) { // check if max range
                    removed = true;
                } else {
                    // move the projectile
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
                            LOGGER.warning("Projectile went out of bounds. Is the map complete?");
                            break;
                        }

                        // remove if projectile hit solid tile
                        if (tileOn.getState() == TileState.SOLID) {
                            removed = currentProjectile.isRemoved(tileOn, Tile.tileToLocation(tileCords[0], tileCords[1]));
                            tileMapView[tileCords[0]][tileCords[1]] = new TileView(tileOn.getType(), tileOn.getState(), true); // Tile hit
                            break;
                        }
                    }

                    // check if projectile collides with a living entity
                    if (!removed) {
                        for (int[] tileCords : tilesOn) {
                            Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                            LinkedHashSet<Integer> entitiesOnTile = tileOn.getEntitiesOnTile();

                            for (Integer entityID : entitiesOnTile) {
                                LivingEntity entityBeingChecked = livingEntities.get(entityID);

                                if (currentProjectile.getTeam() != entityBeingChecked.getTeam() && haveCollided(currentProjectile, entityBeingChecked)) {
                                    entityBeingChecked.addNewForce(currentProjectile.getImpactForce());
                                    entityBeingChecked.setTakenDamage(true);

                                    if (currentProjectile instanceof HasEffect) {
                                        if (entityBeingChecked.hasEffect())
                                            entityBeingChecked = (Enemy) entityBeingChecked.getEffect().clearEffect(entityBeingChecked);
                                        entityBeingChecked.addEffect(((HasEffect) currentProjectile).getEffect());
                                    }
                                    removed = true;

                                    if (entityBeingChecked.damage(currentProjectile.getDamage()) && entityBeingChecked instanceof Enemy) {
                                        Player.changeScore(currentProjectile.getTeam(), ((Enemy) entityBeingChecked).getScoreOnKill());
                                    }
                                    livingEntities.put(entityID, entityBeingChecked);
                                    break; // bullet was removed no need to check other enemies
                                }
                            }

                            if (removed)
                                break; // Projectile is already gone.
                        }
                    }

                }

                // process projectile removal
                if (removed) {
                    if (currentProjectile.getEntityListName() == EntityList.CRYSTAL_BULLET) {
                        newProjectiles.addAll(((CrystalBullet) currentProjectile).getSplitProjectiles());
                    }
                } else {
                    newProjectiles.add(currentProjectile);
                    projectilesView.add(new ProjectileView(currentProjectile.getPose(), currentProjectile.getSize(), currentProjectile.getEntityListName(),
                            currentProjectile.isCloaked(), currentProjectile.getStatus()));
                }
            }

            // physics processing
            for (LivingEntity e : livingEntities.values()) {
                LivingEntity currentEntity = e;
                LinkedHashSet<int[]> tilesOn = tilesOn(currentEntity);
                int entityID = currentEntity.getID();

                if (currentEntity instanceof Player) {
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].removePlayer(entityID);
                    }
                } else {
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].removeEnemy(entityID);
                    }
                }

                currentEntity = (LivingEntity) doPhysics(currentEntity, tileMap, currentTimeDifference);

                tilesOn = tilesOn(currentEntity);
                LinkedHashSet<Integer> entitiesOnTile = new LinkedHashSet<>();

                for (int[] tileCords : tilesOn) {
                    Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                    entitiesOnTile.addAll(tileOn.getEntitiesOnTile());
                }

                double closestThing = Double.MAX_VALUE;
                int closestID = -1;

                for (int entityCheckedID : entitiesOnTile) {
                    LivingEntity entityBeingChecked = livingEntities.get(entityCheckedID);
                    if (entityID != entityCheckedID && haveCollided(currentEntity, entityBeingChecked)) {
                        double dist = getDistSqrd(currentEntity.getLocation(), entityBeingChecked.getLocation());
                        if (dist < closestThing) {
                            closestThing = dist;
                            closestID = entityCheckedID;
                        }
                    }
                }

                if (closestID != -1) {
                    LivingEntity e2 = livingEntities.get(closestID);
                    tilesOn = tilesOn(e2);
                    if (e2 instanceof Player) {
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].removePlayer(closestID);
                        }
                    } else {
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].removeEnemy(closestID);
                        }
                    }

                    HasPhysics result[] = Physics.objectCollision(currentEntity, e2, tileMap);
                    currentEntity = (LivingEntity) result[0];
                    e2 = (LivingEntity) result[1];

                    tilesOn = tilesOn(e2);
                    if (e2 instanceof Player) {
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].addPlayer(closestID);
                        }
                    } else {
                        for (int[] tileCords : tilesOn) {
                            tileMap[tileCords[0]][tileCords[1]].addEnemy(closestID);
                        }
                    }

                    livingEntities.put(closestID, e2);
                }

                tilesOn = tilesOn(currentEntity);
                if (currentEntity instanceof Player) {
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].addPlayer(entityID);
                    }
                } else {
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].addEnemy(entityID);
                    }
                }

                livingEntities.put(entityID, currentEntity);
            }

            // check if player picked up or activated anything
            for (Integer p : playerIDs) {
                Player currentPlayer = (Player) livingEntities.get(p);
                LinkedHashSet<int[]> tilesOn = tilesOn(currentPlayer);
                int playerID = currentPlayer.getID();

                // check for triggers
                for (int[] tileCords : tilesOn) {
                    Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
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
                            int dropQuantity = currentItemDrop.getQuantity();
                            ArrayList<Item> playerItems = currentPlayer.getItems();

                            switch (currentItemDrop.getItemType()) {
                            case AMMO:
                                AmmoList ammoType = currentItemDrop.getItemName().toAmmoList();
                                dropQuantity -= currentPlayer.addAmmo(ammoType, dropQuantity);
                                break;
                            case GUN: // TODO change case to include melee as well
                                if (playerItems.stream().anyMatch((i) -> i.getItemListName() == currentItemDrop.getItemName())) {
                                    // player already has that item TODO take some ammo from it
                                } else if (playerItems.size() < currentPlayer.getMaxItems()
                                        && (lastProcessTime - currentItemDrop.getDropTime()) > ItemDrop.DROP_FREEZE) {
                                    playerItems.add(currentItemDrop.getItem());
                                    dropQuantity -= 1;
                                }
                                break;
                            }

                            // process item drop being taken
                            if (dropQuantity != 0) {
                                currentItemDrop.setQuantity(dropQuantity);
                                items.put(itemDropID, currentItemDrop);
                            } else {
                                dropsToRemove.add(itemDropID);
                            }
                        }
                    }

                    // remove empty itemdrops
                    for (Integer dropID : dropsToRemove) {
                        ItemDrop dropToRemove = items.get(dropID);
                        LinkedHashSet<int[]> itemTilesOn = tilesOn(dropToRemove);
                        for (int[] itemTileCords : itemTilesOn) {
                            tileMap[itemTileCords[0]][itemTileCords[1]].removeItemDrop(dropID);
                        }
                        items.remove(dropID);
                    }

                }
                livingEntities.put(playerID, currentPlayer);
            }

            // process zones
            LinkedList<Integer> zonesToRemove = new LinkedList<>();

            // spawn new enemies from active zones
            for (Zone z : activeZones.values()) {
                for (Entity e : z.getEntitysToSpawn()) {
                    if (e instanceof Enemy) {
                        Enemy enemyToSpawn = (Enemy) e;
                        livingEntities.put(enemyToSpawn.getID(), enemyToSpawn);
                        enemyIDs.add(enemyToSpawn.getID());
                    }
                }

                // process zone tile changes
                for (Map.Entry<int[], Tile> tileChanged : z.getTileChanges().entrySet()) {
                    int[] cords = tileChanged.getKey();
                    Tile newTile = tileChanged.getValue();
                    tileMap[cords[0]][cords[1]] = newTile;
                    tileMapView[cords[0]][cords[1]] = new TileView(newTile.getType(), newTile.getState());
                }

                // mark inactive zones for removal
                if (!z.isActive())
                    zonesToRemove.add(z.getId());
            }

            zonesToRemove.stream().forEach((z) -> activeZones.remove(z));

            // update gamestate
            gameState.setPlayerIDs(playerIDs);
            gameState.setEnemyIDs(enemyIDs);
            gameState.setLivingEntities(livingEntities);
            gameState.setProjectiles(newProjectiles);
            gameState.setItems(items);
            gameState.setTileMap(tileMap);

            // turn players to player view
            LinkedHashSet<PlayerView> playersView = new LinkedHashSet<>();
            for (Integer p : playerIDs) {
                playersView.add(toPlayerView((Player) livingEntities.get(p)));
            }

            // check win condition
            Team remainingTeam = Team.NONE;
            boolean gameOver = true;
            if (playerIDs.size() != 1) {
                for (Integer p : playerIDs) {
                    LivingEntity playerBeingChecked = livingEntities.get(p);
                    if (playerBeingChecked.getStatus() != EntityStatus.DEAD) {
                        Team playerTeam = playerBeingChecked.getTeam();
                        if (remainingTeam != playerTeam && remainingTeam != Team.NONE) {
                            gameOver = false;
                            break;
                        } else {
                            remainingTeam = playerTeam;
                        }
                    }
                }

                if (!gameOver)
                    remainingTeam = Team.NONE;
            }

            // create gameview and send to handler
            GameView view = new GameView(playersView, enemiesView, projectilesView, itemDropsView, tileMapView, remainingTeam);
            handler.updateGameView(view);
        }
        LOGGER.info("Engine stopped!");
        printPerformanceInfo(totalTimeProcessing, numOfProcesses, longestTimeProcessing);
    }

    private void printPerformanceInfo(long totalTimeProcessing, long numOfProcesses, long longestTimeProcessing) {
        double avgTimeProcessing = (double) totalTimeProcessing / numOfProcesses;
        LOGGER.info("LongestTimeProcessing: " + longestTimeProcessing + "\n" + "TimeProcessing: " + totalTimeProcessing + "\n" + "NumOfProcesses: "
                + numOfProcesses + "\n" + "AverageTimeProcessing: " + avgTimeProcessing + "\n" + "ProjectileCount: " + gameState.getProjectiles().size() + "\n"
                + "EnemyCount: " + gameState.getEnemyIDs().size());
    }

    private static double getDistanceMoved(long timeDiff, double speed) {
        double distMoved = Physics.normaliseTime(timeDiff) * speed; // time in millis
        if (distMoved >= Tile.TILE_SIZE)
            LOGGER.warning("Entity moving too fast!");
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
                p.getAmmoList(), p.getID(), p.getTeam(), p.isCloaked(), p.getStatus(), p.getCurrentAction(), p.hasTakenDamage(), p.isMoving(), p.isPaused());
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
                LOGGER.info("Object clipped in tile.");
            }
        }

        int numOfTilesOn = tilesOn.size();
        frictionCoefficient = frictionCoefficient / numOfTilesOn;
        density = density / numOfTilesOn;

        Force frictionForce = Physics.getFrictionalForce(frictionCoefficient, mass, currentVelocity.getDirection());
        Force dragForce = Physics.getDragForce(density, currentVelocity, e.getSize());
        frictionForce.add(dragForce);
        Force newResultantForce = new Force(resultantForce.getDirection(), resultantForce.getForce());
        newResultantForce.add(frictionForce);

        if (resultantForce.getForce() <= frictionForce.getForce()
                && (Physics.getAcceleration(newResultantForce, mass) * Physics.normaliseTime(timeDiff)) > currentVelocity.getSpeed()) {
            e.setVelocity(new Velocity());
            LOGGER.info("Entity stopped by physics.");
            return e;
        } else {
            resultantForce = newResultantForce;
        }

        double acceleration = Physics.getAcceleration(resultantForce, mass);
        currentVelocity = Physics.getNewVelocity(acceleration, currentVelocity, resultantForce.getDirection(), timeDiff);

        e.setVelocity(currentVelocity);

        double distanceMoved = getDistanceMoved(timeDiff, currentVelocity.getSpeed() * Tile.TILE_SIZE);
        Location newLocation = Location.calculateNewLocation(e.getLocation(), currentVelocity.getDirection(), distanceMoved);
        e.setLocation(newLocation);

        // tile collisions
        tilesOn = tilesOn((Entity) e);
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
