package client.net;

public class NetworkInformation {

    private static int lowestAvailableIPAddress = 1;
    private static int lowestAvailablePort = 4444;

    public static int getLowestAvailableIPAddress(){
        return lowestAvailableIPAddress;
    }

    public static int getLowestAvailablePort(){
        return lowestAvailablePort;
    }

    public static  void incrementLowestAvailableIPAddress(){
        lowestAvailableIPAddress++;
    }

    public static void incrementLowestAvailablePort(){
        lowestAvailablePort += 2;
    }
}
