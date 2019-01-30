package server.serverclientdirect;
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
			MulticastSocket socket = new MulticastSocket(4444);
			byte[] buffer = new byte[256];
			InetAddress group = InetAddress.getByName("230.0.0.0");
			socket.joinGroup(group);

			running = true;
			System.out.println("serverclientdirect.Server running");
		while(running) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String receivedString = new String(packet.getData(), 0, packet.getLength());

			if (receivedString.equals("end")) {
				System.out.println("Ending server");
				running = false;
				continue;
			}
			else{
				System.out.println("Server Received: " + receivedString);
			}

			socket.send(packet);

		}
			socket.leaveGroup(group);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
