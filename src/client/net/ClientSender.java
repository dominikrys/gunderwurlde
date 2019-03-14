package client.net;

import shared.lists.Teams;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class ClientSender extends Thread {
    MulticastSocket senderSocket;
    InetAddress senderAddress;
    Boolean running;
    DatagramPacket packet = null;
    int port;
    byte[] buffer;
    int playerID;

    public ClientSender(InetAddress address, MulticastSocket socket, int port, int playerID) throws SocketException {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        this.playerID = playerID;
        running = true;
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
            //Create streams that will turn the data into a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            try {
                // pass the ByteArray stream into the object stream
                out = new ObjectOutputStream(bos);
                // Write the actions to be performed followed by the client performing them to the byte array
                out.writeObject(action);
                out.writeInt(playerID);
                out.flush();
                // set the buffer to the array and send to the server
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
            DataOutputStream out;
            out = new DataOutputStream(bos);
            String data = (playerName + "/" + playerTeam);
            // Instead of actions followed by clientID
            // write the joinGame command followed by the required data
            out.writeInt(99);
            out.writeUTF(data);
            out.flush();
            buffer = bos.toByteArray();
            // send the packet
            packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
            senderSocket.send(packet);
            System.out.println("Join game request sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}