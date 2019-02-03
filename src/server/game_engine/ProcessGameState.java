package server.game_engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import client.data.EnemyView;
import client.data.GameView;
import client.data.ItemDropView;
import client.data.ItemView;
import client.data.PlayerView;
import client.data.ProjectileView;
import client.data.TileView;
import data.GameState;
import data.Location;
import data.Pose;
import data.entity.enemy.Drop;
import data.entity.enemy.Enemy;
import data.entity.enemy.EnemyList;
import data.entity.item.Item;
import data.entity.item.ItemDrop;
import data.entity.item.ItemType;
import data.entity.item.weapon.gun.AmmoList;
import data.entity.item.weapon.gun.Gun;
import data.entity.player.Player;
import data.entity.player.Teams;
import data.entity.projectile.Projectile;
import data.entity.projectile.SmallBullet;
import data.map.GameMap;
import data.map.MapList;
import data.map.Meadow;
import data.map.Round;
import data.map.Wave;
import data.map.tile.Tile;
import data.map.tile.TileState;
import server.game_engine.ai.AIAction;
import server.game_engine.ai.Attack;
import server.game_engine.ai.EnemyAI;
import server.game_engine.ai.ZombieAI;
import server.request.ClientRequests;
import server.request.Request;

public class ProcessGameState extends Thread {
    private static final int MIN_TIME_DIFFERENCE = 17; // number of milliseconds between each process (approx 60th of a second).

    private final HasEngine handler;

    private GameState gameState;
    private GameView view;
    private ClientRequests clientRequests;
    private boolean handlerClosing;

    public ProcessGameState(HasEngine handler, MapList mapName, String hoastName) { // TODO have the engine create the inital gameState
        this.handler = handler;
        LinkedHashMap<Integer, Player> players = new LinkedHashMap<>();
        Player hoastPlayer = new Player(Teams.RED, hoastName);
        players.put(hoastPlayer.getID(), hoastPlayer);
        switch (mapName) {
        case MEADOW:
            this.gameState = new GameState(new Meadow(), players);
            break;
        }
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
        // Players are regenerated each time for now so it can be empty here.
        view = new GameView(new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView);
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
        long lastProcessTime = System.currentTimeMillis();
        long currentTimeDifference = 0;

        // TODO reset all of this if map is changed or find a better way to do this
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
            if (clientRequests == null)
                continue; // waits until clients start doing something.

            // TODO can be multi-threaded with immutable gamestate for each process stage
            // and detailed gamestatechanges used instead which is merged at the end (may be
            // unnecessary)

            // extract/setup necessary data
            GameMap currentMap = gameState.getCurrentMap();
            Tile[][] tileMap = currentMap.getTileMap();
            LinkedHashSet<Projectile> newProjectiles = new LinkedHashSet<>();
            LinkedHashSet<ItemDrop> newItems = new LinkedHashSet<>();
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
                Request request = playerRequest.getValue();

                if (request.getLeave()) {
                    players.remove(playerID);
                    handler.removePlayer(playerID);
                    continue;
                }

                Item currentItem = currentPlayer.getCurrentItem();
                if (currentItem instanceof Gun) {
                    Gun currentGun = ((Gun) currentItem);
                    if (currentGun.isReloading()) {
                        if (currentGun.reload(currentPlayer.getAmmo(currentGun.getAmmoType()))) {
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
                                for (Pose p : bulletPoses) {
                                    SmallBullet b = new SmallBullet(p);
                                    newProjectiles.add(b);
                                    projectilesView.add(new ProjectileView(p, b.getSize(), b.getProjectileType()));
                                }
                                break;
                            default:
                                System.out.println("Projectile type not known for: " + currentItem.getItemName().toString());
                                break;
                            }
                        }
                    }
                }

                if (request.getReload()) {
                    if (currentItem instanceof Gun) {
                        Gun currentGun = ((Gun) currentItem);
                        currentGun.attemptReload(currentPlayer.getAmmo(currentGun.getAmmoType()));
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
                                switch (currentItemDrop.getItemType()) {
                                case AMMO:
                                    AmmoList ammoType = currentItemDrop.getItemName().toAmmoList();
                                    currentPlayer.setAmmo(ammoType, currentPlayer.getAmmo(ammoType) + dropQuantity);
                                    // As there is no max ammo player takes it all and itemdrop is removed
                                    items.remove(itemDropID);
                                    tileMap[tileCords[0]][tileCords[1]].removeItemDrop(itemDropID);
                                    break;
                                case GUN:
                                    if (playerItems.stream().anyMatch((i) -> i.getItemName() == currentItemDrop.getItemName())) {
                                        // player already has that item TODO take some ammo from it
                                    } else {
                                        playerItems.add(currentItemDrop.getItem());
                                        dropQuantity -= 1;
                                        if (dropQuantity != 0) {
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
            LinkedHashSet<ItemDropView> itemDropsView = new LinkedHashSet<>();
            for (ItemDrop i : items.values()) {
                if ((lastProcessTime - i.getDropTime()) > ItemDrop.DECAY_LENGTH) {
                    items.remove(i.getID());
                    // TODO itemdrop change here
                } else {
                    itemDropsView.add(new ItemDropView(i.getPose(), i.getSize(), i.getItemName()));
                }
            }

            // process enemies
            LinkedHashMap<Integer, Enemy> enemies = gameState.getEnemies();
            LinkedHashSet<EnemyView> enemiesView = new LinkedHashSet<>();

            HashSet<Pose> playerPoses = new HashSet<>();
            players.values().stream().forEach((p) -> playerPoses.add(p.getPose()));

            for (Enemy e : enemies.values()) {
                Enemy currentEnemy = e;
                EnemyAI ai;
                EnemyList enemyName = currentEnemy.getEnemyName();
                Pose enemyPose = currentEnemy.getPose(); // don't change
                Location newLocation = enemyPose;
                int direction = enemyPose.getDirection();
                int maxDistanceMoved = getDistanceMoved(currentTimeDifference, currentEnemy.getMoveSpeed());

                switch (enemyName) {
                case ZOMBIE:
                    ai = new ZombieAI(enemyPose, currentEnemy.getSize(), playerPoses, tileMap, maxDistanceMoved);
                    break;
                default:
                    System.out.println("Enemy " + enemyName.toString() + " not known!");
                    ai = new ZombieAI(enemyPose, currentEnemy.getSize(), playerPoses, tileMap, maxDistanceMoved);
                    break;
                }

                AIAction enemyAction = ai.getAction();
                switch (enemyAction) {
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

                currentEnemy.setPose(new Pose(newLocation, direction));
                int enemyID = currentEnemy.getID();
                enemies.put(enemyID, currentEnemy);
                enemiesView.add(new EnemyView(currentEnemy.getPose(), Tile.scaledSize(currentEnemy.getSize()), currentEnemy.getEnemyName())); // slightly outdated for enemies
                                                                                                                             // that die

                int[] oldTileCords = Tile.locationToTile(enemyPose);
                int[] newTileCords = Tile.locationToTile(newLocation);

                if (oldTileCords[0] != newTileCords[0] || oldTileCords[1] != newTileCords[1]) {
                    tileMap[oldTileCords[0]][oldTileCords[1]].removeEnemy(enemyID);
                    tileMap[newTileCords[0]][newTileCords[1]].addEnemy(enemyID);
                }

                // TODO enemy change here
            }

            // process projectiles
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
                    // TODO add data to tile object to store the bullet collision (used for audio
                    // and visual effects).
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
                                for (Drop d : drops) {
                                    int dropAmount = d.getDrop();
                                    if (dropAmount != 0) {
                                        Item itemToDrop = d.getItem();
                                        // TODO have itemdrops of the same type stack
                                        ItemDrop newDrop = new ItemDrop(itemToDrop, enemyLocation);
                                        newItems.add(newDrop);
                                        itemDropsView.add(new ItemDropView(newDrop.getPose(), newDrop.getSize(), newDrop.getItemName()));
                                        tileMap[tileCords[0]][tileCords[1]].addItemDrop(newDrop.getID());
                                    }
                                }
                            } else {
                                // TODO add enemychange
                                enemies.put(enemyID, enemyBeingChecked);
                            }
                            break; // bullet was removed no need to check other enemies
                        }
                    }

                    if (removed) {
                        // TODO removed projectile change
                    } else {
                        // TODO basic projectile change
                        currentProjectile.setLocation(newLocation);
                        otherNewProjectiles.add(currentProjectile);
                        projectilesView
                                .add(new ProjectileView(currentProjectile.getPose(), currentProjectile.getSize(), currentProjectile.getProjectileType()));
                    }
                }
            }

            // TODO process tiles?

            LinkedHashSet<Enemy> newEnemies = new LinkedHashSet<>();
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
                                newEnemies.add(enemyToSpawn);
                            }
                        }
                        newWaves.add(currentWave);
                    }
                }
            } else if (enemies.isEmpty()) {
                if (roundIterator.hasNext()) {
                    currentRound = roundIterator.next();
                } else {
                    // TODO no more rounds left send win message
                }
            }
            currentWaves = newWaves;

            gameState.setPlayers(players);

            // maintain order.
            otherNewProjectiles.addAll(newProjectiles); // TODO remove new versions alltogether (only if single thread perf is good)
            // TODO changes loop of news
            newProjectiles = otherNewProjectiles;
            gameState.setProjectiles(newProjectiles);

            newEnemies.stream().forEach((e) -> enemies.put(e.getID(), e));
            // TODO changes loop of news
            gameState.setEnemies(enemies);

            newItems.stream().forEach((i) -> items.put(i.getID(), i));
            // TODO changes loop of news
            gameState.setItems(items);
            gameState.setTileMap(tileMap);

            // turn players to player view
            LinkedHashSet<PlayerView> playersView = new LinkedHashSet<>();
            for (Player p : players.values()) {
                ArrayList<ItemView> playerItems = new ArrayList<>();
                for (Item i : p.getItems()) {
                    if (i instanceof Gun) {
                        Gun g = (Gun) i;
                        playerItems.add(new ItemView(g.getItemName(), g.getAmmoType(), g.getClipSize(), g.getAmmoInClip()));
                    } else {
                        playerItems.add(new ItemView(i.getItemName(), AmmoList.NONE, 0, 0));
                    }
                }
                playersView.add(new PlayerView(p.getPose(), Tile.scaledSize(p.getSize()), p.getHealth(), p.getMaxHealth(), playerItems, p.getCurrentItemIndex(),
                        p.getScore(), p.getName(), p.getAmmoList(), p.getID()));
            }

            GameView view = new GameView(playersView, enemiesView, projectilesView, itemDropsView, tileMapView);
            handler.updateGameView(view);
            // TODO overhaul gamestatechanges if used for multithreading
        }
        System.out.println("LongestTimeProcessing: " + longestTimeProcessing);
        double avgTimeProcessing = (double) totalTimeProcessing / numOfProcesses;
        System.out.println("TimeProcessing: " + totalTimeProcessing);
        System.out.println("NumOfProcesses: " + numOfProcesses);
        System.out.println("AverageTimeProcessing: " + avgTimeProcessing);
        System.out.println("ProjectileCount: " + gameState.getProjectiles().size());
        System.out.println("EnemyCount: " + gameState.getEnemies().size());
    }

    private static int getDistanceMoved(long timeDiff, int speed) {
        return (int) Math.ceil((timeDiff / 1000.0) * speed); // time in millis
    }

}
