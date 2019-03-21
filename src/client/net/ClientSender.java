package client.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

public class ClientSender extends Thread {
    private MulticastSocket senderSocket;
    private InetAddress senderAddress;
    private Boolean running;
    private DatagramPacket packet = null;
    private int port;
    private byte[] buffer;
    private Scanner scan;

    public ClientSender(InetAddress address, MulticastSocket socket, int port) throws SocketException {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        running = true;
        senderSocket.setInterface(findInetAddress());
        this.start();
    }

    public boolean getRunning() {
        return running;
    }

    public void stopRunning() {
        this.running = false;
    }

    public void run() {
        while (running) {
            Thread.yield();
        }
    }

    public void send(Integer[] action) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
               try {
                out = new ObjectOutputStream(bos);
                // Writes the view object into the BAOutputStream
                out.writeObject(action);
                //flushes anything in the OOutputStream
                out.flush();
                // Writes the info in the BOutputStream to a byte array to be transmitted
                buffer = bos.toByteArray();
                packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
                senderSocket.send(packet);
                // System.out.println("Packet sent from clientSender");
            } finally {
                try {
                    bos.close();
                }
                catch(IOException ex){
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