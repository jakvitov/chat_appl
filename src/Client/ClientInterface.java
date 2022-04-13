package Client;
/**
 * A basic Console client Interface
 * This class is the core of the client communication with the server
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientInterface {

    private Socket socket  = null;

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private String clientID;
    private Scanner scan;
    public ClientInterface (){
        this.scan = new Scanner(System.in);
        System.out.println("Enter your client ID: ");
        this.clientID = scan.nextLine();
        try {
            this.socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 3001);
            System.out.println("Connected!");
            this.clientWriter = new PrintWriter(this.socket.getOutputStream());
            this.clientReader =
                    new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
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
    public void logIn() {
        try{
                Thread.sleep(1000);
        }
        catch (Exception IOE){
                System.out.println("Chyba");
        }

        System.out.println("Logging in as " + this.clientID);
        this.clientWriter.println("\\c\\i" + this.clientID + "\\c\\i");
        this.clientWriter.flush();
        try {
            String response = this.clientReader.readLine();
            if (response.equals("\\s1 LOGGED IN \\s")){
                System.out.println("Sucesfully logged in!");
            }
            else {
                System.out.println("Login failed " + response);
            }
        } catch (IOException IOE) {
            System.out.println("Error while reading the server response");
        }
    }
    public static void wait(int time){
        try {
            Thread.sleep(time);
        }
        catch (java.lang.InterruptedException jlIE){
            System.out.println("Thread interrupted while sleeping!");
        }
    }
    public void logOut(){
        System.out.println("Logging out...");
        this.clientWriter.print("\\c\\l 101 LOGOUT \\c\\l");
        this.clientWriter.flush();
    }
    public void message(){
        System.out.println("Insert ID of who you want to message: ");
        String targetID = scan.nextLine();
        System.out.println("Enter your message: ");
        String message = scan.nextLine();
        this.clientWriter.println(targetID + "\\c\\m" + message + "\\c\\m");
        this.clientWriter.flush();
    }
    public static void main(String[] args) {
        ClientInterface client = new ClientInterface();
        Thread messageListener = new Thread(new MessageListener(client.getClientReader()));
        client.logIn();

        messageListener.start();
        for (int i = 0; i < 5; i ++){
            client.message();
        }

        client.logOut();
    }
}
