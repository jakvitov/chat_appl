package Server;
import java.util.Scanner;

//The console class represents a debugging console for the server
//We read from the console in the infinite loop while the server runs
//The whole server console works in a separate thread
public class Console implements Runnable {
    Scanner scan;
    public void Console(){
        this.scan = new Scanner(System.in);
    }
    @Override
    public void run() {
        System.out.println("Wellcome to the server console.");
        while (true){
            System.out.print("-> ");
            String command = this.scan.nextLine();
        }
    }
}

