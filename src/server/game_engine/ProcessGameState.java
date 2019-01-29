package server.game_engine;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import data.GameState;
import data.entity.enemy.Enemy;
import data.entity.item.ItemDrop;
import data.entity.player.Player;
import data.entity.projectile.Projectile;
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

            // extract necessary data
            GameMap currentMap = gameState.getCurrentMap();
            Tile[][] tileMap = currentMap.getTileMap();

            // process player requests
            LinkedHashMap<Integer, Request> playerRequests = clientRequests.getPlayerRequests();
            LinkedHashMap<Integer, PlayerChange> playerChanges = new LinkedHashMap<>();
            LinkedHashSet<Player> players = gameState.getPlayers();

            for (Map.Entry<Integer, Request> playerRequest : playerRequests.entrySet()) {
                // TODO player processing here
            }

            // process projectiles
            LinkedHashSet<ProjectileChange> projectileChanges = new LinkedHashSet<>();
            LinkedHashSet<Projectile> projectiles = gameState.getProjectiles();

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

            GameStateChanges gameStateChanges = new GameStateChanges(projectileChanges, enemyChanges, playerChanges, tileChanges, itemDropChanges);
            server.updateGameState(gameState, gameStateChanges);
        }
    }

    private GameState process() {
        return null;
    }
}
