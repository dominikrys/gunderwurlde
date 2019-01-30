package server.serverclientdirect;
// Usage:
//        java serverclientdirect.Server
//
// There is no provision for ending the server gracefully.  It will
// end if (and only if) something exceptional happens.

import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;
import java.util.Enumeration;

public class Server{

	public static void main(String[] args) {
		Boolean running;
		String groupName = "239.0.0.1";
		try {
			MulticastSocket socket = new MulticastSocket(4445);
			socket.setReuseAddress(true);
			byte[] buffer = new byte[256];
			InetAddress group = InetAddress.getByName(groupName);
			socket.joinGroup(group);

			running = true;
			System.out.println("serverclientdirect.Server running");

			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while(addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					socket.setInterface(addr);

				}
			}

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
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
