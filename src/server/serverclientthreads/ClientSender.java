package server.serverclientthreads;

import shared.Pose;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

public class ClientSender extends Thread {
    MulticastSocket senderSocket;
    InetAddress senderAddress;
    Boolean running;
    DatagramPacket packet;
    int port;
    byte[] buffer;
    Scanner scan;

    ClientSender(InetAddress address, MulticastSocket socket, int port) throws SocketException {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        running = true;
        senderSocket.setInterface(findInetAddress());
        this.start();
    }

    public void run() {
        scan = new Scanner(System.in);
        try {
            while (running) {
                // Asks for user input
                System.out.print(">> ");
                String userInput = scan.nextLine();

                // Creates and sends the packet to the server
                buffer = userInput.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);

                // Each time we send a packet we need to ensure the interfaces match up


                senderSocket.send(packet);


                // If the messages is exit then the Thread should terminate
                if (userInput.equals("exit")) {
                    running = false;
                }
                Thread.yield();
            }
        } catch (IOException e1) {

        }
    }
    
    public void send(Pose pose) {
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
                out.writeObject(pose);
                //flushes anything in the OOutputStream
                out.flush();
                // Writes the info in the BOutputStream to a byte array to be transmitted
                byte[] buffer = bos.toByteArray();
                packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
                senderSocket.send(packet);

            } finally {
                try {
                    bos.close();
                    System.out.println("SENT");
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