package DataStructures;

public class logOutMessage extends message{

    private messageType type;
    private String source;

    public logOutMessage(messageType type, String source) {
        this.type = messageType.LOGOUT;
        this.source = source;
    }
}
