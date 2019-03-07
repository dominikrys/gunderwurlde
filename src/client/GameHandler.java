package client;

import client.gui.Settings;
import client.input.ActionList;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.Server;
import shared.lists.MapList;
import shared.lists.Teams;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

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
    private Teams team;
    private int numPlayers;
    private String ipAddress;
    private int port;

    public GameHandler(Stage stage, ConnectionType connectionType, Settings settings, String name, Teams team, MapList map, String numOfPlayers) {
        this.stage = stage;
        this.connectionType = connectionType;
        this.settings = settings;
        this.playerName = name;
        this.map = map;
        this.team = team;
        this.numPlayers = Integer.parseInt(numOfPlayers);
    }

    public GameHandler(Stage stage, ConnectionType connectionType, Settings settings, String name, Teams team, MapList map, String numOfPlayers, String ipAddress, String port) {
        this.stage = stage;
        this.connectionType = connectionType;
        this.settings = settings;
        this.playerName = name;
        this.map = map;
        this.team = team;
        this.numPlayers = Integer.parseInt(numOfPlayers);
        this.ipAddress = ipAddress;
        this.port = Integer.parseInt(port);
    }

    public void run() {
        switch (connectionType) {
            case SINGLE_PLAYER:
                // Code for establishing local server
                if (!serverStarted) {
                    server = new Server(map, playerName, team, 1, false);
                    serverStarted = true;
                    client = new Client(stage, playerName, this, settings, 0);
                    client.start();
                }
                break;
            case MULTI_PLAYER_HOST:
                if(!serverStarted) {
                    server = new Server(MapList.MEADOW, playerName, team, numPlayers, true);
                    serverStarted = true;
                    client = new Client(stage, playerName, this, settings, 0);
                    client.start();
                }
                // Code for setting up server, joining it, and waiting for players
                break;
            case MULTI_PLAYER_JOIN:
                // TODO: Potential menu for choosing host address and port number?
                if(!serverStarted) {
                    serverStarted = true;
                    int ID = requestplayerID();
                    client = new Client(stage, playerName, this, settings, ID, ipAddress, port);
                    client.start();
                    while(!client.isThreadsup()){
                        Thread.yield();
                    }
                    client.joinGame(playerName, team);
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

    public int requestplayerID(){
        int clientID = -1;
        try {
            System.out.println("Attempting to listen to port");
            Socket connection = new Socket(InetAddress.getLocalHost(), port);
            InputStream inputStream = connection.getInputStream();
            byte[] clientIDBytes = new byte[4];
            inputStream.read(clientIDBytes);
            ByteBuffer wrappedCommand = ByteBuffer.wrap(clientIDBytes);
             clientID = wrappedCommand.getInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientID;
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