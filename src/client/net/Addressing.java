package client.net;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Addressing {

    // Sets the correct network interface to be communicated with
    public static void setInterfaces(MulticastSocket listenSocket) {
        Enumeration<NetworkInterface> interfaces;
        try {
            //check for interfaces that arent loopbacks
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback())
                    continue;
                // if that interface has an address that is for the ethernet port then add it to the socket
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    System.out.println(iface.getDisplayName());
                    InetAddress addr = addresses.nextElement();
                    if (iface.getDisplayName().equals("Realtek PCIe GBE Family Controller") ||
                            iface.getDisplayName().equals("Realtek Gaming GbE Family Controller")) {
                        System.out.println("Setting interface");
                        listenSocket.setInterface(addr);
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
