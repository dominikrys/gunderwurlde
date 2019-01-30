package server.UPDmulticast;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class MulticastClient {

    public static void main(String args[]) {
        try {

            InetAddress group;
            byte[] buf;
            Scanner scan = new Scanner(System.in);
            MulticastSocket socket = new MulticastSocket();
            group = InetAddress.getByName("224.0.0.120");

            //MulticastServer server = new MulticastServer();
            //server.start();
             NetworkInterface.getNetworkInterfaces();
            System.out.println();

            //server.join();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}