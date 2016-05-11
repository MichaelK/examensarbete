package client;

import services.datapackage.DatapackageGenerator;
import services.datapackage.DatapackageGeneratorImpl;
import services.hash.HashGenerator;
import services.hash.HashGeneratorImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Michael on 2016-04-01.
 */
public class Client {

    // The ObjectInputStream.
    private ObjectInputStream sInput;
    // The ObjectOutputStream.
    private ObjectOutputStream sOutput;
    // The clients socket.
    private Socket socket;
    // The main GUI for this client.
    private SecureChatUI secureChatUI;
    // The server this client is using.
    private String server;
    // The username of the client.
    private String username;
    // The port the server is using.
    private int port;
    // The symmetric key that encrypts ChatMessages between clients.
    private byte[] symmetricKey;
    // The Class that listens for incoming streams.
    private ServerListener serverListener;

    private DatapackageGenerator datapackageGenerator;
    private HashGenerator hashGenerator;

    // Constructor
    public Client(String server, int port, String username, String password, SecureChatUI secureChatUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.secureChatUI = secureChatUI;
        this.datapackageGenerator = new DatapackageGeneratorImpl();
        this.hashGenerator = new HashGeneratorImpl();
        this.symmetricKey = createSymmetricKey(password);
    }

    // Starts the client. Creates a socket, output and input stream, and starts the serverlistener.
    public boolean start() {
        // Try to connect to the server on the port.
        try {
            socket = new Socket(server, port);
        }
        catch(Exception e) {
            appendToChatroom("Error connecting to server:" + e);
            return false;
        }
        // If creating socket succeeds then append this to the chatroom.
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        appendToChatroom(msg);
        // Try creating ObjectOutputStream and ObjectInputStream.
        try
        {
            // OutputStream first to avoid problems.
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            appendToChatroom("Exception creating new Input/output Streams: " + e);
            return false;
        }
        // Creates the Thread to listen from the server
        serverListener = new ServerListener();
        serverListener.start();
        // Send the username to the server.
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException e) {
            appendToChatroom("Exception doing login : " + e);
            return false;
        }
        // It worked.
        return true;
    }

    // Create a symmetricKey from the password by hashing the password.
    public byte[] createSymmetricKey(String password){
        return hashGenerator.generate(password, 256);
    }


    // To send a message to the main GUI.
    private void appendToChatroom(String msg) {
        secureChatUI.append(msg);
    }


    // Sends one of three different ChatMessages to the server.
    public void sendMessage(ChatMessage chatmsg) {
        ChatMessage chatMessage = chatmsg;
        String message = chatMessage.getMessage();
        switch(chatmsg.getType()){
            // This message is always sent between clients to chat.
            case ChatMessage.MESSAGE:
                String generateMsg = datapackageGenerator.generateDatapackage(message, symmetricKey);
                chatMessage.setMessage(generateMsg);
                break;
            // This is only sent to the server to disconnect a client.
            case ChatMessage.LOGOUT:
                chatMessage.setMessage(message);
                break;
            // This is sent to retrieve which users are in the chat at this time.
            case ChatMessage.LOBBY:
                chatMessage.setMessage(message);
                break;
        }
        // Try and write the message to the ObjectOutputStream.
        try {
            sOutput.writeObject(chatMessage);
        }
        catch(IOException e) {
            appendToChatroom("Exception sending message to server: " + e);
        }
    }

    // Try and disconnect the socket and close the streams.
    public void disconnect() {
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

    /**
     * This class listens for incoming objects. If it is a ChatMessage the message part is opened with the DatapackageGenerator class.
     * If it is a String then it is appended to the LobbyGUI.
     */
    class ServerListener extends Thread {
        // Boolean to keep the thread running.
        Boolean keepRunning = true;
        public void run() {
            while(keepRunning) {
                // Try and read the incoming object.
                try {
                    Object obj = sInput.readObject();
                    // If the object is a ChatMessage.
                    if (obj.getClass().equals(ChatMessage.class)){
                        ChatMessage chatMessage = (ChatMessage) obj;
                        String msg = chatMessage.getMessage();
                        String openMsg;
                        // Try to open the datapackage and append it to the chatroom.
                        try {
                            openMsg = datapackageGenerator.openDatapackage(msg, symmetricKey);
                            secureChatUI.append(chatMessage.getSender() + " : " + openMsg);
                        }catch (Exception e){
                            // If the message can not be opened. Probably different passwords used.
                            secureChatUI.append(chatMessage.getSender() + " : " + "Message could not be decrypted. Check password.");
                        }
                    // If it is a String meant for the LobbyGUI.
                    }else if (obj.getClass().equals(String.class)){
                        String msg = (String) obj;
                        secureChatUI.getLobbyGUI().appendToLobby(msg);
                    }
                }
                // If a connection has not been established to the server or has been disconnected.
                catch(IOException e) {
                    appendToChatroom("No connection to the server established. " + e);
                    keepRunning = false;
                    // Reset the connect button to show that you need to connect.
                    getSecureChatUI().resetConnectButton();
                }
                // will probably never happen.
                catch(ClassNotFoundException e) {
                    System.out.println("ClassNotFoundException i ServerListener!");
                    keepRunning = false;
                }
            }
        }
    }

    // Getters adn setters
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(byte[] symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public ObjectInputStream getsInput() {return sInput;}

    public void setsInput(ObjectInputStream sInput) {this.sInput = sInput;}

    public ObjectOutputStream getsOutput() {return sOutput;}

    public void setsOutput(ObjectOutputStream sOutput) {this.sOutput = sOutput;}

    public ServerListener getServerListener() {return serverListener;}

    public void setServerListener(ServerListener serverListener) {this.serverListener = serverListener;}
}
