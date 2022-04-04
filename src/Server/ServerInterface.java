package Server;

import Server.ClientServices.ClientListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

//Server interface represents the running server
public class ServerInterface {
    private Thread console;
    //Hash set representing id numbers of all users online

    private HashSet<Integer> online_ids;
    ServerInterface(){
        //We create console and start it in a separate thread
        console = new Thread(new Console(this));
        console.setDaemon(true);
        console.start();
        online_ids = new HashSet<Integer>();
    }
    //Getter for the online users, mainly used by Console class
    public HashSet<Integer> getOnline_ids() {
        return online_ids;
    }
    //A function to listen for a client and add his id to the online set
    public void Listen(int PortNumber){
        ClientListener client = new ClientListener();
        client.Listen(PortNumber);
        Integer new_client = client.getClientID();
        if (new_client != null){
            this.online_ids.add(new_client);
        }
        //If the new_client is null than the listening was not sucesfull and we do nothing
    }
}
