package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import client.gui.Settings;
import client.net.ClientReceiver;
import client.net.ClientSender;
import client.render.GameRenderer;
import javafx.stage.Stage;
import shared.lists.Teams;
import shared.view.GameView;

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
    private ClientSender sender;
    private ClientReceiver receiver;
    private Stage stage;
    private boolean firstView;
    private GameHandler handler;
    private Settings settings;
    int playerID;


    public Client(Stage stage, String playerName, GameHandler handler, Settings settings, int playerID) {
        this.stage = stage;
        this.playerName = playerName;
        this.handler = handler;
        this.settings = settings;
        this.playerID = playerID;
        firstView = true;
    }

    public void run(){
        try{

            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");

            sender = new ClientSender(senderAddress, sendSocket, SENDPORT, playerID);
            receiver = new ClientReceiver(renderer, listenAddress, listenSocket, this, settings);

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

    public void joinGame(String playerName, Teams team){
        sender.joinGame(playerName, team);
    }
    public void close() {
        sender.stopRunning();
        receiver.stopRunning();
    }

}
