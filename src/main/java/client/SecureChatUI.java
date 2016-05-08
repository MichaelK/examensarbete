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
 * Created by Michael on 2016-02-27.
 */
public class SecureChatUI extends Application{

    private Client client;

    private ServerGUI serverGUI;
    private LobbyGUI lobbyGUI;
    private LoginGUI loginGUI;

    private TextArea chatRoom;
    private TextField chatMessage;

    private Button connectButton;
    private Button startServerButton;

    final GridPane gridPane = new GridPane();
    final HBox hbBottom = new HBox();
    final HBox hbInput = new HBox();
    final HBox hbTop = new HBox();

    final VBox vbox = new VBox();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(new Group());
        primaryStage.setTitle("SecureChat");
        //primaryStage.setWidth(640);
        //primaryStage.setHeight(400);

        connectButton = new Button("Connect");
        connectButtonStyleClass();
        connectButton.setOnAction((event) -> {
            if(connectButton.getText().equals("Connect")){
                if(this.loginGUI == null){
                    this.loginGUI = new LoginGUI(this);
                    this.loginGUI.start();
                }
            }else{
                ChatMessage message = new ChatMessage(2, chatMessage.getText());
                this.client.sendMessage(message);
                this.client.disconnect();
                connectButton.setText("Connect");
                connectButtonStyleClass();
            }
        });

        startServerButton = new Button("Start Server");
        startServerButton.getStyleClass().add("startServerButton");
        startServerButton.setOnAction((event) -> {
            serverGUI = new ServerGUI(this);
            serverGUI.start();
        });

        final Button showLobbyButton = new Button("Show Lobby");
        showLobbyButton.getStyleClass().add("showLobbyButton");
        showLobbyButton.setOnAction((event) ->{
            if(this.client != null){
                if(this.lobbyGUI != null){
                    lobbyGUI.clearLobby();
                    ChatMessage message = new ChatMessage(0, chatMessage.getText());
                    this.client.sendMessage(message);
                }else{
                    lobbyGUI = new LobbyGUI(this);
                    lobbyGUI.start();
                    lobbyGUI.clearLobby();
                    ChatMessage message = new ChatMessage(0, chatMessage.getText());
                    this.client.sendMessage(message);
                }
            }else{
                lobbyGUI = new LobbyGUI(this);
                lobbyGUI.start();
                lobbyGUI.appendToLobby("You are not connected to a server.");
            }
        });

        final Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("sendButton");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction((event) -> {
            if(this.client != null){
                ChatMessage message = new ChatMessage(1, chatMessage.getText());
                this.client.sendMessage(message);
            }else{
                append("Need to connect to a server before sending messages!");
            }
            chatMessage.setText("");
        });

        chatMessage = new TextField();
        chatMessage.getStyleClass().add("chatMessage");
        chatMessage.setPromptText("Enter your chatmessage here!");
        chatMessage.setPrefWidth(420);

        chatRoom = new TextArea();
        chatRoom.getStyleClass().add("chatRoom");
        //chatRoom.setPromptText("Welcome to the chatroom!");
        chatRoom.setPrefWidth(420);
        chatRoom.setEditable(false);

        //chatRoom.setScrollTop(100);

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
        //vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(gridPane);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        primaryStage.setScene(scene);

        scene.getStylesheets().add("myCSS.css");

        startupText();

        primaryStage.show();
    }

    void login(String alias, String serverIp, int portNo, String password){
        try{
            // try creating a new Client with GUI
            this.client = new Client(serverIp, portNo, alias, password, this);
            boolean success = this.client.start();
            if (success){
                connectButton.setText("Logout");
                connectButtonStyleClass();
                clearChatroom();
            }else{
                this.client = null;
            }
        }catch(Exception e){
            append("Error trying to login. Please input correct information.");
            this.client = null;
        }
    }

    // called by the Client to append text in the TextArea
    void append(String str) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoom.appendText(str + "\n");
            }
        });
    }

    void startupText(){
        append("Welcome to the chatroom!\n\n" +
                "1) Start a new server if you are going to be the host.\n" +
                "2) Connect to a known server if you are not the host.\n" +
                "3) All connected to a chatroom need to use the same password.\n" +
                "4) The more complex a password the better the security is!\n");
    }

    void clearChatroom(){
        chatRoom.clear();
    }

    void connectButtonStyleClass(){
        connectButton.getStyleClass().clear();
        connectButton.getStyleClass().add("button");
        if(connectButton.getText().equals("Connect")){
            connectButton.getStyleClass().add("connectButton");
        }else{
            connectButton.getStyleClass().add("logoutButton");
        }
    }


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


}
