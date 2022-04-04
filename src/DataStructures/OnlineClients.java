package DataStructures;

import Server.ClientServices.ClientListener;

import java.util.HashSet;
//Data structure to hold the currently online clients
public class OnlineClients {
    private HashSet<ClientListener> database;
    public OnlineClients(){
        database = new HashSet<ClientListener>();
    }
    //Remove client from online database
    public void remove(Integer ID){
        database.removeIf(user->user.client.ID.equals(ID));
    }
    public void remove(ClientListener cl){
        database.removeIf(user->user.equals(cl));
    }
    //Add new client to the online database
    public void add(ClientListener cl){
        this.database.add(cl);
    }
    //Return if given client is online
    public boolean isOnline(Integer ID){
        for (ClientListener cl : this.database){
            if (cl.client.ID.equals(ID)){
                return true;
            }
        }
        return false;
    }
}
