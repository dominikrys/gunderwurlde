package server.serverclientthreads;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

public class ClientSender extends Thread {
	MulticastSocket senderSocket;
	InetAddress senderAddress;
	Boolean running;
	DatagramPacket packet;
	int port;
	byte[] buffer;
	Scanner scan;

	ClientSender(InetAddress address, MulticastSocket socket, int port) {
		this.senderAddress = address;
		this.senderSocket = socket;
		this.port = port;
		running = true;
		this.start();
	}

	public void run() {
		scan = new Scanner(System.in);
		try {
			while (running) {
				// Asks for user input
				System.out.print(">> ");
				String userInput = scan.nextLine();

				// Creates and sends the packet to the server
				buffer = userInput.getBytes();
				packet = new DatagramPacket(buffer, buffer.length, senderAddress, port);

				// Each time we send a packet we need to ensure the interfaces match up
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) {
					NetworkInterface iface = interfaces.nextElement();
					if (iface.isLoopback() || !iface.isUp())
						continue;

					Enumeration<InetAddress> addresses = iface.getInetAddresses();
//					if(addresses.hasMoreElements()){
//						InetAddress addr = addresses.nextElement();
//						senderSocket.setInterface(addr);
//						senderSocket.send(packet);
//					}
					while(addresses.hasMoreElements()) {
						InetAddress addr = addresses.nextElement();
						senderSocket.setInterface(addr);
						senderSocket.send(packet);
					}

				}

				// If the messages is exit then the Thread should terminate
				if (userInput.equals("exit")){
					running = false;
				}
				Thread.yield();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}