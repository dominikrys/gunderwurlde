package server.UPDmulticast;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MulticastClient {

    public static void main(String args[]) {
        try {
            DatagramSocket socket;
            InetAddress group;
            byte[] buf;
            Scanner scan = new Scanner(System.in);
            socket = new DatagramSocket();
            group = InetAddress.getByName("230.0.0.0");

            MulticastServer server = new MulticastServer();
            server.start();

            while (true) {
                System.out.print(">> ");
                String userInput = scan.nextLine();

                buf = userInput.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
                socket.send(packet);

                if (userInput.equals("exit")) {
                    break;
                }
                TimeUnit.SECONDS.sleep(3);
            }

            server.join();
            socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
