package Client;

/**
 * A basic Console client Interface
 * This class is the core of the client communication with the server
 */

import Client.Activity.Logger;
import Client.Activity.Manual;
import Client.Activity.Messenger;
import Client.Encryption.MessageCrypt;
import Client.History.Archive;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

import static Client.MessageListener.activeClients;

public class ClientInterface {

    private Socket socket  = null;
    private Logger logger;
    private Messenger messenger;
    private String clientID;
    private String name;
    private Scanner scan;
    private Archive archive;
    private MessageCrypt crypt;
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

    public Archive getArchive() {
        return archive;
    }

    public ClientInterface (){
        this.scan = new Scanner(System.in);
        System.out.println("Enter your name: ");
        this.name = scan.nextLine();
        try {
            this.socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 3001);
            System.out.println("Connected!");
            this.clientOutput = new ObjectOutputStream(this.socket.getOutputStream());
            this.clientInput = new ObjectInputStream(this.socket.getInputStream());
            this.logger = new Logger(this.clientInput, this.clientOutput);
            this.crypt = new MessageCrypt(name);
            this.archive = new Archive();
            this.messenger = new Messenger(this.clientOutput, this.clientInput, this.scan, this.archive, this.crypt);
        }
        catch (UnknownHostException u){
            System.out.println("Unknown host!");
            System.exit(1);
        }
        catch (java.net.ConnectException jnCE){
            System.out.println("The server is currently offline!");
            System.exit(1);
        }
        catch (IOException IOE) {
            System.out.println("Error while creating the client socket");
            System.exit(1);
        }
    }
    public ObjectInputStream getClientInput() {
        return this.clientInput;
    }

    public ObjectOutputStream getClientOutput() {
        return this.clientOutput;
    }

    public void logIn() {
        while (this.logger.logIn(this.name) == false){
            System.out.println("Enter your name: ");
            this.name = scan.nextLine();
            wait(100);
        }
    }
    public void logOut(){
        this.logger.logOut();
    }
    public void message(){
        this.messenger.sendMessage();
    }
    public static void main(String[] args) {
        ClientInterface client = new ClientInterface();
        Manual man = new Manual();

        Thread messageListener = new Thread(new MessageListener(client.getClientInput(), client.getArchive(), client.crypt));
        messageListener.setDaemon(false);

        client.logIn();
        messageListener.start();

        while (true){

            System.out.println("------------------------------------");
            System.out.println("What do you want to do?");
            System.out.println("Write \"help\" to see your options");
            System.out.println("------------------------------------");

            String command = client.scan.nextLine();

            if (command.equals("message")){
                client.message();
            }
            else if (command.equals("online")){
                System.out.println("Online Clients");
                activeClients.forEach(System.out::println);
            }
            else if (command.equals("help") || command.equals("?")){
                man.show();
            }
            else if (command.contains("show")){
                StringTokenizer tokenizer = new StringTokenizer(command, " ");
                if (tokenizer.countTokens() >= 2){
                    tokenizer.nextToken();
                    String target = tokenizer.nextToken();
                    System.out.println("Showing history for " + target);
                    client.archive.printConversation(target);
                }
            }
            else if (command.equals("stop")){
                break;
            }
        }
        client.logOut();
    }
}
