import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.regex.PatternSyntaxException;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client. Also controls server behaviour

public class ServerReceiver extends Thread {
	private String clientsName;
	private String myClientsName;
	private BufferedReader myClient;
	private ClientTable clientTable;

	public ServerReceiver(String myCN, String n, BufferedReader c, ClientTable t) {
		myClientsName = myCN;
		clientsName = n;
		myClient = c;
		clientTable = t;
	}

	public void run() {
		Message msg;
		try {
			while (true) {
				// read the command and based on it control the server response
				String command = myClient.readLine();
				if (command.equals(Strings.quit) && clientTable.getUserInfo(clientsName).getNumbOfActiveLogins() == 1) {
					BlockingQueue<Message> recipientsQueue = clientTable.getQueue(myClientsName);
					msg = new Message(Strings.quit, Strings.quit);
					recipientsQueue = clientTable.getQueue(myClientsName);
					recipientsQueue.offer(msg);
					break;

				} else if (command.equals(Strings.quit)) {
					msg = new Message(Strings.pleaseLogout);
					clientTable.getQueue(myClientsName).add(msg);
				}

				if (command.equals(Strings.logout)) {
					msg = new Message(Strings.logout, Strings.logout);
					BlockingQueue<Message> recipientsQueue = clientTable.getQueue(myClientsName);
					recipientsQueue.offer(msg);
					break;
				}

				if (command.equals(Strings.next) || command.equals(Strings.previous)) {
					UserInfo myUserInfo = clientTable.getUserInfo(clientsName);
					myUserInfo.changeCurrent(command);
					clientTable.sendMsg(clientsName, myUserInfo.getCurrentMsg());
				}

				if (command.equals(Strings.delete)) {
					UserInfo myUserInfo = clientTable.getUserInfo(clientsName);
					myUserInfo.deleteCurrentMsg();
				}

				if (command.equals(Strings.createGroup)) {
					String groupName = myClient.readLine();
					String groupMem = myClient.readLine();

					// if user didn't add any members to the group, just add that user alone
					if (groupMem.equals(""))
						groupMem = clientsName;
					else
						groupMem += " " + clientsName;

					String[] names = null;
					try {
						names = groupMem.split("\\s+");
					} catch (PatternSyntaxException ex) {

					}
					clientTable.addGroup(groupName);

					msg = new Message(clientsName + Strings.personAddedToGroup + groupName);
					clientTable.sendMsg(clientsName, msg);

					for (int i = 0; i < names.length; i++) {
						// check if the user that needs to be added exists in the server
						if (clientTable.exists(names[i])) {
							clientTable.getGroup(groupName).addGroupMember(names[i]);
							// if the user is not the client send him or her a message that he or she has
							// been added to a new group
							if (!names[i].equals(clientsName)) {
								msg = new Message(Strings.newGroup + groupName);

								clientTable.sendMsg(names[i], msg);

								msg = new Message(names[i] + Strings.personAddedToGroup + groupName);
								clientTable.sendMsg(clientsName, msg);
							}
						} else {
							msg = new Message(Strings.noName, names[i]);
							clientTable.sendMsg(clientsName, msg);
						}
					}
				}

				if (command.equals(Strings.group)) {
					String groupName = myClient.readLine();
					String text = myClient.readLine();
					Group group = clientTable.getGroup(groupName);

					// checks if the group exists and if the client is a part in that group
					if (clientTable.existsGroup(groupName) && group.checkForName(clientsName)) {

						String[] recipientsNames = group.getRecipientsNames();

						for (int i = 0; i < recipientsNames.length; i++) {
							msg = new Message(groupName, clientsName, text);
							// if there is no member with that name but in the server but there is in the
							// group (user used exit command) then remove the user form the group
							if (!clientTable.exists(recipientsNames[i]))
								group.removeGroupMember(recipientsNames[i]);

							clientTable.sendMsg(recipientsNames[i], msg);

							clientTable.getUserInfo(recipientsNames[i]).add(msg);
						}
					} else {
						msg = new Message(Strings.noGroup + groupName);
						clientTable.sendMsg(clientsName, msg);

					}
				}

				if (command.equals(Strings.addGroupMem)) {
					String groupName = myClient.readLine();
					String member = myClient.readLine();
					String[] recipientsNames = null;
					Group group = clientTable.getGroup(groupName);

					if (group == null) {
						msg = new Message(Strings.noGroup + groupName);
						clientTable.sendMsg(clientsName, msg);
						continue;
					}

					if (group.checkForName(member)) {
						msg = new Message(member + Strings.personAlreadyInGroup);
						clientTable.sendMsg(clientsName, msg);
						continue;
					}
					if (clientTable.exists(member)) {
						recipientsNames = group.getRecipientsNames();
						msg = new Message(groupName, Strings.newGroup + groupName);
						clientTable.sendMsg(member, msg);

						msg = new Message(groupName, member + Strings.personAddedToGroup + groupName);

						// send message to everyone in the group that a new member has been added
						if (recipientsNames != null) {
							for (int i = 0; i < recipientsNames.length; i++) {
								clientTable.sendMsg(recipientsNames[i], msg);
							}
						}
						clientTable.getGroup(groupName).addGroupMember(member);
					} else {
						msg = new Message(Strings.noName, member);
						clientTable.sendMsg(clientsName, msg);
					}
				}

				if (command.equals(Strings.exitGroup)) {
					String groupName = myClient.readLine();
					Group group = clientTable.getGroup(groupName);
					String[] recipientsNames;

					// check if user is in the group and if the group exists
					if (group != null && group.checkForName(clientsName)) {
						group.removeGroupMember(clientsName);

						msg = new Message(groupName, Strings.groupExited);
						clientTable.sendMsg(clientsName, msg);

						recipientsNames = group.getRecipientsNames();

						if (recipientsNames != null) {
							msg = new Message(Strings.personRemovedFromGroup, clientsName);

							for (int i = 0; i < recipientsNames.length; i++) {
								clientTable.sendMsg(recipientsNames[i], msg);
							}

						}
						// if the last person of the group has exited, remove the group
						if (group.getNumbOfMem() == 0)
							clientTable.removeGroup(groupName);

					} else {
						msg = new Message(Strings.noGroup, groupName);
						clientTable.sendMsg(clientsName, msg);
					}
				}

				if (command.equals(Strings.send)) {
					String recipient = myClient.readLine();
					String text = myClient.readLine();

					if (recipient != null && text != null && clientTable.exists(recipient)) {
						msg = new Message(clientsName, text);
						// sends message and adds it to be stored in UserInfo object
						clientTable.sendMsg(recipient, msg);
						clientTable.getUserInfo(recipient).add(msg);
					} else
						// No point in closing socket. Just give up.
						return;
				}
				// resets the command if for some reason there was no match
				command = "";
			}
		} catch (

		IOException e) {
			Report.error("Something went wrong with the client " + clientsName + " " + e.getMessage());
			// No point in trying to close sockets. Just give up.
			// We end this thread (we don't do System.exit(1)).
		}
	}
}
