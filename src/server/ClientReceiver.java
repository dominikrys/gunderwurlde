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
		// Print to the user whatever we get from the server:
		try {
			while (true) {
				String s = server.readLine(); // Matches FFFFF in ServerSender.java
				if (s == null) {
					Report.error("the message is null");
				} else if (s.equals(Strings.quitMsg)) {
					// if the message is quit message, change the value of breakTheLoop in
					// ClientSender for it to stop
					sender.breakTheLoop = true;
					Report.behaviour("You have quited! Send any command to exit the program.");
					break;
				} else if (s.equals(Strings.logoutMsg)) {
					Report.behaviour("You have logged out!");
					break;
				} else {
					System.out.println(s);
				}

			}
		} catch (IOException e) {
			Report.errorAndGiveUp("Server seems to have died " + e.getMessage());
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
