package server;

import data.Pose;
import data.entity.player.Teams;
import data.map.MapList;
import server.game_engine.HasEngine;
import server.game_engine.ProcessGameState;
import server.request.ClientRequests;
import server.request.Request;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client. Also controls server behaviour

public class ServerReceiver extends Thread {
    MulticastSocket listenSocket;
    InetAddress listenAddress;
    ServerSender sender;
    Boolean running;
    DatagramPacket packet;
    byte[] buffer;
    ClientRequests requests;
    int numOfPlayers;
    Server handler;


    public ServerReceiver(InetAddress address, MulticastSocket listenSocket, ServerSender sender, Server handler) {
        this.listenSocket = listenSocket;
        this.listenAddress = address;
        this.sender = sender;
        this.handler = handler;
        buffer = new byte[10000];
        running = true;
        setInterfaces(listenSocket);
        this.start();
    }

    public void setInterfaces(MulticastSocket listenSocket) {
        Enumeration<NetworkInterface> interfaces;
        // attempt to set the sockets interface to all the addresses of the machine
        try {
            // for all interfaces that are not loopback or up get the addresses associated with thos
            // interfaces and set the sockets interface to that address
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

        }
    }

    public void run() {
        try {
            listenSocket.joinGroup(listenAddress);

            while (running) {
                // packet to receive incoming messages
                packet = new DatagramPacket(buffer, buffer.length);
                // blocking method that waits until a packet is received
                listenSocket.receive(packet);
                System.out.println("Packet received length" + packet.getLength());
                System.out.println("Packet data length" + packet.getData().length);

                System.out.println("Packet recieved by ServerReceiver");

                // Creates a bytearrayinputstream from the received packets data
                ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
                //ObjectinputStream to turn the bytes back into an object.
                ObjectInputStream in = null;
                try {
                    in = new ObjectInputStream(bis);

                    Object received =  in.readObject();
                    Pose pose = new Pose();
                    pose.setDirection((int)received);
                    int direction = pose.getDirection();
                    System.out.println("Direction from server" + direction);
                    requests = new ClientRequests(numOfPlayers);
                    requests.playerRequestMovement(0, direction);
                    // Send the request to the Engine
                    handler.sendClientRequest(requests);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
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
            // Waits for the sender to finish its processes before ending itself
            sender.join();
            System.out.println("Ending server receiver");
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
