package client.net;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Class containing static methods to assign interfaces to created sockets
 */
public class Addressing {

    /**
     * Method that sets the interfaces for the socket so it communicates across the ethernet port
     * @param socket
     */
    public static void setInterfaces(MulticastSocket socket) {
        try {
            // Get all network interfaces for this machines
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // For all interfaces that arent loopback
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback())
                    continue;
                // Get all addresses for that interface
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                // Frr all addresses if it matches an ethernet display name, add it to the socket
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (iface.getDisplayName().equals("Realtek PCIe GBE Family Controller") ||
                            iface.getDisplayName().equals("Realtek Gaming GbE Family Controller")) {
                        socket.setInterface(addr);
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket closed in addressing");
        }
    }

    /**
     * A method to get the address of the machine that is hosting the server
     * @return The address to be chosen as the TCP address
     */
    public static InetAddress getAddress(){
        try {
            // Get all network interfaces for this machines
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // For all interfaces that arent loopback
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback())
                    continue;
                // Get all addresses for that interface
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                // If it matches an ethernet display name, then return it as it can be communicated across
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (iface.getDisplayName().equals("Realtek PCIe GBE Family Controller") ||
                            iface.getDisplayName().equals("Realtek Gaming GbE Family Controller")) {
                        return addr;
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket closed in addressing");
        }
        return null;
    }
}
