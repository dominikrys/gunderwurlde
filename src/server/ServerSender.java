import java.io.*;


public class ServerSender extends Thread {
	private PrintStream client;


	public ServerSender(PrintStream c) {
		client = c;
	}

	public void run() {
		System.out.println("SS");
		//Message msg = null;

//		while (true) {
//
//
//			// if the message is the server message quit, pass it over to
//			// ClientReceiver and stop this thread
//			if (msg.getSender().equals("quit")) {
//				client.println(msg);
//				break;
//			}
//
//			if (msg.getSender().equals("logout")) {
//				client.println(msg);
//				break;
//			}
//
//			client.println(msg);
//		}


	}
}

