package client.net;


import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;

import client.Client;
import client.render.GameRenderer;
import shared.view.GameView;

// Gets messages from other clients via the server (by the
// serverclientthreads.ServerSender thread).

public class ClientReceiver extends Thread {
    MulticastSocket listenSocket;
    InetAddress listenAddress;
    Boolean running;
    DatagramPacket packet;
    byte[] buffer;
    Client client;
    GameRenderer renderer;

    public ClientReceiver(GameRenderer renderer, InetAddress listenAddress, MulticastSocket listenSocket, Client client) {
        this.listenSocket = listenSocket;
        this.listenAddress = listenAddress;
        this.client = client;
        this.renderer = renderer;
        buffer = new byte[17000];
        running = true;
        Addressing.setInterfaces(listenSocket);
        this.start();
    }

    public boolean getRunning() {
        return running;
    }

    public void stopRunning() {
        this.running = false;
    }




    public void run() {
        try {
            listenSocket.joinGroup(listenAddress);
            while (running) {

                // creates a packet and waits to receive a message from the server
                packet = new DatagramPacket(buffer, buffer.length);
                // blocking method waiting to receive a message from the server
                listenSocket.receive(packet);
                System.out.println("Receiver packet size is:" + packet.getLength());
                if(packet.getLength() == 4){
                    System.out.println("Updating buffer size");
                    ByteBuffer wrapped = ByteBuffer.wrap(packet.getData());
                    int bufferSize = wrapped.getInt();
                    buffer = new byte[bufferSize];
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
                        client.setGameView(view);
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
}
