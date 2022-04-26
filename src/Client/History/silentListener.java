package Client.History;

import Client.Encryption.MessageCrypt;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import static Client.Activity.onlineClients.onlineClients;

/**
 * This class will silently listen to all communication and add new messages to the archive
 * After each message is delivered, we display alert, wait for some time and hide it
 */
public class silentListener implements Runnable {

    private BufferedReader clientReader;
    private Archive archive;
    private MessageCrypt crypt;
    private Label messageAlert;

    public silentListener(BufferedReader inputReader, Archive archive, MessageCrypt crypt, Label messageAlert){
        this.clientReader = inputReader;
        this.archive = archive;
        this.crypt = crypt;
        this.messageAlert = messageAlert;
    }

    public static void wait(int time){
        try {
            Thread.sleep(time);
        }
        catch (java.lang.InterruptedException jlIE){
            System.out.println("Thread interrupted while sleeping!");
        }
    }

    public void promptAlert (String text){
        this.messageAlert.setVisible(true);
        this.messageAlert.setText(text);
        wait(1000);
        this.messageAlert.setVisible(false);
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
                this.messageAlert.setVisible(true);
                this.messageAlert.setText("Your message was delivered all right!");
                wait(1000);
                this.messageAlert.setVisible(false);
            }
            else if (text.equals("\\s445 Wrong target ID\\s")){
                this.promptAlert("Wrong message target!");
            }
            else if (text.equals("\\s446 Client offline \\s")){
                this.promptAlert("Wrong message target!");
            }
            //If the text contains token that indicates, that it contains names of online clients
            else if (text.startsWith("\\$~\\")){
                onlineClients.clear();
                StringTokenizer tokenizer = new StringTokenizer(text,"\\$~\\");
                while(tokenizer.hasMoreTokens()){
                    onlineClients.add(tokenizer.nextToken());
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

                this.promptAlert("Incomming message from: " + from);

                String finalMessage = crypt.decryptMessage(message);
                this.archive.addInMessage(from, finalMessage);
            }
        }
    }
}
