package server.serverclientthreads;

import client.data.GameView;
import data.GameState;
import data.map.MapList;
import server.game_engine.HasEngine;
import server.game_engine.ProcessGameState;

import java.io.IOException;
import java.net.*;

public class Server extends Thread implements HasEngine {
    // Socket to listen to the server
    MulticastSocket listenSocket = null;
    // Socket to send requests to the server
    MulticastSocket senderSocket = null;
    InetAddress listenAddress = null;
    InetAddress senderAddress = null;
    static final int SENDPORT = 4444;
    static final int LISTENPORT = 4445;
    ProcessGameState engine;


    public Server() {
        this.engine = new ProcessGameState(this, MapList.MEADOW, "Bob");
        engine.start();
        this.start();
    }

    public void run(){
        try {
            listenSocket = new MulticastSocket(LISTENPORT);
            senderSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.0.1");
            senderAddress = InetAddress.getByName("230.0.1.1");
            // Create the threads that will run as sender and receiver
            ServerSender sender = new ServerSender(senderAddress, senderSocket, SENDPORT, engine);
            ServerReceiver receiver = new ServerReceiver(listenAddress, listenSocket, sender, engine);
            // Server will join with receiver when termination is requested
            // Only joins with receiver as receiver waits for sender to join
            receiver.join();
            // Socket is closed as server should end
            senderSocket.close();
            listenSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGameView(GameView view) {
    }

    @Override
    public void removePlayer(int playerID) {

    }
}
