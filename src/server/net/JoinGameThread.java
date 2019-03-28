package server.net;

import server.Server;
import shared.lists.Team;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Thread to handle one client joining a game
 */
public class JoinGameThread extends Thread {

    /**
     * Socket used to communicate with the client joining the game
     */
    private Socket socket;

    /**
     * The ID that will be given to the client
     */
    private int id;

    /**
     * reference to the server so the player can be added to the game engine
     */
    private Server server;

    /**
     * Constructor
     * @param socket Socket used to communicate with the client joining the game
     * @param iD The ID that will be given to the client
     * @param server reference to the server so the player can be added to the game engine
     */
    public JoinGameThread(Socket socket, int iD, Server server){
        this.socket = socket;
        this.id = iD;
        this.server = server;
    }

    /**
     * Run method to perform protocol that gives a player an ID and adds them to the game
     */
    public void run(){
        try {
            // Create the streams

            OutputStream outstream = socket.getOutputStream();
            InputStream insteam = socket.getInputStream();

            // Write the ID to the client
            byte[] buffer = ByteBuffer.allocate(4).putInt(id).array();
            outstream.write(buffer);

            // Read in the playerName and team to be able to pass it to the server
            buffer = new byte[64];
            insteam.read(buffer);
            String nameAndTeam = new String(buffer).trim();
            String[] split = nameAndTeam.split("/");
            String playerName = split[0];
            Team team;
            if(split[1].equals("RED")){
                team = Team.RED;
            }
            else if(split[1].equals("BLUE")){
                team = Team.BLUE;
            }
            else if(split[1].equals("GREEN")){
                team = Team.GREEN;
            }
            else if(split[1].equals("YELLOW")){
                team = Team.YELLOW;
            }
            else{
                team = Team.NONE;
            }
            // Add the player to the game
            server.addPlayer(playerName, team);

            // Send the client confirmation that the add request has been sent
            buffer = ByteBuffer.allocate(4).putInt(1).array();
            outstream.write(buffer);

            //Close streams and the socket
            outstream.close();
            insteam.close();
        } catch (IOException e) {
            // Error with streams, reading and writing to the socket or talking to the server
            e.printStackTrace();
        }
    }
}