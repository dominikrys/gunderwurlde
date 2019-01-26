package serverclientthreads;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

// Gets messages from other clients via the server (by the
// serverclientthreads.ServerSender thread).

public class ClientReceiver extends Thread {
	DatagramSocket socket;
	Boolean running;
	DatagramPacket packet;
	byte[] buffer;

	ClientReceiver(DatagramSocket socket) {
		this.socket = socket;
		running = true;
	}

	public void run() {
		buffer = new byte[255];
		try{
			while(running) {
				// creates a packet and waits to receive a message from the server
				packet = new DatagramPacket(buffer, buffer.length);
				// blocking method waiting to receive a message from the server
				socket.receive(packet);
				System.out.println("Message received: ");
				// Creates a string and prints it to the user
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(received);
				// If message is exit the terminate
				if (received.equals("exit")){
					running = false;
				}
			}
		} catch (SocketException e){
			System.out.println("Socket closed unexpectedly");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
