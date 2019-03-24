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
            // single player works, dont touch it
            case SINGLE_PLAYER:
                // Code for establishing local server
                if (!serverStarted) {
                    try {
                        // Create the server
                        server = new Server(map, playerName, team, 1, false);
                        // create the client
                        client = new Client(stage, this, settings, 0);
                        // start client threads ready to receive and send
                        client.start();
                        // wait for threads to be setup completely
                        client.join();
                        // start server threads ready to send and receive
                        server.start();
                        serverStarted = true;
                        // wait for threads to be setup completely
                        server.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
                //multiplayer needs lots of testing
            case MULTI_PLAYER_HOST:
                if(!serverStarted) {
                    try {
                        // create server
                        server = new Server(MapList.MEADOW, playerName, team, numPlayers, true);
                        // start the server as it needs to listen to requests
                        server.start();
                        serverStarted = true;
                        // When TCP manager setup create and start the client
                        while(!server.isReceiving()){
                            Thread.yield();
                        }
                        client = new Client(stage, this, settings, 0);
                        // setup clients threads
                        client.start();
                        //wait for all threads to finish setting up and client to join the game fully
                        client.join();
                        // wait for the server to receive all players before ending
                        server.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Code for setting up server, joining it, and waiting for players
                break;
            case MULTI_PLAYER_JOIN:
                if(!serverStarted) {
                    try {
                        serverStarted = true;
                        // create a client
                        client = new Client(stage, this, settings, address, port, playerName, team);
                        // setup the threads for that client
                        client.start();
                        // wait for that client to officially join the game
                        client.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Code for joining some server
                }
                break;
        }


        // TODO: handle the game closing once all stuff is running as is supposed to
        System.out.println("Ending GameHandler");
    }

    public void end() {
        // End server if running/exists
        if (server != null) {
            if (server.isAlive()) {
                server.close();
            }
        }
        // Client and threads end on their own
    }
}