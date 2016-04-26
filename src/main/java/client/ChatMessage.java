package client;

import java.io.Serializable;

/*
* This class defines the different type of messages that will be exchanged between the
* Clients and the Server.
*
* Created by Michael on 2016-02-27.
*/
public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1L;

    //lists connected users
    public static final int WHOISIN = 0;

    //a normal message
    public static final int MESSAGE = 1;

    //send a logout command to the server
    public static final int LOGOUT = 2;

    //the type of message
    private int type;

    private String message;

    private String sender;

    // constructor
    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    //getters
    public int getType() {
        return this.type;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSender() { return this.sender;}

}