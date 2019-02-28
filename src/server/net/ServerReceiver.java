package server.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import client.net.Addressing;
import server.Server;
import shared.lists.Teams;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client. Also controls server behaviour

public class ServerReceiver extends Thread {
    MulticastSocket listenSocket;
    InetAddress listenAddress;
    ServerSender sender;
    Boolean running;
    DatagramPacket packet;
    byte[] buffer;
    int numOfPlayers;
    Server handler;


    public ServerReceiver(InetAddress address, MulticastSocket socket, ServerSender sender, Server handler) {
        this.listenSocket = socket;
        this.listenAddress = address;
        this.sender = sender;
        this.handler = handler;
        buffer = new byte[255];
        running = true;
        Addressing.setInterfaces(listenSocket);
        this.start();
    }

    public boolean getRunning() {
        return running;
    }

    public void stopRunning() {
        this.running = false;
    }


    public void run() {
        try {
            // join the multicast group
            listenSocket.joinGroup(listenAddress);
            while (running) {
                // packet to receive incoming messages
                packet = new DatagramPacket(buffer, buffer.length);
                // blocking method that waits until a packet is received
                listenSocket.receive(packet);

                // check for a joinGame command
                byte[] commandBytes = Arrays.copyOfRange(packet.getData(), 0, 4);
                ByteBuffer checkWrap = ByteBuffer.wrap(commandBytes);
                int command = checkWrap.getInt();
                // if it is the joinGame command then call joinGame and continue
                if(command == 99){
                    joinGame(packet);
                    continue;
                }

                // if not joinGame then must be action request
                // Split the byte array into the clientID and the received requests
                byte[] clientIDBytes = Arrays.copyOfRange(packet.getData(), packet.getLength()-4, packet.getLength());
                byte[] receivedBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength()-4);

                // Convert the byteArray clientID to the int clientID
                ByteBuffer wrapped = ByteBuffer.wrap(clientIDBytes);
                int playerID = wrapped.getInt();

                ByteArrayInputStream bis = new ByteArrayInputStream(receivedBytes);
                ObjectInputStream in = null;
                try {
                    // Turn the requests bytearray into a requests Integer[]
                    in = new ObjectInputStream(bis);
                    Integer[] received =  (Integer[]) in.readObject();

                    // Based on the request perform the specified action
                    switch(received[0]) {
                    	case 0 : // ATTACK
                    		//request.requestShoot();
                    		handler.getClientRequests().playerRequestShoot(playerID);
                    		break;
                    	case 1 : // DROPITEM
                    		//request.requestDrop();
                    		handler.getClientRequests().playerRequestDrop(playerID);
                    		break;
                    	case 2 : // RELOAD
                    		//request.requestReload();
                    		handler.getClientRequests().playerRequestReload(playerID);
                    		break;
                    	case 3 : // CHANGEITEM
                    		//request.setSelectItem(received[1]);
                    		handler.getClientRequests().playerRequestSelectItem(playerID, received[1]);
                    		break;
                    	case 4 : // MOVEMENT
                    		//request.setMovementDirection(received[1]);
                    		handler.getClientRequests().playerRequestMovement(playerID, received[1]);
                    		break;
                    	case 5 : // TURN
                    		//request.setFacing(received[1]);
                    		handler.getClientRequests().playerRequestFacing(playerID, received[1]);
                    }
                    /*
                    // Send the request to the Engine
                    handler.setClientRequests(requests);

                    handler.setClientRequests(null);
                    */
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            // Waits for the sender to finish its processes before ending itself
            sender.join();
            // Running = false so the Thread ends gracefully
            running = false;
            System.out.println("Ending server receiver");
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void joinGame(DatagramPacket packet){
        try {
            System.out.println("join game request received");
            // turn the dataBytes into the strings
            byte[] dataBytes = Arrays.copyOfRange(packet.getData(), 4, packet.getLength());
            String data = new String(dataBytes);
            // split on the space to seperate the data
            String[] seperateData = data.split(" ");
            System.out.println("Part 1 is " + seperateData[0]);
            System.out.println("Part 2 is " + seperateData[1]);
            String playerName = seperateData[0];
            Teams team = null;
            if(seperateData[1].equals("RED")){
                team = Teams.RED;
            }
            else if(seperateData[1].equals("BLUE")){
                team = Teams.BLUE;
            }
            else if(seperateData[1].equals("GREEN")){
                team = Teams.GREEN;
            }
            else if(seperateData[1].equals("YELLOW")){
                team = Teams.YELLOW;
            }
            else{
                team = Teams.NONE;
            }
            // call add player from the server
            handler.addPlayer(playerName, team);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
