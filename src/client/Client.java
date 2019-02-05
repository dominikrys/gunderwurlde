package client;

import client.data.GameView;

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
    Renderer renderer;
    String playerName;
    int playerID;


    public Client(Renderer renderer, String playerName, int playerID){
        this.renderer = renderer;
        this.view = null;
        this.playerName = playerName;
        this.playerID = playerID;
    }

    public void run(){
        try{
            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");

            System.out.println("ClientOnline calls server");

            // Start the sender and receiver threads for the client
            ClientSender sender = new ClientSender(senderAddress, sendSocket, SENDPORT);
            ClientReceiver receiver = new ClientReceiver(listenAddress, listenSocket, this);

            if(view != null)
                renderer.renderGameView(view, 0);



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

}
