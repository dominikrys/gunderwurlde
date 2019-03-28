package client.net;

/**
 * Class to hold addressing information so client and server are synced up in simgleplayer and multiplayer host
 */
public class NetworkInformation {

    /**
     * the lowest least signficiant IP address value that can be assigned to a server
     */
    private static int lowestAvailableIPAddress = 1;

    /**
     * the lowest port value that cna be assigned to a server
     */
    private static int lowestAvailablePort = 4444;

    /**
     * Get method for lowestAvailableIPAddress
     * @return the lowestAvailableIPAddress
     */
    public static int getLowestAvailableIPAddress(){
        return lowestAvailableIPAddress;
    }

    /**
     * Get method for the lowestAvailablePort
     * @return the lowestAvailablePort
     */
    public static int getLowestAvailablePort(){
        return lowestAvailablePort;
    }

    /**
     * increases the value of lowestAvailableIPAddress by 1
     */
    public static  void incrementLowestAvailableIPAddress(){
        lowestAvailableIPAddress++;
    }

    /**
     *  increases the value of lowestAvailablePort by 2
     */
    public static void incrementLowestAvailablePort(){
        // Increase by 2 as communications happen 2 ways between server and port so 1 port for each direction
        lowestAvailablePort += 2;
    }
}
