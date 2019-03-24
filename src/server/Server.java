package server;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import client.net.Addressing;
import server.engine.HasEngine;
import server.engine.ProcessGameState;
import server.net.JoinGameManager;
import server.net.ServerReceiver;
import server.net.ServerSender;
import shared.lists.MapList;
import shared.lists.Team;
import shared.request.ClientRequests;
import shared.view.GameView;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * class to initialise the sender, receiver threads and handle the engine
 */
public class Server extends Thread implements HasEngine {

    /**
     * object to hold all client requests to be sent to the engine
     */
    protected ClientRequests clientRequests;

    /**
     * object to hold the actual game itself and handle changes
     */
    protected ProcessGameState engine;

    /**
     * Object to handle sending GameViews to the clients
     */
    private ServerSender sender;

    /**
     * Object to handle receiving Client Requests
     */
    private ServerReceiver receiver;

    /**
     * Socket to receive requests from the clients
     */
    private MulticastSocket listenSocket;

    /**
     * Socket to send game views to the client
     */
    private MulticastSocket sendSocket;

    /**
     * Socket to receive requests to join the game
     */
    private MulticastSocket joinGameSocket;

    /**
     * Address the server will receive requests from
     */
    private InetAddress listenAddress;

    /**
     * Address the server will send GameViews to
     */
    private InetAddress senderAddress;

    /**
     * Address clients will use to request to join a game
     */
    private InetAddress joinGameAddress;

    /**
     *  The address of the server machines ethernet IP
     */
    private InetAddress tcpAddress;

    /**
     * The next assignable IP address
     */
    private static int lowestAvailableAddress = 1;

    /**
     * The port the server will send GameViews across
     */
    private static int sendPort;

    /**
     * The port the server will receive requests across
     */
    private static int listenPort;

    /**
     * The port players will request the TCP address of the server machine on
     */
    private static int JOINPORT = 8080;

    /**
     * Integer to hold the next assignable port
     */
    private static int lowestavailableport = 4444;

    /**
     * manager to create threads for every player joining the game
     */
    private JoinGameManager tcpMananger;

    /**
     * byte array to hold information being received and sent when joining a game
     */
    private byte[] buffer;

    /**
     * The packet used to receive and send information when joining game
     */
    private DatagramPacket packet;

    /**
     * A bool to tell the server if it needs to run the multiplayer setup
     */
    private Boolean multiplayer;

    /**
     * The number of players that have joined the game
     */
    private int joinedPlayers = 1;

    /**
     * The number of players that are expected to join the game
     */
    private int numOfPlayers;

    /**
     * HashMap to hold the players that need to be sent to the engine and added to the game
     */
    private LinkedHashMap<String, Team> playersToAdd;

    /**
     * The map that the game will be played on
     */
    private MapList mapName;

    /**
     * The name of player who is hosting the game
     */
    private String hostName;

    /**
     * The team of the player who is hosting the game
     */
    private Team hostTeam;

    /**
     * bool value to tell if the server is waiting for players to join the game
     */
    private boolean receiving;

    /**
     * Constructor
     * @param mapName The map that the game will be played on
     * @param hostName The name of the player who is hosting the game
     * @param hostTeam The team of the player who is hosting the game
     * @param numOfPlayers The number of the players the game should contain
     * @param multiplayer If the game is multiplayer or singleplayer
     */
    public Server(MapList mapName, String hostName, Team hostTeam, int numOfPlayers, boolean multiplayer) {
        try {
            this.mapName = mapName;
            this.hostName = hostName;
            this.hostTeam = hostTeam;
            this.numOfPlayers = numOfPlayers;
            this.playersToAdd = new LinkedHashMap<>();
            // Add the host to players straight away
            this.playersToAdd.put(this.hostName, this.hostTeam);
            this.multiplayer = multiplayer;
            this.clientRequests = null;
            this.receiving = false;
            // Set ports to be used
            sendPort = lowestavailableport;
            listenPort = lowestavailableport + 1;
            // update next assignable port
            updatedLowestAvailablePort();
            // Set sockets and address to be used
            listenSocket = new MulticastSocket(listenPort);
            Addressing.setInterfaces(listenSocket);

            sendSocket = new MulticastSocket();
            Addressing.setInterfaces(sendSocket);
            listenAddress = InetAddress.getByName("230.0.1." + lowestAvailableAddress);
            listenSocket.joinGroup(listenAddress);
            senderAddress = InetAddress.getByName("230.0.0." + lowestAvailableAddress);
            updatedLowestAvailableAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the game is multiplayer and then create the sender and receiver for the server
     */
    public void run(){
        try {
            // Check if the game is going to be multiplayer
            if(multiplayer){
                // Create the TCP threads to handle the join protocol
                // Setup a TCP manager to receive join requests
                tcpMananger = new JoinGameManager(this);
                tcpMananger.start();

                // Create the socket that will give joining players the TCP address
                joinGameSocket = new MulticastSocket(JOINPORT);
                Addressing.setInterfaces(joinGameSocket);
                joinGameAddress = InetAddress.getByName("230.0.0.0");
                joinGameSocket.joinGroup(joinGameAddress);
                tcpAddress = Addressing.getAddress();

                // loop until all players have joined
                receiving = true;
                while(numOfPlayers > joinedPlayers) {
                    // create a packet to hold the request
                    buffer = new byte[32];
                    packet = new DatagramPacket(buffer, buffer.length);
                    // Wait to receive requests
                    joinGameSocket.receive(packet);
                    // When request is received turn to string
                    byte[] receivedBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                    String recievedString = new String(receivedBytes);

                    // if the sent message is the same as the address of senderAddress
                    //  The player wants to join this game so reply with the TCP address
                    if(recievedString.equals(senderAddress.toString())){
                        String message = senderAddress.toString() + tcpAddress.toString();
                        buffer = message.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, joinGameAddress, JOINPORT);
                        joinGameSocket.send(packet);
                    }
                    // else not talking to this server so continue
                    else{
                        continue;
                    }
                }
                System.out.println("All players have joined the game");
                tcpMananger.close();
                joinGameSocket.close();
            }

            // Create the threads that will run as sender and receiver
            sender = new ServerSender(senderAddress, sendSocket, sendPort);
            receiver = new ServerReceiver(listenSocket, this);


            this.clientRequests = new ClientRequests(numOfPlayers);
            // create the engine and start it
            this.engine = new ProcessGameState(this, mapName, playersToAdd);
            engine.start();
            System.out.println("Closing server");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to update to the next assignable port
     */
    private void updatedLowestAvailablePort() {
        lowestavailableport += 2;
    }

    /**
     * method to update to the next assignable address
     */
    private void updatedLowestAvailableAddress() {
        lowestAvailableAddress += 2;
    }

    /**
     * Methods to tell the ServerSender to send the next GameView
     * @param view the new GameView sent to clients
     */
    @Override
    public void updateGameView(GameView view) {
        sender.send(view);
    }

    /**
     * Method to remove a player from the game
     * @param playerID the ID of the player to be removed
     */
    @Override
    public void removePlayer(int playerID) {
    }

    /**
     * Method ot add a player to the game
     * @param playerName The name of the player to be added
     * @param playerTeam The team of the player to be added
     */
    public void addPlayer(String playerName, Team playerTeam){
        // If there is a playerName clash then add a _
        if(playersToAdd.containsKey(playerName)){
            playerName = playerName + "_";
        }
        // Put them in the list to be sent to the Game Engine
        playersToAdd.put(playerName, playerTeam);
        // increase joinedPlayers
        joinedPlayers++;
        // TODO Keep for testing purposes for now
        System.out.println(playersToAdd);
    }

    /**
     * Method the game engine uses to request the next load of client requests
     */
    @Override
    public void requestClientRequests() {
        if (clientRequests != null) {
            engine.setClientRequests(clientRequests);
            this.clearClientRequests(numOfPlayers);
        }
    }

    /**
     * get the servers current clientRequests object
     * @return
     */
    public ClientRequests getClientRequests() {
        return this.clientRequests;
    }

    /**
     * Empty the servers current clientRequest object
     * @param numOfPlayers the number of players in the game
     */
    public void clearClientRequests(int numOfPlayers) {
        this.clientRequests = new ClientRequests(numOfPlayers);
    }

    /**
     * method to close the server and its threads
     */
    public void close() {
        // close the engine down
        engine.handlerClosing();
        sender.close();
        try {
            sender.join();
            sendSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        receiver.close();
        try {
            receiver.join();
            //ServerReceiver closes itself
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isReceiving() {
        return receiving;
    }
}
