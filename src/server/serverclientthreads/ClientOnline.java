package server.serverclientthreads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientOnline {
    // Socket to listen to the server
    MulticastSocket listenSocket;
    // Socket to send requests to the server
    MulticastSocket sendSocket;
    InetAddress listenAddress;
    InetAddress senderAddress;
    static final int LISTENPORT = 4444;
    static final int SENDPORT = 4445;
    ClientSender sender;
    ClientReceiver receiver;

    public ClientOnline(){
        // To assign the objects to be integrated
    }

    public void run(){
        try{

            listenSocket = new MulticastSocket(LISTENPORT);
            sendSocket = new MulticastSocket();
            listenAddress = InetAddress.getByName("230.0.1.1");
            senderAddress = InetAddress.getByName("230.0.0.1");

            System.out.println("ClientOnline calls server");

            // Start the sender and receiver threads for the client
            sender = new ClientSender(senderAddress, sendSocket, SENDPORT);
            receiver = new ClientReceiver(listenAddress, listenSocket);

            // Waits for the sender to join as that will be the first thread to close
            sender.join();
            // Waits for the receiver thread to end as this will be the second thread to close
            receiver.join();
            // Closes the socket as communication has finished
            sendSocket.close();
            listenSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ClientSender getSender() {
    	return this.sender;
    }
}
