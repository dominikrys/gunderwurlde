package serverclientthreads;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientSender extends Thread {

	private InetAddress address;
	private DatagramSocket socket;
	DatagramPacket packet;
	private int portNumber;
	Boolean running;
	Scanner scan;
	byte[] buffer;

	ClientSender( InetAddress address, DatagramSocket socket, int portNumber) {

		this.address = address;
		this.socket = socket;
		this.portNumber = portNumber;
		this.address = address;
		this.portNumber = portNumber;
		this.socket = socket;
	}

	public void run() {
		// for now system.in
		scan = new Scanner(System.in);
		running = true;
		try {
			while (running) {
				System.out.print(">> ");
				String userInput = scan.nextLine();
				System.out.println("Input: " + userInput);
				buffer = userInput.getBytes();
				packet = new DatagramPacket(buffer, buffer.length, address, portNumber);
				socket.send(packet);
				System.out.println("Packet sent from clientSender");
				if (userInput.equals("exit")){
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Client Sender ending");
	}
}