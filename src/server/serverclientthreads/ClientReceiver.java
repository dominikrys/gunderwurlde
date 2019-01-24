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
	}

	public void run() {
		running = true;
		buffer = new byte[255];
		try{
			while(running) {
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println("Packet is " + received);
				System.out.println("Packet received from ServerSender");
				if (received.equals("exitCode")){
					break;
				}
			}
		} catch (SocketException e){
			System.out.println("Client receiver ending");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
