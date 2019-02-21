package client.net;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Addressing {

    public static void setInterfaces(MulticastSocket listenSocket) {
        Enumeration<NetworkInterface> interfaces;
        // TODO Turn on laptop and check for ethernet address
        // TODO ensure that ethernet address is the chosen address for both desktop and laptop
        // attempt to set the sockets interface to all the addresses of the machine
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback())
                    continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if(iface.getDisplayName().equals("Realtek PCIe GBE Family Controller")){
                        System.out.println("Interface being set");
                        listenSocket.setInterface(addr);
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static NetworkInterface getInterface() {
        Enumeration<NetworkInterface> interfaces;
        NetworkInterface iface = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                iface = interfaces.nextElement();
                if (iface.isLoopback())
                    continue;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return iface;
    }
}
