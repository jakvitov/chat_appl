package Server;
import java.util.Scanner;

//The console class represents a debugging console for the server
//We read from the console in the infinite loop while the server runs
//The whole server console works in a separate thread
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
            if (command.equals("online")){
                System.out.println("Online users: ");
                this.server.getOnline_ids().forEach((user)->System.out.println(user.ID));
            }
        }
    }
}

