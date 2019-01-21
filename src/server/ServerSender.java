import java.io.*;


public class ServerSender extends Thread {
	private PrintStream client;
	private ClientTable clientTable;
	private String clientName;
	private String myClientsName;

	public ServerSender(String myCN, String clientName, PrintStream c) {
		client = c;
		myClientsName = myCN;
		this.clientName = clientName;
		this.clientTable = clientTable;
	}

	public void run() {
		Message msg = null;

		while (true) {


			// if the message is the server message quit, pass it over to
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

