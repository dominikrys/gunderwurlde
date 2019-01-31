package server.serverclientthreads;

import java.io.IOException;
import java.net.*;

public class Server extends Thread {
    // Socket to listen to the server
    MulticastSocket listenSocket = null;
    // Socket to send requests to the server
    MulticastSocket senderSocket = null;
    InetAddress listenAddress = null;
    InetAddress senderAddress = null;
    static final int SENDPORT = 4444;
    static final int LISTENPORT = 4445;


    public Server() {
    }

    public void run(){
        try {
            listenSocket = new MulticastSocket(LISTENPORT);
            senderSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.0.1");
            senderAddress = InetAddress.getByName("230.0.1.1");
            System.out.println("Server starting");
            // Create the threads that will run as sender and receiver
            ServerSender sender = new ServerSender(senderAddress, senderSocket, SENDPORT);
            ServerReceiver receiver = new ServerReceiver(listenAddress, listenSocket, sender);
            System.out.println("Threads up");
            // Server will join with receiver when termination is requested
            // Only joins with receiver as receiver waits for sender to join
            receiver.join();
            // Socket is closed as server should end
            senderSocket.close();
            listenSocket.close();
            System.out.println("Server ended successfully");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Server ended due an interrupt");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
