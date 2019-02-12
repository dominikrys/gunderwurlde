package client;

import client.data.ConnectionType;
import client.input.ActionList;
import client.render.GameRenderer;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.Server;
import server.engine.state.map.Meadow;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.*;
import shared.view.GameView;
import shared.view.ItemView;
import shared.view.TileView;
import shared.view.entity.EnemyView;
import shared.view.entity.ItemDropView;
import shared.view.entity.PlayerView;
import shared.view.entity.ProjectileView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameHandler extends Thread {
    private ConnectionType connectionType;
    private GameRenderer gameRenderer;
    private Stage stage;
    private boolean serverStarted;
    private Server server;
    private Client client;

    public GameHandler(Stage stage, ConnectionType connectionType) {
        this.stage = stage;
        this.connectionType = connectionType;
    }

    public void run() {
        switch (connectionType) {
            case SINGLE_PLAYER:
                // CODE FOR ESTABLISHING LOCAL SERVER
                if (!serverStarted) {
                    server = new Server(MapList.MEADOW, "Player 1");
                    serverStarted = true;

                    GameView initialView = createGameView();

                    gameRenderer = new GameRenderer(stage, initialView, 0);
                    gameRenderer.getKeyboardHandler().setGameHandler(this);
                    gameRenderer.getMouseHandler().setGameHandler(this);
                    client = new Client(gameRenderer, "Player 1", 0);
                    client.start();
                    serverStarted = true;
                }
                break;
            case MULTI_PLAYER:
                // CODE FOR ESTABLISHING CONNECTION WITH REMOTE SERVER
                break;
        }

        //CODE FOR RUNNING THE GAME???
        
        // TODO: handle the game closing
    }

    public void end() {
        // End server if running/exists
        if (server != null) {
            if (server.isAlive()) {
                server.close();
            }
        }

        // End client if running/exists
        if (client != null) {
            if (client.isAlive()) {
                this.client.close();
            }
        }

        // Close stage
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Close stage
                stage.close();
            }
        });
    }

    public GameView createGameView() {
        // Creates an initial GameView
        LinkedHashSet<PlayerView> examplePlayers = new LinkedHashSet<PlayerView>();
        ArrayList<ItemView> exampleItems = new ArrayList<ItemView>();
        exampleItems.add(new ItemView(ItemList.PISTOL, AmmoList.BASIC_AMMO, 0, 0));
        LinkedHashMap<AmmoList, Integer> exampleAmmo = new LinkedHashMap<AmmoList, Integer>();
        exampleAmmo.put(AmmoList.BASIC_AMMO, 0);
        PlayerView examplePlayer = new PlayerView(new Pose(32, 32, 0), 1, 20, 20, exampleItems, 0, 0, "Player 1", exampleAmmo, 0, Teams.BLUE);
        examplePlayers.add(examplePlayer);
        LinkedHashSet<EnemyView> exampleEnemies = new LinkedHashSet<EnemyView>();
        EnemyView exampleEnemy = new EnemyView(new Pose(120, 120, 45), 1, EntityList.ZOMBIE);
        exampleEnemies.add(exampleEnemy);
        LinkedHashSet<ProjectileView> exampleProjectiles = new LinkedHashSet<ProjectileView>();
        ProjectileView exampleProjectile = new ProjectileView(new Pose(400, 300, 70), 1, EntityList.BASIC_BULLET);
        exampleProjectiles.add(exampleProjectile);
        LinkedHashSet<ItemDropView> exampleItemDrops = new LinkedHashSet<ItemDropView>();
        ItemDropView exampleItemDrop = new ItemDropView(new Pose(50, 250), 1, EntityList.PISTOL);
        exampleItemDrops.add(exampleItemDrop);
        TileView[][] exampleTile = new TileView[Meadow.DEFAULT_X_DIM][Meadow.DEFAULT_Y_DIM];
        Tile[][] tile = Meadow.generateTileMap();
        for (int i = 0; i < Meadow.DEFAULT_X_DIM; i++) {
            for (int j = 0; j < Meadow.DEFAULT_Y_DIM; j++) {
                TileView tileView = new TileView(tile[i][j].getType(), tile[i][j].getState());
                exampleTile[i][j] = tileView;
            }
        }
        GameView view = new GameView(examplePlayers, exampleEnemies, exampleProjectiles, exampleItemDrops, exampleTile);
        return view;
    }

    public void send(ActionList action) {
        switch (action.toString()) {
            case "ATTACK": // 0
                client.getClientSender().send(new Integer[]{0});
                break;
            case "DROPITEM": // 1
                client.getClientSender().send(new Integer[]{1});
                break;
            case "RELOAD": // 2
                client.getClientSender().send(new Integer[]{2});
                break;
        }
    }

    public void send(ActionList action, int parameter) {
        switch (action.toString()) {
            case "CHANGEITEM": // 3
                client.getClientSender().send(new Integer[]{3, parameter});
                break;
            case "MOVEMENT": // 4
                client.getClientSender().send(new Integer[]{4, parameter});
                break;
            case "TURN": //5
                client.getClientSender().send(new Integer[]{5, parameter});
        }
    }
}

/*
    STUFF STILL TO IMPLEMENT!!!

    private audioController audio;


    public void setPlayerName(String name) {
        this.playerName = name;
        sendPlayerName();
    }

    public void setID(int id) {
        this.playerID = id;
        renderer.setID(id);
    }

    public void requestToLeave() {
        sender.requestLeave();
    }
 */
