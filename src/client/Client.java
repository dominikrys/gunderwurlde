package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
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


    private MulticastSocket listenSocket;
    private MulticastSocket sendSocket;
    private MulticastSocket joinGameSocket;
    private Socket tcpSocket;

    // Address to be communicated on
    private InetAddress listenAddress;
    private InetAddress senderAddress;
    private InetAddress joinGameAddress;
    private InetAddress tcpAddress;
    private int lowestAvailableAddress = 1;

    // Ports to be communicated on
    private int listenPort;
    private int sendPort;
    private static final int JOINPORT = 8080;
    private static final int TCPPORT = 8081;
    static int lowestAvailablePort = 4444;

    // Used for sending information
    byte[] buffer;
    DatagramPacket packet;
    Boolean waiting;


    private String playerName;
    private Team team;
    private GameView view;
    private GameRenderer renderer;
    private ClientSender sender;
    private ClientReceiver receiver;
    private Stage stage;
    private boolean firstView;
    private GameHandler handler;
    private Settings settings;
    int playerID;
    boolean idIsSet;
    boolean threadsup;
    boolean joinedGame;


    public Client(Stage stage, GameHandler handler, Settings settings, int playerID) {
       try {
           joinedGame = true;
           this.listenPort = lowestAvailablePort;
           this.sendPort = lowestAvailablePort + 1;
           updateLowestAvailablePort();
           this.listenAddress = InetAddress.getByName("230.0.0." + lowestAvailableAddress);
           this.senderAddress = InetAddress.getByName("230.0.1." + lowestAvailableAddress);
           updateLowestAvailableAddress();
           this.stage = stage;
           this.handler = handler;
           this.settings = settings;
           this.playerID = playerID;
           firstView = true;
           threadsup = false;
           System.out.println("C: constructor finished");
       } catch (UnknownHostException e) {
           e.printStackTrace();
       }
    }

    private void updateLowestAvailableAddress() {
        lowestAvailableAddress++;
    }

    private void updateLowestAvailablePort() {
        lowestAvailablePort += 2;
    }

    public Client(Stage stage, GameHandler handler, Settings settings,String ipValue, int portValue, String playerName, Team team) {
        try {
            joinedGame = false;
            this.playerName = playerName;
            this.team = team;
            this.listenPort = portValue;
            this.sendPort = portValue + 1;
            this.listenAddress = InetAddress.getByName("230.0.0." + ipValue);
            this.senderAddress = InetAddress.getByName("230.0.1." + ipValue);
            System.out.println("C: addresses and ports set");
            this.stage = stage;
            this.handler = handler;
            this.settings = settings;
            idIsSet = true;
            firstView = true;
            threadsup = false;
            joinGame();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            listenSocket = new MulticastSocket(listenPort);
            listenSocket.joinGroup(listenAddress);
            sendSocket = new MulticastSocket();
            Addressing.setInterfaces(sendSocket);
            sender = new ClientSender(senderAddress, sendSocket, sendPort, playerID);
            receiver = new ClientReceiver(renderer, listenAddress, listenSocket, this, settings);
            threadsup = true;
            System.out.println("C: Threads up");
            // Waits for the sender to join as that will be the first thread to close
            sender.join();
            // Waits for the receiver thread to end as this will be the second thread to close
            receiver.join();
            // Closes the socket as communication has finished
            sendSocket.close();
            listenSocket.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameView(GameView view, Settings settings){
        System.out.println("C: Setting GameView");
        this.view = view;
        if (firstView) {
            firstView = false;
            renderer = new GameRenderer(stage, this.view, playerID, settings, this);
            renderer.getKeyboardHandler().setGameHandler(handler);
            renderer.getMouseHandler().setGameHandler(handler);
            renderer.run();
        } else {
            renderer.updateGameView(this.view);
        }
    }

    public void stopThreads(){
        sender.setRunning(false);
        receiver.setRunning(false);
    }

    public void joinGame() {
        try {
            // Start creating the joinGame Information
            joinGameSocket = new MulticastSocket(JOINPORT);
            Addressing.setInterfaces(joinGameSocket);
            joinGameAddress = InetAddress.getByName("230.0.0.0");
            joinGameSocket.joinGroup(joinGameAddress);

            String gameIPAddress = listenAddress.toString();
            buffer = gameIPAddress.getBytes();

            packet = new DatagramPacket(buffer, buffer.length, joinGameAddress, JOINPORT);
            System.out.println("Sending join request ");
            joinGameSocket.send(packet);

            buffer = new byte[32];
            packet = new DatagramPacket(buffer, buffer.length);
            waiting = true;
            while (waiting) {
                joinGameSocket.receive(packet);
                // message that we want to receive will always be 2 IP addresses
                // one for ID, one for value
                // 11 is largest value for sending just 1 IP so has to be greater than that
                if (packet.getLength() > 12) {
                    byte[] recievedBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                    String messageReceived = new String(recievedBytes);
                    String[] split = messageReceived.split("/");
                    System.out.println("tcpADDRESS: " + split[2]);
                    if (("/" + split[1]).equals(listenAddress.toString())) {
                        tcpAddress = InetAddress.getByName(split[2]);
                        waiting = false;
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
                // We have received the servers IP address so can begin TCP communication
                tcpSocket = new Socket(tcpAddress, TCPPORT);
                InputStream is = tcpSocket.getInputStream();
                OutputStream os = tcpSocket.getOutputStream();
                byte[] clientIDBytes = new byte[4];
                is.read(clientIDBytes);
                ByteBuffer wrappedCommand = ByteBuffer.wrap(clientIDBytes);
                this.playerID = wrappedCommand.getInt();
                System.out.println("PlayerID: " + playerID);

                // Begin the join game Process
                String data = (playerName + "/" + team);
                byte[] nameAndTeamBytes = data.getBytes();
                os.write(nameAndTeamBytes);

                byte[] confirmationBytes = new byte[4];
                is.read(confirmationBytes);
                wrappedCommand = ByteBuffer.wrap(confirmationBytes);
                int confirmation = wrappedCommand.getInt();
                if (confirmation == 1) {
                    System.out.println("Player successfully joined");
                    joinedGame = true;
                } else {
                    System.out.println("player failed to join");
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

    public ClientSender getClientSender() {
        return this.sender;
    }

    public void close() {
    }
}
