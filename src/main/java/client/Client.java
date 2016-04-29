package client;

import services.datapackage.DatapackageGenerator;
import services.datapackage.DatapackageGeneratorImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Michael on 2016-02-27.
 */
public class Client {

    private ObjectInputStream sInput;       // to read from the socket
    private ObjectOutputStream sOutput;     // to write on the socket
    private Socket socket;

    private SecureChatUI secureChatUI;

    // the server, the port and the username
    private String server;
    private String username;
    private int port;

    private boolean isServer;

    private DatapackageGenerator datapackageGenerator;


    Client(String server, int port, String username, SecureChatUI secureChatUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.secureChatUI = secureChatUI;
        this.datapackageGenerator = new DatapackageGeneratorImpl();
    }

    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
        }
        catch(Exception e) {
            display("Error connecting to server:" + e);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
        System.out.println(msg);
        display(msg);

        // Creating both Data Stream
        try
        {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ServerListener().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException e) {
            display("Exception doing login : " + e);
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }


    // To send a message to the console or the GUI

    private void display(String msg) {
        if(secureChatUI == null)
            System.out.println("display secureChatUI == null");      // println in console mode
        else
            secureChatUI.append(msg);      // append to the ClientGUI TextArea
    }


    // To send a message to the server
    void sendMessage(ChatMessage chatmsg) {
        ChatMessage chatMessage = chatmsg;
        String message = chatMessage.getMessage();
        switch(chatmsg.getType()){
            case ChatMessage.MESSAGE:
                String generateMsg = datapackageGenerator.generateDatapackage(message);
                chatMessage.setMessage(generateMsg);
                break;
            case ChatMessage.LOGOUT:
                display("Connection is closed!" + "\n");
                chatMessage.setMessage(message);
                break;
            case ChatMessage.WHOISIN:
                chatMessage.setMessage(message);
                break;
        }
        try {
            sOutput.writeObject(chatMessage);
        }
        catch(IOException e) {
            display("Exception sending message to server: " + e + "\n");
        }
    }

    void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {}
    }

    public static void main(String[] args) {

    }

    public boolean isServer() {
        return isServer;
    }

    /*
     * a class that waits for the message from the server and append them to the TextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ServerListener extends Thread {

        public void run() {
            while(true) {
                try {
                    Object obj = sInput.readObject();
                    if (obj.getClass().equals(ChatMessage.class)){
                        ChatMessage chatMessage = (ChatMessage) obj;
                        String msg = chatMessage.getMessage();
                        String openMsg = datapackageGenerator.openDatapackage(msg);
                        secureChatUI.append(openMsg + "\n");
                    }else if (obj.getClass().equals(String.class)){
                        String msg = (String) obj;
                        secureChatUI.append(msg);
                    }
                }
                catch(IOException e) {
                    //display("Server has close the connection: " + e);
                }
                catch(ClassNotFoundException e) {
                    System.out.println("ClassNotFoundException i ServerListener!");
                }
            }
        }
    }
}
