package server.net;

import server.Server;
import shared.lists.Team;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class JoinGameThread extends Thread {
    public Socket connection;
    public int id;
    private Server server;

    public JoinGameThread(Socket connection, int iD, Server server){
        this.connection = connection;
        this.id = iD;
        this.server = server;
    }

    public void run(){
        try {
            OutputStream outstream = connection.getOutputStream();
            InputStream insteam = connection.getInputStream();
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
            System.out.println("Adding player to game");
            server.addPlayer(playerName, team);

            System.out.println("Sending confirmation");
            buffer = ByteBuffer.allocate(4).putInt(1).array();
            outstream.write(buffer);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}