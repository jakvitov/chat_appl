package Client.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * A class that takes care of the sending the messages to the server
 * Confirmation of the messages if taken care of by the Message Listener class separately
 */
public class Messenger {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Scanner scanner;

    public Messenger (PrintWriter clientWriter, BufferedReader clientReader, Scanner scanner){
            this.clientWriter = clientWriter;
            this.clientReader = clientReader;
            this.scanner = scanner;
    }

    public void sendMessage(){
        String token = "\\§\\{}\\";
        System.out.println("Insert username of who you want to message: ");
        String targetID = Integer.toString(this.scanner.nextLine().hashCode());
        System.out.println("Enter your message: ");
        String message = this.scanner.nextLine();
        this.clientWriter.println(targetID + token + message + token);
        this.clientWriter.flush();
    }
}
