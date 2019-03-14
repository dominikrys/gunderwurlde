package server.net;

import server.Server;

import java.net.ServerSocket;
import java.net.Socket;

public class JoinGameManager extends Thread {

    private static int lowestAvailableID;
    boolean running;
    private Server server;
    public JoinGameManager(Server server){
        lowestAvailableID = 0;
        this.running = true;
        this.server = server;
    }

    public void run(){
        try {
            ServerSocket joinSocket = new ServerSocket(8081);
            while (running) {
                // For each connection spin off a new protocol instance.
                Socket connection = joinSocket.accept();
                increaseAvailableID();
                System.out.println("Creating player with ID: " +lowestAvailableID);
                Thread instance = new Thread(new JoinGameThread(connection, lowestAvailableID, server));
                System.out.println("Starting thread");
                instance.start();
            }
        } catch (Exception e) {
            System.out.println("Doh "+e);
        }
    }

    public static void increaseAvailableID(){
        lowestAvailableID++;
    }

    public void end(){
        running = false;
    }
}
