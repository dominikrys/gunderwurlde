package client;

import client.data.entity.EnemyView;
import client.data.entity.GameView;
import client.data.entity.ItemDropView;
import client.data.ItemView;
import client.data.entity.PlayerView;
import client.data.entity.ProjectileView;
import client.data.TileView;
import data.Pose;
import data.entity.EntityList;
import data.entity.player.Teams;
import data.item.ItemList;
import data.item.weapon.gun.AmmoList;
import data.map.Meadow;
import data.map.tile.Tile;
import client.inputhandler.KeyboardHandler;
import client.inputhandler.Movement;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Client extends Thread {
    MulticastSocket listenSocket;
    // Socket to send requests to the server
    MulticastSocket sendSocket;
    InetAddress listenAddress;
    InetAddress senderAddress;
    static final int LISTENPORT = 4444;
    static final int SENDPORT = 4445;
    GameView view;
    GameRenderer renderer;
    String playerName;
    int playerID;
    Boolean running;
    Movement movement;
    ClientSender sender;
    ClientReceiver receiver;



    public Client(GameRenderer renderer, String playerName, int playerID){
        this.renderer = renderer;
        /*
        LinkedHashSet<PlayerView> examplePlayers = new LinkedHashSet<PlayerView>();
    	ArrayList<ItemView> exampleItems = new ArrayList<ItemView>();
    	exampleItems.add(new ItemView(ItemList.PISTOL, AmmoList.BASIC_AMMO, 0, 0));
    	LinkedHashMap<AmmoList, Integer> exampleAmmo = new LinkedHashMap<AmmoList, Integer>();
    	exampleAmmo.put(AmmoList.BASIC_AMMO, 0);
    	PlayerView examplePlayer = new PlayerView(new Pose(48, 48, 45), 1, 100, 100, exampleItems, 0, 0, "Player 1", exampleAmmo, 0, Teams.BLUE);
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
    	for(int i = 0; i < Meadow.DEFAULT_X_DIM ; i++) {
			for(int j = 0; j < Meadow.DEFAULT_Y_DIM ; j++) {
				TileView tileView = new TileView(tile[i][j].getType(), tile[i][j].getState());
				exampleTile[i][j] = tileView;
			}
		}
        
        this.view = new GameView(examplePlayers, exampleEnemies, exampleProjectiles, exampleItemDrops, exampleTile);
        */
        this.playerName = playerName;
        this.playerID = playerID;
        this.running = true;
    }

    public void run(){
        try{
            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");

            System.out.println("ClientOnline calls server");

            // Start the sender and receiver threads for the client
            sender = new ClientSender(senderAddress, sendSocket, SENDPORT);
            receiver = new ClientReceiver(listenAddress, listenSocket, this);
            
            renderer.setClientSender(sender);

            while(running){
                if(view != null) {
                	System.out.println("here");
                	renderer.updateGameView(view);
                    renderer.renderGameView();
                }
            }



            // How will these threads close if the client is constantly rendering
            // Waits for the sender to join as that will be the first thread to close
            sender.join();
            // Waits for the receiver thread to end as this will be the second thread to close
            receiver.join();
            // Closes the socket as communication has finished
            sendSocket.close();
            listenSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameView(GameView view){
        this.view = view;
    }
    
    public void close() {
    	this.running = false;
    	sender.running = false;
    	receiver.running = false;
    }

}
