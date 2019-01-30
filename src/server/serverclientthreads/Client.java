package server.serverclientthreads;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

    /*
    *   Main class that holds all information regarding a single player aswell as a gamestate.
    *   If a host machine will setup the server and start communicating through threads
    *   If a joining machine will simply obtain the address of the server to contact and start communicating
    */


    public static void main(String[] args) {
        DatagramSocket socket;
        int port = 4445;

        try{
            Server server = new Server();
            server.start();
            // No port is specified as the server already bound to specified port
            socket = new DatagramSocket();
            // This threads address so the server knows who sent the message
            InetAddress senderaddress = InetAddress.getByName("230.0.0.1");
            InetAddress receiveraddress = InetAddress.getByName("230.0.1.1");

            // Start the sender and receiver threads for the client
            ClientSender sender = new ClientSender(senderaddress, socket, port);
            ClientReceiver receiver = new ClientReceiver(receiveraddress, socket);
            sender.start();
            receiver.start();

            // Waits for the sender to join as that will be the first thread to close
            sender.join();
            // Waits for the receiver thread to end as this will be the second thread to close
            receiver.join();
            // Closes the socket as communication has finished
            socket.close();
        }catch (UnknownHostException | InterruptedException | SocketException e) {
            e.printStackTrace();
        }
    }
}
