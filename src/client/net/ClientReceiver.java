package client.net;


import client.Client;
import client.Settings;
import client.render.GameRenderer;
import shared.view.GameView;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

// Gets messages from other clients via the server (by the
// serverclientthreads.ServerSender thread).

public class ClientReceiver extends Thread {
    private MulticastSocket listenSocket;
    private InetAddress listenAddress;
    private Boolean running;
    private DatagramPacket packet;
    private byte[] buffer;
    private Client client;
    private GameRenderer renderer;
    private Settings settings;

    public ClientReceiver(GameRenderer renderer, InetAddress listenAddress, MulticastSocket socket, Client client, Settings settings) {
        this.listenSocket = socket;
        this.listenAddress = listenAddress;
        this.client = client;
        this.renderer = renderer;
        this.settings = settings;
        buffer = new byte[2000000];
        running = true;
        this.start();
    }

    public boolean getRunning() {
        return running;
    }

    public void stopRunning() {
        this.running = false;
    }

    public void run() {
        System.out.println("CLIENT RECEIVER RUNNING");
        try {
            while (running) {
                // creates a packet and waits to receive a message from the server
                packet = new DatagramPacket(buffer, buffer.length);
                // blocking method waiting to receive a message from the server
                System.out.println("Listening for request");
                listenSocket.receive(packet);
                System.out.println("Client received a packet");
                if(packet.getLength() == 8){
                    byte[] commandBytes = Arrays.copyOfRange(packet.getData(), 0, 4);
                    byte[] ValueBytes = Arrays.copyOfRange(packet.getData(), 4, 8);
                    ByteBuffer wrappedCommand = ByteBuffer.wrap(commandBytes);
                    int command = wrappedCommand.getInt();
                    ByteBuffer wrappedValue = ByteBuffer.wrap(ValueBytes);
                    int value = wrappedValue.getInt();
                    if(command == 1){
                        buffer = new byte[value];
                    }
                    continue;
                }
                else {
                    // System.out.println("Size of received packet" + packet.getData().length);
                    // Creates a bytearrayinputstream from the received packets data
                    ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
                    //ObjectinputStream to turn the bytes back into an object.
                    ObjectInputStream in = null;
                    GameView view = null;
                    try {
                        // System.out.println("Client received new gameview");
                        in = new ObjectInputStream(bis);
                        view = (GameView) in.readObject();
                        client.setGameView(view, settings);
                        // renderer.updateGameView(view);
                        // renderer.getKeyboardHandler().setGameView(view);
                        // renderer.getMouseHandler().setGameView(view);
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (EOFException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            System.out.println("Ending client receiver");
        }
        catch (SocketException e) {
            System.out.println("Socket closed unexpectedly");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setRunning(boolean value){
        running = value;
    }
}
