package server.game_engine;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import data.GameState;
import data.Location;
import data.Pose;
import data.entity.enemy.Enemy;
import data.entity.item.Item;
import data.entity.item.ItemDrop;
import data.entity.item.ItemType;
import data.entity.item.weapon.Gun;
import data.entity.player.Player;
import data.entity.projectile.Projectile;
import data.entity.projectile.SmallBullet;
import data.map.GameMap;
import data.map.tile.Tile;
import data.map.tile.TileState;
import server.Server;
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
    private static final int minTimeDifference = 17; // number of milliseconds between each process (approx 60th of a second).

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
            long timeDiff = minTimeDifference - currentTimeDifference;
            if (timeDiff > 0) {
                try {
                    this.wait(timeDiff);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    continue;
                }
                if (serverClosing)
                    break;
                currentTimeDifference = minTimeDifference;
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
            // LinkedHashSet<Player> players = gameState.getPlayers();
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
                            for (Integer itemDropID : itemsOnTile) {
                                ItemDrop currentItemDrop = items.get(itemDropID);
                                // TODO add items to player inventory and as much ammo as possible to compatible
                                // weapons.
                            }
                        }
                    }

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
                // TODO check which items are to decay
            }

            // process enemies
            LinkedHashSet<EnemyChange> enemyChanges = new LinkedHashSet<>();
            LinkedHashMap<Integer, Enemy> enemies = new LinkedHashMap<>();
            gameState.getEnemies().stream().forEach((e) -> enemies.put(e.getID(), e));

            for (Enemy e : enemies.values()) {
                // TODO enemy processing here
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
                    // TODO add data to tile object to store the bullet collision
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

            gameState.setItems(newItems);
            // TODO loop through tile changes and set tile on gameState

            // TODO change to use hashmaps with ids for changes
            GameStateChanges gameStateChanges = new GameStateChanges(projectileChanges, enemyChanges, playerChanges, tileChanges, itemDropChanges);
            server.updateGameState(gameState, gameStateChanges);
        }
    }

    private int getDistanceMoved(long timeDiff, int speed) {
        return (int) Math.ceil((timeDiff / 1000.0) * speed); // time in millis
    }

}
