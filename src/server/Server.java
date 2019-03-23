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

public class Server extends Thread implements HasEngine {
    // Requests for the engine to handle
    protected ClientRequests clientRequests;

    // engine to pass the gameStates
    protected ProcessGameState engine;

    // Threads to send gameViews and receive commands
    ServerSender sender;
    ServerReceiver receiver;

    // Sockets for listening and sending
    MulticastSocket listenSocket = null;
    MulticastSocket senderSocket = null;
    MulticastSocket joinGameSocket = null;

    // Addresses for listening on
    InetAddress listenAddress = null;
    InetAddress senderAddress = null;
    InetAddress joinGameAddress = null;
    InetAddress tcpAddress = null;
    static int lowestAvailableAddress = 1;

    // Ports to be sent and received on
    static int sendPort;
    static int listenPort;
    static int JOINPORT = 8080;
    static int lowestavailableport = 4444;

    // Manager to handle the TCP protocol
    JoinGameManager tcpMananger;

    // Items used in communication for multiplayer
    byte[] buffer;
    DatagramPacket packet;

    // bool for if the server is handling a multiplayer game
    Boolean multiplayer;

    // the number of players the server should expect to join
    int numOfPlayers;
    // joinedPlayers =1 because host is always present
    int joinedPlayers = 1;

    boolean isThreadsUp;
    // The players that need to be added to the engine
    LinkedHashMap<String, Team> playersToAdd;

    MapList mapName;
    String hostName;
    Team hostTeam;


    public Server(MapList mapName, String hostName, Team hostTeam, int numOfPlayers, boolean multiplayer) {
        this.mapName = mapName;
        this.hostName = hostName;
        this.hostTeam = hostTeam;
        this.numOfPlayers = numOfPlayers;
        this.playersToAdd = new LinkedHashMap<>();
        this.playersToAdd.put(hostName, hostTeam);
        System.out.println(playersToAdd);
        this.multiplayer = multiplayer;
        this.clientRequests = null;
        isThreadsUp = false;
        this.start();
    }

    public void run(){
        try {
            // Declare ports to be used
            sendPort = lowestavailableport;
            listenPort = lowestavailableport+1;
            updatedLowestAvailablePort();
            // Start by setting up the threads that will be used during the actual game
            listenSocket = new MulticastSocket(listenPort);
            Addressing.setInterfaces(listenSocket);
            senderSocket = new MulticastSocket();
            Addressing.setInterfaces(senderSocket);
            listenAddress = InetAddress.getByName("230.0.1." + lowestAvailableAddress);
            senderAddress = InetAddress.getByName("230.0.0." + lowestAvailableAddress);
            updatedLowestAvailableAddress();
            isThreadsUp = true;

            // Check if the game is going to be multiplayer
            if(multiplayer){
                // Create the TCP threads to handle the join protocol
                tcpMananger = new JoinGameManager(this);
                tcpMananger.start();

                joinGameSocket = new MulticastSocket(JOINPORT);
                Addressing.setInterfaces(joinGameSocket);
                joinGameAddress = InetAddress.getByName("230.0.0.0");
                joinGameSocket.joinGroup(joinGameAddress);
                tcpAddress = Addressing.getAddress();

                // loop until all players have joined
                while(numOfPlayers > joinedPlayers) {

                    // create a packet to hold the request
                    buffer = new byte[32];
                    packet = new DatagramPacket(buffer, buffer.length);
                    // Wait to receive requests
                    joinGameSocket.receive(packet);
                    // When request is received shorten to only the message and translate to string
                    byte[] receivedBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                    String recievedString = new String(receivedBytes);

                    // if the sent message is the same as the address this server will operate on
                    // this request is meant for this game so send back the address of the server
                    if(recievedString.equals(senderAddress.toString())){
                        String message = senderAddress.toString() + tcpAddress.toString();
                        System.out.println("");
                        buffer = message.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, joinGameAddress, JOINPORT);
                        joinGameSocket.send(packet);
                    }
                    else{
                        continue;
                    }
                    int oldJoinedCount = joinedPlayers;
                    while(oldJoinedCount == joinedPlayers){
                        Thread.yield();
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("All players have joined the game");
                tcpMananger.end();
                joinGameSocket.close();
            }


            // Create the threads that will run as sender and receiver
            sender = new ServerSender(senderAddress, senderSocket, sendPort);
            receiver = new ServerReceiver(listenAddress, listenSocket, sender, this);
            isThreadsUp = true;

            // create the engine and start it
            this.engine = new ProcessGameState(this, mapName, playersToAdd);
            engine.start();
            this.clientRequests = new ClientRequests(numOfPlayers);
            // Socket is closed as server should end
            System.out.println("Closing server");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatedLowestAvailablePort() {
        lowestavailableport += 2;
    }
    private void updatedLowestAvailableAddress() {
        lowestAvailableAddress += 2;
    }

    @Override
    public void updateGameView(GameView view) {
        sender.send(view);
    }

    @Override
    public void removePlayer(int playerID) {
    }

    // Add player request from the serverReceiver sent to the engine
    public void addPlayer(String playerName, Team playerTeam){
        if(playersToAdd.containsKey(playerName)){
            playerName = playerName + "_";
        }
        playersToAdd.put(playerName, playerTeam);
        joinedPlayers++;
        System.out.println(playersToAdd);
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
        engine.handlerClosing();
        sender.close();
        try {
            sender.join();
            senderSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        receiver.close();
        try {
            receiver.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isThreadsUp(){
        return isThreadsUp;
    }

}
