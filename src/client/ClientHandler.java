package client;

<<<<<<< HEAD
import client.data.EnemyView;
import client.data.GameView;
import client.data.ItemDropView;
import client.data.PlayerView;
import client.data.ProjectileView;
import client.data.TileView;
import client.data.ItemView;
import data.Pose;
=======
>>>>>>> e18ab9d2d89b8046c1d208386966a73918338e0c
import data.Constants;
import data.SystemState;
import data.entity.enemy.EnemyList;
import data.entity.item.ItemList;
import data.entity.item.weapon.gun.AmmoList;
import data.entity.projectile.ProjectileList;
import data.map.Meadow;
import data.map.tile.Tile;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
<<<<<<< HEAD
import server.Server;
=======
import server.serverclientthreads.ClientOnline;
import server.serverclientthreads.Server;
>>>>>>> e18ab9d2d89b8046c1d208386966a73918338e0c

import static data.SystemState.MENUS;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ClientHandler extends Thread {
    GameRenderer gameRenderer;
    private Stage stage;
    private boolean running;
<<<<<<< HEAD
    private boolean inGame;
    private boolean serverStarted;
    private Server server;
    private Client client;
=======
    private Server server;
    private ClientOnline clientOnline;
    private boolean gameRunning;
>>>>>>> e18ab9d2d89b8046c1d208386966a73918338e0c

    public ClientHandler(Stage stage) {
        this.stage = stage;
        running = true;
<<<<<<< HEAD
        inGame = false;
        serverStarted = false;
=======
        gameRunning = false;
        gameRenderer = null;
>>>>>>> e18ab9d2d89b8046c1d208386966a73918338e0c
    }

    public void run() {
        MenuController menuController = new MenuController(stage);

        // Example game state to render
        SystemState systemState = MENUS;

        // Load font
        Font.loadFont(getClass().getResourceAsStream(Constants.MANASPACE_FONT_PATH), 36);

        while (running) {
            switch (systemState) {
                case MENUS:
                    // Render menu
                    gameRunning = false;
                    menuController.renderMenu();
                    systemState = menuController.getSystemState();
                    break;
                case GAME:
                    // Render game state
<<<<<<< HEAD
                	//inGame = true;
                	/*
                	LinkedHashSet<PlayerView> examplePlayers = new LinkedHashSet<PlayerView>();
                	ArrayList<ItemView> exampleItems = new ArrayList<ItemView>();
                	exampleItems.add(new ItemView(ItemList.PISTOL, AmmoList.BASIC_AMMO, 0, 0));
                	LinkedHashMap<AmmoList, Integer> exampleAmmo = new LinkedHashMap<AmmoList, Integer>();
                	exampleAmmo.put(AmmoList.BASIC_AMMO, 0);
                	PlayerView examplePlayer = new PlayerView(new Pose(48, 48, 45), 1, 100, 100, 1, exampleItems, 0, 0, "Player 1", exampleAmmo, 1);
                	examplePlayers.add(examplePlayer);
                	LinkedHashSet<EnemyView> exampleEnemies = new LinkedHashSet<EnemyView>();
                	EnemyView exampleEnemy = new EnemyView(new Pose(120, 120, 45), 1, EnemyList.ZOMBIE);
                	exampleEnemies.add(exampleEnemy);
                	LinkedHashSet<ProjectileView> exampleProjectiles = new LinkedHashSet<ProjectileView>();
                	ProjectileView exampleProjectile = new ProjectileView(new Pose(400, 300, 70), 1, ProjectileList.SMALLBULLET);
                	exampleProjectiles.add(exampleProjectile);
                	LinkedHashSet<ItemDropView> exampleItemDrops = new LinkedHashSet<ItemDropView>();
                	ItemDropView exampleItemDrop = new ItemDropView(new Pose(50, 250), 1, ItemList.PISTOL);
                	exampleItemDrops.add(exampleItemDrop);
                	TileView[][] exampleTile = new TileView[Meadow.DEFAULT_X_DIM][Meadow.DEFAULT_Y_DIM];
                	Tile[][] tile = Meadow.generateTileMap();
                	for(int i = 0; i < Meadow.DEFAULT_X_DIM ; i++) {
        				for(int j = 0; j < Meadow.DEFAULT_Y_DIM ; j++) {
        					TileView tileView = new TileView(tile[i][j].getType(), tile[i][j].getState());
        					exampleTile[i][j] = tileView;
        				}
        			}
                	
                    renderer.renderGameView(new GameView(examplePlayers, exampleEnemies, exampleProjectiles, exampleItemDrops, exampleTile), 1);
                    */
                    //systemState = renderer.getSystemState();
                    break;
                case SINGLE_PLAYER:
                    // CODE FOR ESTABLISHING LOCAL SERVER
                    // Starts the server
                    // TODO pass the text from the box into the server and client
                	if(!serverStarted) {
	                    server = new Server("Host");
	                    //server.start();
	                    client = new Client(renderer, "Host", 0);
	                    client.start();
	                    serverStarted = true;
	                    systemState = SystemState.GAME; // REMOVE THIS
                	}
=======

                    // MAKE SURE GAMEVIEW IS SEND TO GAMERENDERER AT A TIMER/TIMELINE!!!

                    /*
                    if (!gameRunning) {
                        gameRenderer = new GameRenderer(stage, gameView, 0);
                        gameRenderer.setDaemon(true);
                        gameRenderer.run();
                        gameRunning = true;
                    }

                    gameRenderer.updateGameView(gameView);
                    */
                    //systemState = menuController.getSystemState(); TODO: change this to update state from game/controller
                    break;
                case SINGLE_PLAYER_CONNECTION:
                    // Start local server and run it
                    server = new Server();
                    server.start();
                    clientOnline = new ClientOnline();
                    clientOnline.run();

                    // Set appropriate systemstates
                    systemState = SystemState.GAME;
                    menuController.setSystemState(SystemState.GAME);
>>>>>>> e18ab9d2d89b8046c1d208386966a73918338e0c
                    break;
                case MULTI_PLAYER_CONNECTION:
                    // Start server and run it
                    server = new Server();
                    server.start();
                    clientOnline = new ClientOnline();
                    clientOnline.run();

                    // Set appropriate systemstates
                    systemState = SystemState.GAME;
                    menuController.setSystemState(SystemState.GAME);
                    break;
                case QUIT:
                    // Quit program
                    running = false;

                    Platform.runLater(() -> {
                        // Close stage
                        stage.close();
                    });
                    break;
            }
        }
    }

    public void end() {
        this.running = false;
        this.server.close();
        this.client.close();
    }
}

/*
    private ClientSender sender;
    private ClientReceiver receiver;
    private RendererController renderer;
    private inputController input;
    private inputChecker inChecker;
    //private audioController audio;
    private Socket server;

    public void setPlayerName(String name) {
        this.playerName = name;
        sendPlayerName();
    }

    public void joinServer(Socket server) {
        this.server = server;
        tryConnection();
    }

    public void createServer() { //will need to include params i just can't be bothered to guess.

    }

    /*
    Methods called by receiver:
    *//*

    public void setID(int id) {
        this.playerID = id;
        renderer.setID(id);
    }

    public void updateGameState(GameState gameState) {
        if (this.gameState == null) {
            this.gameState = gameState;
            input = new inputController(this); //assuming input controller is only for ingame input???
        }
        renderer.renderGame(this.gameState);
    }

    public void close() {
        sender.close();
        //whatever you wanna do here to close the client, System.exit(0)?
    }
*/
    /*
    Request methods to be called by input controller:
    They are to be checked and if it passes return true after submitting the request to the sender to handle.
    *//*
    public void requestToFace(int direction) {
        sender.requestToFace(direction);
    }

    public boolean requestToMove(int direction) {
        if (inChecker.checkMove(direction)) {
            sender.requestMove(direction);
            return true;
        } else return false;
    }

    public boolean requestToChangeItem(int newCurrentItem) {
        if (inChecker.checkItemChange(newCurrentItem)) {
            sender.requestItemChange(newCurrentItem);
            return true;
        } else return false;
    }

    public boolean requestNextItem() {
        if (inChecker.checkOtherItem()) {
            sender.requestNextItem();
            return true;
        } else return false;
    }

    public boolean requestPreviousItem() {
        if (inChecker.checkOtherItem()) {
            sender.requestPreviousItem();
            return true;
        } else return false;
    }

    public boolean requestToReload() {
        if (inChecker.checkHasAmmo()) {
            sender.requestReload();
            return true;
        } else return false;
    }

    public void requestToLeave() {
        sender.requestLeave();
    }

    /*
    Other methods that call the sender:
    */
/*
    private void sendName() {
        sender.sendName(this.playerName);
    }

    /*
    Misc:
    *//*

    private void tryConnection() {

        try {
            receiver = new ClientReceiver(this, new BufferedReader(new InputStreamReader(server.getInputStream())));
            receiver.start();
            sender = new ClientSender(new PrintStream(server.getOutputStream()));
        } catch (IOException e) {
            //insert whatever you wanna do here
        }


    }
 */
