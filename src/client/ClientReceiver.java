package client;

import client.data.GameView;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

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
        buffer = new byte[255];
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

                // Turns packet of bytes back into GameView
                GameView view = new GameView();


                // Passes the GameView back to the handler
                client.setGameView(view);

                // TODO how do threads exit?
            }
        } catch (SocketException e) {
            System.out.println("Socket closed unexpectedly");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
