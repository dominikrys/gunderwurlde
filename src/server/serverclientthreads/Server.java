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

        sender.start();
        receiver.start();
    }
}
