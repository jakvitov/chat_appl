package Server;

import Server.ClientServices.Client;
import Server.ClientServices.ClientListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

//Server interface represents the running server
public class ServerInterface {
    private Thread console;
    //Hash set representing id numbers of all users online

    private HashSet<Client> online_clients;
    ServerInterface(){
        //We create console and start it in a separate thread
        console = new Thread(new Console(this));
        console.setDaemon(true);
        console.start();
        online_clients = new HashSet<Client>();
    }
    //Getter for the online users, mainly used by Console class
    public HashSet<Client> getOnline_ids() {
        return online_clients;
    }
    //A function to listen for a client and add his id to the online set
    public void ListenNewClient(int PortNumber){
        ClientListener client = new ClientListener();
        client.Listen(PortNumber);
        Client new_client = client.getNewClient();
        if (new_client != null){
            this.online_clients.add(new_client);
        }
        //If the new_client is null than the listening was not sucesfull and we do nothing
    }
}
