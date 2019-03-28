package client.net;

import client.Client;
import client.Settings;
import shared.view.GameView;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;

/**
 * Thread that handles the receiving of requests
 */
public class ClientReceiver extends Thread {

    /**
     * Socket to receive GameViews from the server
     */
    private MulticastSocket listenSocket;

    /**
     * boolean to state if the thread should stay alive
     */
    private Boolean running;

    /**
     * packet to receive the transmission from the server
     */
    private DatagramPacket packet;

    /**
     * byte array to hold packet data received
     */
    private byte[] buffer;

    /**
     * reference to creator object to set new GameViews
     */
    private Client client;

    /**
     * object to hold previously saved settings e.g. sound settings
     */
    private Settings settings;

    int test = 0;
    /**
     * Constructor
     * @param socket Socket to receive GameViews from the server
     * @param client reference to creator object to set new GameViews
     * @param settings object to hold previously saved settings e.g. sound settings
     */
    public ClientReceiver(MulticastSocket socket, Client client, Settings settings) {
        this.listenSocket = socket;
        this.client = client;
        this.settings = settings;
        buffer = new byte[2000000];
        running = true;
        this.start();
    }

    /**
     * method to get the value of running
     * @return the value of running
     */
    public boolean getRunning() {
        return running;
    };

    /**
     * run method for this thread, to receive packets from the server
     */
    public void run() {
        try {
            while (running) {
                // creates a packet and wait for the server to send a GameView
                packet = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(packet);
                // If the packet is 8 bytes long then it is a special command
                    if(packet.getLength() == 8){
                    // Copy across the command and value bytes to integers
                    byte[] commandBytes = Arrays.copyOfRange(packet.getData(), 0, 4);
                    byte[] ValueBytes = Arrays.copyOfRange(packet.getData(), 4, 8);
                    ByteBuffer wrappedCommand = ByteBuffer.wrap(commandBytes);
                    int command = wrappedCommand.getInt();
                    ByteBuffer wrappedValue = ByteBuffer.wrap(ValueBytes);
                    int value = wrappedValue.getInt();
                    // if command == 1 then it is updating the size of the buffer
                    if(command == 1){
                        buffer = new byte[value];
                    }
                    continue;
                }
                // if not 8 bytes long it must be a new Game View
                else {
                    // Create a byte stream to receive the byte array
                    ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
                    // Create an object stream to turn the byte array into objects
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    GameView view;
                    try{
                        // create a GameView from the byte array received
                        view = (GameView) ois.readObject();
                        // set the gameView for the client
                        client.setGameView(view, settings);
                    }catch(SocketTimeoutException ex){
                        ex.printStackTrace();
                        System.out.println("lost connection to the host");
                    }
                    catch(SocketException ex){
                        System.out.println("lost connection to the host, or paused");
                        ex.printStackTrace();
                    }
                    catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (EOFException ex) {
                        ex.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    // finally close the streams down
                    finally {
                        ois.close();
                        bis.close();
                    }
                }
            }
            System.out.println("ClientReceiver ending");
        }catch(SocketException ex){
            System.out.println("Closing clientReceiver");
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * method to close the thread
     */
    public void close(){
        // stop the while loop
        running = false;
        // closing the socket will cause an IOException which will end the thread
        listenSocket.close();
    }
}
