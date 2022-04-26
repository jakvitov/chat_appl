package Client.Activity;

import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to manage the number of clients who are currently online
 * It runs in a separate thread for each client and once each period of time it refreshes the offline available
 * list of online clients on the server, to whom it is possible to message
 * The basic refresh time is set to 5s
 */
public class onlineClients implements Runnable{

    public static Set<String> onlineClients;
    private PrintWriter clientWriter;
    private int refreshTime;
    private ListView displayList;

    public onlineClients (PrintWriter clientWriter) {
            this.clientWriter = clientWriter;
            this.onlineClients = Collections.synchronizedSet(new HashSet<String>());
            this.refreshTime = 10000;
    }
    public onlineClients (PrintWriter clientWriter, ListView displayList) {
        this.clientWriter = clientWriter;
        this.onlineClients = Collections.synchronizedSet(new HashSet<String>());
        this.refreshTime = 10000;
        this.displayList = displayList;
    }


    //Overloaded constructor in case we want to control refresh time
    public onlineClients (PrintWriter clientWriter, int refreshTime) {
        this.clientWriter = clientWriter;
        this.onlineClients = Collections.synchronizedSet(new HashSet<String>());
        this.refreshTime = refreshTime;
    }

    public static void wait(int time){
        try {
            Thread.sleep(time);
        }
        catch (java.lang.InterruptedException jlIE){
            System.out.println("Thread interrupted while sleeping!");
        }
    }

    //We only send the request to the server, the respond is taken care of by the MessangeListener class
    public void refreshList(){
        this.clientWriter.println("\\§\\§\\ONLINE SET\\§\\§\\");
        this.clientWriter.flush();
    }

    public void printOnline(){
        System.out.println("--------------------------");
        System.out.println("Online clients:");
        onlineClients.forEach(System.out::println);
        System.out.println("--------------------------");
    }

    public void refreshOnScreen(){
        ArrayList<String> onlineList = new ArrayList<String>();
        onlineClients.forEach((user)->onlineList.add(user));
        this.displayList.getItems().clear();
        for (String user : onlineList) {
            this.displayList.getItems().add(user);
        }
    }

    @Override
    public void run (){
        while (true) {
            this.refreshList();
            if (this.displayList != null){
                this.refreshOnScreen();
            }
            wait(this.refreshTime);
        }
    }
}
