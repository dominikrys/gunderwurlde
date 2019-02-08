package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import client.data.entity.GameView;
import data.entity.player.Teams;
import data.map.MapList;
import server.game_engine.HasEngine;
import server.game_engine.ProcessGameState;
import server.request.ClientRequests;

public class Server extends Thread implements HasEngine {
	protected ClientRequests clientRequests;
	protected ProcessGameState engine;
    private final String hostName;
    ServerSender sender;
    ServerReceiver receiver;
    // Socket to listen to the server
    MulticastSocket listenSocket = null;
    // Socket to send requests to the server
    MulticastSocket senderSocket = null;
    InetAddress listenAddress = null;
    InetAddress senderAddress = null;
    // Ports to be sent and received on
    static final int SENDPORT = 4444;
    static final int LISTENPORT = 4445;


    public Server(MapList mapName, String hostName) {
    	this.engine = new ProcessGameState(this, mapName, hostName);
        engine.start();
        engine.addPlayer(hostName, Teams.RED);
        this.hostName = hostName;
        this.start();
    }

    public void run(){
        try {
            listenSocket = new MulticastSocket(LISTENPORT);
            senderSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.0.1");
            senderAddress = InetAddress.getByName("230.0.1.1");
            System.out.println("Server starting");
            // Create the initial GameView to be sent to the clients


            // Create the threads that will run as sender and receiver
            sender = new ServerSender(senderAddress, senderSocket, SENDPORT);
            receiver = new ServerReceiver(listenAddress, listenSocket, sender, this);
            System.out.println("Threads up");
            // Server will join with receiver when termination is requested
            // Only joins with receiver as receiver waits for sender to join
            sender.join();
            receiver.join();
            engine.handlerClosing();
            // Socket is closed as server should end
            senderSocket.close();
            listenSocket.close();
            System.out.println("Server ended successfully");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Server ended due an interrupt");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGameView(GameView view) {
        sender.send(view);
    }

    @Override
    public void removePlayer(int playerID) {
    }

    @Override
    public void requestClientRequests() {
        if (clientRequests != null) engine.setClientRequests(clientRequests);
    }
    
    public void sendClientRequest(ClientRequests request) {
        engine.setClientRequests(request);
    }


	public void close() {
		sender.running = false;
		receiver.running = false;
	}

}
