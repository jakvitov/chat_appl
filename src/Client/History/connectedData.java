package Client.History;

/**
 * Simple data class holding the info about current loggen in client
 */

public class connectedData {

    public String name;
    public String serverIP;

    public connectedData(String name, String serverIP){
        this.name = name;
        this.serverIP = serverIP;
    }

}
