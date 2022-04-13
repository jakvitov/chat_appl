package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * This class will run in a separate thread listening for the messages from the server
 * The class gets the socket reader from the client Interface and uses it as well
 */
public class MessageListener implements Runnable {

    private BufferedReader clientReader;

    MessageListener(BufferedReader inputReader){
        this.clientReader = inputReader;
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
            if (text.equals("\\s500 Message OK\\s")){
                System.out.println("Message was delivered all right!");
            }
            if (text.equals("\\s445 Wrong target ID\\s")){
                System.out.println("Targeted user does not exist!");
            }
            if (text.equals("\\s446 Client offline \\s")){
                System.out.println("Requested client is currently offline!");
            }
            //If the message is not valid we continue looping for the next one
            if (!text.startsWith("\\§~\\") || !text.endsWith("\\§~\\")){
                continue;
            }
            StringTokenizer tokenizer = new StringTokenizer(text, "\\§~\\");
            String message = tokenizer.nextToken();
            String from = tokenizer.nextToken();
            System.out.println("-------------------------------------------------");
            System.out.println("Incomming message: " + message + ", from: " + from);
            System.out.println("-------------------------------------------------");
        }
    }
}
