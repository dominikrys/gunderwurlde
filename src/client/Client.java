package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import client.input.ActionList;
import client.net.Addressing;
import client.net.ClientReceiver;
import client.net.ClientSender;
import client.render.GameRenderer;
import javafx.stage.Stage;
import shared.lists.Team;
import shared.view.GameView;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client extends Thread {


    /**
     * Socket to receive GameViews from the server
     */
    private MulticastSocket listenSocket;

    /**
     * Socket to send client requests to the server
     */
    private MulticastSocket sendSocket;

    /**
     * Socket to return the TCP address needed to join the game
     */
    private MulticastSocket joinGameSocket;

    /**
     * Socket that handles a player joining a game
     */
    private Socket tcpSocket;


    /**
     * Address the client will receive gameViews on
     */
    private InetAddress listenAddress;

    /**
     * Address the client will send requests on
     */
    private InetAddress senderAddress;

    /**
     * The UDP address to request to join a game
     */
    private InetAddress joinGameAddress;

    /**
     * The variable to hold the server machines IP address
     */
    private InetAddress tcpAddress;

    /**
     * Integer to hold the next assignable IP address
     */
    private int lowestAvailableAddress = 1;


    /**
     * The port the client will receive gameView across
     */
    private int listenPort;

    /**
     * The port the client will send requests across
     */
    private int sendPort;

    /**
     * The port players will request the TCP address of the server machine on
     */
    private static final int JOINPORT = 8080;

    /**
     * The port the player will use to join the game
     */
    private static final int TCPPORT = 8081;

    /**
     * Integer to hold the next assignable port
     */
    private static int lowestAvailablePort = 4444;


    /**
     * byte array to hold information being received and sent when joining a game
     */
    private byte[] buffer;

    /**
     * The packet that will be received and sent when joining a game
     */
    private DatagramPacket packet;

    /**
     * boolean to declare that the client is waiting for a response from the server
     */
    private Boolean waiting;

    /**
     * The name of the player paying the game
     */
    private String playerName;

    /**
     * The team of the player playing the game
     */
    private Team team;

    /**
     * The object that will hold the constantly updating view of the game
     */
    private GameView view;

    /**
     * The object that will render the graphics to the screen
     */
    private GameRenderer renderer;


    /**
     * Thread that handles sending requests to the server
     */
    private ClientSender sender;

    /**
     * Thread that handles receiving GameViews from the server
     */
    private ClientReceiver receiver;

    // TODO still dont really know what this is
    private Stage stage;

    /**
     * boolean to say if the client needs to receive their first gameView
     */
    private boolean firstView;

    /**
     * GameHandler that created this client. Used to end the threads on close
     */
    private GameHandler handler;

    /**
     * object to hold previously saved settings e.g. sound settings
     */
    private Settings settings;

    /**
     * The identification value for a player
     */
    private int playerID;


    /**
     * Constructor for single player or multiplayer host
     * @param stage
     * @param handler object that created this client. Used to end the threads on close
     * @param settings object to hold previously saved settings e.g. sound settings
     * @param playerID The identification value for a player
     */
    public Client(Stage stage, GameHandler handler, Settings settings, int playerID) {
       try {
           // Assign the value for the ports and update the next available port
           this.listenPort = lowestAvailablePort;
           this.sendPort = lowestAvailablePort + 1;
           updateLowestAvailablePort();
           // Assign the values for the addresses and update the next available address
           this.listenAddress = InetAddress.getByName("230.0.0." + lowestAvailableAddress);
           this.senderAddress = InetAddress.getByName("230.0.1." + lowestAvailableAddress);
           updateLowestAvailableAddress();
           this.stage = stage;
           this.handler = handler;
           this.settings = settings;
           this.playerID = playerID;
           firstView = true;
       } catch (UnknownHostException e) {
           e.printStackTrace();
       }
    }

    /**
     * Constructor for multiplayer join
     * @param stage
     * @param handler object that created this client. Used to end the threads on close
     * @param settings object to hold previously saved settings e.g. sound settings
     * @param ipValue The IP address the server is sending on
     * @param portValue The port the server is sending across
     * @param playerName The name of the player playing the game
     * @param team The team of the player playing the game
     */
    public Client(Stage stage, GameHandler handler, Settings settings,String ipValue, int portValue, String playerName, Team team) {
        try {
            // Assign the player name and player value
            this.playerName = playerName;
            this.team = team;
            // Set the port and address value
            this.listenPort = portValue;
            this.sendPort = portValue + 1;
            this.listenAddress = InetAddress.getByName("230.0.0." + ipValue);
            this.senderAddress = InetAddress.getByName("230.0.1." + ipValue);
            this.stage = stage;
            this.handler = handler;
            this.settings = settings;
            firstView = true;
            // Start the joinGame process
            joinGame();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to start sender and receiver threads for the client
     */
    public void run() {
        try {
            listenSocket = new MulticastSocket(listenPort);
            listenSocket.joinGroup(listenAddress);
            sendSocket = new MulticastSocket();
            Addressing.setInterfaces(sendSocket);
            sender = new ClientSender(senderAddress, sendSocket, sendPort, playerID);
            receiver = new ClientReceiver(renderer, listenAddress, listenSocket, this, settings);
            System.out.println("Closing client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to set the GameView for the client so that new graphics appear
     * @param view the GameView that will be displayed to the screen
     * @param settings object to hold previously saved settings e.g. sound settings
     */
    public void setGameView(GameView view, Settings settings){
        // sets the value for view
        this.view = view;
        // if this is the first GameView then create a renderer
        if (firstView) {
            firstView = false;
            renderer = new GameRenderer(stage, this.view, playerID, settings, this);
            renderer.getKeyboardHandler().setGameHandler(this);
            renderer.getMouseHandler().setGameHandler(this);
            renderer.run();
        }
        //else just update the GameView
        else {
            renderer.updateGameView(this.view);
        }
    }

    /**
     * Method for retrieving the IP address of the server machine so the client can join the game
     */
    public void joinGame() {
        try {
            // Initialise the joinGame sockets
            joinGameSocket = new MulticastSocket(JOINPORT);
            Addressing.setInterfaces(joinGameSocket);
            joinGameAddress = InetAddress.getByName("230.0.0.0");
            joinGameSocket.joinGroup(joinGameAddress);

            // Prepare the gameIPAddress to transmission
            String gameIPAddress = listenAddress.toString();
            buffer = gameIPAddress.getBytes();
            // Create the packet to be sent and send it
            packet = new DatagramPacket(buffer, buffer.length, joinGameAddress, JOINPORT);
            joinGameSocket.send(packet);

            //Resize the buffer and create a new packet in prep to recieve a message
            buffer = new byte[32];
            packet = new DatagramPacket(buffer, buffer.length);
            waiting = true;
            // wait until you receive a message where the first half is the message you sent
            while (waiting) {
                joinGameSocket.receive(packet);
                // If the message is greater than the size of 1 IP address
                //  split the message down to determine the TCP address
                if (packet.getLength() > 12) {
                    byte[] recievedBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                    String messageReceived = new String(recievedBytes);
                    String[] split = messageReceived.split("/");
                    // if the first half is the address you sent then its the correct message
                    if (("/" + split[1]).equals(listenAddress.toString())) {
                        tcpAddress = InetAddress.getByName(split[2]);
                        waiting = false;
                    }
                    // If a different start then ignore as its not meant for this client
                    else {
                        continue;
                    }
                }
                // If one IP address then ignore as its not meant for this client
                else {
                    continue;
                }

                // Now we have the correct TCP address we can create the TCP socket
                tcpSocket = new Socket(tcpAddress, TCPPORT);
                InputStream is = tcpSocket.getInputStream();
                OutputStream os = tcpSocket.getOutputStream();

                // Listen for the players clientID to be sent across
                byte[] clientIDBytes = new byte[4];
                is.read(clientIDBytes);
                ByteBuffer wrappedCommand = ByteBuffer.wrap(clientIDBytes);
                this.playerID = wrappedCommand.getInt();


                // Now we have the playerID we can request to join the game on the server side
                // Send across the playerName and team
                String data = (playerName + "/" + team);
                byte[] nameAndTeamBytes = data.getBytes();
                os.write(nameAndTeamBytes);

                // Once confirmation that this has been received we end the process
                byte[] confirmationBytes = new byte[4];
                is.read(confirmationBytes);
                wrappedCommand = ByteBuffer.wrap(confirmationBytes);
                int confirmation = wrappedCommand.getInt();
                if (confirmation == 1) {
                    System.out.println("Player successfully joined");
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Multiplayer not supported locally");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for closing down all threads stemming from this client and forwarding the close request to the handler
     */
    public void close() {
        try {
            sender.close();
            sender.join();
            sendSocket.close();
            System.out.println("ClientSender ended");
            receiver.close();
            receiver.join();
            handler.end();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for sending requests that do not require parameters
     * @param action The action that the server needs to process
     */
    public void send(ActionList action) {
        switch (action.toString()) {
            case "ATTACK": // 0
                sender.send(new Integer[]{0});
                break;
            case "DROPITEM": // 1
                sender.send(new Integer[]{1});
                break;
            case "RELOAD": // 2
                sender.send(new Integer[]{2});
                break;
        }
    }

    /**
     * Method for sending requests that do require a parameter
     * @param action The action that the server needs to process
     * @param parameter The value that this action takes
     */
    public void send(ActionList action, int parameter) {
        switch (action.toString()) {
            case "CHANGEITEM": // 3
                sender.send(new Integer[]{3, parameter});
                break;
            case "MOVEMENT": // 4
                sender.send(new Integer[]{4, parameter});
                break;
            case "TURN": //5
                sender.send(new Integer[]{5, parameter});
        }
    }

    /**
     * Method to update the next assignable IP address
     */
    private void updateLowestAvailableAddress() {
        lowestAvailableAddress++;
    }

    /**
     * Method to update the next assignable port
     */
    private void updateLowestAvailablePort() {
        lowestAvailablePort += 2;
    }
}
