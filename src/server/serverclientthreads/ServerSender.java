package server.serverclientthreads;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class ServerSender extends Thread {
	DatagramSocket socket;
	int port;
	Boolean running;
	InetAddress address;
	byte[] buffer;
	DatagramPacket packet;

	public ServerSender(DatagramSocket socket){
		this.socket = socket;
		this.running = true;
	}

	public void run() {
		while (running) {
			// While loop is running it takes up CPU cycles unnecessarily
			// Thread.onSpinWait gives more CPU time to other threads
			Thread.yield();
		}
		System.out.println("Ending server sender");
	}

	// sends a confirmation back to the client that the message has been received
	// in future will be used to send the continuous game state to the user/users
	public void confirm(DatagramPacket received) {
		try {
			// The address of the message to be sent to
			address = received.getAddress();
			int port = received.getPort();
			// Creates the string from the received packet
			String confirm = (new String(received.getData(), 0, received.getLength()));
			buffer = confirm.getBytes();
			// Creates a new packet to be sent to the address specified
			packet = new DatagramPacket(buffer, buffer.length, address, port);
			// Sends the packet
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// method used only for testing exit works
	// will be replaced in future clean up
	public void exit(DatagramPacket received){
		InetAddress address;
		byte[] buffer;
		DatagramPacket packet;
		try {
			// string to be sent to the clientReceiver to end it
			String exitCommand = ("exit");
			buffer = exitCommand.getBytes();
			address = received.getAddress();
			port = received.getPort();
			packet = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(packet);
			// Running is set to false so that this thread will end gracefully
			running = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

