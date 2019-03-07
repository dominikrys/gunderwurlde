package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.LinkedHashMap;

import server.engine.HasEngine;
import server.engine.ProcessGameState;
import server.net.ServerReceiver;
import server.net.ServerSender;
import server.net.JoinGameThread;
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
    int senderPort;
    int listenPort;
    Boolean multiplayer;
    int numOfPlayers;
    int joinedPlayers;
    LinkedHashMap<String, Teams> playersToAdd;
    MapList mapName;
    static int lowestAvailableIP = 0;
    static int lowestAvailablePort = 4444;
    ServerSocket joinSocket;


    public Server(MapList mapName, String hostName, Teams hostTeam, int numOfPlayers, boolean multiplayer) {
        this.mapName = mapName;
        this.hostName = hostName;
        this.numOfPlayers = numOfPlayers;
        playersToAdd = new LinkedHashMap<>();
        playersToAdd.put(hostName, hostTeam);
        this.multiplayer = multiplayer;
        this.clientRequests = null;
        this.start();
    }

    public void run(){
        try {
            senderPort = lowestAvailablePort;
            listenPort = lowestAvailablePort+1;
            lowestAvailablePort += 2;
            listenSocket = new MulticastSocket(listenPort);
            senderSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.0." + lowestAvailableIP);
            senderAddress = InetAddress.getByName("230.0.1." + lowestAvailableIP);
            lowestAvailableIP += 1;
            System.out.println("Server starting");
            // Create the initial GameView to be sent to the clients

            // Create the threads that will run as sender and receiver
            sender = new ServerSender(senderAddress, senderSocket, senderPort);
            receiver = new ServerReceiver(listenAddress, listenSocket, sender, this);
            joinedPlayers = 1;
            if(multiplayer){
                // loop until all players have joined
                try {
                    joinSocket = new ServerSocket(senderPort);
                    System.out.println("Server listenting on port: 9999");
                    while (numOfPlayers > joinedPlayers) {
                        // For each connection spin off a new protocol instance.
                        Socket connection = joinSocket.accept();
                        Thread instance = new Thread(new JoinGameThread(connection));
                        instance.start();
                        JoinGameThread.increaseAvailableID();
                    }
                } catch (Exception e) {
                    System.out.println("Doh "+e);
                }

                System.out.println("All players have joined the game");
            }
            this.engine = new ProcessGameState(this, mapName, playersToAdd);
            engine.start();
            System.out.println("Threads up");

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

    // Add player request from the serverReceiver sent to the engine
    public void addPlayer(String playerName, Teams playerTeam){
        playersToAdd.put(playerName, playerTeam);
        joinedPlayers++;
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
}
