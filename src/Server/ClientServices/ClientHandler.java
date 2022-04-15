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
        String suffix = "\\ß\\§\\";
        Integer resultID = 0;
        try {
            String input;
            input = this.clientReader.readLine();
            if (input.equals(null)){
                System.out.println("Input is null");
                System.exit(1);
            }
            if (!input.startsWith(suffix) || !input.endsWith(suffix)){
                System.out.println("Wrong client input name format!");
                System.exit(1);
            }
            StringTokenizer tokenizer = new StringTokenizer(input, suffix);
            String number = tokenizer.nextToken();
            String name = tokenizer.nextToken();
            resultID = Integer.parseInt(number);
            this.client = new Client(resultID, name);
            this.clientWriter.println("\\s1 LOGGED IN \\s");
            this.clientWriter.flush();
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
        String messageToken = "\\§\\{}\\";
        Integer intID = new Integer(-1111);
        try {
            input = this.clientReader.readLine();
            //If an error occurs and we scan a null string as an input, to solve this we log out the client
            if (input == null){
                System.out.println("Null input in message");
                Pair lognoutMessage = new Pair(intID, "logout_message");
                return lognoutMessage;
            }
        }
        catch (IOException IOE){
            System.out.println("Error while reading the message from client reader!");
            Pair emptyMessage = new Pair(intID, "empty_message");
            return emptyMessage;
        }
        //In case the client is logging out we receive special pair for logout
        if (input.contains("\\c\\l 101 LOGOUT \\c\\l")){
            Pair lognoutMessage = new Pair(intID, "logout_message");
            return lognoutMessage;
        }
        //If the client is requesting names of the online clients available
        else if (input.equals("\\§\\§\\ONLINE SET\\§\\§\\")){
            Pair message = new Pair(intID, "set_request");
            return message;
        }
        //If the message is a normal message we start here
        StringTokenizer tokenizer = new StringTokenizer(input, messageToken);
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
            this.clientWriter.flush();
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
                  this.clientWriter.flush();
                  continue;
              }
              //We remove clientHandler from the database if he logs out and we cancel this loop to stop the logged out hanler
              else if (message.getSecond().equals("logout_message")){
                  System.out.println("Logging out user: " + this.client.ID);
                  ServerInterface.database.removeIf((user)->user.getClient().ID.equals(this.client.ID));
                  break;
              }
              else if (message.getSecond().equals("set_request")){
                  String outputToken = "\\$~\\";
                  String output = outputToken;
                  for (ClientHandler cl : database){
                      output += (cl.client.nick);
                      output += outputToken;
                  }
                  clientWriter.println(output);
                  clientWriter.flush();
                  continue;
              }
              //If we failed to scan any messages we reset the loop
              else if (message.getSecond().equals("empty_message")){
                  continue;
              }
          }
          //If the message is all right we search for output port in the current online database and send there the message
          boolean found = false;
          String outToken = "\\§~\\";
            for (ClientHandler cl : ServerInterface.database){
              if (cl.client.ID.equals(message.getFirst())){
                  cl.clientWriter.println(outToken + message.getSecond() + outToken + this.client.nick + outToken);
                  cl.clientWriter.flush();
                  //We confirm that we delivered the message to the client
                  this.clientWriter.println("\\s500 Message OK\\s");
                  this.clientWriter.flush();
                  found = true;
              }
          }
            //In case the client is not found
            if (found = false) {
                this.clientWriter.println("\\s446 Client offline \\s");
                this.clientWriter.flush();
                System.out.println("Error - message cannot be sent to an offline client: " + message.getFirst());
            }
        }
    }
}
