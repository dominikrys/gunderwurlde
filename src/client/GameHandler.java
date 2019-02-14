package client;

import client.data.ConnectionType;
import client.gui.Settings;
import client.input.ActionList;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.Server;
import shared.lists.MapList;

public class GameHandler extends Thread {
    // Server variables
    private ConnectionType connectionType;
    private Server server;
    private Client client;
    private boolean serverStarted;

    // Renderer variables
    private Stage stage;

    // Misc
    private Settings settings;
    private String playerName;
    private MapList map;

    public GameHandler(Stage stage, ConnectionType connectionType, Settings settings, String name, MapList map) {
        this.stage = stage;
        this.connectionType = connectionType;
        this.settings = settings;
        this.playerName = name;
        this.map = map;
    }

    public void run() {
        switch (connectionType) {
            case SINGLE_PLAYER:
                // Code for establishing local server
                if (!serverStarted) {
                    server = new Server(map, playerName);
                    serverStarted = true;
                    client = new Client(stage, playerName, 0, this, settings);
                    client.start();
                }
                break;
            case MULTI_PLAYER_HOST:
                // Code for setting up server, joining it, and waiting for players
                break;
            case MULTI_PLAYER_JOIN:
                // Code for joining some server
                break;
        }

        //CODE FOR RUNNING THE GAME???

        /*
        Implement user leaving game

        public void requestToLeave() {
            sender.requestLeave();
        }
        */

        // TODO: handle the game closing once all stuff is running as is supposed to
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
        Platform.runLater(() -> {
            // Close stage
            stage.close();
        });
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