package server;

import client.data.GameView;
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

public class ServerReceiver extends Thread{
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
        buffer = new byte[255];
        running = true;
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
            setInterfaces(listenSocket);
            while (running) {
                // packet to receive incoming messages
                packet = new DatagramPacket(buffer, buffer.length);
                // blocking method that waits until a packet is received
                listenSocket.setSoTimeout(10000);
                listenSocket.receive(packet);
                // Read the received packet into a request

                requests = new ClientRequests(numOfPlayers);

                // Send the request to the Engine
                handler.sendClientRequest(requests);


                // Need some sort of exit sequence for the server
                if (true) {

                    // Waits for the sender to finish its processes before ending itself
                    sender.join();
                    // Running = false so the Thread ends gracefully
                    running = false;
                    System.out.println("Ending server receiver");
                }
            }
        } catch (SocketTimeoutException e) {
        	System.out.println("Timeout");
        	e.printStackTrace();
        }
          catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
