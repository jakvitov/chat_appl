package Client;

import Client.Encryption.MessageCrypt;
import Client.History.Archive;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import static Client.Activity.onlineClients.activeClients;

/**
 * This class will run in a separate thread listening for the messages from the server
 * The class gets the socket reader from the client Interface and uses it as well
 */
public class MessageListener implements Runnable {

    private BufferedReader clientReader;
    private Archive archive;
    private MessageCrypt crypt;

    MessageListener(BufferedReader inputReader, Archive archive, MessageCrypt crypt){
        this.clientReader = inputReader;
        this.archive = archive;
        this.crypt = crypt;
    }

    @Override
    public void run () {
        while (true){
            String text = "empty";
            try {
                text = clientReader.readLine();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the socket!");
                IOE.printStackTrace();
            }
            if (text == null){
                continue;
            }
            else if (text.equals("\\s500 Message OK\\s")){
                System.out.println("Message was delivered all right!");
            }
            else if (text.equals("\\s445 Wrong target ID\\s")){
                System.out.println("Targeted user does not exist!");
            }
            else if (text.equals("\\s446 Client offline \\s")){
                System.out.println("Requested client is currently offline!");
            }
            //If the text contains token that indicates, that it contains names of online clients
            else if (text.startsWith("\\$~\\")){
                activeClients.clear();
                StringTokenizer tokenizer = new StringTokenizer(text,"\\$~\\");
                while(tokenizer.hasMoreTokens()){
                    activeClients.add(tokenizer.nextToken());
                }
            }
            //If the message is not valid we continue looping for the next one
            else if (!text.startsWith("\\§~\\") || !text.endsWith("\\§~\\")){
                continue;
            }
            else {
                StringTokenizer tokenizer = new StringTokenizer(text, "\\§~\\");
                String message = tokenizer.nextToken();
                String from = tokenizer.nextToken();
                System.out.println("-------------------------------------------------");
                System.out.println("Incomming message from: " + from);
                System.out.println("-------------------------------------------------");
                String finalMessage = crypt.decryptMessage(message);
                this.archive.addInMessage(from, finalMessage);
            }
        }
    }
}
