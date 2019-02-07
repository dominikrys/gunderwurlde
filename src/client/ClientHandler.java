package client;

import client.data.EnemyView;
import client.data.GameView;
import client.data.ItemDropView;
import client.data.PlayerView;
import client.data.ProjectileView;
import client.data.TileView;
import client.data.ItemView;
import data.Pose;
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
import server.Server;

import static data.SystemState.MENU;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ClientHandler extends Thread {
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
        Renderer renderer = new Renderer(stage);

        // Example game state to render
        SystemState systemState = MENU;

        // Load font
        Font.loadFont(getClass().getResourceAsStream(Constants.MANASPACE_FONT_PATH), 36);

        while (running) {
            switch (systemState) {
                case MENU:
                    // Render menu
                    renderer.renderMenu();
                    systemState = renderer.getSystemState();
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
                    break;
                case MULTI_PLAYER:
                    // CODE FOR ESTABLISHING CONNECTION WITH REMOVE SERVER
                    break;
                case QUIT:
                    // Quit program
                    running = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // Close stage
                            stage.close();
                        }
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
