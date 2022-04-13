package Client.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * A class that takes care of the sending the messages to the server
 *
 */
public class Messenger {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Scanner scanner;

    public void Messenger (PrintWriter clientWriter, BufferedReader clientReader, Scanner scanner){
            this.clientWriter = clientWriter;
            this.clientReader = clientReader;
            this.scanner = scanner;
    }

    //A function used to determine if a message was delivered succesfully
    public boolean confirmMessage(String targetID){
        //A part where we check if the message was delivered
        try {
            String confirmation = this.clientReader.readLine();
            if (confirmation == null){
                return false;
            }
            else if (confirmation.equals("\\s500 Message OK\\s")){
                return true;
            }
            else if (confirmation.equals("\\s446 Client offline \\s")){
                    System.out.println(targetID + " is currently offline.");
                    return false;
            }
            else {
                return false;
            }
        }
        catch (IOException IOe){
            System.out.println("Error while reading from the socket!");
            System.exit(1);
        }
        return false;
    }

    public boolean sendMessage(){
        String token = "\\§\\{}\\";
        System.out.println("Insert ID of who you want to message: ");
        String targetID = this.scanner.nextLine();
        System.out.println("Enter your message: ");
        String message = this.scanner.nextLine();
        this.clientWriter.println(targetID + token + message + token);
        this.clientWriter.flush();
        if (confirmMessage(targetID)){
            return true;
        }
        else {
            System.out.println("Message was not delivered due to an error.");
            return false;
        }
    }
}
