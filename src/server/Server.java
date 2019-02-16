package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import server.engine.HasEngine;
import server.engine.ProcessGameState;
import server.net.ServerReceiver;
import server.net.ServerSender;
import shared.lists.MapList;
import shared.lists.Teams;
import shared.request.ClientRequests;
import shared.view.GameView;

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
    int numOfPlayers;


    public Server(MapList mapName, String hostName, Teams hostTeam, int numOfPlayers) {
        this.engine = new ProcessGameState(this, mapName, hostName, hostTeam);
        this.hostName = hostName;
        this.numOfPlayers = numOfPlayers;
        // TODO: set num of player
        this.clientRequests = null;
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
            engine.start();
            System.out.println("Threads up");
            
            // TODO: num of player setting
            this.clientRequests = new ClientRequests(numOfPlayers);
            
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

    public void addPlayer(String playerName, Teams playerTeam){
        engine.addPlayer(playerName, playerTeam);
    }

    @Override
    public void requestClientRequests() {
        if (clientRequests != null) {
        	engine.setClientRequests(clientRequests);
        	 this.clearClientRequests(numOfPlayers);
        }
    }
    
    public ClientRequests getClientRequests() {
        return this.clientRequests;
    }

    public void clearClientRequests(int numOfPlayers) {
        this.clientRequests = new ClientRequests(numOfPlayers);
    }

    public void close() {
        sender.stopRunning();
        receiver.stopRunning();
    }
	
	/*
	public void setClientRequests(ClientRequests clientRequests) {
		this.clientRequests = clientRequests;
	}
	*/

}
