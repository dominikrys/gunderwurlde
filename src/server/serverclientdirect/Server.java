package serverclientdirect;
// Usage:
//        java serverclientdirect.Server
//
// There is no provision for ending the server gracefully.  It will
// end if (and only if) something exceptional happens.

import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;

public class Server{

	public static void main(String[] args) {
		Boolean running;
		try {
			DatagramSocket socket = new DatagramSocket(Port.number);
			byte[] buffer = new byte[256];

		running = true;
			System.out.println("serverclientdirect.Server running");
		while(running) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);


			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			packet = new DatagramPacket(buffer, buffer.length, address, port);
			String receivedString = new String(packet.getData(), 0, packet.getLength());

			if (receivedString.equals("end")) {
				System.out.println("Ending server");
				running = false;
				continue;
			}
			else{
				System.out.println(receivedString);
			}

			socket.send(packet);

		}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
