package Client.Activity;

import Client.Encryption.MessageCrypt;
import Client.History.Archive;
import DataStructures.Message;
import DataStructures.messageType;

import java.io.*;
import java.util.Scanner;

/**
 * A class that takes care of the sending the messages to the server
 * Confirmation of the messages if taken care of by the Message Listener class separately
 */
public class Messenger {

    private ObjectOutputStream clientOutput;
    private ObjectInputStream clientIntput;
    private Scanner scanner;
    private Archive archive;
    private MessageCrypt crypt;

    //Constructor with the input scanner, - we take user input later - CLI
    public Messenger (ObjectOutputStream clientOutput, ObjectInputStream clientInput, Scanner scanner, Archive archive
            ,MessageCrypt crypt){
        this.clientIntput = clientInput;
        this.clientOutput = clientOutput;
        this.scanner = scanner;
        this.archive = archive;
        this.crypt = crypt;
    }

    //A constructor for the class without input scanner - we do not take user input, just send over the messages (GUI)
    public Messenger (ObjectOutputStream clientOutput, ObjectInputStream clientInput, Archive archive
            ,MessageCrypt crypt){
        this.clientIntput = clientInput;
        this.clientOutput = clientOutput;
        this.archive = archive;
        this.crypt = crypt;
    }

    //Given output stream, target and text, send message to that given output stream, usually called by the
    //message method
    public void sendTextMessage(ObjectOutputStream ooe, String target, String text){
        try {
            Message message = new Message(messageType.TEXT, target , text);
            ooe.writeObject(message);
            ooe.flush();
        }
        catch (IOException IOE){
            System.out.println("Error while sending the text - message");
        }
    }

    //A method for the CLI client to scan a message form the terminal and send it
    public void sendMessage(){
        System.out.println("Insert username of who you want to message: ");
        String target = scanner.nextLine();
        String targetID = Integer.toString(target.hashCode());
        System.out.println("Enter your message: ");
        String message = this.scanner.nextLine();
        String finalMessage = crypt.encryptMessage(target,message);
        this.sendTextMessage(this.clientOutput, targetID, finalMessage);
        this.archive.addOutMessage(target, message);
    }

    //Send text message to the given target
    public void message(String target, String message){
        String targetID = Integer.toString(target.hashCode());
        String finalMessage = crypt.encryptMessage(target,message);
        this.sendTextMessage(this.clientOutput, targetID, finalMessage);
        this.archive.addOutMessage(target, message);
    }
}
