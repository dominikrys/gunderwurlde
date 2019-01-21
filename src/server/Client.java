
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

	    Server server = new Server();
	    server.run();

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
			System.out.println().errorAndGiveUp("Unknown host: " + hostname);
		} catch (IOException e) {
			System.out.println().errorAndGiveUp("The server doesn't seem to be running " + e.getMessage());
		}

		// Start the client initialising
		membInit();

		// Create two client threads of a different nature:
		ClientSender sender = new ClientSender(username, toServer, fromServer);
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
			System.out.println().errorAndGiveUp("Something wrong " + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println().errorAndGiveUp("Unexpected interruption " + e.getMessage());
		}

	}

	// Speaks with ClientInit thread to register and login the user
	private static void membInit() {
		String userCommand = "";
		boolean logedIn = false;
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

		// If the user logs in, the while stops
		while (!logedIn) {
			try {
				// Only register and login are available as commands
				while (!(userCommand.equals("register") || userCommand.equals("login"))) {
					System.out.println("Please type register or login and type your username in the next line.");
					userCommand = user.readLine();
				}

				System.out.println("Please enter your username.");
				username = user.readLine();

			} catch (IOException e) {
				System.out.println.errorAndGiveUp(e.getMessage());
			}

			if (userCommand.equals("register")) {
				toServer.println("register");
				toServer.println(username);

				// Based on the answer from the ClientInit, send the correct response
				try {
					if (fromServer.readLine().equals("exists")) {
						System.out.println.error("A client with this username already exists.");
						username = "";
						userCommand = "";
					} else {
						System.out.println("You have successfully registered. Please login if you wish to continue.");
						username = "";
						userCommand = "";
					}
				} catch (IOException e) {
					System.out.println("Communication with the server broke" + e.getMessage());
				}

			} else if (userCommand.equals("login")) {
				toServer.println("login");
				toServer.println(username);

				// If login is successful, stop the while loop
				try {
					if (fromServer.readLine().equals("exists")) {
						logedIn = true;
						System.out.println.error("You have successfully logged in.");
					} else {
						System.out.println("There is no client with this name. Have you registered?");
						username = "";
						userCommand = "";
					}
				} catch (IOException e) {
					System.out.println("Communication with the server broke" + e.getMessage());
				}
			}
		}
	}
}
