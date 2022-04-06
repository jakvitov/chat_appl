package Server;

import Server.ClientServices.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

//Server interface represents the running server
public class ServerInterface {
    private Thread console;
    //Database that contains currently connected clients
    static HashSet<ClientHandler> database;
    ServerInterface(){
        //We create console and start it in a separate thread
        console = new Thread(new Console(this));
        console.setDaemon(false);
        console.start();
    }

    public static void main(String[] args) {
        //Those null values are safe, because all fails of opening those sockets either
        //break the program or reset listening on port
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(3001);
        }
        catch (IOException IOE){
            System.out.println("Error in opening the server socket");
            IOE.printStackTrace();
            System.exit(1);
        }
        //Infinite loop in which we listen for clients
        while (true){
            try {
                clientSocket = serverSocket.accept();
                System.out.println("New client accepted: " + clientSocket);
            }
            //If we fail to accept the connection we reset the loop and start listening again
            catch (IOException IOE){
                System.out.println("Error while accepting the client.");
                continue;
            }
            ClientHandler clHandler = new ClientHandler(clientSocket);
            Thread clThread = new Thread(clHandler);
            //Now we add the client to the set.
            database.add(clHandler);
            //And starting the client handler in a separate thread
            clThread.start();
        }
    }
}
