package Client;

/**
 * A basic Console client Interface
 * This class is the core of the client communication with the server
 */

import Client.Activity.Logger;
import Client.Activity.Messenger;
import Client.Activity.onlineClients;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientInterface {

    private Socket socket  = null;
    private Logger logger;
    private Messenger messenger;
    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private String clientID;
    private String name;
    private Scanner scan;

    public static void wait(int time){
        try {
            Thread.sleep(time);
        }
        catch (java.lang.InterruptedException jlIE){
            System.out.println("Thread interrupted while sleeping!");
        }
    }

    public ClientInterface (){
        this.scan = new Scanner(System.in);
        System.out.println("Enter your name: ");
        this.name = scan.nextLine();
        try {
            this.socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 3001);
            System.out.println("Connected!");
            this.clientWriter = new PrintWriter(this.socket.getOutputStream());
            this.clientReader =
                    new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.logger = new Logger(this.clientWriter, this.clientReader);
            this.messenger = new Messenger(this.clientWriter, this.clientReader, this.scan);
        }
        catch (UnknownHostException u){
            System.out.println("Unknown host!");
            u.printStackTrace();
            System.exit(1);
        }
        catch (java.net.ConnectException jnCE){
            System.out.println("The server is currently offline!");
            jnCE.printStackTrace();
            System.exit(1);
        }
        catch (IOException IOE) {
            System.out.println("Error while creating the client socket");
            System.exit(1);
        }
    }
    public BufferedReader getClientReader() {
        return clientReader;
    }

    public PrintWriter getClientWriter() {
        return clientWriter;
    }

    public void logIn() {
        wait(1000);
        this.logger.logIn(this.name);
    }
    public void logOut(){
        this.logger.logOut();
    }
    public void message(){
        this.messenger.sendMessage();
    }
    public static void main(String[] args) {
        ClientInterface client = new ClientInterface();

        Thread messageListener = new Thread(new MessageListener(client.getClientReader()));
        onlineClients clients = new onlineClients(client.getClientWriter());
        Thread onlineClients = new Thread(clients);

        client.logIn();
        messageListener.start();
        onlineClients.start();

        while (true){
            System.out.println("What do you want to do?");
            System.out.println("Write \"help\" to see your options");
            String command = client.scan.nextLine();

            if (command.equals("message")){
                client.message();
            }
            else if (command.equals("online")){
                clients.printOnline();
            }
            else if (command.equals("help")){
                System.out.println("message");
                System.out.println("online");
                System.out.println("stop");
            }
            else if (command.equals("stop")){
                break;
            }
        }
        client.logOut();
    }
}
