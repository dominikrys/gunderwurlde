package server.net;

import server.Server;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import client.net.Addressing;
import shared.lists.Team;

/**
 * Thread to handle receiving client requests
 */
public class ServerReceiver extends Thread {

    /**
     * Socket to receive Client requests on
     */
    private MulticastSocket listenSocket;

    /**
     * Boolean to tell if the thread should keep running
     */
    private Boolean running;

    /**
     * packet used to accept incoming client requests
     */
    private DatagramPacket packet;

    /**
     * byte array to hold incoming client requests data
     */
    private byte[] buffer;

    /**
     * reference to server so we can add to the ClientRequests object
     */
    private Server handler;

    /**
     * Constructor
     * @param socket Socket to receive Client requests on
     * @param handler reference to server so we can add to the ClientRequests object
     */
    public ServerReceiver( MulticastSocket socket, Server handler) {
        this.listenSocket = socket;
        this.handler = handler;
        buffer = new byte[255];
        running = true;
        this.start();
    }

    /**
     * method to get if the thread is still running
     * @return
     */
    public boolean getRunning() {
        return running;
    }

    /**
     * Method to close the thread
     */
    public void close() {
        this.running = false;
        // Causes Exception which closes the thread
        listenSocket.close();
    }

    /**
     * run method to receive client Requests and pass them onto the server
     */
    public void run() {
        try {
            while (running) {
                // Create a packet and listen for incoming requests
                packet = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(packet);

                // Split the byte array into the clientID and the received requests
                byte[] clientIDBytes = Arrays.copyOfRange(packet.getData(), packet.getLength()-4, packet.getLength());
                byte[] receivedBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength()-4);

                // Create a playerID from the bytes
                ByteBuffer wrapped = ByteBuffer.wrap(clientIDBytes);
                int playerID = wrapped.getInt();
                // Create streams to turn the bytes into an integer list
                ByteArrayInputStream bis = new ByteArrayInputStream(receivedBytes);
                ObjectInputStream ins = new ObjectInputStream(bis);
                try {
                    Integer[] received =  (Integer[]) ins.readObject();
                    // Based on the request perform the specified action
                    switch(received[0]) {
                        case 0 : // ATTACK
                            handler.getClientRequests().playerRequestShoot(playerID);
                            break;
                        case 1 : // DROPITEM
                            handler.getClientRequests().playerRequestDrop(playerID);
                            break;
                        case 2 : // RELOAD
                            handler.getClientRequests().playerRequestReload(playerID);
                            break;
                        case 3 : // CHANGEITEM
                            handler.getClientRequests().playerRequestSelectItem(playerID, received[1]);
                            break;
                        case 4 : // MOVEMENT
                            handler.getClientRequests().playerRequestMovement(playerID, received[1]);
                            break;
                        case 5 : // TURN
                            handler.getClientRequests().playerRequestFacing(playerID, received[1]);
                            break;
                        case 6 : // CONSUME
                            handler.getClientRequests().playerRequestShoot(playerID);
                            handler.getClientRequests().playerRequestConsume(playerID, received[1]);
                            break;
                        case 7 : // PAUSE
                            handler.getClientRequests().playerRequestPause(playerID);
                            break;
                        case 8 : // RESUME
                            handler.getClientRequests().playerRequestResume(playerID);
                            break;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    ins.close();
                    bis.close();
                }
            }

        }catch(SocketException ex){
            System.out.println("closing ServerReceiver");
            ex.printStackTrace();
            // Server Receiver told to close
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
