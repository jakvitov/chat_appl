package Server.ClientServices;

import DataStructures.Pair;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ClientListener{
    private Socket clientSocket;
    private BufferedReader clientReader;

    public Client client;
    //A listener class that starts to listen on a given port, in the end it returns socket used to communicate
    //with the client.
    //This method must be used as the first, so the instance can bind to a particular client port
    public ClientListener (int PortNumber){
        try {
            ServerSocket serverSocket = new ServerSocket(PortNumber);
            System.out.println("Server started listening on port: " + PortNumber);
            this.clientSocket = serverSocket.accept();
            System.out.println("Client connection established on port: " + PortNumber);
            this.clientReader =
                    new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        }
        catch (
        IOException IOE){
            System.out.println("Error while opening the server socket.");
            IOE.printStackTrace();
        }
    }
    //A class used to get new client form reding his id fromt the client
    //The official version of the client message containg ID is \a\b<client_id>\a\b
    //This method must be used as the second, so the instance can get additional client info
    public void getNewClient (){
        String suffix = "\\a\\b";
        Integer resultID = 0;
        try {
            String input = this.clientReader.readLine();
            if (input.length() <= 8 || !input.startsWith(suffix) || !input.endsWith(suffix)){
                System.out.println("Wrong client input name format!");
            }
            input.replace(suffix, "");
            resultID = Integer.parseInt(input);
            //Nicks will be laster implemented as getting them from the database
            Client resultClient = new Client(this.clientSocket, resultID, "Not_implemented");
            this.client = resultClient;
        }
        catch (NumberFormatException NFE){
            System.out.println("Client message includes characters!");
            NFE.printStackTrace();
        }
        catch (IOException IOE){
            System.out.println("Error while reading from the client socket!");
            IOE.printStackTrace();
        }
    }
    //Listen for a specified message by client
    //We return Pair representing target_id and message as a string
    //Format of every message if <target>\t\g<message>
    public Pair<Integer, String> getMessage(Socket clientSocket){
        String separator = "\\t\\g";
        try {
        String message = this.clientReader.readLine();
        //We check for separator presence in message
        if (!message.contains(separator)){
            System.out.println("Wrong message format: " + message);
        }
        //We derive the tarvet and text from the message
        String target = message.substring(0, message.indexOf(separator));
        String text = message.substring(message.indexOf(separator, message.length()));
        Integer int_target = Integer.parseInt(target);
        var result = new Pair<Integer, String>(int_target, text);
        return result;
        }
        catch (NumberFormatException NFE){
            System.out.println("Target in message contains illigal characters!");
            NFE.printStackTrace();
        }
        catch (IOException IOE){
            System.out.println("Error while reading the message from the client!");
            IOE.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientListener that = (ClientListener) o;
        return clientSocket.equals(that.clientSocket) && clientReader.equals(that.clientReader) && client.equals(that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientSocket, clientReader, client);
    }

}
