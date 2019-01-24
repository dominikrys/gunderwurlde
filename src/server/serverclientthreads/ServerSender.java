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
	Boolean running;
	byte[] buffer;

	public ServerSender(DatagramSocket socket){
		this.socket = socket;
	}

	public void run() {
		running = true;
		while(running){

		}

	}

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

	public void exit(DatagramPacket received){
		try {
			String exitCommand = ("exitCode");
			buffer = exitCommand.getBytes();
			address = received.getAddress();
			port = received.getPort();
			packet = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

