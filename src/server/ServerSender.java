package server;

import client.data.GameView;
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

    public ServerSender(InetAddress address, MulticastSocket socket, int port) throws SocketException {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        running = true;
        senderSocket.setInterface(findInetAddress());
        this.start();
    }

    public void run() {
        while (running) {
            Thread.yield();

        }
        System.out.println("Ending server sender");
    }

    // sends a confirmation back to the client that the message has been received
    // in future will be used to send the continuous game state to the user/users
    public void send(GameView view) {
        try {
            // Turn the received GameView into a byte array
            // Output Stream for the byteArray. Will grow as data is added
            // Allows the object to be written to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // Output stream that will hold the object
            ObjectOutputStream out = null;
            try {
                // OOutputStream to read the GameView into the byteArray
                out = new ObjectOutputStream(bos);
                // Writes the view object into the BAOutputStream
                out.writeObject(view);
                //flushes anything in the OOutputStream
                out.flush();
                // Writes the info in the BOutputStream to a byte array to be transmitted
                byte[] yourBytes = bos.toByteArray();
                packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
                senderSocket.send(packet);

            } finally {
                try {
                    bos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            // TODO Will be set on a loop to send every ______ seconds

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
}

