package serverclientdirect;
// Usage:
//        java serverclientdirect.Client user-nickname server-hostname
//
// After initializing and opening appropriate sockets, we start two
// client threads, one to send messages, and another one to get
// messages.

import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client {

	public static void main(String[] args) {
		DatagramSocket socket;
		InetAddress address;
		Boolean running = true;
		Scanner sc = new Scanner(System.in);
		byte[] buffer;

		try{
			//Establish the connection to the server
			socket = new DatagramSocket();
			address = InetAddress.getByName("localhost");

			System.out.println("serverclientdirect.Client Running");
			while(running){
				System.out.println("Give input");
				String userInput = sc.nextLine();
				buffer = userInput.getBytes();
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4444);
				socket.send(packet);
				if(userInput.equals("end")){
					System.out.println("Ending client");
					running = false;
					continue;
				}
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(received);
			}
			socket.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}
