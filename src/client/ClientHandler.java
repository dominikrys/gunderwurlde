package client;


import client.data.ItemView;
import client.data.TileView;
import client.data.entity.*;
import client.inputhandler.ActionList;
import data.Pose;
import data.SystemState;
import data.entity.EntityList;
import data.entity.player.Teams;
import data.item.ItemList;
import data.item.weapon.gun.AmmoList;
import data.map.MapList;
import data.map.Meadow;
import data.map.tile.Tile;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.Server;

import java.util.*;

import static data.SystemState.MENUS;

public class ClientHandler extends Thread{
    GameRenderer gameRenderer;
    private Stage stage;
    private boolean running;
    private boolean inGame;
    private boolean serverStarted;
    private Server server;
    private Client client;

    public ClientHandler(Stage stage) {
        this.stage = stage;
        running = true;
        inGame = false;
        serverStarted = false;
    }

    public void run() {
        MenuManager menuManager = new MenuManager(stage);

        // Example game state to render
        SystemState systemState = MENUS;

        while (running) {
            switch (systemState) {
                case MENUS:
                    // Render menu
                    menuManager.renderMenu();
                    systemState = menuManager.getSystemState();
                    break;
                case GAME:
                    // Render game state
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
                case SINGLE_PLAYER_CONNECTION:
                    // CODE FOR ESTABLISHING LOCAL SERVER
                    if (!serverStarted) {
                        server = new Server(MapList.MEADOW, "Player 1");
                        serverStarted = true;

                        GameView initialView = createGameView();

                        gameRenderer = new GameRenderer(stage, initialView, 0);
                        gameRenderer.getKeyboardHandler().setClientHandler(this);
                        gameRenderer.getMouseHandler().setClientHandler(this);
                        client = new Client(gameRenderer, "Player 1", 0);
                        client.start();
                        serverStarted = true;
                        systemState = SystemState.GAME; // REMOVE THIS
                    }
                    break;
                case MULTI_PLAYER_CONNECTION:
                    // CODE FOR ESTABLISHING CONNECTION WITH REMOVE SERVER
                    break;
                case QUIT:
                    // Quit program
                    end();
                    break;
            }
        }
    }

    public void end() {
        this.running = false;

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
        PlayerView examplePlayer = new PlayerView(new Pose(32, 32, 0), 1, 100, 100, exampleItems, 0, 0, "Player 1", exampleAmmo, 0, Teams.BLUE);
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
    
    public void send(ActionList action,int parameter) {
    	switch(action.toString()) {
    		case "CHANGEITEM" : // 3
    			client.getClientSender().send(new Integer[] {3, parameter});
    			break;
    		case "MOVEMENT" : // 4
    			client.getClientSender().send(new Integer[] {4, parameter});
    			break;
    		case "TURN" : //5
    			client.getClientSender().send(new Integer[] {5, parameter});
    	}
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
