package serverclientthreads;

import serverclientdirect.Port;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {


    public static void main(String[] args) throws SocketException {
        DatagramSocket socket;
        socket = new DatagramSocket(4445);

        System.out.println("Server started");
        ServerSender sender = new ServerSender(socket);
        ServerReceiver receiver = new ServerReceiver(socket, sender);
        try{
            sender.start();
            receiver.start();
            System.out.println("Sender and receiver started");
            // receiver joins with the sender so only receiver join needed here
            receiver.join();
            System.out.println("Receiver joined");
            System.out.println("Server ended");
            socket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Server ended successfully");
    }
}
