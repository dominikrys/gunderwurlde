package server.serverclientdirect;
// Usage:
//        java serverclientdirect.Client user-nickname server-hostname
//
// After initializing and opening appropriate sockets, we start two
// client threads, one to send messages, and another one to get
// messages.

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

class Client {

	public static void main(String[] args) {
		MulticastSocket socket;
		InetAddress groupAddress;
		InetAddress address;
		Boolean running = true;
		Scanner sc = new Scanner(System.in);
		byte[] buffer;

		try{
			//Establish the connection to the server
			socket = new MulticastSocket();
			groupAddress = InetAddress.getByName("239.0.0.1");
			address = InetAddress.getLocalHost();
			socket.setInterface(address);

			System.out.println("serverclientdirect.Client Running");
			while(running){
				System.out.print(">> ");
				String userInput = sc.nextLine();
				buffer = userInput.getBytes();
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, 4445);

				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) {
					NetworkInterface iface = interfaces.nextElement();
					if (iface.isLoopback() || !iface.isUp())
						continue;

					Enumeration<InetAddress> addresses = iface.getInetAddresses();
					while(addresses.hasMoreElements()) {
						InetAddress addr = addresses.nextElement();
						socket.setInterface(addr);
						socket.send(packet);
					}
				}

				if(userInput.equals("end")){
					System.out.println("Ending client");
					running = false;
					continue;
				}
			}
			socket.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}
