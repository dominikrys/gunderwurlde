package server.serverclientthreads;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientSender extends Thread {

	private InetAddress address;
	private DatagramSocket socket;
	private int port;
	Boolean running;
	Scanner scan;
	byte[] buffer;
	DatagramPacket packet;

	ClientSender(InetAddress address, DatagramSocket socket, int port) {
		this.address = address;
		this.socket = socket;
		this.port = port;
		running = true;
	}

	public void run() {
		// For now the messages are created from System.in
		// Will eventually be created by through the objects
		scan = new Scanner(System.in);
		try {
			while (running) {
				// Asks for user input
				System.out.print(">> ");
				String userInput = scan.nextLine();

				// Creates and sends the packet to the server
				buffer = userInput.getBytes();
				packet = new DatagramPacket(buffer, buffer.length, address, port);
				socket.send(packet);

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