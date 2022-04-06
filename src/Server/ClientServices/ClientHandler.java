package Server.ClientServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
    @Override
    public void run() {
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
    }
}
