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

/**
 * Thread to handle sending of GameViews to the clients
 */
public class ServerSender extends Thread {

    /**
     * Socket to send GameViews to the client
     */
    private MulticastSocket sendSocket;

    /**
     * Address the server will send GameViews to
     */
    private InetAddress sendAddress;

    /**
     * boolean to tell the thread to keep running
     */
    private Boolean running;

    /**
     * packet used to receive and send information when joining game
     */
    private DatagramPacket packet;

    /**
     * port the server will send gameViews across
     */
    private int port;

    /**
     * byte array to hold the GameView to be sent to clients
     */
    private byte[] buffer;

    /**
     * integar value used to know when the buffer size for the client must increase
     */
    private int maxBufferSize;

    /**
     * Constructor
     * @param address Address to send the GameViews to
     * @param socket Socket used to send the GameViews to the clients
     * @param port Port used to Send the Gameviews to the clients on
     */
    public ServerSender(InetAddress address, MulticastSocket socket, int port) {
        this.sendAddress = address;
        this.sendSocket = socket;
        this.port = port;
        running = true;
        this.start();
    }

    /**
     * Method to get if the thread is still running
     * @return if the thread is still running
     */
    public boolean getRunning() {
        return running;
    }

    /**
     * Ends the thread by setting running to false
     */
    public void close() {
        this.running = false;
        this.interrupt();
        sendSocket.close();
    }

    /**
     * run method that keeps the thread alive
     */
    public void run() {
        while (running) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("closing ServerSender");
    }

    /**
     * Method to send the GameView to the clients
     * @param view The GameView to be sent to the clients
     */
    public void send(GameView view) {
        try {
            // byte array stream to turn the objects to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // Output stream that will hold the object
            ObjectOutputStream out = new ObjectOutputStream(bos);
            try {
                // Writes the view object into the byte
                out.writeObject(view);
                out.flush();
                int buffersize = bos.toByteArray().length;
                // check if the buffersize needs to be increased for the client
                if(buffersize > maxBufferSize){
                    buffer = ByteBuffer.allocate(8).putInt(1).putInt(buffersize).array();
                    maxBufferSize = buffersize;
                    packet = new DatagramPacket(buffer, buffer.length, sendAddress, port);
                    sendSocket.send(packet);
                }
                // set the buffer to the new view
                buffer = bos.toByteArray();
                // Create a packet and send it to the clients
                packet = new DatagramPacket(buffer, buffer.length, sendAddress, port);
                sendSocket.send(packet);
            } finally {
                try {
                    bos.close();
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            // TODO Will be set on a loop to send every ______ seconds

        } catch (SocketException e) {
            System.out.println("Serversender ending");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


