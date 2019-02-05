package client;

import java.io.*;
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