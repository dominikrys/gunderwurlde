package client;

import client.data.entity.GameView;
import client.data.entity.PlayerView;
import client.inputhandler.KeyboardHandler;
import client.inputhandler.MouseHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
    ClientSender sender;
    ClientReceiver receiver;


    public Client(GameRenderer renderer, String playerName, int playerID){
        this.renderer = renderer;
        this.playerName = playerName;
        this.playerID = playerID;
        this.running = true;
        setGameView(renderer.getView());

    }

    public void run(){
        try{
            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");


            // Start the sender and receiver threads for the client
            sender = new ClientSender(senderAddress, sendSocket, SENDPORT);
            receiver = new ClientReceiver(listenAddress, listenSocket, this);
            renderer.getKeyboardHandler().setClientSender(sender);
            renderer.run();

            while(running){
                if(view != null) {
                    for(PlayerView view:view.getPlayers()){
                        System.out.println(view.getPose().getX());
                        System.out.println(view.getPose().getY());
                    }

                    System.out.println("Updating renderer gameView");
                	renderer.updateGameView(view);
                	Thread.sleep(100);
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
