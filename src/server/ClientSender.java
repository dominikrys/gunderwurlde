import java.io.*;

public class ClientSender extends Thread {

	private PrintStream toServer;
	BufferedReader fromServer;
	public boolean breakTheLoop = false;

	ClientSender(PrintStream toServer, BufferedReader fromServer) {
		this.toServer = toServer;
		this.fromServer = fromServer;
	}

	public void run() {
		System.out.println("CS");
		System.out.println("From now all it does is that it takes a message and sends it back to the client.\n" +
				"Write something and it will give you back the same message.");
		// So that we can use the method readLine:
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

		try {
			// Then loop forever sending messages to recipients via the server:
			String command;
			while (true) {
				// The command to send to the server
				command = user.readLine();
				// Check if ClientReceiver changed the value and if so stop the thread by
				// breaking the while loop
				if (breakTheLoop)
					break;

				toServer.println(command);

				// based on command wait for more information and send it over to ServerReceiver
//				if (command.equals("send")) {
//					String recipient = user.readLine();
//					String text = user.readLine();
//					toServer.println(command);
//					toServer.println(recipient);
//					toServer.println(text);
//				} else if (command.equals("quit")) {
//					toServer.println(command);
//
//				} else if (command.equals("logout")) {
//					toServer.println(command);
//					break;
//				} else {
//					System.out.println("Unknown command");
//				}
			}
		} catch (IOException e) {
			System.out.println("Communication broke in ClientSender" + e.getMessage());
		}
	}
}
