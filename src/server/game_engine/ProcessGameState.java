package server.game_engine;

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
import server.Server;
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
            
            LinkedHashSet<Player> newplayers = new LinkedHashSet<>();
            LinkedHashSet<Projectile> newProjectiles = new LinkedHashSet<>();
            LinkedHashSet<Enemy> newEnemies = new LinkedHashSet<>();
            LinkedHashSet<ItemDrop> newItems = new LinkedHashSet<>();
            // TODO include tile changes here

            // process player requests                       
            LinkedHashMap<Integer, Request> playerRequests = clientRequests.getPlayerRequests();
            LinkedHashMap<Integer, PlayerChange> playerChanges = new LinkedHashMap<>();
            LinkedHashSet<Player> players = gameState.getPlayers();


            for (Map.Entry<Integer, Request> playerRequest : playerRequests.entrySet()) {
                int playerID = playerRequest.getKey();
                Player currentPlayer = players.stream().filter(p -> p.getID() == playerID).findFirst().get();
                Request request = playerRequest.getValue();

                if (request.getLeave()) {
                    players.removeIf(p -> p.getID() == playerID);
                    server.removePlayer(playerID);
                    continue;
                }

                Item currentItem = currentPlayer.getCurrentItem();
                if (currentItem.getItemType() == ItemType.GUN) {
                    if (((Gun) currentItem).isReloading()) {
                        if (((Gun) currentItem).reload()) {
                            // TODO reload complete, gun updates itself but it might be good to send confirmation as well?
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
                            if (numOfBullets > 1) bulletSpacing = (2*spread)/(numOfBullets-1);
                            
                            LinkedList<Pose> bulletPoses = new LinkedList<>();
                            Pose gunPose = currentPlayer.getPose(); // TODO change pose to include gunlength/position
                            
                            int nextDirection = gunPose.getDirection() - spread;
                            for (int i=0;i<numOfBullets;i++) {
                                bulletPoses.add(new Pose(gunPose, nextDirection));
                                nextDirection+=bulletSpacing;
                            }                          
                            
                            switch (currentGun.getProjectileType()) {
                            case SMALLBULLET:
                                for (Pose p:bulletPoses) newProjectiles.add(new SmallBullet(p));
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
                        int distanceMoved = (int) Math.ceil((currentTimeDifference / 1000.0) * currentPlayer.getMoveSpeed());
                        // TODO check if player movement is valid & include knockback of player/enemies depending on size.
                        newLocation = Location.calculateNewLocation(playerPose, request.getMovementDirection(), distanceMoved);
                    }

                    playerPose = new Pose(newLocation, facingDirection);
                    currentPlayer.setPose(playerPose);
                }

                currentPlayer.setCurrentItem(currentItem); // sets item changes before switching

                if (request.selectItemExists()) {
                    currentPlayer.setCurrentItemIndex(request.getSelectItem());
                }

                newplayers.add(currentPlayer);

            }

            // process projectiles
            LinkedHashSet<ProjectileChange> projectileChanges = new LinkedHashSet<>();
            LinkedHashSet<Projectile> projectiles = gameState.getProjectiles();
            LinkedHashSet<Projectile> otherNewProjectiles = new LinkedHashSet<>();

            for (Projectile p : projectiles) {
                // TODO projectile processing here
            }          
            
            // process tilechanges
            int xDim = currentMap.getXDim();
            int yDim = currentMap.getYDim();
            TileChange[][] tileChanges = new TileChange[xDim][yDim];

            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    // TODO tile processing here
                }
            }

            // process enemies
            LinkedHashSet<EnemyChange> enemyChanges = new LinkedHashSet<>();
            LinkedHashSet<Enemy> enemies = gameState.getEnemies();

            for (Enemy e : enemies) {
                // TODO enemy processing here
            }

            // process item drops
            LinkedHashSet<ItemDropChange> itemDropChanges = new LinkedHashSet<>();
            LinkedHashSet<ItemDrop> items = gameState.getItems();

            for (ItemDrop i : items) {
                // TODO item processing here
            }

            // TODO any other major changes here
            
            gameState.setPlayers(newplayers);
            
            //maintain order.
            otherNewProjectiles.addAll(newProjectiles);
            newProjectiles = otherNewProjectiles;
            gameState.setProjectiles(newProjectiles);
            
            gameState.setEnemies(newEnemies);
            gameState.setItems(newItems);
            // TODO loop through tile changes and set tile on gameState
            
            GameStateChanges gameStateChanges = new GameStateChanges(projectileChanges, enemyChanges, playerChanges, tileChanges, itemDropChanges);
            server.updateGameState(gameState, gameStateChanges);
        }
    }

}
