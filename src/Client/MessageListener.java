package Client;

import Client.Encryption.MessageCrypt;
import Client.History.Archive;
import DataStructures.Message;
import DataStructures.messageType;
import javafx.beans.binding.StringExpression;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * This class will run in a separate thread listening for the messages from the server
 * The class gets the socket reader from the client Interface and uses it as well
 */
public class MessageListener implements Runnable {

    private ObjectInputStream clientInput;
    private Archive archive;
    private MessageCrypt crypt;
    public static Set<String> activeClients;

    MessageListener(ObjectInputStream clientInput, Archive archive, MessageCrypt crypt){
        this.clientInput = clientInput;
        this.archive = archive;
        this.crypt = crypt;
    }

    @Override
    public void run () {
        while (true){
            Message message = new Message();
            try {
                message = (Message) this.clientInput.readObject();
            }
            catch (ClassNotFoundException CNFE){
                System.out.println("Error while reading the message from the socket!");
                CNFE.printStackTrace();
            }
            catch (IOException IOE){
                System.out.println("Error while reading the message from the socket!");
                IOE.printStackTrace();
            }
            if (message == null || message.getType().equals(messageType.EMPTY)){
                continue;
            }
            else if (message.getType().equals(messageType.MESSAGE_OK)){
                System.out.println("Message was delivered all right!");
            }
            else if (message.getType().equals(messageType.WRONG_FORMAT)){
                System.out.println("Targeted user does not currently online on the server!");
            }
            //If the text contains token that indicates, that it contains names of online clients
            else if (message.getType().equals(messageType.ACTIVE)){
                activeClients.clear();
                message.getServerActiveList().forEach((user)->activeClients.add(user));
            }
            else if (message.getType().equals(messageType.TEXT)){
                System.out.println("-------------------------------------------------");
                System.out.println("Incomming message from: " + message.getTarget());
                System.out.println("-------------------------------------------------");
                String finalMessage = crypt.decryptMessage(message.getText());
                this.archive.addInMessage(message.getTarget(), finalMessage);
            }
        }
    }
}
