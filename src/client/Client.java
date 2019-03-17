package client;

import client.net.ClientReceiver;
import client.net.ClientSender;
import client.render.GameRenderer;
import javafx.stage.Stage;
import shared.view.GameView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client extends Thread {
    private MulticastSocket listenSocket;
    // Socket to send requests to the server
    private MulticastSocket sendSocket;
    private InetAddress listenAddress;
    private InetAddress senderAddress;
    private static final int LISTENPORT = 4444;
    private static final int SENDPORT = 4445;
    private GameView view;
    private GameRenderer renderer;
    private String playerName;
    private int playerID;
    private Boolean running;
    private ClientSender sender;
    private ClientReceiver receiver;
    private Stage stage;
    private boolean firstView;
    private GameHandler handler;
    private Settings settings;


    public Client(Stage stage, String playerName, int playerID, GameHandler handler, Settings settings) {
        this.stage = stage;
        this.playerName = playerName;
        this.playerID = playerID;
        this.running = true;
        this.handler = handler;
        this.settings = settings;
        firstView = true;
    }

    public void run(){
        try{
            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");


            // Start the sender and receiver threads for the client
            sender = new ClientSender(senderAddress, sendSocket, SENDPORT);
            receiver = new ClientReceiver(renderer, listenAddress, listenSocket, this, settings);

            // TODO: How will these threads close if the client is constantly rendering
            // Waits for the sender to join as that will be the first thread to close
            sender.join();
            // Waits for the receiver thread to end as this will be the second thread to close
            receiver.join();
            // Closes the socket as communication has finished
            sendSocket.close();
            listenSocket.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameView(GameView view, Settings settings){
        this.view = view;
        if (firstView) {
            firstView = false;
            renderer = new GameRenderer(stage, this.view, playerID, settings);
            renderer.getKeyboardHandler().setGameHandler(handler);
            renderer.getMouseHandler().setGameHandler(handler);
            renderer.run();
        } else {
            renderer.updateGameView(this.view);
        }
    }
    
    public ClientSender getClientSender() {
    	return this.sender;
    }

    public void close() {
    	this.running = false;
        sender.stopRunning();
        receiver.stopRunning();
    }

}
