package server.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.engine.ai.AIAction;
import server.engine.ai.enemyAI.EnemyAI;
import server.engine.state.ContainsAttack;
import server.engine.state.GameState;
import server.engine.state.HasEffect;
import server.engine.state.entity.Entity;
import server.engine.state.entity.ItemDrop;
import server.engine.state.entity.LivingEntity;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.player.Player;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.CreatesProjectiles;
import server.engine.state.item.Item;
import server.engine.state.item.consumable.Consumable;
import server.engine.state.item.pickup.Health;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.LaserGun;
import server.engine.state.item.weapon.gun.ProjectileGun;
import server.engine.state.laser.Laser;
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
import shared.lists.EntityStatus;
import shared.lists.ItemType;
import shared.lists.MapList;
import shared.lists.Team;
import shared.lists.TileState;
import shared.request.ClientRequests;
import shared.request.Request;
import shared.view.GameView;
import shared.view.GunView;
import shared.view.ItemView;
import shared.view.LaserView;
import shared.view.TileView;
import shared.view.entity.EnemyView;
import shared.view.entity.ItemDropView;
import shared.view.entity.PlayerView;
import shared.view.entity.ProjectileView;

public class ProcessGameState extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ProcessGameState.class.getName());
    private static final int MIN_TIME_DIFFERENCE = 17; // number of milliseconds between each process (approx 60th of a second).
    private static final int TICKS_TILL_INFO = 3600; // ticks between performance info being logged

    static {
        LOGGER.setLevel(Level.WARNING);
    }

    private final HasEngine handler;

    private GameState gameState;
    private GameView view;
    private ClientRequests clientRequests;
    private boolean handlerClosing;


    public ProcessGameState(HasEngine handler, MapList mapName, LinkedHashMap<String, Team> playersToAdd) {
        this.handler = handler;

        // generate initial player objects
        LinkedHashMap<Integer, LivingEntity> players = new LinkedHashMap<>();
        for (Map.Entry<String, Team> player : playersToAdd.entrySet()) {
            Player playerToAdd = new Player(player.getValue(), player.getKey());
            players.put(playerToAdd.getID(), playerToAdd);
            System.out.println(player.getValue());
        }


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

        // generates initial player views
        LinkedHashSet<PlayerView> playerViews = new LinkedHashSet<>();
        for (Map.Entry<Integer, LivingEntity> player : players.entrySet()) {
            Player playerToView = (Player) player.getValue();
            playerViews.add(toPlayerView(playerToView));
        }

        view = new GameView(playerViews, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView, Team.NONE);
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

    @Override
    public void run() {
        LOGGER.info("Starting engine.");
        handler.updateGameView(view); // sends initial view

        // time tracking variables
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
        boolean tileMapChanged = true;

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

            // calculates time difference and if a delay is needed
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
            LinkedHashSet<Laser> newLasers = new LinkedHashSet<>();
            LinkedHashMap<Integer, ItemDrop> items = gameState.getItems();
            LinkedHashMap<Integer, LivingEntity> livingEntities = gameState.getLivingEntities();

            // views for the handler
            TileView[][] tileMapView = view.getTileMap();
            LinkedHashSet<ProjectileView> projectilesView = new LinkedHashSet<>();
            LinkedHashSet<LaserView> lasersView = new LinkedHashSet<>();
            LinkedHashSet<ItemDropView> itemDropsView = new LinkedHashSet<>();

            // process player requests
            LinkedHashMap<Integer, Request> playerRequests = clientRequests.getPlayerRequests();
            LinkedHashSet<Integer> playerIDs = gameState.getPlayerIDs();
            clientRequests = new ClientRequests(playerIDs.size()); // clears requests to prevent duplicate actions

            // process player requests and updated number of paused players
            numPaused = processPlayerRequests(lastProcessTime, numPaused, tileMap, newProjectiles, newLasers, items, livingEntities, projectilesView,
                    lasersView, itemDropsView, playerRequests, playerIDs);

            // prevent any processing if paused
            if (numPaused == playerIDs.size()) {
                paused = true;
                continue;
            } else {
                paused = false;
            }

            // process item drops
            processItemDrops(lastProcessTime, currentTimeDifference, tileMap, items, itemDropsView);

            // process enemies
            LinkedHashSet<EnemyView> enemiesView = new LinkedHashSet<>();
            LinkedHashSet<Integer> enemyIDs = gameState.getEnemyIDs();
            LinkedList<Integer> enemiesToRemove = new LinkedList<>();

            HashSet<Pose> playerPoses = new HashSet<>();
            for (Integer p : playerIDs) {
                LivingEntity currentPlayer = livingEntities.get(p);
                // if (currentPlayer.getStatus() != EntityStatus.DEAD) //TODO when fixed in AI
                    playerPoses.add(currentPlayer.getPose());
            }

            // updated AIs tileMap if it changed
            if (tileMapChanged)
                EnemyAI.setTileMap(tileMap);

            processEnemies(tileMap, newProjectiles, livingEntities, projectilesView, enemiesView, enemyIDs, enemiesToRemove, playerPoses);

            // tileMap now assumed to be unchanged
            tileMapChanged = false;

            // process and remove dead enemies
            removeDeadEnemies(activeZones, tileMap, items, livingEntities, itemDropsView, enemyIDs, enemiesToRemove);

            // process projectiles
            LinkedHashSet<Projectile> projectiles = gameState.getProjectiles();
            processProjectiles(currentTimeDifference, tileMap, newProjectiles, livingEntities, tileMapView, projectilesView, projectiles);

            // lasers processing
            LinkedHashSet<Laser> lasers = gameState.getLasers();
            processLasers(tileMap, newLasers, livingEntities, lasersView, lasers);

            // physics processing
            processPhysics(currentTimeDifference, tileMap, livingEntities);

            // check if player picked up or activated anything
            for (Integer p : playerIDs) {
                processPlayerInteraction(lastProcessTime, inactiveZones, activeZones, tileMap, items, livingEntities, p);
            }

            // process zones
            tileMapChanged = processZones(activeZones, tileMap, livingEntities, tileMapView, enemyIDs, tileMapChanged);

            // update gamestate
            gameState.setPlayerIDs(playerIDs);
            gameState.setEnemyIDs(enemyIDs);
            gameState.setLivingEntities(livingEntities);
            gameState.setProjectiles(newProjectiles);
            gameState.setItems(items);
            gameState.setLasers(newLasers);
            gameState.setTileMap(tileMap);

            // turn players to player view
            LinkedHashSet<PlayerView> playersView = new LinkedHashSet<>();
            for (Integer p : playerIDs) {
                playersView.add(toPlayerView((Player) livingEntities.get(p)));
            }

            // check win condition
            Team remainingTeam = checkWinCondition(livingEntities, playerIDs);

            // create gameview and send to handler
            GameView view = new GameView(playersView, enemiesView, projectilesView, itemDropsView, lasersView, tileMapView, remainingTeam);
            handler.updateGameView(view);
        }
        LOGGER.info("Engine stopped!");
        printPerformanceInfo(totalTimeProcessing, numOfProcesses, longestTimeProcessing);
    }

    private Team checkWinCondition(LinkedHashMap<Integer, LivingEntity> livingEntities, LinkedHashSet<Integer> playerIDs) {
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
        return remainingTeam;
    }

    private int processPlayerRequests(long lastProcessTime, int numPaused, Tile[][] tileMap, LinkedHashSet<Projectile> newProjectiles,
            LinkedHashSet<Laser> newLasers, LinkedHashMap<Integer, ItemDrop> items, LinkedHashMap<Integer, LivingEntity> livingEntities,
            LinkedHashSet<ProjectileView> projectilesView, LinkedHashSet<LaserView> lasersView, LinkedHashSet<ItemDropView> itemDropsView,
            LinkedHashMap<Integer, Request> playerRequests, LinkedHashSet<Integer> playerIDs) {
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
                continue;
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
                LinkedHashSet<int[]> playerTilesOn = currentPlayer.getTilesOn();
                for (int[] playerTileCords : playerTilesOn) {
                    tileMap[playerTileCords[0]][playerTileCords[1]].removePlayer(playerID);
                }

                spawnDrops(tileMap, items, itemDropsView, currentPlayer);

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
            else if (currentPlayer.getStatus() == EntityStatus.SPAWNING)
                currentPlayer.setStatus(EntityStatus.NONE);

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

            long useTime = currentPlayer.getLastUseTime();
            if (request.getShoot() && useTime < lastProcessTime) {
                if (currentItem.getItemType() == ItemType.GUN) {
                    Gun currentGun = (Gun) currentItem;
                    if (currentGun.shoot(currentPlayer.getAmmo(currentGun.getAmmoType()))) {
                        currentPlayer.setCurrentAction(ActionList.ATTACKING);
                        shootGun(tileMap, newProjectiles, newLasers, projectilesView, lasersView, currentPlayer, currentGun);
                    }
                } else if (currentItem.getItemType() == ItemType.CONSUMEABLE
                        && (lastProcessTime - useTime) > Player.CONSUMABLE_COOLDOWN) {
                    Consumable currentConsumable = (Consumable) currentItem;
                    // TODO use enum to improve performance?
                    if (currentItem instanceof CreatesProjectiles) {
                        newProjectiles.addAll(((CreatesProjectiles) currentConsumable).getProjectiles(playerPose, currentPlayer.getTeam()));
                    } else if (currentItem instanceof HasEffect) {
                        // TODO
                    } else if (currentItem instanceof ContainsAttack) {
                        // TODO
                    } else {
                        LOGGER.warning("Unknown consumable used: " + currentItem.getItemListName().toString());
                    }

                    if (currentConsumable.isRemoved()) {
                        currentPlayer.setUseTime(lastProcessTime + 150); // TODO better if this could be solved client-side
                        if (!currentPlayer.removeItem(currentPlayer.getCurrentItemIndex())) {
                            LOGGER.warning("Player: " + playerID + "Failed to remove consumable.");
                        }
                    } else {
                        currentPlayer.setUseTime(lastProcessTime);
                    }
                }
            }

            if (request.getDrop() && currentPlayer.getItems().size() > 1) {
                if (currentPlayer.removeItem(currentPlayer.getCurrentItemIndex())) {
                    LOGGER.info("Player: " + playerID + " dropped item.");
                    currentPlayer.setCurrentAction(ActionList.THROW);
                    dropItem(tileMap, items, currentPlayer, currentItem);
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
        return numPaused;
    }

    private void spawnDrops(Tile[][] tileMap, LinkedHashMap<Integer, ItemDrop> items, LinkedHashSet<ItemDropView> itemDropsView, LivingEntity entity) {
        LinkedList<ItemDrop> drops = entity.getDrops();
        for (ItemDrop newDrop : drops) {
            items.put(newDrop.getID(), newDrop);
            // TODO spawned itemdrop status? is this needed?
            itemDropsView.add(new ItemDropView(newDrop.getPose(), newDrop.getSize(), newDrop.getEntityListName(), newDrop.isCloaked(),
                    newDrop.getStatus(), (int) ItemDrop.DECAY_LENGTH));

            LinkedHashSet<int[]> itemTilesOn = newDrop.getTilesOn();
            for (int[] itemTileCords : itemTilesOn) {
                tileMap[itemTileCords[0]][itemTileCords[1]].addItemDrop(newDrop.getID());
            }
        }
    }

    private void shootGun(Tile[][] tileMap, LinkedHashSet<Projectile> newProjectiles, LinkedHashSet<Laser> newLasers,
            LinkedHashSet<ProjectileView> projectilesView, LinkedHashSet<LaserView> lasersView, LivingEntity shootingEntity, Gun gunShot) {
        Pose gunPose = shootingEntity.getPose();
        if (gunShot instanceof ProjectileGun) {
            LinkedList<Projectile> shotProjectiles = ((ProjectileGun) gunShot).getProjectiles(gunPose, shootingEntity.getTeam());
            for (Projectile p : shotProjectiles) {
                newProjectiles.add(p);
                projectilesView.add(new ProjectileView(p.getPose(), p.getSize(), p.getEntityListName(), p.isCloaked(), p.getStatus()));
            }
        } else {
            LinkedList<Laser> shotLasers = ((LaserGun) gunShot).getLasers(gunPose, shootingEntity.getTeam(), tileMap);
            for (Laser l : shotLasers) {
                newLasers.add(l);
                lasersView.add(new LaserView(l.getStart(), l.getEnd(), l.getSize(), l.getTeam()));
            }
        }
    }

    private void dropItem(Tile[][] tileMap, LinkedHashMap<Integer, ItemDrop> items, LivingEntity entityWhoDropped, Item itemToDrop) {
        Pose dropPose = entityWhoDropped.getPose();
        Velocity dropVelocity = new Velocity(dropPose.getDirection(), 30);
        dropVelocity.add(entityWhoDropped.getVelocity());
        ItemDrop itemDropped = new ItemDrop(itemToDrop, dropPose, dropVelocity);
        // TODO turn into projectile if melee weapon
        items.put(itemDropped.getID(), itemDropped);

        LinkedHashSet<int[]> tilesOn = itemDropped.getTilesOn();
        for (int[] tileCords : tilesOn) {
            tileMap[tileCords[0]][tileCords[1]].addItemDrop(itemDropped.getID());
        }
    }

    private void processItemDrops(long currentTime, long timeDiff, Tile[][] tileMap, LinkedHashMap<Integer, ItemDrop> items,
            LinkedHashSet<ItemDropView> itemDropsView) {
        LinkedList<Integer> itemsToRemove = new LinkedList<>();
        for (ItemDrop i : items.values()) {
            LinkedHashSet<int[]> tilesOn = i.getTilesOn();
            for (int[] tileCords : tilesOn) {
                tileMap[tileCords[0]][tileCords[1]].removeItemDrop(i.getID());
            }

            int timeLeft = (int) (ItemDrop.DECAY_LENGTH - (currentTime - i.getDropTime()));
            if (timeLeft <= 0) {
                itemsToRemove.add(i.getID());
            } else {
                doMovementPhysics(i, tileMap, timeDiff);
                tilesOn = i.getTilesOn();
                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].addItemDrop(i.getID());
                }
                itemDropsView.add(new ItemDropView(i.getPose(), i.getSize(), i.getEntityListName(), i.isCloaked(), i.getStatus(), timeLeft));
            }
        }
        itemsToRemove.stream().forEach((i) -> items.remove(i));
    }

    private void processEnemies(Tile[][] tileMap, LinkedHashSet<Projectile> newProjectiles, LinkedHashMap<Integer, LivingEntity> livingEntities,
            LinkedHashSet<ProjectileView> projectilesView, LinkedHashSet<EnemyView> enemiesView, LinkedHashSet<Integer> enemyIDs,
            LinkedList<Integer> enemiesToRemove, HashSet<Pose> playerPoses) {
        for (Integer e : enemyIDs) {
            Enemy currentEnemy = (Enemy) livingEntities.get(e);

            // reset values
            currentEnemy.setMoving(false);
            currentEnemy.setTakenDamage(false);

            if (currentEnemy.hasEffect())
                currentEnemy = (Enemy) currentEnemy.getEffect().applyEffect(currentEnemy);
            else if (currentEnemy.getStatus() == EntityStatus.SPAWNING)
                currentEnemy.setStatus(EntityStatus.NONE);

            int enemyID = currentEnemy.getID();
            double maxMovementForce = currentEnemy.getMovementForce();
            EnemyAI ai = currentEnemy.getAI();

            if (!ai.isProcessing()) {
                ai.setInfo(currentEnemy, playerPoses);
            }

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
                        applyAoeAttack(tileMap, livingEntities, currentEnemy.getLocation(), (AoeAttack) a);
                        break;
                    case PROJECTILE:
                        ProjectileAttack projectileAttack = (ProjectileAttack) a;
                        for (Projectile p : projectileAttack.getProjectiles()) {
                            newProjectiles.add(p);
                            projectilesView.add(new ProjectileView(p.getPose(), 1, p.getEntityListName(), p.isCloaked(), p.getStatus()));
                        }
                        break;
                    default:
                        LOGGER.warning("Unkown attack: " + a.getAttackType().toString() + ". from enemy: " + currentEnemy.getID());
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
                Location oldLoc = currentEnemy.getLocation();
                int oldSize = currentEnemy.getSize();
                currentEnemy = ai.getUpdatedEnemy();
                handleEnemyChange(tileMap, currentEnemy, enemyID, oldLoc, oldSize);
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
    }

    private void handleEnemyChange(Tile[][] tileMap, Enemy currentEnemy, int enemyID, Location oldLoc, int oldSize) {
        Location newLoc = currentEnemy.getLocation();
        int newSize = currentEnemy.getSize();
        if (!newLoc.equals(oldLoc) || newSize < oldSize) {
            currentEnemy.setLocation(oldLoc);
            LinkedHashSet<int[]> enemyTilesOn = currentEnemy.getTilesOn();
            for (int[] enemyTileCords : enemyTilesOn) {
                tileMap[enemyTileCords[0]][enemyTileCords[1]].removeEnemy(enemyID);
            }
            currentEnemy.setLocation(newLoc);
            enemyTilesOn = currentEnemy.getTilesOn();
            for (int[] enemyTileCords : enemyTilesOn) {
                tileMap[enemyTileCords[0]][enemyTileCords[1]].addEnemy(enemyID);
            }
        } else if (newSize > oldSize) {
            LinkedHashSet<int[]> enemyTilesOn = currentEnemy.getTilesOn();
            for (int[] enemyTileCords : enemyTilesOn) {
                tileMap[enemyTileCords[0]][enemyTileCords[1]].addEnemy(enemyID);
            }
        }
    }

    private void removeDeadEnemies(LinkedHashMap<Integer, Zone> activeZones, Tile[][] tileMap, LinkedHashMap<Integer, ItemDrop> items,
            LinkedHashMap<Integer, LivingEntity> livingEntities, LinkedHashSet<ItemDropView> itemDropsView, LinkedHashSet<Integer> enemyIDs,
            LinkedList<Integer> enemiesToRemove) {
        for (Integer enemyID : enemiesToRemove) {
            LivingEntity enemyToRemove = livingEntities.get(enemyID);
            LinkedHashSet<int[]> enemyTilesOn = enemyToRemove.getTilesOn();
            for (int[] enemyTileCords : enemyTilesOn) {
                tileMap[enemyTileCords[0]][enemyTileCords[1]].removeEnemy(enemyID);
            }

            spawnDrops(tileMap, items, itemDropsView, enemyToRemove);

            livingEntities.remove(enemyID);
            enemyIDs.remove(enemyID);
            activeZones.get(enemyToRemove.getZoneID()).entityRemoved();
        }
    }

    private void processPhysics(long timeDiff, Tile[][] tileMap, LinkedHashMap<Integer, LivingEntity> livingEntities) {
        for (LivingEntity e : livingEntities.values()) {
            LivingEntity currentEntity = e;
            int entityID = currentEntity.getID();

            removeToTiles(tileMap, currentEntity, entityID);

            doMovementPhysics(currentEntity, tileMap, timeDiff);

            int closestID = getClosestEntity(tileMap, currentEntity, entityID, livingEntities);

            if (closestID != -1) {
                LivingEntity e2 = livingEntities.get(closestID);
                removeToTiles(tileMap, e2, closestID);

                Physics.objectCollision(currentEntity, e2, tileMap);
                addToTiles(tileMap, e2, closestID);
                livingEntities.put(closestID, e2);
            }

            addToTiles(tileMap, currentEntity, entityID);
            livingEntities.put(entityID, currentEntity);
        }
    }

    private Integer getClosestEntity(Tile[][] tileMap, LivingEntity entity, int entityID, LinkedHashMap<Integer, LivingEntity> livingEntities) {
        LinkedHashSet<Integer> entitiesOnTile = new LinkedHashSet<>();

        for (int[] tileCords : entity.getTilesOn()) {
            Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
            entitiesOnTile.addAll(tileOn.getEntitiesOnTile());
        }

        double closestThing = Double.MAX_VALUE;
        int closestID = -1;

        for (int entityCheckedID : entitiesOnTile) {
            LivingEntity entityBeingChecked = livingEntities.get(entityCheckedID);
            if (entityID != entityCheckedID && entity.haveCollided(entityBeingChecked)) {
                double dist = getDistSqrd(entity.getLocation(), entityBeingChecked.getLocation());
                if (dist < closestThing) {
                    closestThing = dist;
                    closestID = entityCheckedID;
                }
            }
        }
        return closestID;
    }

    private void removeToTiles(Tile[][] tileMap, LivingEntity entity, int entityID) {
        LinkedHashSet<int[]> tilesOn;
        tilesOn = entity.getTilesOn();
        if (entity instanceof Player) {
            for (int[] tileCords : tilesOn) {
                tileMap[tileCords[0]][tileCords[1]].removePlayer(entityID);
            }
        } else {
            for (int[] tileCords : tilesOn) {
                tileMap[tileCords[0]][tileCords[1]].removeEnemy(entityID);
            }
        }
    }

    private void addToTiles(Tile[][] tileMap, LivingEntity entity, int entityID) {
        LinkedHashSet<int[]> tilesOn;
        tilesOn = entity.getTilesOn();
        if (entity instanceof Player) {
            for (int[] tileCords : tilesOn) {
                tileMap[tileCords[0]][tileCords[1]].addPlayer(entityID);
            }
        } else {
            for (int[] tileCords : tilesOn) {
                tileMap[tileCords[0]][tileCords[1]].addEnemy(entityID);
            }
        }
    }

    private static void doMovementPhysics(HasPhysics e, Tile[][] tileMap, long timeDiff) {
        Force resultantForce = e.getResultantForce();
        Velocity currentVelocity = e.getVelocity();

        if (currentVelocity.getSpeed() == 0 && resultantForce.getForce() == 0)
            return;

        double mass = e.getMass();
        LinkedHashSet<int[]> tilesOn = ((Entity) e).getTilesOn();

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
            return;
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
        tilesOn = ((Entity) e).getTilesOn();
        int[] mostSigTileCords = getMostSignificatTile(newLocation, tilesOn, tileMap);
        if (mostSigTileCords[0] != -1) {
            // TODO add hit sound to tile if velocity is high
            Location mostSigTileLoc = Tile.tileToLocation(mostSigTileCords[0], mostSigTileCords[1]);
            Physics.tileCollision(e, mostSigTileLoc, tileMap[mostSigTileCords[0]][mostSigTileCords[1]].getBounceCoefficient());
        }
    }

    private void processLasers(Tile[][] tileMap, LinkedHashSet<Laser> newLasers, LinkedHashMap<Integer, LivingEntity> livingEntities,
            LinkedHashSet<LaserView> lasersView, LinkedHashSet<Laser> lasers) {
        for (Laser l : lasers) {
            if (!l.isRemoved()) {
                LinkedHashSet<int[]> laserTilesOn = l.getTilesOn();
                for (int[] tileCords : laserTilesOn) {
                    LinkedHashSet<Integer> entitiesOnTile = tileMap[tileCords[0]][tileCords[1]].getEntitiesOnTile();
                    for (Integer entityID : entitiesOnTile) {
                        LivingEntity entityBeingChecked = livingEntities.get(entityID);
                        if (l.getTeam() != entityBeingChecked.getTeam() && l.canDamage(entityID) && entityBeingChecked.haveCollided(l)) {
                            entityBeingChecked.damage(l.getDamage());
                            l.Damaged(entityID);
                            // TODO add force from laser
                            livingEntities.put(entityID, entityBeingChecked);
                        }
                    }
                }

                lasersView.add(new LaserView(l.getStart(), l.getEnd(), l.getSize(), l.getTeam()));
                newLasers.add(l);
            }
        }
    }

    private void processPlayerInteraction(long lastProcessTime, LinkedHashMap<Integer, Zone> inactiveZones, LinkedHashMap<Integer, Zone> activeZones,
            Tile[][] tileMap, LinkedHashMap<Integer, ItemDrop> items, LinkedHashMap<Integer, LivingEntity> livingEntities, Integer p) {
        Player currentPlayer = (Player) livingEntities.get(p);
        LinkedHashSet<int[]> tilesOn = currentPlayer.getTilesOn();
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

                if (currentPlayer.haveCollided(currentItemDrop)) {
                    int dropQuantity = currentItemDrop.getQuantity();
                    ArrayList<Item> playerItems = currentPlayer.getItems();

                    ItemType dropType = currentItemDrop.getItemType();
                    switch (dropType) {
                    case HEALTH:
                        int healthDiff = currentPlayer.getMaxHealth() - currentPlayer.getHealth();
                        if (healthDiff > 0) {
                            if (dropQuantity > healthDiff) {
                                dropQuantity -= healthDiff;
                                currentPlayer.setHealth(currentPlayer.getMaxHealth());
                                if (dropQuantity == 1) {
                                    currentItemDrop.setItem(Health.makeHealth(1));
                                }
                            } else {
                                currentPlayer.setHealth(currentPlayer.getHealth() + dropQuantity);
                                dropQuantity = 0;
                            }
                        }
                        break;
                    case AMMO:
                        AmmoList ammoType = currentItemDrop.getItemName().toAmmoList();
                        dropQuantity -= currentPlayer.addAmmo(ammoType, dropQuantity);
                        break;
                    case CONSUMEABLE:
                    case MELEE_WEAPON:
                    case GUN:
                        if (playerItems.stream().anyMatch((i) -> i.getItemListName() == currentItemDrop.getItemName())) {
                            if (dropType == ItemType.CONSUMEABLE) {
                                // TODO stack the amount
                            } else if (dropType == ItemType.GUN) {
                                // TODO take some ammo from it
                            }
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
                LinkedHashSet<int[]> itemTilesOn = dropToRemove.getTilesOn();
                for (int[] itemTileCords : itemTilesOn) {
                    tileMap[itemTileCords[0]][itemTileCords[1]].removeItemDrop(dropID);
                }
                items.remove(dropID);
            }

        }
        livingEntities.put(playerID, currentPlayer);
    }

    private boolean processZones(LinkedHashMap<Integer, Zone> activeZones, Tile[][] tileMap, LinkedHashMap<Integer, LivingEntity> livingEntities,
            TileView[][] tileMapView, LinkedHashSet<Integer> enemyIDs, boolean tileMapChanged) {
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
                tileMapChanged = true;
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
        return tileMapChanged;
    }

    private void processProjectiles(long currentTimeDifference, Tile[][] tileMap, LinkedHashSet<Projectile> newProjectiles,
            LinkedHashMap<Integer, LivingEntity> livingEntities, TileView[][] tileMapView, LinkedHashSet<ProjectileView> projectilesView,
            LinkedHashSet<Projectile> projectiles) {
        for (Projectile p : projectiles) {
            Projectile currentProjectile = p;
            boolean removed = currentProjectile.isRemoved();
            
            if (!removed) {
                double distanceMoved = getDistanceMoved(currentTimeDifference, currentProjectile.getSpeed());

                if (currentProjectile.maxRangeReached(distanceMoved)) { // check if max range
                    removed = true;
                } else {
                    // move the projectile
                    Location newLocation = Location.calculateNewLocation(currentProjectile.getLocation(), currentProjectile.getPose().getDirection(),
                            distanceMoved);
                    Laser projectileCoverage = new Laser(currentProjectile.getLocation(), newLocation, currentProjectile.getSize(), 0, 0, Team.NONE);
                    currentProjectile.setLocation(newLocation);
                    LinkedHashSet<int[]> tilesOn = projectileCoverage.getTilesOn();
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
                            LinkedHashSet<Integer> entitiesOnTile = tileMap[tileCords[0]][tileCords[1]].getEntitiesOnTile();
                            for (Integer entityID : entitiesOnTile) {
                                LivingEntity entityBeingChecked = livingEntities.get(entityID);

                                if (currentProjectile.getTeam() != entityBeingChecked.getTeam() && entityBeingChecked.haveCollided(projectileCoverage)) {
                                    removed = applyProjectileHit(livingEntities, currentProjectile, entityID, entityBeingChecked);
                                    if (removed)
                                        break; // Projectile is already gone.
                                }
                            }

                            if (removed)
                                break; // Projectile is already gone.
                        }
                    }
                }
            }

            // process projectile removal
            if (removed) {
                processProjectileRemoval(tileMap, newProjectiles, livingEntities, projectilesView, currentProjectile);
            } else {
                newProjectiles.add(currentProjectile);
                projectilesView.add(new ProjectileView(currentProjectile.getPose(), currentProjectile.getSize(), currentProjectile.getEntityListName(),
                        currentProjectile.isCloaked(), currentProjectile.getStatus()));
            }
        }
    }

    private void processProjectileRemoval(Tile[][] tileMap, LinkedHashSet<Projectile> newProjectiles, LinkedHashMap<Integer, LivingEntity> livingEntities,
            LinkedHashSet<ProjectileView> projectilesView, Projectile currentProjectile) {
        if (currentProjectile instanceof ContainsAttack) {
            Attack attack = ((ContainsAttack) currentProjectile).getAttack();
            switch (attack.getAttackType()) {
            case AOE:
                applyAoeAttack(tileMap, livingEntities, currentProjectile.getLocation(), (AoeAttack) attack);
                break;
            case PROJECTILE:
                ProjectileAttack projectileAttack = (ProjectileAttack) attack;
                for (Projectile p2 : projectileAttack.getProjectiles()) {
                    newProjectiles.add(p2);
                    projectilesView.add(new ProjectileView(p2.getPose(), 1, p2.getEntityListName(), p2.isCloaked(), p2.getStatus()));
                }
                break;
            default:
                LOGGER.warning("Unkown attack: " + attack.getAttackType().toString() + ". from projectile: "
                        + currentProjectile.getEntityListName().toString());
                break;
            }
        }
    }

    private boolean applyProjectileHit(LinkedHashMap<Integer, LivingEntity> livingEntities, Projectile currentProjectile, Integer entityID,
            LivingEntity entityBeingChecked) {
        boolean removed;
        entityBeingChecked.addNewForce(currentProjectile.getImpactForce());
        entityBeingChecked.setTakenDamage(true);

        if (currentProjectile instanceof HasEffect) {
            if (entityBeingChecked.hasEffect())
                entityBeingChecked = entityBeingChecked.getEffect().clearEffect(entityBeingChecked);
            entityBeingChecked.addEffect(((HasEffect) currentProjectile).getEffect());
        }
        removed = currentProjectile.isRemoved(entityBeingChecked);

        if (entityBeingChecked.damage(currentProjectile.getDamage()) && entityBeingChecked instanceof Enemy) {
            Player.changeScore(currentProjectile.getTeam(), ((Enemy) entityBeingChecked).getScoreOnKill());
        }
        livingEntities.put(entityID, entityBeingChecked);
        return removed;
    }

    private void printPerformanceInfo(long totalTimeProcessing, long numOfProcesses, long longestTimeProcessing) {
        double avgTimeProcessing = (double) totalTimeProcessing / numOfProcesses;
        LOGGER.info("LongestTimeProcessing: " + longestTimeProcessing + "\n" + "TimeProcessing: " + totalTimeProcessing + "\n" + "NumOfProcesses: "
                + numOfProcesses + "\n" + "AverageTimeProcessing: " + avgTimeProcessing + "\n" + "ProjectileCount: " + gameState.getProjectiles().size() + "\n"
                + "EnemyCount: " + gameState.getEnemyIDs().size());
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

    private static double getDistanceMoved(long timeDiff, double speed) {
        double distMoved = Physics.normaliseTime(timeDiff) * speed; // time in millis
        if (distMoved >= Tile.TILE_SIZE) {
            LOGGER.warning("Entity moving too fast! Slowing it down...");
            distMoved = Tile.TILE_SIZE;
        }
        return distMoved;
    }

    private static PlayerView toPlayerView(Player p) {
        ArrayList<ItemView> playerItems = new ArrayList<>();
        for (Item i : p.getItems()) {
            if (i.getItemType() == ItemType.GUN) {
                Gun g = (Gun) i;
                playerItems.add(new GunView(g.getItemListName(), g.getAmmoType(), g.getClipSize(), g.getAmmoInClip(), g.isAutoFire(), g.getReloadTime()));
            } else {
                playerItems.add(new ItemView(i.getItemListName(), i.getItemType()));
            }
        }
        return new PlayerView(p.getPose(), p.getSize(), p.getHealth(), p.getMaxHealth(), playerItems, p.getCurrentItemIndex(), p.getScore(), p.getName(),
                p.getAmmoList(), p.getID(), p.getTeam(), p.isCloaked(), p.getStatus(), p.getCurrentAction(), p.hasTakenDamage(), p.isMoving(), p.isPaused());
    }

    private static void applyAoeAttack(Tile[][] tileMap, LinkedHashMap<Integer, LivingEntity> livingEntities, Location source,
            AoeAttack aoeAttack) {
        LinkedHashSet<int[]> tilesOn = aoeAttack.getTilesOn();
        LinkedHashSet<Integer> affectedEntities = new LinkedHashSet<>();

        for (int[] tileCords : tilesOn) {
            Tile tileOn;

            // easy way to handle attack clipping out of the map.
            try {
                tileOn = tileMap[tileCords[0]][tileCords[1]];
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }

            LinkedHashSet<Integer> entitiesOnTile = tileOn.getEntitiesOnTile();

            for (Integer entitiyID : entitiesOnTile) {
                if (affectedEntities.contains(entitiyID))
                    continue;
                LivingEntity entitiyBeingChecked = livingEntities.get(entitiyID);
                if (aoeAttack.getTeam() != entitiyBeingChecked.getTeam() && aoeAttack.haveCollided(entitiyBeingChecked)) {
                    affectedEntities.add(entitiyID);
                    entitiyBeingChecked.damage(aoeAttack.getDamage());
                    entitiyBeingChecked.setTakenDamage(true);
                    entitiyBeingChecked.addNewForce(aoeAttack.getForce(entitiyBeingChecked.getPose(), source));
                    livingEntities.put(entitiyID, entitiyBeingChecked);
                }
            }
        }
    }

    private static double getDistSqrd(Location e1Loc, Location e2Loc) {
        double xDiff = e1Loc.getX() - e2Loc.getX();
        double yDiff = e1Loc.getY() - e2Loc.getY();
        double distSqrd = Math.pow(xDiff, 2) + Math.pow(yDiff, 2);

        return distSqrd;
    }

}
