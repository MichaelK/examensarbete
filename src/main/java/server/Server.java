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
 * Created by Michael on 2016-02-28.
 */
public class Server {

    private int port;

    private boolean keepGoing = true;

    private ArrayList<ClientThread> clientThreads = new ArrayList<>();

    // to display time
    private SimpleDateFormat simpleDateFormat;

    // if I am in a GUI
    private ServerGUI serverGUI;

    // a unique ID for each connection
    private int uniqueId;

    public Server(int port){
        this.port = port;
    }

    public Server(int port, ServerGUI serverGUI){
        this.port = port;
        this.serverGUI = serverGUI;
    }

    public void start() {
        keepGoing = true;
        // create socket server and wait for connection requests
        try
        {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);

            // format message saying we are waiting
            display("Server waiting for Clients on port " + port + ".");

            // infinite loop to wait for connections
            while(keepGoing)
            {
                Socket socket = serverSocket.accept();      // accept connection
                // if I was asked to stop
                if(!keepGoing)
                    break;
                ClientThread clientThread = new ClientThread(socket);  // make a thread of it
                clientThreads.add(clientThread);       // save it in the ArrayList
                clientThread.start();
            }
            // I was asked to stop
            try {
                serverSocket.close();
                for(int i = 0; i < clientThreads.size(); ++i) {
                    ClientThread clientThread = clientThreads.get(i);
                    try {
                        clientThread.sInput.close();
                        clientThread.sOutput.close();
                        clientThread.socket.close();
                    }
                    catch(IOException ex) {
                        // not much I can do
                    }
                }
            }
            catch(Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        // something went bad
        catch (IOException ex) {
            String msg = simpleDateFormat.format(new Date()) + " Exception on creating ServerSocket: " + ex + "\n";
            display(msg);
        }
    }

    /*
     * Display an event (not a message) to the console or the GUI
     */
    private void display(String msg) {
        simpleDateFormat = new SimpleDateFormat();
        String time = simpleDateFormat.format(new Date()) + " " + msg;
        serverGUI.appendEvent(time + "\n");
    }

    // For the GUI to stop the server

    protected void stop() {
        keepGoing = false;
        // connect to myself as Client to exit statement
        // Socket socket = serverSocket.accept();
        try {
            new Socket("localhost", port);
        }
        catch(Exception e) {
            // nothing I can really do
        }
    }

    private synchronized void broadcast(ChatMessage chatMessage) {
        //Show undecrypted message in the ServerGUI
        this.serverGUI.appendRoom(chatMessage.getSender() + " : " + chatMessage.getMessage() + "\n");
        // add HH:mm:ss and \n to the message
        String time = simpleDateFormat.format(new Date());
        String msg = time + ": " + chatMessage.getMessage() + "\n";
        //send message to all in chat and remove those that have disconnected
        for(int i = 0; i < clientThreads.size(); i++){
            ClientThread clientThread = clientThreads.get(i);
            if(!clientThread.writeMsg(chatMessage)){
                clientThreads.remove(i);
                display(clientThread.username + " has disconnected from the chat!");
            }
        }
    }

    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < clientThreads.size(); ++i) {
            ClientThread ct = clientThreads.get(i);
            // found it
            if(ct.id == id) {
                clientThreads.remove(i);
                return;
            }
        }
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    // One instance of this thread will run for each client
    class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // unique id (easier for disconnection)
        int id;
        // the Username of the Client
        String username;
        // the only type of message the server will receive
        ChatMessage chatMessage;
        // the date I connect
        String date;

        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Thread trying to create Object Input/Output Streams");
            try
            {
                // create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                // read the username
                username = (String) sInput.readObject();
                display(username + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            // have to catch ClassNotFoundException
            catch (ClassNotFoundException e) {
            }
            date = new Date().toString() + "\n";
        }

        // what will run forever
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while(keepGoing) {
                // read a String (which is an object)
                try {
                    chatMessage = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e) {
                    break;
                }
                // the message part of the ChatMessage
                String message = chatMessage.getMessage();
                // Switch on the type of message receive
                switch(chatMessage.getType()) {
                    case ChatMessage.MESSAGE:
                        chatMessage.setSender(username);
                        broadcast(chatMessage);
                        //broadcast(username + ": " + message);
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        this.close();
                        // TODO: 2016-04-10 remove the clientThread
                        break;
                    case ChatMessage.WHOISIN:
                        writeMsg("List of the users connected at " + simpleDateFormat.format(new Date()) + "\n");
                        //scan all the users connected
                        for(int i = 0; i < clientThreads.size(); ++i) {
                            ClientThread clientThread = clientThreads.get(i);
                            writeMsg((i+1) + ") " + clientThread.username + " since " + clientThread.date);
                        }
                        break;
                }
            }
            // remove from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }

        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if(sOutput != null){
                    sOutput.close();
                }
            }
            catch(Exception e) {}
            try {
                if(sInput != null){
                    sInput.close();
                }
            }
            catch(Exception e) {}
            try {
                if(socket != null){
                    socket.close();
                }
            }
            catch (Exception e) {}
        }


        // Write an Object to the Client output stream
        private boolean writeMsg(Object object) {
            // write the message to the stream
            try {
                if (object.getClass().equals(ChatMessage.class)){
                    ChatMessage chatMessage = (ChatMessage) object;
                    sOutput.writeObject(chatMessage);
                }else if (object.getClass().equals(String.class)){
                    String msg = (String) object;
                    sOutput.writeObject(msg);
                }
                return true;
            }
            // if an error occurs, do not abort just inform the user
            catch(IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return false;
        }
    }
}

