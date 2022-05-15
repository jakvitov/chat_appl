package Client.History;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Archive class will take care of storing conversation with different people
 *
 * All data is held as non persistent just in RAM, to limit the memmory usage we defaultly store only the
 * last 20 messages
 */
public class Archive {

    private final int storeSize = 20;

    private HashMap<String, ObservableList<String>> data;

    public HashMap<String, ObservableList<String>> getData() {
        return data;
    }

    public Archive (){
        data = new HashMap<String, ObservableList<String>>();
    }

    //A method to archive outcomming message from the client
    //We store only the last, storeSize messages, therefore we need to check if we exceed that value and
    //delete the last message if we do
    public void addOutMessage(String target, String message){
        if (data.containsKey(target) && (data.get(target).size() < storeSize)){
            data.get(target).add("You: " + message);
        }
        //If we exceeded given storeSize we must delete the last message from the archive
        else if (data.containsKey(target) && (data.get(target).size() == storeSize)){
            data.get(target).remove(0);
            data.get(target).add("You: " + message);
        }
        //If we do not store the given user yet, we create a new array
        else {
            data.put(target, FXCollections.observableArrayList());
            data.get(target).add("You: " + message);
        }
    }

    //A method to archive incomming message from the client
    public void addInMessage(String from, String message){
        if (data.containsKey(from) && (data.get(from).size() < storeSize)){
            data.get(from).add(from + ": " + message);
        }
        else if (data.containsKey(from) && (data.get(from).size() == storeSize)){
            data.get(from).remove(0);
            data.get(from).add(from + ": " + message);
        }
        else {
            data.put(from, FXCollections.observableArrayList());
            data.get(from).add(from + ": " + message);
        }
    }

    //A method used to print a particular conversation with one person
    public void printConversation(String target){
        System.out.println("--------------------------------------");
        if (data.containsKey(target)){
            data.get(target).forEach(System.out::println);
        }
        System.out.println("--------------------------------------");
    }

    //Return a conversation history with someone as a list
    public ObservableList<String> getConversationList(String target){
        if (data.containsKey(target)){
            return data.get(target);
        }
        return null;
    }

    //A method to initialize empty conversation list with someone
    public void innitEmptyList(ArrayList<String> activeList){
        for (String name : activeList){
            if (data.containsKey(name) == false){
                data.put(name, FXCollections.observableArrayList());
            }
        }
    }
}
