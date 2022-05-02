package Client.Activity;

import Client.ClientInterface;
import DataStructures.Message;
import DataStructures.messageType;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * This is a class which is responsible to log client in and log him out
 * This class is mostly used by the ClientInterface to do its job
 */

public class Logger {

    private ObjectOutputStream clientOutput;
    private ObjectInputStream clientInput;

    public Logger(ObjectInputStream clientInput, ObjectOutputStream clientOutput){
            this.clientInput = clientInput;
            this.clientOutput = clientOutput;
    }

    //Given a string of user name, calculate his id and log him in on the server
    //Return true on sucess, false on error
    public boolean logIn(String name){
        String clientID = Integer.toString(name.hashCode());

        try {

            this.clientOutput.writeObject(new Message(messageType.LOGIN, name));

            Message response = (Message) this.clientInput.readObject();

            if (response.getType().equals(messageType.LOGGED_IN)){
                System.out.println("Sucesfully logged in!");
                return true;
            }
            else if (response.getType().equals(messageType.NAME_TAKEN)){
                System.out.println("Name is already taken!");
                return false;
            }
            else if (response.getType().equals(messageType.WRONG_FORMAT)){
                System.out.println("Wrong message type!");
                return false;
            }
            else {
                System.out.println("Login failed " + response);
                return false;
            }
        }
        catch (ClassNotFoundException CNFE){
            System.out.println("Class not found!");
            return false;
        }
        catch (IOException IOE) {
            System.out.println("Error while reading the server response");
            return false;
        }
    }

    public void logOut(){
        System.out.println("Logging out...");
        try {
            this.clientOutput.writeObject(new Message(messageType.LOGOUT));
        }
        catch (IOException IOE){
            System.out.println("Error while logging out!");
        }
    }
}

