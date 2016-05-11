package client;

import java.io.Serializable;

/**
 * This class is the ChatMessage that are sent between clients and server.
 * Has three different kinds of options depending on what the message if for.
 * Created by Michael on 2016-04-01.
 */
public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1L;

    // This is sent to retrieve who is connected to a port.
    public static final int LOBBY = 0;

    // A normal message sent between clients.
    public static final int MESSAGE = 1;

    // Sent to the server to logout the client.
    public static final int LOGOUT = 2;

    // The numerical type of message.
    private int type;

    // This is the data string that contains the message.
    private String message;

    // The client name.
    private String sender;

    // Constructor
    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    // Getters and setters
    public int getType() {
        return this.type;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSender() {
        return this.sender;}

    public void setSender(String sender){
        this.sender = sender;}

}