package server.serverclientthreads;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {
    DatagramSocket socket;
    int port = 4445;

    public Server() {
        try {
            this.socket = new DatagramSocket(port);
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        // Create the socket that will be used to transfer data

        // Specify the port as this is the primary machine listening on that port
        try {

            // Create the threads that will run as sender and receiver
            ServerSender sender = new ServerSender(socket);
            ServerReceiver receiver = new ServerReceiver(socket, sender);

            // Start the sender and receiver
            sender.start();
            receiver.start();

            // Server will join with receiver when termination is requested
            // Only joins with receiver as receiver waits for sender to join
            receiver.join();
            // Socket is closed as server should end
            socket.close();
            System.out.println("Server ended successfully");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Server ended due an interrupt");
        }
    }
}
