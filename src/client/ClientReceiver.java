package client;


import java.io.*;
import java.net.*;
import java.util.Enumeration;

import client.data.entity.GameView;

// Gets messages from other clients via the server (by the
// serverclientthreads.ServerSender thread).

public class ClientReceiver extends Thread {
    MulticastSocket listenSocket;
    InetAddress listenAddress;
    Boolean running;
    DatagramPacket packet;
    byte[] buffer;
    Client client;

    ClientReceiver(InetAddress listenAddress, MulticastSocket listenSocket, Client client) {
        this.listenSocket = listenSocket;
        this.listenAddress = listenAddress;
        this.client = client;
        buffer = new byte[20000];
        running = true;
        setInterfaces(listenSocket);
        this.start();
    }

    public void setInterfaces(MulticastSocket listenSocket) {
        Enumeration<NetworkInterface> interfaces = null;
        // attempt to set the sockets interface to all the addresses of the machine
        try {
            // for all interfaces that are not loopback or up get the addresses associated with thos
            // interfaces and set the sockets interface to that address
//			}
            interfaces = NetworkInterface.getNetworkInterfaces();
            //while (interfaces.hasMoreElements()) {
            NetworkInterface iface = null;
            if (interfaces.hasMoreElements()) {
                iface = interfaces.nextElement();
            }

            if (!iface.isLoopback() || iface.isUp()) {
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                if (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    listenSocket.setInterface(addr);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        try {
            listenSocket.joinGroup(listenAddress);
            while (running) {

                // creates a packet and waits to receive a message from the server
                packet = new DatagramPacket(buffer, buffer.length);
                // blocking method waiting to receive a message from the server
                listenSocket.receive(packet);
                System.out.println("Packet received by clientreceiver");
                System.out.println("Size of received packet" + packet.getData().length);
                System.out.println("Packet length " + packet.getLength());
                // Creates a bytearrayinputstream from the received packets data
                ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
                //ObjectinputStream to turn the bytes back into an object.
                ObjectInputStream in = null;
                GameView view = null;
                try {
                    in = new ObjectInputStream(bis);
                    view = (GameView)in.readObject();
                    System.out.println("Updating gameview");
                    client.setGameView(view);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (EOFException ex) {
                    ex.printStackTrace();
                    }
                    finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                // TODO how do threads exit?
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
