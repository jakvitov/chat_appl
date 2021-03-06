package Server.ClientServices;
/**
 * The client data structure holding all the information about Client
 * This class is purely used for holding the data, therefore all parameters are declared public
 */

import java.io.BufferedReader;
import java.net.Socket;
import java.util.Objects;

//A class representing one client and all data related to him
public class Client {
    public Client(int input_ID, String input_nick){
        this.ID = input_ID;
        this.nick = input_nick;
    }
    //All attributes are public, because Client is purely a data structure
    public Integer ID;
    public String nick;
}
