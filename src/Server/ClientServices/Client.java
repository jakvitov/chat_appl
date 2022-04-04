package Server.ClientServices;

import java.net.Socket;

//A class representing one client and all data related to him
public class Client {
    //All attributes are public, because of their pernament usage making getter useless
    public Socket socket;
    public Integer ID;
    public String nick;
    public Client(Socket input_socket, Integer input_id, String input_nick){
        this.socket = input_socket;
        this.ID = input_id;
        this.nick = input_nick;
    }
}
