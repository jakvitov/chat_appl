package Client.Activity;

import Client.Encryption.MessageCrypt;
import Client.History.Archive;

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
    private Archive archive;
    private MessageCrypt crypt;

    public Messenger (PrintWriter clientWriter, BufferedReader clientReader, Scanner scanner, Archive archive
    ,MessageCrypt crypt){
            this.clientWriter = clientWriter;
            this.clientReader = clientReader;
            this.scanner = scanner;
            this.archive = archive;
            this.crypt = crypt;
    }

    public void sendMessage(){
        String token = "\\§\\{}\\";
        System.out.println("Insert username of who you want to message: ");
        String target = scanner.nextLine();
        String targetID = Integer.toString(target.hashCode());
        System.out.println("Enter your message: ");
        String message = this.scanner.nextLine();
        String finalMessage = crypt.encryptMessage(target,message);
        this.clientWriter.println(targetID + token + finalMessage + token);
        this.clientWriter.flush();
        this.archive.addOutMessage(target, message);
    }
}
