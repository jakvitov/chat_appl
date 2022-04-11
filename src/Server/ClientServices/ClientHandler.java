package Server.ClientServices;

import DataStructures.Pair;
import Server.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import static Server.ServerInterface.database;

//A class to be called to handle a client in a single thread
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    private Client client;

    public Client getClient() {
        return client;
    }

    //Constructor opening sockets for the client
    public ClientHandler (Socket inputSocket){
        this.clientSocket = inputSocket;
        try {
            this.clientReader =
                    new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.clientWriter = new PrintWriter(this.clientSocket.getOutputStream());
        }
        catch (IOException IOE){
            System.out.println("Error while opening client reader/writer.");
            IOE.printStackTrace();
        }
    }
    //Method to get the name from the client
    //A class used to get new client form reding his id fromt the client
    //The official version of the client message containg ID is \c\i<client_id>\c\i
    //This method must be used as the second, so the instance can get additional client info
    public void getNewClient () throws IllegalArgumentException {
        String suffix = "\\c\\i";
        Integer resultID = 0;
        try {
            String input = this.clientReader.readLine();
            if (input.length() <= 8 || !input.startsWith(suffix) || !input.endsWith(suffix)){
                System.out.println("Wrong client input name format!");
            }
            input.replace(suffix, "");
            resultID = Integer.parseInt(input);
            this.client = new Client(resultID, "not_implemented_yet");
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
        String input;
        Integer intID = new Integer(-1111);
        try {
            input = this.clientReader.readLine();
        }
        catch (IOException IOE){
            System.out.println("Error while reading the message from client reader!");
            Pair emptyMessage = new Pair(intID, "empty_message");
            return emptyMessage;
        }
        //In case the client is logging out we receive special pair for logout
        if (input.equals("\\c\\l 101 LOGOUT \\c\\l")){
            Pair lognoutMessage = new Pair(intID, "logout_message");
            return lognoutMessage;
        }
        StringTokenizer tokenizer = new StringTokenizer(input, "\\c\\m");
        String strID = tokenizer.nextToken();
        String messageText = tokenizer.nextToken();
        try {
           intID = Integer.parseInt(strID);
        }
        catch (NumberFormatException NFE){
            System.out.println("Invalid client ID!");
            Pair wrongTargetMessage = new Pair(-1111, "wrong_target");
            return wrongTargetMessage;
        }
        Pair rightMessage = new Pair(intID, messageText);
        return rightMessage;
    }
    @Override
    public void run() {
        //First we get new clients id and initialize client variable
        try {
            this.getNewClient();
        }
        //In case the client name has wrong format
        catch (IllegalArgumentException IAE){
            this.clientWriter.println("\\s444 Wrong client ID\\s");
            try {
                this.clientSocket.close();
            }
            catch (IOException IOE){
                System.out.println("Error while closing the client socket");
                IOE.printStackTrace();
            }
        }
        //Now we read and send messages in an infinite loop
        while (true){
          Pair<Integer, String> message = this.getMessage();
          //In case the message has special code
          if (message.getFirst().equals(-1111)){
              if (message.getSecond().equals("wrong_target")){
                  this.clientWriter.println("\\s445 Wrong target ID\\s");
              }
              //We remove clientHandler from the database if he logs out
              else if (message.getSecond().equals("logout_message")){
                  database.removeIf((user)->user.getClient().ID.equals(this.client.ID));
              }
              //If we failed to scan any messages we reset the loop
              else if (message.getSecond().equals("empty_message")){
                  continue;
              }
          }
          //If the message is all right we search for output port in the current online database and send there the message
          boolean found = false;
            for (ClientHandler cl : database){
              if (cl.client.ID.equals(message.getFirst())){
                  cl.clientWriter.println("\\cm" + message.getSecond() + "\\cm");
                  found = true;
              }
          }
            //In case the client is not found
            if (found = false) {
                this.clientWriter.println("\\s446 Client offline \\s");
                System.out.println("Error - message cannot be sent to an offline client: " + message.getFirst());
            }
        }
    }
}
