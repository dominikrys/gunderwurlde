package server;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;

import client.net.Addressing;
import server.engine.HasEngine;
import server.engine.ProcessGameState;
import server.net.ServerReceiver;
import server.net.ServerSender;
import shared.lists.MapList;
import shared.lists.Teams;
import shared.request.ClientRequests;
import shared.view.GameView;

import javax.xml.crypto.Data;

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
    InetAddress joinAddress = null;
    // Ports to be sent and received on
    static final int SENDPORT = 4444;
    static final int LISTENPORT = 4445;
    Boolean multiplayer;
    int numOfPlayers;
    int nextID = 1;
    int joinedPlayers = 1;
    LinkedHashMap<String, Teams> playersToAdd;
    MapList mapName;


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
            listenSocket = new MulticastSocket(LISTENPORT);
            senderSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.0.1");
            senderAddress = InetAddress.getByName("230.0.1.1");
            System.out.println("Server starting");
            // Create the initial GameView to be sent to the clients

            // Create the threads that will run as sender and receiver
            sender = new ServerSender(senderAddress, senderSocket, SENDPORT);
            receiver = new ServerReceiver(listenAddress, listenSocket, sender, this);
            int requestedJoins = 1;

            if(multiplayer){
                System.out.println("joinedPlauers:" + joinedPlayers);
                System.out.println("numOfplayers:" + numOfPlayers);
                // loop until all players have joined
                while(numOfPlayers != requestedJoins) {
                    int JOINPORT = 8889;
                    int RECJOINPORT = 8888;
                    MulticastSocket sendJoinSocket = new MulticastSocket(JOINPORT);
                    Addressing.setInterfaces(sendJoinSocket);
                    MulticastSocket receiveJoinSocket = new MulticastSocket(RECJOINPORT);
                    Addressing.setInterfaces(receiveJoinSocket);
                    InetAddress joinAddress = InetAddress.getByName("230.2.0.0");
                    InetAddress recJoinAddress = InetAddress.getByName("230.1.0.0");
                    receiveJoinSocket.joinGroup(recJoinAddress);
                    System.out.println("num not equal to join");
                    byte[] joinRequest = new byte[4];
                    DatagramPacket receivePacket = new DatagramPacket(joinRequest, joinRequest.length);
                    receiveJoinSocket.receive(receivePacket);
                    System.out.println("Received request");
                    byte[] commandBytes = Arrays.copyOfRange(receivePacket.getData(), 0, 4);
                    ByteBuffer wrappedCommand = ByteBuffer.wrap(commandBytes);
                    int request = wrappedCommand.getInt();
                    if(request == 50){
                        byte[] sendIDBytes = ByteBuffer.allocate(4).putInt(nextID).array();
                        nextID++;
                        DatagramPacket sendPacket = new DatagramPacket(sendIDBytes, sendIDBytes.length, joinAddress, JOINPORT);
                        System.out.println("Sending clientID");
                        sendJoinSocket.send(sendPacket);
                        requestedJoins++;
                    }
                }

                System.out.println("All players have joined the game");
                System.out.println("Socket closed");
            }
            while(numOfPlayers != joinedPlayers){
                Thread.yield();
                System.out.println("expected: " + numOfPlayers + "Joined: " + joinedPlayers);
                Thread.sleep(5000);
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
