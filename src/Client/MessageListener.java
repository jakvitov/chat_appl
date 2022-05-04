package Client;

import Client.Encryption.MessageCrypt;
import Client.History.Archive;
import DataStructures.Message;
import DataStructures.messageType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;


/**
 * This class will run in a separate thread listening for the messages from the server
 * The class gets the socket reader from the client Interface and uses it as well
 *
 * The silent parameter in the constructors is used to turn on and off console/GUI version of the listener
 */
public class MessageListener implements Runnable {

    private ObjectInputStream clientInput;
    private Archive archive;
    private MessageCrypt crypt;
    private boolean silent;
    public static Set<String> activeClients;
    public static ObservableList<String> observableClients;

    MessageListener(ObjectInputStream clientInput, Archive archive, MessageCrypt crypt){
        activeClients = new HashSet<String>();
        observableClients = FXCollections.observableArrayList();
        this.clientInput = clientInput;
        this.archive = archive;
        this.crypt = crypt;
        this.silent = false;
    }


    MessageListener(ObjectInputStream clientInput, Archive archive, MessageCrypt crypt, boolean silent){
        activeClients = new HashSet<String>();
        this.clientInput = clientInput;
        this.archive = archive;
        this.crypt = crypt;
        this.silent = true;
    }

    @Override
    public void run () {
        while (true){
            Message message = new Message();
            try {
                message = (Message) this.clientInput.readObject();
            }
            catch (ClassNotFoundException CNFE){
                if (this.silent == true) {
                    continue;
                }
                System.out.println("Error while reading the message from the socket!");
                CNFE.printStackTrace();
            }
            catch (IOException IOE){
                if (this.silent == true) {
                    continue;
                }
                System.out.println("Error while reading the message from the socket!");
                IOE.printStackTrace();
            }
            if (message == null || message.getType().equals(messageType.EMPTY)){
                continue;
            }
            else if (message.getType().equals(messageType.MESSAGE_OK)){
                if (this.silent == true) {
                    continue;
                }
                System.out.println("Message was delivered all right!");
            }
            else if (message.getType().equals(messageType.WRONG_FORMAT)){
                if (this.silent == true) {
                    continue;
                }
                System.out.println("Targeted user does not currently online on the server!");
            }
            //If the text contains token that indicates, that it contains names of online clients
            else if (message.getType().equals(messageType.ACTIVE)){
                activeClients.clear();
                observableClients.clear();
                message.getServerActiveList().forEach((user)->activeClients.add(user));
                message.getServerActiveList().forEach((user)->observableClients.add(user));
            }
            else if (message.getType().equals(messageType.TEXT)){
                if (this.silent == false) {
                    System.out.println("-------------------------------------------------");
                    System.out.println("Incomming message from: " + message.getTarget());
                    System.out.println("-------------------------------------------------");
                }
                String finalMessage = crypt.decryptMessage(message.getText());
                this.archive.addInMessage(message.getTarget(), finalMessage);
            }
        }
    }
}
