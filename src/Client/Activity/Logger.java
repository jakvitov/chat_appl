package Client.Activity;

import Client.ClientInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This is a class which is responsible to log client in and log him out
 * This class is mostly used by the ClientInterface to do its job
 */

public class Logger {private Socket socket  = null;

    private PrintWriter clientWriter;
    private BufferedReader clientReader;

    public Logger(PrintWriter clientWriter, BufferedReader clientReader){
            this.clientReader = clientReader;
            this.clientWriter = clientWriter;
    }

    //Given a string of user name, calculate his id and log him in on the server
    //Return true on sucess, false on error
    public boolean logIn(String name){
        String token = "\\ß\\§\\";

        String clientID = Integer.toString(name.hashCode());

        System.out.println("Logging in as " + clientID);
        this.clientWriter.println(token + clientID + token + name + token);
        this.clientWriter.flush();
        try {
            String response = this.clientReader.readLine();
            if (response.equals("\\s1 LOGGED IN \\s")){
                System.out.println("Sucesfully logged in!");
                return true;
            }
            else {
                System.out.println("Login failed " + response);
                return false;
            }
        } catch (IOException IOE) {
            System.out.println("Error while reading the server response");
            return false;
        }
    }

    public void logOut(){
        System.out.println("Logging out...");
        this.clientWriter.print("\\c\\l 101 LOGOUT \\c\\l");
        this.clientWriter.flush();
    }
}

