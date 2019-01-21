import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.regex.PatternSyntaxException;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client. Also controls server behaviour

public class ServerReceiver extends Thread {
	private BufferedReader fromClient;
	private PrintStream toClient;

	public ServerReceiver(PrintStream toClient, BufferedReader fromClient) {
        this.toClient = toClient;
		this.fromClient = fromClient;
	}

	public void run() {
        System.out.println("SR");
		//Message msg;
		try {
			while (true) {
				// read the command and based on it control the server response
				String command = fromClient.readLine();
				toClient.println(command + " received");
//				if (command.equals(Strings.quit) && clientTable.getUserInfo(clientsName).getNumbOfActiveLogins() == 1) {
//					BlockingQueue<Message> recipientsQueue = clientTable.getQueue(myClientsName);
//					msg = new Message(Strings.quit, Strings.quit);
//					recipientsQueue = clientTable.getQueue(myClientsName);
//					recipientsQueue.offer(msg);
//					break;
//
//				} else if (command.equals(Strings.quit)) {
//					msg = new Message(Strings.pleaseLogout);
//					clientTable.getQueue(myClientsName).add(msg);
//				}
//				if (command.equals(Strings.send)) {
//					String recipient = myClient.readLine();
//					String text = myClient.readLine();
//
//					if (recipient != null && text != null && clientTable.exists(recipient)) {
//						msg = new Message(clientsName, text);
//						// sends message and adds it to be stored in UserInfo object
//						clientTable.sendMsg(recipient, msg);
//						clientTable.getUserInfo(recipient).add(msg);
//					} else
//						// No point in closing socket. Just give up.
//						return;
//				}
				// resets the command if for some reason there was no match
				command = "";
			}
		} catch (

		IOException e) {
			System.out.println("Something went wrong with the client " + e.getMessage());
			// No point in trying to close sockets. Just give up.
			// We end this thread (we don't do System.exit(1)).
		}
	}
}
