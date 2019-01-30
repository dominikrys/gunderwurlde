package server.UPDmulticast;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class MulticastSocketServer {
    
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;

    public static void main(String[] args) throws UnknownHostException, InterruptedException, SocketException {
        // Get the address that we are going to connect to.
        InetAddress address = InetAddress.getByName(INET_ADDR);
        InetAddress addr = null;

        Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
        while(faces.hasMoreElements()) {
            NetworkInterface iface = faces.nextElement();
            if(iface.isLoopback() | !iface.isUp()) continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while(addresses.hasMoreElements()) {
                addr = addresses.nextElement();
                if(INET_ADDR.equals(addr.toString()))
                    break;
            }
        }
     
        // Open a new DatagramSocket, which will be used to send the data.
        try (MulticastSocket serverSocket = new MulticastSocket()) {
            serverSocket.setInterface(addr);
            for (int i = 0; i < 5; i++) {
                String msg = "Sent message no " + i;

                // Create a packet that will contain the data
                // (in the form of bytes) and send it.
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, address, PORT);
                serverSocket.send(msgPacket);

                System.out.println("Server sent packet with msg: " + msg);
                Thread.sleep(500);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}