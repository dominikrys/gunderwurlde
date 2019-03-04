package server.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class JoinGameThread extends Thread {

    public Socket connection;
    public static int lowestAvailableID;
    public JoinGameThread(Socket connection){
        this.connection = connection;
        this.lowestAvailableID = 1;
    }

    public void run(){
        try {
            OutputStream outstream = connection.getOutputStream();
            byte[] buffer = ByteBuffer.allocate(4).putInt(1).putInt(lowestAvailableID).array();
            outstream.write(buffer);
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void increaseAvailableID(){
        lowestAvailableID++;
    }

}
