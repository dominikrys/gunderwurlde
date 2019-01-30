package server.UPDmulticast;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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

            while (true) {
                System.out.print(">> ");
                String userInput = scan.nextLine();

                buf = userInput.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);

                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    if (iface.isLoopback() || !iface.isUp())
                        continue;

                    Enumeration<InetAddress> addresses = iface.getInetAddresses();

                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        socket.setInterface(addr);

                        socket.send(packet);


                        if (userInput.equals("exit")) {
                            break;
                        }
                        TimeUnit.SECONDS.sleep(3);
                        break;
                    }

                    //server.join();
                    socket.close();
                }

            }
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
}