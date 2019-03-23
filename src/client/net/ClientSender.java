package client.net;

import java.io.ByteArrayOutputStream;
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

    public void run() {
        while (running) {
            Thread.yield();
        }
        System.out.println("Closing ClientSender");
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
            //e.printStackTrace();

            System.out.println("unable to send message");
        }
    }

    public void setRunning(boolean value){
        running = value;
    }

    public void close(){
        running = false;

    }
}