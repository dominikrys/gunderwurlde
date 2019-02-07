package server.game_engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import client.data.entity.EnemyView;
import client.data.entity.GameView;
import client.data.entity.ItemDropView;
import client.data.ItemView;
import client.data.entity.PlayerView;
import client.data.entity.ProjectileView;
import client.data.TileView;
import data.GameState;
import data.Location;
import data.Pose;
import data.entity.Entity;
import data.entity.EntityList;
import data.entity.enemy.Drop;
import data.entity.enemy.Enemy;
import data.item.Item;
import data.entity.ItemDrop;
import data.item.ItemType;
import data.item.weapon.gun.AmmoList;
import data.item.weapon.gun.Gun;
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

    public ProcessGameState(HasEngine handler, MapList mapName, String hostName) {
        this.handler = handler;
        LinkedHashMap<Integer, Player> players = new LinkedHashMap<>();
        Player hostPlayer = new Player(Teams.RED, hostName);
        players.put(hostPlayer.getID(), hostPlayer);
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

            handler.requestClientRequests();
            if (clientRequests == null)
                continue; // waits until clients start doing something.

            // TODO can be multi-threaded with immutable gamestate for each process stage
            // and detailed gamestatechanges used instead which is merged at the end (may be
            // unnecessary)

            // extract/setup necessary data
            GameMap currentMap = gameState.getCurrentMap();
            Tile[][] tileMap = currentMap.getTileMap();
            LinkedHashSet<Projectile> newProjectiles = new LinkedHashSet<>(); // TODO change projectiles to use hashmap with id which may improve performance
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
                Pose playerPose = currentPlayer.getPose();

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
                            Pose gunPose = playerPose; // TODO change pose to include gunlength/position

                            int nextDirection = gunPose.getDirection() - spread;
                            for (int i = 0; i < numOfBullets; i++) {
                                bulletPoses.add(new Pose(gunPose, nextDirection));
                                nextDirection += bulletSpacing;
                            }

                            switch (currentGun.getProjectileType()) {
                                case BASIC_BULLET:
                                for (Pose p : bulletPoses) {
                                    SmallBullet b = new SmallBullet(p);
                                    newProjectiles.add(b);
                                    projectilesView.add(new ProjectileView(p, b.getSizeScaleFactor(), b.getEntityListName()));
                                }
                                break;
                            default:
                                System.out.println("Projectile type not known for: " + currentItem.getItemListName().toString());
                                break;
                            }
                        }
                    }
                }

                if (request.getDrop() && currentPlayer.getItems().size() > 1) {
                    ItemDrop itemDropped = new ItemDrop(currentItem, playerPose);
                    items.put(itemDropped.getID(), itemDropped);

                    LinkedHashSet<int[]> tilesOn = tilesOn(itemDropped);
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].addItemDrop(itemDropped.getID());
                    }
                    currentPlayer.removeItem(currentPlayer.getCurrentItemIndex());

                } else if (request.getReload()) {
                    if (currentItem instanceof Gun) {
                        Gun currentGun = ((Gun) currentItem);
                        currentGun.attemptReload(currentPlayer.getAmmo(currentGun.getAmmoType()));
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
                        int distanceMoved = getDistanceMoved(currentTimeDifference, currentPlayer.getMoveSpeed());
                        newLocation = Location.calculateNewLocation(playerPose, request.getMovementDirection(), distanceMoved);
                        currentPlayer.setLocation(newLocation);
                        boolean pushedBack = false;

                        LinkedHashSet<int[]> tilesOn = tilesOn(currentPlayer);
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
                                ArrayList<Item> playerItems = currentPlayer.getItems();

                                for (Integer itemDropID : itemsOnTile) {
                                    ItemDrop currentItemDrop = items.get(itemDropID);

                                    if (haveCollided(currentPlayer, currentItemDrop)) {
                                        boolean removed = false;
                                        int dropQuantity = currentItemDrop.getQuantity();

                                        switch (currentItemDrop.getItemType()) {
                                        case AMMO:
                                            AmmoList ammoType = currentItemDrop.getItemName().toAmmoList();
                                            currentPlayer.setAmmo(ammoType, currentPlayer.getAmmo(ammoType) + dropQuantity);
                                            // As there is no max ammo player takes it all and itemdrop is removed
                                            removed = true;
                                            break;
                                        case GUN: // TODO change for everything that isn't ammo or powerup
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
                                            items.remove(itemDropID);
                                            LinkedHashSet<int[]> itemTilesOn = tilesOn(currentItemDrop);
                                            for (int[] itemTileCords : itemTilesOn) {
                                                tileMap[itemTileCords[0]][itemTileCords[1]].removeItemDrop(itemDropID);
                                            }
                                        }
                                    }
                                }
                            }
                        }                                              
                    }

                    // TODO player changed data
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
                    LinkedHashSet<int[]> tilesOn = tilesOn(i);
                    for (int[] tileCords : tilesOn) {
                        tileMap[tileCords[0]][tileCords[1]].removeItemDrop(i.getID());
                    }
                    // TODO itemdrop change here
                } else {
                    itemDropsView.add(new ItemDropView(i.getPose(), i.getSizeScaleFactor(), i.getEntityListName()));
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
                EntityList enemyName = currentEnemy.getEntityListName();
                Pose enemyPose = currentEnemy.getPose(); // don't change
                Location newLocation = enemyPose;
                int direction = enemyPose.getDirection();
                int maxDistanceMoved = getDistanceMoved(currentTimeDifference, currentEnemy.getMoveSpeed());

                switch (enemyName) {
                case ZOMBIE:
                    ai = new ZombieAI(enemyPose, currentEnemy.getSizeScaleFactor(), playerPoses, tileMap, maxDistanceMoved);
                    break;
                default:
                    System.out.println("Enemy " + enemyName.toString() + " not known!");
                    ai = new ZombieAI(enemyPose, currentEnemy.getSizeScaleFactor(), playerPoses, tileMap, maxDistanceMoved);
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

                int enemyID = currentEnemy.getID();
                LinkedHashSet<int[]> tilesOn = tilesOn(currentEnemy);

                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].removeEnemy(enemyID);
                }

                currentEnemy.setPose(new Pose(newLocation, direction));
                tilesOn = tilesOn(currentEnemy);
                for (int[] tileCords : tilesOn) {
                    tileMap[tileCords[0]][tileCords[1]].addEnemy(enemyID);
                }

                enemies.put(enemyID, currentEnemy);
                enemiesView.add(new EnemyView(currentEnemy.getPose(), currentEnemy.getSizeScaleFactor(), currentEnemy.getEntityListName()));

                // TODO enemy change here
            }

            // process projectiles
            LinkedHashSet<Projectile> projectiles = gameState.getProjectiles();

            for (Projectile p : projectiles) {
                boolean removed = false;
                Projectile currentProjectile = p;
                int distanceMoved = getDistanceMoved(currentTimeDifference, currentProjectile.getSpeed());

                if (currentProjectile.maxRangeReached(distanceMoved)) {
                    removed = true;
                } else {
                    Location newLocation = Location.calculateNewLocation(currentProjectile.getLocation(), currentProjectile.getPose().getDirection(),
                            distanceMoved);
                    currentProjectile.setLocation(newLocation);
                    LinkedHashSet<int[]> tilesOn = tilesOn(currentProjectile);
                    for (int[] tileCords : tilesOn) {
                        Tile tileOn = tileMap[tileCords[0]][tileCords[1]];
                        if (tileOn.getState() == TileState.SOLID) {
                            removed = true;
                            // TODO add data to tile object to store the bullet collision (used for audio
                            // and visual effects).
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
                                                items.put(newDrop.getID(), newDrop);
                                                // TODO item change here
                                                itemDropsView.add(new ItemDropView(newDrop.getPose(), newDrop.getSizeScaleFactor(), newDrop.getEntityListName()));

                                                LinkedHashSet<int[]> itemTilesOn = tilesOn(newDrop);
                                                for (int[] itemTileCords : itemTilesOn) {
                                                    tileMap[itemTileCords[0]][itemTileCords[1]].addItemDrop(newDrop.getID());
                                                }

                                            }
                                        }
                                    } else {
                                        // TODO add enemychange
                                        enemies.put(enemyID, enemyBeingChecked);
                                    }
                                    break; // bullet was removed no need to check other enemies
                                }
                            }
                            if (removed)
                                break; // Projectile is already gone.
                        }
                    }

                }
                if (removed) {
                    // TODO removed projectile change
                } else {
                    // TODO basic projectile change
                    newProjectiles.add(currentProjectile);
                    projectilesView.add(new ProjectileView(currentProjectile.getPose(), currentProjectile.getSizeScaleFactor(), currentProjectile.getEntityListName()));
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
                                enemies.put(enemyToSpawn.getID(), enemyToSpawn);
                                // TODO enemy change here
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
            // TODO changes loop of new projectiles
            gameState.setProjectiles(newProjectiles);
            gameState.setEnemies(enemies);
            gameState.setItems(items);
            gameState.setTileMap(tileMap);

            // turn players to player view
            LinkedHashSet<PlayerView> playersView = new LinkedHashSet<>();
            for (Player p : players.values()) {
                ArrayList<ItemView> playerItems = new ArrayList<>();
                for (Item i : p.getItems()) {
                    if (i instanceof Gun) {
                        Gun g = (Gun) i;
                        playerItems.add(new ItemView(g.getItemListName(), g.getAmmoType(), g.getClipSize(), g.getAmmoInClip()));
                    } else {
                        playerItems.add(new ItemView(i.getItemListName(), AmmoList.NONE, 0, 0));
                    }
                }
                playersView.add(new PlayerView(p.getPose(), p.getSizeScaleFactor(), p.getHealth(), p.getMaxHealth(), playerItems, p.getCurrentItemIndex(),
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

    private static boolean haveCollided(Entity e1, Entity e2) {
        Location e1_loc = e1.getLocation();
        int e1_radius = e1.getSizeScaleFactor() / 2;
        int e1_x = e1_loc.getX();
        int e1_y = e1_loc.getY();

        Location e2_loc = e2.getLocation();
        int e2_radius = e2.getSizeScaleFactor() / 2;
        int e2_x = e2_loc.getX();
        int e2_y = e2_loc.getY();

        return ((e1_x - e1_radius) <= (e2_x + e2_radius) && (e1_x + e1_radius) >= (e2_x - e2_radius) && (e1_y - e1_radius) <= (e2_y + e2_radius)
                && (e1_y + e1_radius) >= (e2_y - e2_radius));
    }

    private static LinkedHashSet<int[]> tilesOn(Entity e) {
        Location loc = e.getLocation();
        int size = e.getSizeScaleFactor();
        int radius = size / 2;
        int x = loc.getX();
        int max_x = x + radius;
        int min_x = x - radius;
        int y = loc.getY();
        int max_y = y + radius;
        int min_y = y - radius;

        LinkedHashSet<int[]> tilesOn = new LinkedHashSet<>();

        int check_x = min_x;
        int check_y = min_y;
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

}
