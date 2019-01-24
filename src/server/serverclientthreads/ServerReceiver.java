package serverclientthreads;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.regex.PatternSyntaxException;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client. Also controls server behaviour

public class ServerReceiver extends Thread {
	DatagramSocket socket;
	private ServerSender sender;
	Boolean running;
	DatagramPacket packet;
	byte[] buffer;


	public ServerReceiver(DatagramSocket socket, ServerSender sender) {
        this.socket = socket;
		this.sender = sender;
	}

	public void run() {
		running = true;
		buffer = new byte[255];
		try {
			while (running) {
				// packet to receive incoming messages
				packet = new DatagramPacket(buffer, buffer.length);
				// blocking method
				socket.receive(packet);
				String receivedString = new String(packet.getData(), 0, packet.getLength());

				System.out.println("Packet received by ClientSender");
				System.out.println("Packet contents is " + receivedString);
				sender.confirm(packet);

				if(receivedString.equals("exit")){
					sender.exit(packet);
					running = false;
					continue;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sender.interrupt();
	}
}
