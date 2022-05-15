package Client;

import Client.Activity.Logger;
import Client.Activity.Messenger;
import Client.Encryption.MessageCrypt;
import Client.History.Archive;
import Client.History.connectedData;
import DataStructures.logState;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * A client backed class that will take care of the backend behind GUI performing all the actions for it
 */
public class ClientBackend {

    private boolean loggedIn;
    private String serverIP;
    private Socket clientSocket;
    private Logger logger;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private Archive archive;
    private MessageCrypt crypt;
    private Messenger messenger;
    private Thread onlineClients;
    private MessageListener listener;
    private Thread threadListener;
    private connectedData currentInfo;

    public ClientBackend(){
        this.loggedIn = false;
        this.serverIP = "Not connected to any servers yet!";
    }

    public boolean isLoggedIn (){
        return this.loggedIn;
    }

    //Log in to the given server with given name and return our LogState after the attempt
    public logState logIn (String IP, String clientName){
        this.archive = new Archive();
        try{
            this.clientSocket = new Socket();
            this.clientSocket.connect(new InetSocketAddress(IP, 3001), 1000);
            this.clientOutput = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.clientInput = new ObjectInputStream(this.clientSocket.getInputStream());
            this.crypt = new MessageCrypt(clientName);
            this.messenger = new Messenger(this.clientOutput, this.clientInput, this.archive, this.crypt);
            this.listener = new MessageListener(this.clientInput, this.archive, this.crypt, true);
            this.threadListener = new Thread(this.listener);
        }
        catch (UnknownHostException UHE){
            return logState.OFFLINE;
        }
        catch (IOException IOE){
            return logState.OFFLINE;
        }

        this.logger = new Logger(this.clientInput, this.clientOutput);
        if (logger.logIn(clientName) == false){
            return logState.NAMETAKEN;
        }

        this.loggedIn = true;
        this.currentInfo = new connectedData(clientName, IP);
        this.threadListener.start();
        return logState.CONNECTED;

    }

    //Log the client out
    public void logOut (){
        this.archive = new Archive();
        this.loggedIn = false;
        System.out.println("setting null");
        this.logger.logOut();
    }

    //Send a message to targetName including the message text
    public void message(String targetName, String message){
        this.messenger.message(targetName, message);
    }

    public ObservableList<String> history(String target){
        return this.archive.getConversationList(target);
    }

    //Return text including info about the current name and IP of the connected server, this string
    //is passed into the server info label
    public String getInfo(){
        if (this.loggedIn == false){
            return new String("You are currently not\nconnected to any server!");
        }
        else return new String("Server IP: " + this.currentInfo.serverIP + "\nUsername: " + this.currentInfo.name);
    }

    //Return our current username
    public String getName(){
        if (this.loggedIn == false){
            return new String("Not logged in yet.");
        }
        else return this.currentInfo.name;
    }

}
