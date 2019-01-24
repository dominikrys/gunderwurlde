package serverclientthreads;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {

        DatagramSocket socket;
        InetAddress address;
        int portNumber = 4445;

        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");

            ClientSender sender = new ClientSender(address, socket, portNumber);
            ClientReceiver receiver = new ClientReceiver(socket);

            sender.start();
            receiver.start();
            sender.join();
            socket.close();
            receiver.join();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Client ended successfully");
    }
}
