package server.net;

import shared.view.GameView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import client.net.Addressing;

public class ServerSender extends Thread {
    MulticastSocket senderSocket;
    InetAddress senderAddress;
    Boolean running;
    DatagramPacket packet;
    int port;
    byte[] buffer;
    int maxBufferSize;

    public ServerSender(InetAddress address, MulticastSocket socket, int port) {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        running = true;
        Addressing.setInterfaces(senderSocket);
        this.start();
    }

    public boolean getRunning() {
        return running;
    }

    public void close() {
        this.running = false;
    }

    public void run() {
        while (running) {
            Thread.yield();
        }
        System.out.println("Closing serversender");
    }

    // sends a confirmation back to the client that the message has been received
    // in future will be used to send the continuous game state to the user/users
    public void send(GameView view) {


        // System.out.println("Server received new GameView");
        try {
            // Turn the received GameView into a byte array
            // Output Stream for the byteArray. Will grow as data is added
            // Allows the object to be written to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // Output stream that will hold the object
            ObjectOutputStream out = null;
            try {
                // OOutputStream to read the GameView into the byteArray
                out = new ObjectOutputStream(bos);
                // Writes the view object into the BAOutputStream
                out.writeObject(view);
                //flushes anything in the OOutputStream
                out.flush();
                // Writes the info in the BOutputStream to a byte array to be transmitted

                int buffersize = bos.toByteArray().length;
                if(buffersize > maxBufferSize){
                    buffer = ByteBuffer.allocate(8).putInt(1).putInt(buffersize).array();
                    maxBufferSize = buffersize;
                    packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
                    senderSocket.send(packet);
                }

                buffer = bos.toByteArray();
                // System.out.println("Size of packet to be sent " + buffer.length);
                packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
                senderSocket.send(packet);

            } finally {
                try {
                    bos.close();
                } catch (IOException ex) {
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

}


