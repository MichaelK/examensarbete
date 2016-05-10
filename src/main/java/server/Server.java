package server;

import client.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Michael on 2016-04-01.
 */
public class Server {

    // The port this server is running on.
    private int port;

    // Boolean to stop the server. Volatile so other threads can stop it.
    private volatile boolean running;

    // An ArrayList that holds all connection threads.
    private ArrayList<ClientThread> clientThreads = new ArrayList<>();

    // to appendEvent time
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    // The GUI that corresponds to this server.
    private ServerGUI serverGUI;

    // Each connection has its own Id.
    private int uniqueId;

    // The thread the server is running on.
    private Thread thread;

    // The ServerSocket of the server.
    private ServerSocket serverSocket;

    // Constructor. Starts the server when a new server is created.
    public Server(int port, ServerGUI serverGUI) {
        this.port = port;
        this.serverGUI = serverGUI;
        start();
    }

    // This will run continuously as long as the server isn't stopped.
    public synchronized void start() {
        // Try and create the ServerSocket. Starts thread to accepts connections.
        try {
            // Starts a ServerSocket with the port.
            serverSocket = new ServerSocket(port);

            // Set message that we are waiting for connections.
            appendEvent("Server waiting for Clients on port " + port + ".");
        } catch (IOException e) {
            // If something went wrong.
            String msg = simpleDateFormat.format(new Date()) + " Exception on creating ServerSocket: " + e;
            appendEvent(msg);
        }
        // Start the thread that accepts connections.
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    running = true;
                    while (running) {
                        // Accept connection.
                        Socket socket = serverSocket.accept();
                        // Make a new clientThread.
                        ClientThread clientThread = new ClientThread(socket);
                        // Save the thread in the ArrayList.
                        clientThreads.add(clientThread);
                        clientThread.start();
                    }
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        });
        thread.start();
    }


    // Append an event in the eventlog of the serverGUI.
    private void appendEvent(String msg) {
        String time = simpleDateFormat.format(new Date()) + " " + msg;
        serverGUI.appendEvent(time);
    }

    // To manually stop the server and close open the ServerSocket and streams.
    protected void stop() {
        // If thread is null there is no reason to go further.
        if(thread == null) {
            return;
        }
        // Set boolean to stop the server.
        running = false;
        // Try and stop ServerSocket and close streams.
        try{
            serverSocket.close();
            for (int i = 0; i < clientThreads.size(); i++) {
                ClientThread clientThread = clientThreads.get(i);
                try {
                    clientThread.sInput.close();
                    clientThread.sOutput.close();
                    clientThread.socket.close();
                } catch (IOException e) {
                    System.out.println("Problem closing clientThread.");
                }
            }
        }catch (Exception e) {
            // Append event to the ServerGUI.
            appendEvent("Exception closing the server and clients: " + e);
        }
        // If closing worked then add an event to the ServerGUI.
        appendEvent("All connections to server and serverSocket closed.");
    }

    // Method that sends a ChatMessage to everyone.
    private synchronized void sendToAll(ChatMessage chatMessage) {
        // Show the unencrypted message in the ServerGUI.
        this.serverGUI.appendRoom(chatMessage.getSender() + " : " + chatMessage.getMessage());
        // Send the message to everyone in the ClientThreads ArrayList.
        for (int i = 0; i < clientThreads.size(); i++) {
            ClientThread clientThread = clientThreads.get(i);
            // If message can not be sent then remove the connection and append event.
            if (!clientThread.writeMsg(chatMessage)) {
                clientThreads.remove(i);
                appendEvent(clientThread.username + " has disconnected from the chat!");
            }
        }
    }

    // Method to remove a connection from the ClientThreads ArrayList by Id.
    synchronized void remove(int id) {
        // Search the ArrayList for Id.
        for (int i = 0; i < clientThreads.size(); ++i) {
            ClientThread ct = clientThreads.get(i);
            // Remove the corresponding thread.
            if (ct.id == id) {
                clientThreads.remove(i);
                return;
            }
        }
    }

    // One instance of this thread will run for each client.
    class ClientThread extends Thread {
        // The socket that connects to the ServerSocket.
        Socket socket;
        // The ObjectInputStream.
        ObjectInputStream sInput;
        // The ObjectOutpulStream.
        ObjectOutputStream sOutput;
        // Each connection has an unique Id.
        int id;
        // The username of the client.
        String username;
        // The object sent to the Server from clients.
        ChatMessage chatMessage;
        // A value to set the current date.
        String date;

        // Constructor
        ClientThread(Socket socket) {
            // Get a unique Id to the connection.
            id = ++uniqueId;
            // The socket connecting.
            this.socket = socket;
            // Create streams.
            try{
                // Must create output sream first or might be problems.
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                // Read the username.
                username = (String) sInput.readObject();
                appendEvent(username + " just connected.");
            } catch (IOException e) {
                appendEvent("Exception creating new Input/output Streams: " + e);
                return;
            }
            // If ClassNotFoundException.
            catch(ClassNotFoundException e){
                // Do nothing.
            }
            // Current date of connection.
            date = new Date().toString();
        }

        // This clientThread will run until removed by the server.
        public void run() {
            // Boolean to stop the clientThread.
            boolean keepGoing = true;
            while (keepGoing) {
                // Try and read the object that is a ChatMessage.
                try {
                    chatMessage = (ChatMessage) sInput.readObject();
                } catch (IOException e) {
                    // Append event in the ServerGUI if error.
                    appendEvent(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e) {
                    // Will probably never happen.
                    appendEvent(username + " Exception reading Streams: " + e);
                    break;
                }
                // The ChatMessage can be one of the three different types.
                switch (chatMessage.getType()) {
                    // All messages that send to another client is a ChatMessage.MESSAGE.
                    case ChatMessage.MESSAGE:
                        // Set sender and send to all other connections.
                        chatMessage.setSender(username);
                        sendToAll(chatMessage);
                        break;
                    // If you manually click the logout button on the SecureChatUI.
                    case ChatMessage.LOGOUT:
                        // Append event that user logged off.
                        appendEvent(username + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        // Close the socket and streams.
                        this.close();
                        break;
                    // ChatMessage to see who is connected to the same port. Show users in the LobbyGUI.
                    case ChatMessage.LOBBY:
                        // Will create a readable list in the LobbyGUI.
                        writeMsg("List of the users connected at " + simpleDateFormat.format(new Date()));
                        for (int i = 0; i < clientThreads.size(); ++i) {
                            ClientThread clientThread = clientThreads.get(i);
                            writeMsg((i + 1) + ") " + clientThread.username + " since " + clientThread.date);
                        }
                        break;
                }
            }
            // If boolean keepGoing is false then remove the unique connection Id and close the socket and streams.
            remove(id);
            close();
        }

        // Try to close the socket and streams.
        private void close(){
            try{
                if (sOutput != null){
                    sOutput.close();
                }
            }catch (Exception e){
                // Do nothing.
            }
            try{
                if (sInput != null){
                    sInput.close();
                }
            }catch (Exception e){
                // Do nothing.
            }
            try{
                if (socket != null){
                    socket.close();
                }
            }catch (Exception e){
                // Do nothing.
            }
        }


        // Write an Object to the Client output stream
        private boolean writeMsg(Object object) {
            // Try and write to the outputstream.
            try {
                // If object is ChatMessage then this.
                if (object.getClass().equals(ChatMessage.class)) {
                    ChatMessage chatMessage = (ChatMessage) object;
                    sOutput.writeObject(chatMessage);
                // If object is a String then this.
                } else if (object.getClass().equals(String.class)) {
                    String msg = (String) object;
                    sOutput.writeObject(msg);
                }
                // Return true if successful.
                return true;
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                // If fail then append event.
                appendEvent("Error sending message to " + username);
            }
            // If you gotten this far then return false. Message not sent.
            return false;
        }
    }
}

