package client;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import client.gui.Settings;
import client.net.Addressing;
import client.net.ClientReceiver;
import client.net.ClientSender;
import client.render.GameRenderer;
import javafx.stage.Stage;
import shared.lists.Teams;
import shared.view.GameView;

public class Client extends Thread {
    private MulticastSocket listenSocket;
    // Socket to send requests to the server
    private MulticastSocket sendSocket;
    private InetAddress listenAddress;
    private InetAddress senderAddress;
    private static final int LISTENPORT = 4444;
    private static final int SENDPORT = 4445;
    private GameView view;
    private GameRenderer renderer;
    private String playerName;
    private ClientSender sender;
    private ClientReceiver receiver;
    private Stage stage;
    private boolean firstView;
    private GameHandler handler;
    private Settings settings;
    int playerID;
    boolean idIsSet;
    boolean threadsup;


    public Client(Stage stage, String playerName, GameHandler handler, Settings settings, int playerID) {
        this.stage = stage;
        this.playerName = playerName;
        this.handler = handler;
        this.settings = settings;
        this.playerID = playerID;
        firstView = true;
        threadsup = false;
        System.out.println("client created");
    }

    public Client(Stage stage, String playerName, GameHandler handler, Settings settings) {
        this.stage = stage;
        this.playerName = playerName;
        this.handler = handler;
        this.settings = settings;
        this.playerID = requestClientID();
        idIsSet = true;
        firstView = true;
        threadsup = false;
        System.out.println("client created");
    }

    public void run(){
        try{
            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");

            sender = new ClientSender(senderAddress, sendSocket, SENDPORT, playerID);
            receiver = new ClientReceiver(renderer, listenAddress, listenSocket, this, settings);
            threadsup = true;
            System.out.println("Client running");
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

    public void setGameView(GameView view, Settings settings){
            this.view = view;
            if (firstView) {
                firstView = false;
                renderer = new GameRenderer(stage, this.view, playerID, settings);
                renderer.getKeyboardHandler().setGameHandler(handler);
                renderer.getMouseHandler().setGameHandler(handler);
                renderer.run();
            } else {
                renderer.updateGameView(this.view);
            }
    }

    public int requestClientID(){
        int clientID = -1;
        byte[] requestBytes = ByteBuffer.allocate(4).putInt(50).array();
        try {
            int JOINPORT = 8888;
            int RECJOINPORT = 8889;
            MulticastSocket sendJoinSocket = new MulticastSocket(JOINPORT);
            Addressing.setInterfaces(sendJoinSocket);
            MulticastSocket receiveJoinSocket = new MulticastSocket(RECJOINPORT);
            Addressing.setInterfaces(receiveJoinSocket);
            InetAddress joinAddress = InetAddress.getByName("230.1.0.0");
            InetAddress recJoinAddress = InetAddress.getByName("230.2.0.0");
            receiveJoinSocket.joinGroup(recJoinAddress);
            DatagramPacket sendPacket = new DatagramPacket(requestBytes, requestBytes.length, joinAddress, JOINPORT);
            System.out.println("Sending clientID request");
            sendJoinSocket.send(sendPacket);
            boolean idset = false;
            while(!idset) {
                byte[] receiveBytes = new byte[4];
                DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
                System.out.println("Waiting for clientID return");
                receiveJoinSocket.receive(receivePacket);

                byte[] commandBytes = Arrays.copyOfRange(receivePacket.getData(), 0, 4);
                ByteBuffer wrappedCommand = ByteBuffer.wrap(commandBytes);
                clientID = wrappedCommand.getInt();
                if (clientID == 50) {
                    continue;
                }
                else {
                    System.out.println("received ID:" + clientID);
                    idset = true;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientID;
    }

    public ClientSender getClientSender() {
        return this.sender;
    }

    public void joinGame(String playerName, Teams team){
        System.out.println("Sending request to engine");
        sender.joinGame(playerName, team);
    }
    public void close() {
        sender.stopRunning();
        receiver.stopRunning();
    }

    public boolean isThreadsup() {
        return threadsup;
    }

    public boolean isIdIsSet(){
        return idIsSet;
    }
}
