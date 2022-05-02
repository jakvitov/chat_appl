package DataStructures;

import java.io.Serializable;
import java.util.ArrayList;

import static DataStructures.messageType.EMPTY;

/**
 * A class representing one message, that will be sent between clients and server
 * Each messageType guarantess certain values to be not null, all others, that are useless for that are null
 * by default
 */

public class Message implements Serializable {

    private messageType type;
    private String target =  null;
    private String text = null;
    private String name = null;
    private String source = null;
    private ArrayList<String> serverActiveList = null;

    public Message(){
        this.type = EMPTY;
    }

    //Constructor for login message
    public Message(messageType type, String name){
        this.type = type;
        this.name = name;
    }

    //A constructor for only type message (NAME_TAKEN, LOGGED_IN etc.)
    public Message(messageType type){
        this.type = type;
    }

    //A constructor for normal text message
    public Message(messageType type, String target, String text){
        this.type = type;
        this.target = target;
        this.text = text;
    }

    //A constructor for the message that contains all online users
    public Message(messageType type, ArrayList<String> activeList){
        this.type = type;
        this.serverActiveList = activeList;
    }

    public messageType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public ArrayList<String> getServerActiveList() {
        return serverActiveList;
    }
}
