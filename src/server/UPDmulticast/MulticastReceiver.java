package server.UPDmulticast;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

public class MulticastReceiver {
        public static void main(String[] args) throws Exception {
            int mcPort = 12345;
            String mcIPStr = "230.1.1.1";
            MulticastSocket mcSocket = null;
            InetAddress mcIPAddress = null;
            mcIPAddress = InetAddress.getByName(mcIPStr);
            mcSocket = new MulticastSocket(mcPort);
            System.out.println("Multicast Receiver running at:"
                    + mcSocket.getLocalSocketAddress());
            mcSocket.joinGroup(mcIPAddress);

            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

            System.out.println("Waiting for a  multicast message...");
            mcSocket.receive(packet);
            String msg = new String(packet.getData(), packet.getOffset(),
                    packet.getLength());
            System.out.println("[Multicast  Receiver] Received:" + msg);

            mcSocket.leaveGroup(mcIPAddress);
            mcSocket.close();
        }
    }


