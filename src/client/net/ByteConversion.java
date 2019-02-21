package client.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteConversion {

    public static void main(String[] args) {

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            byte[] buffer;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(new int[]{1,2});
                out.writeInt(2);
                out.flush();
                buffer = bos.toByteArray();

                byte[] receivedBytes = Arrays.copyOfRange(buffer, 0, buffer.length-4);
                byte[] clientIDBytes = Arrays.copyOfRange(buffer, buffer.length-4, buffer.length);

                ByteBuffer wrapped = ByteBuffer.wrap(clientIDBytes);
                int playerID = wrapped.getInt();
                System.out.println(playerID);
            } finally {
                try {
                    bos.close();
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
            // TODO Will be set on a loop to send every ______ seconds

        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
