import java.io.*;

// Continuously reads from message queue for a particular client,
// forwarding to the client.

public class ServerSender extends Thread {
	private PrintStream client;
	private ClientTable clientTable;
	private String clientName;
	private String myClientsName;

	public ServerSender(ClientTable clientTable, String myCN, String clientName, PrintStream c) {
		client = c;
		myClientsName = myCN;
		this.clientName = clientName;
		this.clientTable = clientTable;
	}

	public void run() {
		clientTable.getQueue(myClientsName).clear();
		Message msg = null;
		if (clientTable.getUserInfo(clientName).getAllMessages().size() > 0)
			msg = clientTable.getUserInfo(clientName).getCurrentMsg();

		if (msg != null)
			client.println(msg);

		while (true) {
			try {
				msg = clientTable.getQueue(myClientsName).take(); // Matches EEEEE in ServerReceiver
			} catch (InterruptedException e) {
			}

			// if the message is the server message quit or logout, pass it over to
			// ClientReceiver and stop this thread
			if (msg.getSender().equals(Strings.quit)) {
				Report.behaviour("Closing " + clientName + " client");
				clientTable.remove(clientName);
				client.println(msg);
				break;
			}

			if (msg.getSender().equals(Strings.logout)) {
				Report.behaviour("Client " + clientName + " is logging out");
				client.println(msg);
				clientTable.logout(clientName, myClientsName);
				break;
			}

			client.println(msg);
		}
	}
}

