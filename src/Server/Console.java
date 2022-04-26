package Server;
import java.util.Scanner;

/**
 * The console class represents a debugging console for the server
 * We read from the console in the infinite loop while the server runs
 * The whole server console works in a separate thread
 */

public class Console implements Runnable {
    private Scanner scan;
    private ServerInterface server;
    public Console(Server.ServerInterface server){
        this.scan = new Scanner(System.in);
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Wellcome to the server console.");
        while (true){
            System.out.print("-> ");
            String command = this.scan.nextLine();
            //Command to print all currently online users
            if (command.equals("online ID")){
                System.out.println("Online users: ");
                if (ServerInterface.database.isEmpty()){
                    System.out.println("The online database is empty!");
                    continue;
                }
                ServerInterface.database.forEach((user)->System.out.println(user.getClient().ID));
            }
            else if (command.equals("online name")){
                if (ServerInterface.database.isEmpty()){
                    System.out.println("The online database is empty!");
                    continue;
                }
                System.out.println("Online users: ");
                ServerInterface.database.forEach((user)->System.out.println(user.getClient().nick));
            }
            else if (command.equals("sum")){
                if (ServerInterface.database.isEmpty()){
                    System.out.println("The online database is empty!");
                }
                System.out.println("Total online users: " + ServerInterface.database.size());
            }
            else if (command.equals("stop")){
                System.out.println("Server exits listening!");
                System.exit(0);
                this.server.stop = true;
            }
            else if (command.equals("help") || command.equals("?")){
                System.out.println("online ID");
                System.out.println("online name");
                System.out.println("sum");
                System.out.println("stop");
                this.server.stop = true;
            }
            else if (command.equals("")){
                continue;
            }
            else if (command.equals("clear")){
                System.out.println("\\033[H\\033[2J");
            }
            else {
                System.out.println("Unknown command: " + command);
            }
        }
    }
}

