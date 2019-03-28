package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import client.input.CommandList;
import client.net.Addressing;
import client.net.ClientReceiver;
import client.net.ClientSender;
import client.net.NetworkInformation;
import client.render.GameRenderer;
import javafx.stage.Stage;
import server.engine.state.map.MapReader;
import shared.lists.Team;
import shared.view.GameView;
import shared.view.TileView;


/**
 * Class to initialise the sender, receiver threads and join the game
 */
public class Client extends Thread {

    /**
     * The port players will request the TCP address of the server machine on
     */
    private static final int JOINPORT = 8080;

    /**
     * The port the player will use to join the game
     */
    private static final int TCPPORT = 8081;

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
     * The server machines IP address
     */
    private InetAddress tcpAddress;

    /**
     * The port the client will receive gameView across
     */
    private int listenPort;

    /**
     * The port the client will send requests across
     */
    private int sendPort;

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
     * TileView array to hold the map to be generated
     */
    private TileView[][] tileMap;

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

    /**
     * Stage to render to
     */
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
     * Connection type
     */
    private ConnectionType connectionType;

    /**
     * boolean to tell if the client should begin the close sequence
     */
    private boolean shouldClose = false;

    /**
     * Constructor for single player or multiplayer host
     *
     * @param stage
     * @param handler        object that created this client. Used to end the threads on close
     * @param settings       object to hold previously saved settings e.g. sound settings
     * @param playerID       The identification value for a player
     * @param connectionType Connection type
     */
    public Client(Stage stage, GameHandler handler, Settings settings, int playerID, ConnectionType connectionType) {
        try {
            // Assign the value for the ports and update the next available port
            this.listenPort = NetworkInformation.getLowestAvailablePort();
            this.sendPort = NetworkInformation.getLowestAvailablePort() + 1;
            // Assign the values for the addresses and update the next available address
            this.listenAddress = InetAddress.getByName("230.0.0." + NetworkInformation.getLowestAvailableIPAddress());
            this.senderAddress = InetAddress.getByName("230.0.1." + NetworkInformation.getLowestAvailableIPAddress());
            this.stage = stage;
            this.handler = handler;
            this.settings = settings;
            this.playerID = playerID;
            this.connectionType = connectionType;
            firstView = true;
            // Check that the game isn't supposed to close
            if(shouldClose){
                this.close(false);
            }
        } catch (UnknownHostException e) {
            // Error case where the adress specified in not a real address
            this.close(false);
        }
    }

    /**
     * Constructor for multiplayer join
     *
     * @param stage
     * @param handler        object that created this client. Used to end the threads on close
     * @param settings       object to hold previously saved settings e.g. sound settings
     * @param ipValue        The IP address the server is sending on
     * @param portValue      The port the server is sending across
     * @param playerName     The name of the player playing the game
     * @param team           The team of the player playing the game
     * @param connectionType Connection ype
     */
    public Client(Stage stage, GameHandler handler, Settings settings, String ipValue, int portValue, String playerName,
                  Team team, ConnectionType connectionType) {
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
            this.connectionType = connectionType;
            joinGame();
            if(shouldClose){
                this.close(false);
            }
        } catch (UnknownHostException e) {
            this.close(false);
        }
    }

    /**
     * Method to start sender and receiver threads for the client
     */
    public void run() {
        try {
            listenSocket = new MulticastSocket(listenPort);
            Addressing.setInterfaces(listenSocket);
            listenSocket.joinGroup(listenAddress);
            sendSocket = new MulticastSocket();
            Addressing.setInterfaces(sendSocket);
            sender = new ClientSender(senderAddress, sendSocket, sendPort, playerID);
            sender.setName("ClientSender");
            receiver = new ClientReceiver(listenSocket, this, settings);
            receiver.setName("ClientReceiver");
            if(shouldClose){
                this.close(false);
            }
        } catch (IOException e) {
            this.close(false);
        }
    }

    /**
     * Method to set the GameView for the client so that new graphics appear
     *
     * @param view     the GameView that will be displayed to the screen
     * @param settings object to hold previously saved settings e.g. sound settings
     */
    public void setGameView(GameView view, Settings settings) {
        // sets the value for view
        this.view = view;
        // if this is the first GameView then create a renderer
        if (firstView) {
            firstView = false;
            this.tileMap = MapReader.readMapView(view.getMapName());
            this.view.setTileMap(tileMap);
            renderer = new GameRenderer(stage, this.view, playerID, settings, this, connectionType);
            renderer.getKeyboardHandler().setGameHandler(this);
            renderer.getMouseHandler().setGameHandler(this);
            renderer.run();
        }
        //else just update the GameView
        else {
            updateTileMap();
            this.view.setTileMap(tileMap);
            renderer.updateGameView(this.view);
        }
    }
    
    /**
     * Method for updating the tileMap with the changes from the GameView
     */
    private void updateTileMap() {
        for (Map.Entry<int[], TileView> tileChange : view.getTileViewChanges().entrySet()) {
            int[] cords = tileChange.getKey();
            tileMap[cords[0]][cords[1]] = tileChange.getValue();
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
            // If socket loses connection within 5 seconds it closes the socket
            joinGameSocket.setSoTimeout(5000);

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
                        System.out.println("tcpAddress: " + tcpAddress.toString());
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
                // We have received the servers IP address so can begin TCP communication
                tcpSocket = new Socket(tcpAddress, TCPPORT);
                // If the tcp socket loses connection for 5 seconds it closes
                tcpSocket.setSoTimeout(5000);
                InputStream is = tcpSocket.getInputStream();
                OutputStream os = tcpSocket.getOutputStream();

                // Listen for playerID to be sent
                byte[] clientIDBytes = new byte[4];
                is.read(clientIDBytes);
                ByteBuffer wrappedCommand = ByteBuffer.wrap(clientIDBytes);
                this.playerID = wrappedCommand.getInt();

                // Begin the join game Process
                String data = (playerName + "/" + team);
                byte[] nameAndTeamBytes = data.getBytes();
                os.write(nameAndTeamBytes);

                // Listen for confirmation joinGame request has been passed on
                byte[] confirmationBytes = new byte[4];
                is.read(confirmationBytes);
                wrappedCommand = ByteBuffer.wrap(confirmationBytes);
                int confirmation = wrappedCommand.getInt();
                if (confirmation == 1) {
                    System.out.println("Player successfully joined");
                } else {
                    System.out.println("player failed to join");
                }
                is.close();
                os.close();
            }
            // Close sockets and streams
            joinGameSocket.close();
            tcpSocket.close();
        } catch(NullPointerException ex){
            // Error case when Addressing class fails to set ports;
            this.close(false);
        }
        catch (UnknownHostException e) {
            // Error occurs when attempting to create multiplayer without a switch
            // InetAddress starts using IPV6 instead of IPV4 for some reason
            // No idea why
            // TODO if time find out how to fix
            System.out.println("Multiplayer not supported locally");
            this.close(false);
        } catch (SocketTimeoutException ex) {
            System.out.println("Failed to reach server, Is the IP and port correct");
            this.close(false);
        } catch (SocketException e) {
            this.close(false);
        } catch (IOException e) {
            // Any other error
            this.close(false);
        }
    }

    /**
     * Method to end this thread
     */
    public void close(boolean passUp) {
        try {
            shouldClose = true;
            if(joinGameSocket != null){
                joinGameSocket.close();
            }
            if(tcpSocket != null){
                tcpSocket.close();
            }
            if (renderer != null && renderer.isRunning()) {
                renderer.stop();
            }
            if(sender != null) {
                // Close sender first
                sender.close();
                // When sender has joined close the socket
                sender.join();
            }
            if(receiver != null) {
                // Close receiver
                receiver.close();
                // when receiver has joined tell handler to close server if exists
                receiver.join();
                // no need to close socket as its closed within receiver
            }
            // check if close needs to be passed up the trail
            // if close requests sent from renderer then pass up
            // else dont pass up
            if(passUp) {
                handler.end();
            }

        } catch (InterruptedException e) {
            // Threads failed to join?
            e.printStackTrace();
        } catch (IOException e) {
            // Any other error
            e.printStackTrace();
        }
    }


    /**
     * Method to send requests that do no require parameters to the server
     *
     * @param action the list of actions to be sent to the server
     */
    public void send(CommandList action) {
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
            case "PAUSED": //7
                sender.send(new Integer[]{7});
                break;
            case "RESUME": //8
                sender.send(new Integer[]{8});
        }
    }

    /**
     * Method to send requests that do require parameters to the server
     *
     * @param action    the list of actions to be sent to the server
     * @param parameter the parameters that actionlist requires
     */
    public void send(CommandList action, int parameter) {
        switch (action.toString()) {
            case "CHANGEITEM": // 3
                sender.send(new Integer[]{3, parameter});
                break;
            case "MOVEMENT": // 4
                sender.send(new Integer[]{4, parameter});
                break;
            case "TURN": //5
                sender.send(new Integer[]{5, parameter});
                break;
            case "CONSUMABLE": // 6
                sender.send(new Integer[]{6, parameter});
        }
    }
}
