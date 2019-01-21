
// Usage:
//        java Client user-nickname server-hostname
//
// After initializing and opening appropriate sockets, we start two
// client threads, one to send messages, and another one to get
// messages.


import java.io.*;
import java.net.*;

class Client {

	private static String username = "";
	private static PrintStream toServer = null;
	private static BufferedReader fromServer = null;
	private static Socket server = null;

	public static void main(String[] args) {

	    Server serverThread = new Server();
	    serverThread.start();

		// Check correct usage:
		if (args.length != 1) {
			System.out.println("Usage: java Client server-hostname");
		}

		// Initialize information:
		String hostname = args[0];

		// Open sockets:
		try {
			server = new Socket(hostname, Port.number); // Matches AAAAA in Server.java
			toServer = new PrintStream(server.getOutputStream());
			fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + hostname);
		} catch (IOException e) {
			System.out.println("The server doesn't seem to be running " + e.getMessage());
		}

		// Create two client threads of a different nature:
		ClientSender sender = new ClientSender(toServer, fromServer);
		ClientReceiver receiver = new ClientReceiver(fromServer, sender);

		// Run them in parallel:
		sender.start();
		receiver.start();
		// Wait for them to end and close sockets.
		try {
			sender.join();
			receiver.join();
			toServer.close();
			fromServer.close();
			server.close();
		} catch (IOException e) {
			System.out.println("Something wrong " + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Unexpected interruption " + e.getMessage());
		}

		return;
	}
}
