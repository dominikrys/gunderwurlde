package server.game_engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import data.GameState;
import data.Location;
import data.Pose;
import data.entity.enemy.Drop;
import data.entity.enemy.Enemy;
import data.entity.enemy.EnemyList;
import data.entity.item.Item;
import data.entity.item.ItemDrop;
import data.entity.item.ItemType;
import data.entity.item.weapon.gun.Gun;
import data.entity.player.Player;
import data.entity.projectile.Projectile;
import data.entity.projectile.SmallBullet;
import data.map.GameMap;
import data.map.tile.Tile;
import data.map.tile.TileState;
import server.Server;
import server.game_engine.ai.AIAction;
import server.game_engine.ai.Attack;
import server.game_engine.ai.EnemyAI;
import server.game_engine.ai.ZombieAI;
import server.game_engine.data.ChangeType;
import server.game_engine.data.EnemyChange;
import server.game_engine.data.GameStateChanges;
import server.game_engine.data.ItemDropChange;
import server.game_engine.data.PlayerChange;
import server.game_engine.data.ProjectileChange;
import server.game_engine.data.TileChange;
import server.request.ClientRequests;
import server.request.Request;

public class ProcessGameState extends Thread {
    private static final int MIN_TIME_DIFFERENCE = 17; // number of milliseconds between each process (approx 60th of a second).

    private final Server server;

    private GameState gameState;
    private ClientRequests clientRequests;
    private boolean serverClosing;

    public ProcessGameState(Server server, GameState gameState) {
        this.server = server;
        this.gameState = gameState;
        this.serverClosing = false;
    }

    public void setClientRequests(ClientRequests clientRequests) {
        this.clientRequests = clientRequests;
    }

    public void serverClosing() {
        this.serverClosing = true;
        this.notify();
    }

    @Override
    public void run() {
        long lastProcessTime = System.currentTimeMillis();
        long currentTimeDifference = 0;

        while (!serverClosing) {
            currentTimeDifference = System.currentTimeMillis() - lastProcessTime;
            long timeDiff = MIN_TIME_DIFFERENCE - currentTimeDifference;
            if (timeDiff > 0) {
                try {
                    this.wait(timeDiff);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    continue;
                }
                if (serverClosing)
                    break;
                currentTimeDifference = MIN_TIME_DIFFERENCE;
            }
            lastProcessTime = System.currentTimeMillis();
            if (clientRequests != null)
                continue; // waits until clients start doing something.

            // extract/setup necessary data
            GameMap currentMap = gameState.getCurrentMap();
            Tile[][] tileMap = currentMap.getTileMap();

            LinkedHashSet<Projectile> newProjectiles = new LinkedHashSet<>();
            LinkedHashSet<ItemDrop> newItems = new LinkedHashSet<>();

            LinkedHashSet<ItemDropChange> itemDropChanges = new LinkedHashSet<>();
            LinkedHashMap<Integer, ItemDrop> items = new LinkedHashMap<>();
            gameState.getItems().stream().forEach((i) -> items.put(i.getID(), i));

            // TODO include tile changes here

            // process player requests
            LinkedHashMap<Integer, Request> playerRequests = clientRequests.getPlayerRequests();
            LinkedHashMap<Integer, PlayerChange> playerChanges = new LinkedHashMap<>();
            LinkedHashMap<Integer, Player> players = new LinkedHashMap<>();
            gameState.getPlayers().stream().forEach((p) -> players.put(p.getID(), p));

            for (Map.Entry<Integer, Request> playerRequest : playerRequests.entrySet()) {
                int playerID = playerRequest.getKey();
                Player currentPlayer = players.get(playerID);
                Request request = playerRequest.getValue();

                if (request.getLeave()) {
                    players.remove(playerID);
                    server.removePlayer(playerID);
                    continue;
                }

                Item currentItem = currentPlayer.getCurrentItem();
                if (currentItem.getItemType() == ItemType.GUN) {
                    if (((Gun) currentItem).isReloading()) {
                        if (((Gun) currentItem).reload()) {
                            // TODO reload complete, gun updates itself but it might be good to send
                            // confirmation as well?
                        }
                    }
                }

                if (request.getShoot()) {
                    if (currentItem.getItemType() == ItemType.GUN) {
                        Gun currentGun = (Gun) currentItem;
                        if (currentGun.shoot()) {
                            int numOfBullets = currentGun.getProjectilesPerShot();
                            int spread = currentGun.getSpread();
                            int bulletSpacing = 0;
                            if (numOfBullets > 1)
                                bulletSpacing = (2 * spread) / (numOfBullets - 1);

                            LinkedList<Pose> bulletPoses = new LinkedList<>();
                            Pose gunPose = currentPlayer.getPose(); // TODO change pose to include gunlength/position

                            int nextDirection = gunPose.getDirection() - spread;
                            for (int i = 0; i < numOfBullets; i++) {
                                bulletPoses.add(new Pose(gunPose, nextDirection));
                                nextDirection += bulletSpacing;
                            }

                            switch (currentGun.getProjectileType()) {
                            case SMALLBULLET:
                                for (Pose p : bulletPoses)
                                    newProjectiles.add(new SmallBullet(p));
                                break;
                            default:
                                System.out.println("Projectile type not known for: " + currentItem.getItemName().toString());
                                break;
                            }
                        }
                    }
                }

                if (request.getReload()) {
                    if (currentItem.getItemType() == ItemType.GUN) {
                        ((Gun) currentItem).attemptReload();
                    }
                }

                if (request.facingExists() || request.movementExists()) {
                    Pose playerPose = currentPlayer.getPose();
                    int facingDirection = playerPose.getDirection();
                    Location newLocation = playerPose;

                    if (request.facingExists()) {
                        facingDirection = request.getFacing();
                    }

                    if (request.movementExists()) {
                        int distanceMoved = getDistanceMoved(currentTimeDifference, currentPlayer.getMoveSpeed());
                        newLocation = Location.calculateNewLocation(playerPose, request.getMovementDirection(), distanceMoved);

                        int[] tileCords = Tile.locationToTile(newLocation);
                        Tile tileOn = tileMap[tileCords[0]][tileCords[1]];

                        if (tileOn.getState() == TileState.SOLID)
                            newLocation = playerPose;
                        else {
                            // TODO include knock-back of player/enemies depending on some factor e.g. size.

                            HashSet<Integer> itemsOnTile = tileOn.getItemDropsOnTile();
                            ArrayList<Item> playerItems = currentPlayer.getItems();
                            for (Integer itemDropID : itemsOnTile) {
                                ItemDrop currentItemDrop = items.get(itemDropID);
                                int dropQuantity = currentItemDrop.getQuantity();
                                switch(currentItemDrop.getItemType()) {
                                case AMMO:                                 
                                    LinkedHashSet<Gun> compatibleGuns = new LinkedHashSet<>();
                                    playerItems.stream()
                                        .forEach((g)-> {
                                            if (g instanceof Gun && ((Gun)g).getAmmoType().toItemList() == currentItemDrop.getItemName()) compatibleGuns.add((Gun)g);
                                        });
                                    
                                    int availableAmmo = dropQuantity;
                                    for (Gun g: compatibleGuns) {
                                        int currentAmmo = g.getCurrentAmmo();
                                        int maxAmmo = g.getMaxAmmo();
                                        int maxAmmoToTake = maxAmmo-currentAmmo;
                                        if (availableAmmo > maxAmmoToTake) {
                                            g.setCurrentAmmo(maxAmmo);
                                            availableAmmo-=maxAmmoToTake;
                                        } else {
                                            g.setCurrentAmmo(currentAmmo+availableAmmo);
                                            availableAmmo = 0;
                                            break; //no more available ammo
                                        }
                                    }
                                    
                                    if (availableAmmo != 0) {
                                        currentItemDrop.setQuantity(availableAmmo);
                                        items.put(itemDropID, currentItemDrop);
                                    } else {
                                        items.remove(itemDropID);
                                    }
                                    break;
                                case GUN:
                                    if (playerItems.stream().anyMatch((i)->i.getItemName()==currentItemDrop.getItemName())) {
                                        //player already has that item TODO take some ammo from it
                                    } else {
                                        playerItems.add(currentItemDrop.getItem());                                       
                                        dropQuantity-=1;
                                        if (dropQuantity !=0) {
                                            currentItemDrop.setQuantity(dropQuantity);
                                            items.put(itemDropID, currentItemDrop);
                                        } else {
                                            items.remove(itemDropID);
                                        }
                                    }
                                    break;                                
                                }
                            }
                        }
                    }

                    // TODO player changed data
                    playerPose = new Pose(newLocation, facingDirection);
                    currentPlayer.setPose(playerPose);
                }

                currentPlayer.setCurrentItem(currentItem); // sets item changes before switching

                if (request.selectItemExists()) {
                    currentPlayer.setCurrentItemIndex(request.getSelectItem());
                }

                players.put(playerID, currentPlayer);

            }

            // process item drops

            for (ItemDrop i : items.values()) {
                if ((lastProcessTime-i.getDropTime()) > ItemDrop.DECAY_LENGTH) items.remove(i.getID());
                // TODO itemdrop change here
            }

            // process enemies
            LinkedHashSet<EnemyChange> enemyChanges = new LinkedHashSet<>();
            LinkedHashMap<Integer, Enemy> enemies = new LinkedHashMap<>();
            gameState.getEnemies().stream().forEach((e) -> enemies.put(e.getID(), e));
            
            HashSet<Pose> playerPoses = new HashSet<>();
            players.values().stream().forEach((p)->playerPoses.add(p.getPose()));
            
            for (Enemy e : enemies.values()) {
                Enemy currentEnemy = e;
                EnemyAI ai;
                EnemyList enemyName = currentEnemy.getEnemyName();
                Pose enemyPose = currentEnemy.getPose();
                Location newLocation = enemyPose;
                int direction = enemyPose.getDirection();
                int maxDistanceMoved = getDistanceMoved(currentTimeDifference, currentEnemy.getMoveSpeed());
                
                switch(enemyName) {
                case ZOMBIE:
                    ai = new ZombieAI(enemyPose,currentEnemy.getSize(),playerPoses,tileMap,maxDistanceMoved);
                    break;
                default:
                    System.out.println("Enemy " + enemyName.toString() + " not known!");
                    ai = new ZombieAI(enemyPose,currentEnemy.getSize(),playerPoses,tileMap,maxDistanceMoved);
                    break;                    
                }
                
                AIAction enemyAction = ai.getAction();
                switch(enemyAction) {
                case ATTACK:
                    Attack enemyAttack = ai.getAttack();
                    direction = ai.getDirection();
                    // TODO attack processing here once ai is completed.
                    break;
                case MOVE:
                    direction = ai.getDirection();                    
                    newLocation = ai.getNewLocation();
                    // TODO include knock-back of player/enemies depending on some factor e.g. size.
                    break;
                case WAIT:
                    break;
                default:
                    System.out.println("Action " + enemyAction.toString() + " not known!");
                    break;               
                }
                
                currentEnemy.setPose(new Pose(newLocation,direction));
                enemies.put(currentEnemy.getID(), currentEnemy);
            }

            // process projectiles
            LinkedHashSet<ProjectileChange> projectileChanges = new LinkedHashSet<>();
            LinkedHashSet<Projectile> projectiles = gameState.getProjectiles();
            LinkedHashSet<Projectile> otherNewProjectiles = new LinkedHashSet<>();

            for (Projectile p : projectiles) {
                boolean removed = false;
                Projectile currentProjectile = p;
                int distanceMoved = getDistanceMoved(currentTimeDifference, currentProjectile.getSpeed());
                Location newLocation = Location.calculateNewLocation(currentProjectile.getLocation(), currentProjectile.getPose().getDirection(),
                        distanceMoved);
                int[] tileCords = Tile.locationToTile(newLocation);
                Tile tileOn = tileMap[tileCords[0]][tileCords[1]];

                if (tileOn.getState() == TileState.SOLID) {
                    removed = true;
                    // TODO add data to tile object to store the bullet collision (used for audio and visual effects).
                } else if (currentProjectile.maxRangeReached(distanceMoved)) {
                    removed = true;
                } else {
                    HashSet<Integer> enemiesOnTile = tileOn.getEnemiesOnTile();
                    for (Integer enemyID : enemiesOnTile) {
                        Enemy enemyBeingChecked = enemies.get(enemyID);
                        Location enemyLocation = enemyBeingChecked.getLocation();

                        int enemyX = enemyLocation.getX();
                        int enemyY = enemyLocation.getY();
                        int boundrySize = enemyBeingChecked.getSize() / 2;
                        int newX = newLocation.getX();
                        int newY = newLocation.getY();

                        if ((enemyX - boundrySize) <= newX && (enemyX + boundrySize) >= newX && (enemyY - boundrySize) <= newY
                                && (enemyY + boundrySize) >= newY) {
                            removed = true;
                            if (enemyBeingChecked.damage(currentProjectile.getDamage())) {
                                // TODO add enemychange
                                enemies.remove(enemyID);
                                tileMap[tileCords[0]][tileCords[1]].removeEnemy(enemyID);
                                
                                LinkedHashSet<Drop> drops = enemyBeingChecked.getDrops();
                                for (Drop d: drops) {
                                    int dropAmount = d.getDrop();
                                    if (dropAmount != 0) {
                                        Item itemToDrop = d.getItem();
                                        //TODO drop items
                                    }
                                }
                            } else {
                                // TODO add enemychange
                                enemies.put(enemyID, enemyBeingChecked);
                            }
                        }
                    }

                    if (removed)
                        projectileChanges.add(new ProjectileChange(ChangeType.REMOVED));
                    else {
                        projectileChanges.add(new ProjectileChange(ChangeType.BASIC_CHANGE));
                        otherNewProjectiles.add(currentProjectile);
                    }
                }
            }

            // process tilechanges

            int xDim = currentMap.getXDim();
            int yDim = currentMap.getYDim();
            TileChange[][] tileChanges = new TileChange[xDim][yDim];
            /*
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    // TODO tile processing here
                }
            }
            */
            LinkedHashSet<Enemy> newEnemies = new LinkedHashSet<>();
            // TODO spawn new enemies

            // TODO check for next round

            gameState.setPlayers(new LinkedHashSet<>(players.values()));

            // maintain order.
            otherNewProjectiles.addAll(newProjectiles);
            for (int i = 0; i < newProjectiles.size(); i++)
                projectileChanges.add(new ProjectileChange(ChangeType.NEW));
            newProjectiles = otherNewProjectiles;
            gameState.setProjectiles(newProjectiles);

            LinkedHashSet<Enemy> enemiesToBeAdded = new LinkedHashSet<>(enemies.values());
            enemiesToBeAdded.addAll(newEnemies);
            gameState.setEnemies(enemiesToBeAdded);

            // TODO make sure old items are added to newItems or appended.
            LinkedHashSet<ItemDrop> itemsToBeAdded = new LinkedHashSet<>(items.values());
            itemsToBeAdded.addAll(newItems);
            gameState.setItems(itemsToBeAdded);
            gameState.setTileMap(tileMap);

            // TODO change to use hashmaps with ids for changes
            GameStateChanges gameStateChanges = new GameStateChanges(projectileChanges, enemyChanges, playerChanges, tileChanges, itemDropChanges);
            server.updateGameState(gameState, gameStateChanges);
        }
    }

    private static int getDistanceMoved(long timeDiff, int speed) {
        return (int) Math.ceil((timeDiff / 1000.0) * speed); // time in millis
    }

}
