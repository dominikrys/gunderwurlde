import java.io.*;

// Gets messages from other clients via the server (by the
// ServerSender thread).

public class ClientReceiver extends Thread {

	private BufferedReader server;
	private ClientSender sender;

	ClientReceiver(BufferedReader server, ClientSender sender) {
		this.server = server;
		this.sender = sender;
	}

	public void run() {
		System.out.println("CR");
		// Print to the user whatever we get from the server:
		try {
			while (true) {
				String s = server.readLine();
				if (s == null) {
					System.out.println("the message is null");
				} else if (s.equals("quit")) {
					// if the message is quit message, change the value of breakTheLoop in
					// ClientSender for it to stop
					sender.breakTheLoop = true;
					System.out.println("You have quited! Send any command to exit the program.");
					break;
				} else {
					System.out.println(s);
				}

			}
		} catch (IOException e) {
			System.out.println("Server seems to have died " + e.getMessage());
		}
	}
}

/*
 * 
 * The method readLine returns null at the end of the stream
 * 
 * It may throw IoException if an I/O error occurs
 * 
 * See https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#
 * readLine--
 * 
 * 
 */
