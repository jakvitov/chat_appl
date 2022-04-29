package DataStructures;

import java.util.ArrayList;
import java.util.Set;

/**
 * A message that contains active members on the server
 */
public class activeMessage extends message{

    private ArrayList<String> list;
    private messageType type;

    public activeMessage(Set<String> activeSet) {
        this.list = new ArrayList<String>();
        activeSet.forEach(user -> list.add(user));
        this.type = messageType.ACTIVE;
    }
}
