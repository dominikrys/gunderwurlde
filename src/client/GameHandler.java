package client;

import client.input.ActionList;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.Server;
import shared.lists.MapList;
import shared.lists.Team;

/**
 * GameHandler class. Starts the game and all the necessary threads
 */
public class GameHandler extends Thread {
    // Server variables
    private ConnectionType connectionType;
    private Server server;
    private Client client;
    private boolean serverStarted;

    private String address;
    private int port;

    // Renderer variables
    private Stage stage;

    // Misc
    private Settings settings;
    private String playerName;
    private MapList map;
    private Team team;
    private int numPlayers;

    public GameHandler(Stage stage, ConnectionType connectionType, Settings settings, String name, Team team, MapList map, String numOfPlayers) {
        this.stage = stage;
        this.connectionType = connectionType;
        this.settings = settings;
        this.playerName = name;
        this.map = map;
        this.team = team;
        this.numPlayers = Integer.parseInt(numOfPlayers);
    }

    public GameHandler(Stage stage, ConnectionType connectionType, Settings settings, String name, Team team, MapList map, String numOfPlayers, String ipValue, String portValue) {
        this.stage = stage;
        this.connectionType = connectionType;
        this.settings = settings;
        this.playerName = name;
        this.map = map;
        this.team = team;
        this.numPlayers = Integer.parseInt(numOfPlayers);
        String[] split = ipValue.split("\\.");
        this.address = split[3];
        this.port = Integer.parseInt(portValue);
    }

    public void run() {
        switch (connectionType) {
            case SINGLE_PLAYER:
                // Code for establishing local server
                if (!serverStarted) {
                    server = new Server(map, playerName, team, 1, false);
                    serverStarted = true;
                    client = new Client(stage, this, settings, 0);
                    client.start();
                }
                break;
            case MULTI_PLAYER_HOST:
                if(!serverStarted) {
                    System.out.println("Handler starting server");
                    server = new Server(MapList.MEADOW, playerName, team, numPlayers, true);
                    serverStarted = true;
                    System.out.println("Handler creating client");
                    client = new Client(stage, this, settings, 0);
                    System.out.println("Waiting for the go");
                    client.start();
                    System.out.println("Host setup and ready");
                }
                // Code for setting up server, joining it, and waiting for players
                break;
            case MULTI_PLAYER_JOIN:
                // TODO: Potential menu for choosing host address and port number?
                if(!serverStarted) {
                    serverStarted = true;
                    client = new Client(stage, this, settings, address, port, playerName, team);
                    System.out.println("Waiting for the go");
                    client.start();
                    System.out.println("Client received the go");
                    // Code for joining some server
                }
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