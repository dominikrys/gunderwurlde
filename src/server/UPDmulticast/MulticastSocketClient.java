package server.UPDmulticast;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class MulticastSocketClient {
    
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;

    public static void main(String[] args) throws UnknownHostException, SocketException {
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
        // Create a buffer of bytes, which will be used to store
        // the incoming bytes containing the information from the server.
        // Since the message is small here, 256 bytes should be enough.
        byte[] buf = new byte[256];
        
        // Create a new Multicast socket (that will allow other sockets/programs
        // to join it as well.
        try (MulticastSocket clientSocket = new MulticastSocket(PORT)){
            //Joint the Multicast group.
            clientSocket.setInterface(addr);
            clientSocket.joinGroup(address);
            System.out.println(clientSocket.isConnected());
     
            while (true) {
                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(msgPacket);

                String msg = new String(buf, 0, buf.length);
                System.out.println("Socket 1 received msg: " + msg);
            }
        } catch (IOException ex) {
          ex.printStackTrace();
        }
    }
}