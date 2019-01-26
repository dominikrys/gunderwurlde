package serverclientthreads;

import serverclientdirect.Port;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {


    public static void main(String[] args) throws SocketException {
        // Create the socket that will be used to transfer data
        DatagramSocket socket;
        int port = 4445;
        // Specify the port as this is the primary machine listening on that port
        try {
            socket = new DatagramSocket(port);
            System.out.println("Server started");

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
        catch(BindException e){
            System.out.println("Port already in use please choose a different port");
        }
    }
}
