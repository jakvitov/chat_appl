package Server;

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
}
