package Client;

import Client.Activity.Logger;
import DataStructures.logState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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

        try{
            this.clientSocket = new Socket(IP, 3001);
            this.clientWriter = new PrintWriter(this.clientSocket.getOutputStream());
            this.clientReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
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
        this.loggedIn = false;
        this.logger.logOut();
    }



}
