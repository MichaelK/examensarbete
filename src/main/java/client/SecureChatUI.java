package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.ServerGUI;


/**
 * The main GUI that starts the applikation
 * Created by Michael on 2016-04-01.
 */
public class SecureChatUI extends Application{

    // The Client.
    private Client client;
    // These GUIs are all started from the main GUI.
    private ServerGUI serverGUI;
    private LobbyGUI lobbyGUI;
    private LoginGUI loginGUI;

    // The chatroom area.
    private TextArea chatRoom;
    // The textfield where you enter messages.
    private TextField chatMessage;

    // The button that opens the LoginGUI to login to a server.
    private Button connectButton;
    // The button that starts the ServerGUI.
    private Button startServerButton;
    // The button that starts the LobbyGUI.
    private Button showLobbyButton;

    // GridPane, HBox and VBox.
    final GridPane gridPane = new GridPane();
    final HBox hbBottom = new HBox();
    final HBox hbInput = new HBox();
    final HBox hbTop = new HBox();
    final VBox vbox = new VBox();

    // Need to start the application.
    public static void main(String[] args) {
        launch(args);
    }

    // Starts the main GUI.
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Creates a Scene and set the title.
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("SecureChat");

        // The connectbutton that starts LoginGUI for login. And styleclass for this button.
        connectButton = new Button("Connect");
        connectButtonStyleClass();

        // Handles the events when clicking connectbutton.
        connectButton.setOnAction((event) -> {
            // If buttontext says Connect.
            if(connectButton.getText().equals("Connect")){
                if(this.loginGUI == null){
                    this.loginGUI = new LoginGUI(this);
                    this.loginGUI.start();
                }
            // If buttontext says Logout.
            }else{
                // Send a logout ChatMessage to server.
                ChatMessage message = new ChatMessage(2, chatMessage.getText());
                this.client.sendMessage(message);
                this.client.disconnect();
                connectButton.setText("Connect");
                connectButtonStyleClass();
            }
        });

        // Starts the serverGUI and sets styleclass.
        startServerButton = new Button("Start Server");
        startServerButton.getStyleClass().add("startServerButton");

        // Handles event to start serverGUI.
        startServerButton.setOnAction((event) -> {
            serverGUI = new ServerGUI(this);
            serverGUI.start();
        });

        // Starts the LobbyGUI and sets styleclass.
        showLobbyButton = new Button("Show Lobby");
        showLobbyButton.getStyleClass().add("showLobbyButton");

        // Handles events to start the lobbyGUI.
        showLobbyButton.setOnAction((event) ->{
            if(this.client != null){
                // If a LobbyGUI already exists then use that one to update.
                if(this.lobbyGUI != null){
                    lobbyGUI.clearLobby();
                    ChatMessage message = new ChatMessage(0, chatMessage.getText());
                    this.client.sendMessage(message);
                // If no LobbyGUI exist then start one and get the lobby.
                }else{
                    lobbyGUI = new LobbyGUI(this);
                    lobbyGUI.start();
                    lobbyGUI.clearLobby();
                    ChatMessage message = new ChatMessage(0, chatMessage.getText());
                    this.client.sendMessage(message);
                }
            // If a client is not started then show lobby with this text.
            }else{
                lobbyGUI = new LobbyGUI(this);
                lobbyGUI.start();
                lobbyGUI.appendToLobby("You are not connected to a server.");
            }
        });

        // The sendbutton that sends messages and styleclass.
        final Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("sendButton");

        // Is set as defaultbutton to not have to click mouse to send. Can press enter.
        sendButton.setDefaultButton(true);

        // Handles button to send messages.
        sendButton.setOnAction((event) -> {
            // If connected.
            if(this.client != null){
                // As long as it isn't an empty string then send.
                if(!chatMessage.getText().equals("")){
                    ChatMessage message = new ChatMessage(1, chatMessage.getText());
                    this.client.sendMessage(message);
                }
            // If not connected.
            }else{
                append("Need to connect to a server before sending messages!");
            }
            // Set the textfield to empty string after a message is sent.
            chatMessage.setText("");
        });

        // Handles events to close the stage. Tries to disconnect the client.
        primaryStage.setOnCloseRequest(event -> {
            if(this.client != null){
                ChatMessage message = new ChatMessage(2, chatMessage.getText());
                this.client.sendMessage(message);
                this.client.disconnect();
            }
            primaryStage.close();
        });

        // Textfield where you enter message.
        chatMessage = new TextField();
        chatMessage.getStyleClass().add("textField");
        chatMessage.setPromptText("Enter your chatmessage here!");
        chatMessage.setPrefWidth(420);

        // Textarea where all messages are seen.
        chatRoom = new TextArea();
        chatRoom.getStyleClass().add("textArea");
        chatRoom.setPrefWidth(420);
        chatRoom.setEditable(false);

        // HBox, VBox and GridPane.
        hbBottom.setSpacing(5);
        hbBottom.setPadding(new Insets(25, 0, 0, 0));
        hbBottom.getChildren().addAll(hbInput);

        hbInput.setSpacing(5);
        hbInput.setPadding(new Insets(0, 50, 0, 0));
        hbInput.getChildren().addAll(chatMessage, sendButton);

        hbTop.setSpacing(5);
        hbTop.setPadding(new Insets(0, 50, 0, 0));
        hbTop.getChildren().addAll(connectButton, startServerButton, showLobbyButton);

        gridPane.getStyleClass().add("gridPane");
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.add(hbTop, 0, 0);
        gridPane.add(chatRoom, 0, 1);
        gridPane.add(hbBottom, 0, 2);

        vbox.setSpacing(5);
        vbox.getChildren().addAll(gridPane);

        // Add all children to the root.
        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        // Set the scene to the stage.
        primaryStage.setScene(scene);

        // Add the stylesheet to the scene.
        scene.getStylesheets().add("myCSS.css");

        // Appends the startup text to the chatarea.
        startupText();

        // Show the primaryStage.
        primaryStage.show();
    }

    // Method called from LoginGUI to connect a client to the server.
    void login(String alias, String serverIp, int portNo, String password){
        // Try creating a new Client with GUI
        try{
            this.client = new Client(serverIp, portNo, alias, password, this);
            // If successful then change button style and clear chatroom.
            boolean success = this.client.start();
            if (success){
                connectButton.setText("Logout");
                connectButtonStyleClass();
                clearChatroom();
            // If not successful then set client to null.
            }else{
                this.client = null;
            }
        // Show this message in the chatarea if fail to login.
        }catch(Exception e){
            append("Error trying to login. Please input correct information.");
            this.client = null;
        }
    }

    // Called by the Client to append text in the TextArea
    void append(String str) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoom.appendText(str + "\n");
            }
        });
    }

    // The startup text that is shown in the textarea.
    private void startupText(){
        append("Welcome to the chatroom!\n\n" +
                "1) Start a new server if you are going to be the host.\n" +
                "2) Connect to a known server if you are not the host.\n" +
                "3) All connected to a chatroom need to use the same password.\n" +
                "4) The more complex a password the better the security is!\n");
    }

    // Clear the chatarea.
    void clearChatroom(){
        chatRoom.clear();
    }

    // Sets the style for the connectButton depending on being logged in or not.
    void connectButtonStyleClass(){
        connectButton.getStyleClass().clear();
        connectButton.getStyleClass().add("button");
        if(connectButton.getText().equals("Connect")){
            connectButton.getStyleClass().add("connectButton");
        }else{
            connectButton.getStyleClass().add("logoutButton");
        }
    }

    // Method to reset the connectButton if Server shuts down unexpectedly.
    void resetConnectButton(){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                connectButton.setText("Connect");
                connectButtonStyleClass();
                client = null;
            }
        });
    }

    // Getters and setters.
    public LobbyGUI getLobbyGUI() {
        return lobbyGUI;
    }

    public void setLobbyGUI(LobbyGUI lobbyGUI) {
        this.lobbyGUI = lobbyGUI;
    }

    public ServerGUI getServerGUI() {
        return serverGUI;
    }

    public void setServerGUI(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    public LoginGUI getLoginGUI() {
        return loginGUI;
    }

    public void setLoginGUI(LoginGUI loginGUI) {
        this.loginGUI = loginGUI;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


}
