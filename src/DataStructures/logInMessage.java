package DataStructures;

public class logInMessage extends message{

    private messageType type;
    private String name;

    public logInMessage(messageType type, String name) {
        this.type = messageType.LOGIN;
        this.name = name;
    }
}
