package Server.ClientServices;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListener {
    private Socket clientSocket;
    private BufferedReader clientReader;
    //A listener class that starts to listen on a given port, in the end it returns socket used to communicate
    //with the client.
    public void Listen(int PortNumber){
        try {
            ServerSocket serverSocket = new ServerSocket(PortNumber);
            System.out.println("Server started listening on port: " + PortNumber);
            this.clientSocket = serverSocket.accept();
            System.out.println("Client connection established on port: " + PortNumber);
            this.clientReader =
                    new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));;
        }
        catch (
        IOException IOE){
            System.out.println("Error while opening the server socket.");
            IOE.printStackTrace();
        }
    }
    //A class used to get new client form reding his id fromt the client
    //The official version of the client message containg ID is \a\b<client_id>\a\b
    public Client getNewClient (){
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
            return resultClient;
        }
        catch (NumberFormatException NFE){
            System.out.println("Client message includes characters!");
            NFE.printStackTrace();
        }
        catch (IOException IOE){
            System.out.println("Error while reading from the client socket!");
            IOE.printStackTrace();
        }
        finally {
            return null;
        }
    }
}
