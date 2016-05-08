package client;

import services.datapackage.DatapackageGenerator;
import services.datapackage.DatapackageGeneratorImpl;
import services.hash.HashGenerator;
import services.hash.HashGeneratorImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
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
    private byte[] symmetricKey;

    private DatapackageGenerator datapackageGenerator;
    private HashGenerator hashGenerator;


    Client(String server, int port, String username, String password, SecureChatUI secureChatUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.secureChatUI = secureChatUI;
        this.datapackageGenerator = new DatapackageGeneratorImpl();
        this.hashGenerator = new HashGeneratorImpl();
        this.symmetricKey = createSymmetricKey(password);
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

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(msg);
        display(msg);

        // Creating both Data Stream
        try
        {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            display("Exception creating new Input/output Streams: " + e);
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

    // Create a symmetricKey from the password
    private byte[] createSymmetricKey(String password){
        return hashGenerator.generate(password, 256);
    }


    // To send a message to the console or the GUI
    private void display(String msg) {
        // append to the ClientGUI TextArea
        secureChatUI.append(msg);
    }


    // To send a message to the server
    void sendMessage(ChatMessage chatmsg) {
        ChatMessage chatMessage = chatmsg;
        String message = chatMessage.getMessage();
        switch(chatmsg.getType()){
            case ChatMessage.MESSAGE:
                String generateMsg = datapackageGenerator.generateDatapackage(message, symmetricKey);
                chatMessage.setMessage(generateMsg);
                break;
            case ChatMessage.LOGOUT:
                chatMessage.setMessage(message);
                break;
            case ChatMessage.LOBBY:
                chatMessage.setMessage(message);
                break;
        }
        try {
            sOutput.writeObject(chatMessage);
        }
        catch(IOException e) {
            display("Exception sending message to server: " + e);
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

    public SecureChatUI getSecureChatUI() {
        return this.secureChatUI;
    }

    /*
     * a class that waits for the message from the server and append them to the TextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ServerListener extends Thread {

        Boolean keepRunning = true;
        public void run() {
            while(keepRunning) {
                try {
                    Object obj = sInput.readObject();
                    if (obj.getClass().equals(ChatMessage.class)){
                        ChatMessage chatMessage = (ChatMessage) obj;
                        String msg = chatMessage.getMessage();
                        String openMsg = datapackageGenerator.openDatapackage(msg, symmetricKey);
                        secureChatUI.append(chatMessage.getSender() + " : " + openMsg);
                    }else if (obj.getClass().equals(String.class)){
                        String msg = (String) obj;
                        secureChatUI.getLobbyGUI().appendToLobby(msg);
                    }
                }
                catch(IOException e) {
                    display("No connection to the server established. " + e);
                    keepRunning = false;
                    getSecureChatUI().resetConnectButton();
                }
                catch(ClassNotFoundException e) {
                    System.out.println("ClassNotFoundException i ServerListener!");
                    keepRunning = false;
                }
            }
        }
    }
}
