package client;


import data.GameState;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    MulticastSocket listenSocket;
    MulticastSocket sendSocket;
    InetAddress listenAddress;
    InetAddress senderAddress;
    private static final int LISTENPORT = 4444;
    private static final int SENDPORT = 4445;
    GameState gameState;
    Renderer renderer;

    // Where am i getting 'inputChecker' from
    // Should the client have a gameState or a gameView

    public Client(Renderer renderer, GameState gameState){
        this.gameState = gameState;
        this.renderer = renderer;
        networkSetup();
    }


    public void networkSetup(){
        try{
            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");

            // Start the sender and receiver threads for the client
            ClientSender sender = new ClientSender(senderAddress, sendSocket, SENDPORT);
            ClientReceiver receiver = new ClientReceiver(listenAddress, listenSocket);

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


}
