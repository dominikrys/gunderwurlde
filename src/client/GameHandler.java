package client;

import client.gui.menucontrollers.LoadScreenController;
import client.net.NetworkInformation;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.Server;
import shared.lists.MapList;
import shared.lists.Team;

import java.util.Set;

/**
 * GameHandler class. Starts the game and all the necessary threads
 */
public class GameHandler extends Thread {
    /**
     * A variable stating if the game is single player, multiplayer host or multiplayer join
     */
    private ConnectionType connectionType;

    /**
     * The object that handles the client requests and overall game engine
     */
    private Server server;

    /**
     * The object that handles the clients sending and receiving
     */
    private Client client;

    /**
     * boolean to declare the server thread has been started
     */
    private boolean serverStarted;

    /**
     * The value of the address to be connected to.
     * Only used in multiplayer
     */
    private String address;

    /**
     * The value of the port to be communicated across.
     * Only used in multiplayer
     */
    private int port;

    /**
     * Stage to be drawn to
     */
    private Stage stage;

    /**
     * Object to hold the machines saved settings e.g. sound
     */
    private Settings settings;

    /**
     * The name of the player creating/joining a game
     */
    private String playerName;

    /**
     * The map the game is being played on
     */
    private MapList map;

    /**
     * The team the player is going to join
     */
    private Team team;

    /**
     * The number of players the game will have in it.
     * Only used in multiplayer
     */
    private int numPlayers;

    /**
     * VBox holding the loading screen
     */
    private VBox loadScreen;

    /**
     * Controller for loading screen
     */
    private LoadScreenController loadScreenController;
    private boolean shouldClose = false;

    /**
     * Constructor for single player and multiplayer host instances.
     *
     * @param stage
     * @param connectionType A variable stating if the game is single player, multiplayer host or multiplayer join
     * @param settings       Object to hold the machines saved settings
     * @param name           The name of the player creating/joining the game
     * @param team           The team of the player joining/creating the game
     * @param map            The map the game is being played on
     * @param numOfPlayers   The number of players the game will have
     */
    public GameHandler(Stage stage, ConnectionType connectionType, Settings settings, String name, Team team, MapList map, String numOfPlayers) {
        this.stage = stage;
        this.connectionType = connectionType;
        this.settings = settings;
        this.playerName = name;
        this.map = map;
        this.team = team;
        this.numPlayers = Integer.parseInt(numOfPlayers);
    }

    /**
     * Constructor for multiplayer join instances
     *
     * @param stage
     * @param connectionType A variable stating if the game is single player, multiplayer host or multiplayer join
     * @param settings       Object to hold the machines saved settings
     * @param name           The name of the player creating/joining the game
     * @param team           The team of the player joining/creating the game
     * @param map            The map the game is being played on
     * @param numOfPlayers   The number of players the game will have
     * @param ipValue        The IP address the player will connect to
     * @param portValue      The port the player will communicate across
     */
    public GameHandler(Stage stage, ConnectionType connectionType, Settings settings, String name, Team team, MapList map, String numOfPlayers, String ipValue, String portValue) {
        this.stage = stage;
        this.connectionType = connectionType;
        this.settings = settings;
        this.playerName = name;
        this.map = map;
        this.team = team;
        this.numPlayers = Integer.parseInt(numOfPlayers);
        String[] split = ipValue.split("\\.");
        this.address = split[3]; // TODO: in case this is now size of 3, causes issues. limit here or in map selection.
        this.port = Integer.parseInt(portValue);
    }

    /**
     * Method to initialise server and client to start the game for the player
     */
    public void run() {
        // Load loading screen and set it to stage
        loadLoadingScreen();
        stage.getScene().setRoot(loadScreen);
        stage.setOnCloseRequest(we -> {
            stage.close();
            this.end();
        });

        switch (connectionType) {
            case SINGLE_PLAYER:
                if (!serverStarted) {
                    try {
                        // Create the server
                        server = new Server(map, playerName, team, 1, false);
                        server.setName("Server");
                        System.out.println("\n\n Threads alive when server constructed \n\n");
                        // create the client
                        client = new Client(stage, this, settings, 0, connectionType);
                        client.setName("Client");
                        System.out.println("\n\n Threads alive when client constructed \n\n");

                        // start client threads ready to receive and send
                        client.start();
                        // wait for threads to be setup completely
                        client.join();
                        if(shouldClose){
                            this.end();
                        }
                        System.out.println("\n\n Threads alive when client threads setup \n\n");
                        // start server threads ready to send and receive
                        server.start();
                        serverStarted = true;
                        // wait for threads to be setup completely
                        server.join();
                        NetworkInformation.incrementLowestAvailableIPAddress();
                        NetworkInformation.incrementLowestAvailablePort();
                        if(shouldClose){
                            this.end();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MULTI_PLAYER_HOST:
                if (!serverStarted) {
                    try {
                        // create the server
                        server = new Server(map, playerName, team, numPlayers, true);
                        server.setName("Server");
                        // start the server as it needs to listen to requests
                        server.join();
                        if(shouldClose){
                            this.end();
                        }
                        String[] splitIPAddress = server.getIPAddress().split("/");
                        loadScreenController.update(splitIPAddress[1], server.getPort());
                        server.start();
                        serverStarted = true;
                        // Wait for TCPManager to be up and receiving
                        while (!server.isReceiving()) {
                            Thread.yield();
                        }
                        // create the client
                        client = new Client(stage, this, settings, 0, connectionType);
                        client.setName("Client");

                        // setup clients threads

                        client.start();
                        //wait for all threads to finish setting up and client to join the game fully
                        client.join();
                        if(shouldClose){
                            this.end();
                        }

                        // wait for the server to receive all players before ending
                        server.join();
                        if(shouldClose){
                            this.end();
                        }
                        NetworkInformation.incrementLowestAvailableIPAddress();
                        NetworkInformation.incrementLowestAvailablePort();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MULTI_PLAYER_JOIN:
                if (!serverStarted) {
                    try {
                        // if joining server must already be started
                        serverStarted = true;
                        // create a client
                        client = new Client(stage, this, settings, address, port, playerName, team, connectionType);
                        client.setName("Client");
                        // Wait for the client to fully join the game
                        System.out.println("client joined");
                        client.join();
                        if(shouldClose){
                            this.end();
                        }
                        // setup the threads for that client
                        client.start();
                        System.out.println("client started");
                        // wait for threads to completely setup and player to join game
                        client.join();
                        if(shouldClose){
                            this.end();
                        }
                        System.out.println("Client joined again");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        System.out.println("Ending GameHandler");
    }

    public void end() {
        shouldClose = true;
        // End server if running/exists
        System.out.println("Told to end server threads");
        if (server != null) {
            server.close();
        }
        if(client != null){
            client.close(false);
        }
        // Client and threads end on their own when renderer calls
    }

    /**
     * Load the loading screen FXML and set its constructor
     */
    private void loadLoadingScreen() {
        try {
            // Load loading screen FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/load_screen.fxml"));
            loadScreen = fxmlLoader.load();
            // Set controller and update its settings value
            loadScreenController = fxmlLoader.getController();
            loadScreenController.initialise();
        } catch (Exception e) {
            System.out.println("Couldn't load the load screen FXML!");
            e.printStackTrace();
        }
    }
}