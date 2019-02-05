package server;

import server.game_engine.ProcessGameState;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;


public class ServerSender extends Thread {
    MulticastSocket senderSocket;
    InetAddress senderAddress;
    Boolean running;
    DatagramPacket packet;
    int port;
    byte[] buffer;
    Scanner scan;

    public ServerSender(InetAddress address, MulticastSocket socket, int port, ProcessGameState gameState) throws SocketException {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        running = true;
        senderSocket.setInterface(findInetAddress());
        this.start();

    }

    public void run() {
        while (running) {
            // While loop is running it takes up CPU cycles unnecessarily
            // Thread.onSpinWait gives more CPU time to other threads
            Thread.yield();
        }
        System.out.println("Ending server sender");
    }

    // sends a confirmation back to the client that the message has been received
    // in future will be used to send the continuous game state to the user/users
    public void confirm(DatagramPacket received) {
        try {
            // The address of the message to be sent to
            // Creates the string from the received packet
            String confirm = (new String(received.getData(), 0, received.getLength()));
            buffer = confirm.getBytes();
            // Creates a new packet to be sent to the address specified
            packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
            senderSocket.send(packet);


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InetAddress findInetAddress() {
        InetAddress addr = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            //while (interfaces.hasMoreElements()) {
            NetworkInterface iface = null;
            if (interfaces.hasMoreElements()) {
                iface = interfaces.nextElement();
            }

            if (!iface.isLoopback() || iface.isUp()) {
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                if (addresses.hasMoreElements()) {
                    addr = addresses.nextElement();
                }
            }
        } catch (SocketException e) {

        }
        return addr;
    }

    // method used only for testing exit works
    // will be replaced in future clean up
    public void exit(DatagramPacket received) {
        InetAddress address;
        byte[] buffer;
        DatagramPacket packet;
        try {
            // string to be sent to the clientReceiver to end it
            String exitCommand = ("exit");
            buffer = exitCommand.getBytes();
            address = received.getAddress();
            port = received.getPort();
            packet = new DatagramPacket(buffer, buffer.length, address, port);
            senderSocket.send(packet);
            // Running is set to false so that this thread will end gracefully
            running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

