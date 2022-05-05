package Server.ClientServices;

/**
 * A handler class which takes care of a single client
 * All client handlers run in a separate thread and work in a infinite cycle that breaks
 * when the client logs out
 */

import Client.Exceptions.logOutException;
import DataStructures.*;
import Server.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static DataStructures.messageType.*;
import static Server.ServerInterface.database;

//A class to be called to handle a client in a single thread
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Client client;
    private ObjectOutputStream clientOutput;
    private ObjectInputStream clientInput;

    public static void wait(int time){
        try {
            Thread.sleep(time);
        }
        catch (java.lang.InterruptedException jlIE){
            System.out.println("Thread interrupted while sleeping!");
        }
    }

    public Client getClient() {
        return client;
    }

    //Constructor opening sockets for the client
    public ClientHandler (Socket inputSocket){
        this.clientSocket = inputSocket;
        try {
            this.clientOutput = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.clientInput = new ObjectInputStream(this.clientSocket.getInputStream());
        }
        catch (IOException IOE){
            System.out.println("Error while opening client reader/writer.");
            IOE.printStackTrace();
        }
    }

    //A method used to send a simple text message to client later used in run method
    public void sendTextMessage(ObjectOutputStream ooe, String target, String text){
        try {
            ooe.writeObject(new Message(messageType.TEXT, target , text));
            ooe.flush();
        }
        catch (IOException IOE){
            System.out.println("Error while sending the text - message");
        }
    }

    //A method used to send an ACTIVE client message containing all the online clients in array list
    public void sendActiveMessage (ArrayList<String> list){
        try {
            this.clientOutput.writeObject(new Message(ACTIVE, list));
        }
        catch (IOException IOE){
            System.out.println("Error while sending the active clients message!");
        }
    }

    //A method to notify all online clients about server active clients change
    //We run this method every time someone logs in or out to notify everyone
    public void activeNotify(){
        ArrayList<String> onlineName = new ArrayList<String>();
        database.forEach((clientHandler) -> onlineName.add(clientHandler.client.nick));
        database.forEach((clientHandler) -> clientHandler.sendActiveMessage(onlineName));
    }

    //A method used to send a simple only-type message to the client
    public void sendTypeMessage(ObjectOutputStream ooe, messageType type){
        try {
            ooe.writeObject(new Message(type));
            ooe.flush();
        }
        catch (IOException IOE){
            System.out.println("Error while sending the type - message");
        }
    }

    //Method to get the name from the client
    //A class used to get new client form reding his id fromt the client
    //The official version of the client message containg ID is \c\i<client_id>\c\i
    //This method must be used as the second, so the instance can get additional client info
    public boolean getNewClient () throws IllegalArgumentException {
        Integer resultID = 0;
        try {
            Message login = (Message) this.clientInput.readObject();
            if (login.equals(null)){
                System.out.println("Input is null");
                return false;
            }
            if (!login.getType().equals(messageType.LOGIN) || login.getName().isEmpty()){
                System.out.println("Wrong client input name format!");
                return false;
            }
            String name = login.getName();
            resultID = name.hashCode();

            //Now we need to check if that name is original
            for (ClientHandler cl : database){
                if (cl.client != null && cl.client.ID.equals(resultID)){
                    System.out.println("Name is already taken!");
                    this.clientOutput.writeObject(new Message(messageType.NAME_TAKEN));
                    this.clientOutput.flush();
                    return false;
                }
            }

            this.client = new Client(resultID, name);
            this.clientOutput.writeObject(new Message(messageType.LOGGED_IN));
            this.clientOutput.flush();
            this.activeNotify();
            return true;
        }
        catch (ClassNotFoundException CFNE){
            System.out.println("Error - message class not found");
            throw new IllegalArgumentException();
        }
        catch (NumberFormatException NFE){
            System.out.println("Client message includes characters!");
            NFE.printStackTrace();
            throw new IllegalArgumentException();
        }
        catch (IOException IOE){
            System.out.println("Error while reading from the client socket!");
            IOE.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
    //Get a message from a client and its target
    //The return pair represents client ID and message as a string
    public Pair<Integer, String> getMessage(){
        Message message;
        try {
            message = (Message) this.clientInput.readObject();
            //If an error occurs and we scan a null string as an input, to solve this we log out the client
            if (message == null){
                throw new IllegalStateException("The message is null");
            }
        }
        catch (ClassNotFoundException CNFE){
            System.out.println("Class not found!");
            throw new IllegalArgumentException("Class not found");
        }
        catch (IOException IOE){
            System.out.println("Error while reading the message from client reader!");
            throw new IllegalArgumentException("Error while reading.");
        }
        //In case the client is logging out we receive special pair for logout
        if (message.getType().equals(messageType.LOGOUT)){
            throw new logOutException();
        }
        return new Pair(Integer.parseInt(message.getTarget()), message.getText());
    }
    @Override
    public void run() {
        //First we get new clients id and initialize client variable
        try {
            while (this.getNewClient() == false){
                continue;
            }
        }
        //In case the client name has wrong format
        catch (IllegalArgumentException IAE){
            try {
                this.clientOutput.writeObject(new Message(messageType.WRONG_FORMAT));
                this.clientOutput.flush();
                this.clientSocket.close();
            }
            catch (IOException IOE){
                System.out.println("Error while closing the client socket");
                IOE.printStackTrace();
            }
        }
        //Now we read and send messages in an infinite loop
        while (true){
            Pair<Integer, String> message;
            try {
                message = this.getMessage();
            }
          catch (logOutException LOE){
              System.out.println("Logging out user: " + this.client.ID);
              ServerInterface.database.removeIf((user)->user.getClient().ID.equals(this.client.ID));
              this.activeNotify();
              break;
          }
          catch (IllegalArgumentException IAE){
              System.out.println("Wrong input message format!");
              try {
                  this.clientOutput.writeObject(new Message(messageType.WRONG_FORMAT));
                  this.clientOutput.flush();
              }
              //Client shut down the application
              catch (IOException IOE){
                  System.out.println("Client disconnected - loggin out user: " + this.client.ID);
                  ServerInterface.database.removeIf((user)->user.getClient().ID.equals(this.client.ID));
                  this.activeNotify();
                  break;
              }
              continue;
          }
          catch (IllegalStateException ISE){
              System.out.println("Null message - loggin out user:" + this.client.ID);
              ServerInterface.database.removeIf((user)->user.getClient().ID.equals(this.client.ID));
              this.activeNotify();
              break;
          }
          //If the message is all right we search for output port in the current online database and send there the message
          boolean found = false;
            for (ClientHandler cl : ServerInterface.database){
              if (cl.client.ID.equals(message.getFirst())){
                  this.sendTextMessage(cl.clientOutput, this.client.nick, message.getSecond());
                  //We confirm that we delivered the message to the client
                  this.sendTypeMessage(this.clientOutput, MESSAGE_OK);
                  found = true;
              }
          }
            //In case the client is not found
            if (found = false) {
                this.sendTypeMessage(this.clientOutput, TARGET_NOT_FOUND);
                System.out.println("Error - message cannot be sent to an offline client: " + message.getFirst());
            }
        }
    }
}
