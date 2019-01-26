package serverclientthreads;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class ServerSender extends Thread {
	InetAddress address;
	DatagramSocket socket;
	DatagramPacket packet;
	int port;
	Boolean running = true;
	byte[] buffer;

	public ServerSender(DatagramSocket socket){
		this.socket = socket;
	}

	public void run() {
		while (running) {
			// while loop taking up CPU cycles for nothing
			// Thread.onSpinWait gives more CPU time to other threads
			Thread.onSpinWait();
		}
		System.out.println("Ending server sender");
	}

	// sends a confirmation back to the client that the message has been received
	// in future will be used to send the continuous game state to the user/users
	public void confirm(DatagramPacket received) {
		try {
			address = received.getAddress();
			port = received.getPort();
			String confirm = ("confirmed: " + new String(received.getData(), 0, received.getLength()));
			buffer = confirm.getBytes();
			packet = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(packet);
			System.out.println("Packet confirmed by ServerSender");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// method used only for testing exit works
	// will be replaced in future clean up
	public void exit(DatagramPacket received){
		try {
			String exitCommand = ("exit");
			buffer = exitCommand.getBytes();
			address = received.getAddress();
			port = received.getPort();
			packet = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(packet);
			System.out.println("server sender exit method finished");
			running = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

