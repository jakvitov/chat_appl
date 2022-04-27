package Client;

import Client.Activity.Logger;
import Client.Activity.Messenger;
import Client.Activity.onlineClients;
import Client.Encryption.MessageCrypt;
import Client.History.Archive;
import Client.History.silentListener;
import ClientGUI.ChatGUIInterface;
import DataStructures.logState;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * A client backed class that will take care of the backend behind GUI performing all the actions for it
 */
public class ClientBackend {

    private boolean loggedIn;
    private String clientName;
    private String serverIP;
    private Socket clientSocket;
    private Logger logger;
    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Archive archive;
    private MessageCrypt crypt;
    private Messenger messenger;
    private Thread onlineClients;

    public ClientBackend(){
        this.loggedIn = false;
        this.clientName = "Not set yet!";
        this.serverIP = "Not connected to any servers yet!";
    }

    public boolean isLoggedIn (){
        return this.loggedIn;
    }

    //Log in to the given server with given name and return if we can
    public logState logIn (String IP, String clientName){
        this.archive = new Archive();
        try{
            this.clientSocket = new Socket(IP, 3001);
            this.clientWriter = new PrintWriter(this.clientSocket.getOutputStream());
            this.clientReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.crypt = new MessageCrypt(clientName);
            this.messenger = new Messenger(this.clientWriter, this.clientReader, this.archive, this.crypt);

        }
        catch (UnknownHostException UHE){
            return logState.OFFLINE;
        }
        catch (IOException IOE){
            return logState.OFFLINE;
        }

        this.logger = new Logger(this.clientWriter, this.clientReader);
        if (logger.logIn(clientName) == false){
            return logState.NAMETAKEN;
        }

        this.loggedIn = true;
        return logState.CONNECTED;
    }

    //Log us out
    public void logOut (){
        this.archive = new Archive();
        this.loggedIn = false;
        this.logger.logOut();
    }

    //Start listening to incomming messages
    public void startListening (Label alertLabel){
        Thread listener = new Thread(new silentListener(this.clientReader, this.archive, this.crypt, alertLabel));
        listener.start();
    }

    public void message(String targetName, String message){
        this.messenger.message(targetName, message);
    }

    public ArrayList<String> history(String target){
        return this.archive.getConversationList(target);
    }

    public void startOnlineList (ListView displayList){
        try {
            onlineClients  = new Thread(new onlineClients(this.clientWriter, displayList));
            onlineClients.start();
            onlineClients.setPriority(9);
        }
        catch (Exception e){
        }
    }

}
