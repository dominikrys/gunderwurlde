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
	ServerSender sender;
	Boolean running;
	DatagramPacket packet;
	byte[] buffer;


	public ServerReceiver(DatagramSocket socket, ServerSender sender) {
        this.socket = socket;
		this.sender = sender;
		running = true;
		buffer = new byte[255];
	}

	public void run() {
		try {
			while (running) {
				// packet to receive incoming messages
				packet = new DatagramPacket(buffer, buffer.length);
				// blocking method that waits until a packet is received
				socket.receive(packet);
				System.out.println("Message received");
				// creates a string from the received packet
				String receivedString = new String(packet.getData(), 0, packet.getLength());
				System.out.println("Message is " + receivedString);

				// If string == exitCode then begin the exit sequence
				if(receivedString.equals("exit")){
					// Tells the sender to exit
					sender.exit(packet);
					// Waits for the sender to finish its processes before ending itself
					sender.join();
					// Running = false so the Thread ends gracefully
					running = false;
				}
				// If not the exit code simply send the confirmation message to the client
				else {
					sender.confirm(packet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
