package server.serverclientthreads;

import java.io.IOException;
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


    public ServerReceiver(InetAddress address, MulticastSocket listenSocket, ServerSender sender) {
        this.listenSocket = listenSocket;
        this.listenAddress = address;
        this.sender = sender;
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
//			interfaces = NetworkInterface.getNetworkInterfaces();
//			while (interfaces.hasMoreElements()) {
//				NetworkInterface iface = interfaces.nextElement();
//				if (iface.isLoopback() || !iface.isUp())
//					continue;
//
//				Enumeration<InetAddress> addresses = iface.getInetAddresses();
//				while(addresses.hasMoreElements()) {
//					InetAddress addr = addresses.nextElement();
//					listenSocket.setInterface(addr);
//					break;
//				}
//				break;
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
                listenSocket.receive(packet);
                // creates a string from the received packet
                String receivedString = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Server received: " + receivedString);
                // If string == exitCode then begin the exit sequence
                if (receivedString.equals("exit")) {
                    // Tells the sender to exit
                    sender.exit(packet);
                    // Waits for the sender to finish its processes before ending itself
                    sender.join();
                    // Running = false so the Thread ends gracefully
                    running = false;
                }
                // If not the exit code simply send the confirmation message to the client
                else {
                    sender.confirm(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
