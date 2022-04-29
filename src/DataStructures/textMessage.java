package DataStructures;

/**
 * A message including the target and text for the client to be delivered
 */
public class textMessage extends message{

    private String source;
    private String target;
    private String text;
    private messageType type;

    public textMessage(String source, String target, String text){
        this.source = source;
        this.target = target;
        this.text = text;
        this.type = messageType.TEXT;
    }

}
