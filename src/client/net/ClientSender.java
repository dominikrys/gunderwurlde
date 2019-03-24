package client.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Thread that handles the sending of requests to the server
 */
public class ClientSender extends Thread {

    /**
     * Socket to send client requests to the sever
     */
    private MulticastSocket senderSocket;

    /**
     * Address to send client requests to
     */
    private InetAddress senderAddress;

    /**
     * boolean to state if the thread should keep running
     */
    private Boolean running;

    /**
     * packet to be transmitted to the server
     */
    private DatagramPacket packet;

    /**
     * Port to send client requests on
     */
    private int port;

    /**
     * byte array to hold information to be sent
     */
    private byte[] buffer;

    /**
     * identification so the server knows who sent the packet
     */
    private int playerID;

    /**
     * Constructor
     * @param address Address to send client requests to
     * @param socket Socket to send client requests to the sever
     * @param port Port to send client requests on
     * @param playerID identification so the server knows who sent the packet
     */
    public ClientSender(InetAddress address, MulticastSocket socket, int port, int playerID) {
        this.senderAddress = address;
        this.senderSocket = socket;
        this.port = port;
        this.playerID = playerID;
        running = true;
        this.start();
    }

    /**
     * Method to say if the thread is still running
     * @return true if the server is still running
     */
    public boolean getRunning() {
        return running;
    }

    /**
     * run method to keep the thread alive
     */
    public void run() {
        while (running) {
            Thread.yield();
        }
    }

    /**
     * Method to send requests that dont need parameters to the server
     * @param action
     */
    public void send(Integer[] action) {
        try {
            // byte stream to turn the objects into a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // object stream to collect the objects together
            ObjectOutputStream out = new ObjectOutputStream(bos);
            try {
                // Write the objects to the object stream
                out.writeObject(action);
                out.writeInt(playerID);
                out.flush();
                // convert the objects to a byte array, packet them and send it to the server
                buffer = bos.toByteArray();
                packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);
                senderSocket.send(packet);
            } finally {
                out.close();
                bos.close();
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("unable to send message");
        }
    }

    /**
     * method to set the value of the bool running
     * @param value the new value of running
     */
    public void setRunning(boolean value){
        running = value;
    }

    /**
     * Method to end the thread
     */
    public void close(){
        running = false;
    }
}