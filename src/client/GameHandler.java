package client;

import client.gui.menucontrollers.LoadScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.Server;
import shared.lists.MapList;
import shared.lists.Team;

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
        this.address = split[3];
        this.port = Integer.parseInt(portValue);
    }

    /**
     * Method to initialise server and client to start the game for the player
     */
    public void run() {
        // Load loading screen and set it to stage
        loadLoadingScreen();
        stage.getScene().setRoot(loadScreen);

        switch (connectionType) {
            case SINGLE_PLAYER:
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
            case MULTI_PLAYER_HOST:
                if (!serverStarted) {
                    try {
                        // create the server
                        server = new Server(map, playerName, team, numPlayers, true);
                        // start the server as it needs to listen to requests
                        server.start();
                        serverStarted = true;
                        // Wait for TCPManager to be up and receiving
                        while (!server.isReceiving()) {
                            Thread.yield();
                        }
                        System.out.println("Server setup and waiting");
                        // create the client
                        client = new Client(stage, this, settings, 0);
                        // setup clients threads
                        client.start();
                        System.out.println("Client started");
                        //wait for all threads to finish setting up and client to join the game fully
                        client.join();
                        System.out.println("Client joined");
                        // wait for the server to receive all players before ending
                        server.join();
                        System.out.println("Server joined");
                        System.out.println("Client setup and waiting");
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
                        client = new Client(stage, this, settings, address, port, playerName, team);
                        // Wait for the client to fully join the game
                        System.out.println("client joined");
                        client.join();
                        // setup the threads for that client
                        client.start();
                        System.out.println("client started");
                        // wait for threads to completely setup
                        client.join();
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
        // End server if running/exists
        if (server != null) {
            if (server.isAlive()) {
                server.close();
            }
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