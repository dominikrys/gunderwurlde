package client.net;

import shared.lists.Teams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ClientSender extends Thread {
    MulticastSocket senderSocket;
    InetAddress senderAddress;
    Boolean running;
    DatagramPacket packet = null;
    int port;
    byte[] buffer;
    int clientID;

    public ClientSender(InetAddress address, MulticastSocket socket, int port, int clientID) throws SocketException {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        this.clientID = clientID;
        running = true;
        senderSocket.setInterface(Addressing.findInetAddress());
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
            ObjectOutputStream out;
               try {
                out = new ObjectOutputStream(bos);
                out.writeObject(action);
                out.write(clientID);
                out.flush();
                buffer = bos.toByteArray();
                packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
                senderSocket.send(packet);
            } finally {
                try {
                    bos.close();
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
            // TODO Will be set on a loop to send every ______ seconds

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinGame(String playerName, Teams playerTeam){
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            out = new ObjectOutputStream(bos);
            out.writeInt(99);
            String data = (playerName + " " + playerTeam);
            out.writeBytes(data);
            out.flush();
            buffer = data.getBytes();
            packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
            senderSocket.send(packet);
            System.out.println("Join game request sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}