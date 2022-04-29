package DataStructures;

import java.util.ArrayList;
import java.util.Set;

/**
 * A class representing one message, that will be sent between clients and server
 */

public abstract class message {

    private messageType type;
    private String target =  null;
    private String text = null;
    private String name = null;
    private ArrayList<String> serverActiveList = null;

}
