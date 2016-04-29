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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.ServerGUI;


/**
 * Created by Michael on 2016-02-27.
 */
public class SecureChatUI extends Application
{
    private Label label;
    // to Logout and get the list of the users
    private Button login, logout, whoIsIn;
    // for the chat room
    private TextArea chatArea;
    // the Client object
    private Client client;

    private ServerGUI serverGUI;

    private TextField portNo;
    private TextField userName;
    private TextField serverIpField;
    private TextArea chatRoom;
    private TextField chatMessage;
    private Button logoutButton;
    private Button loginButton;
    private Button startServer;

    final HBox hbConnection = new HBox();
    final HBox hbBottom = new HBox();
    final HBox hbInput = new HBox();
    final HBox hbStartSever = new HBox();

    final VBox vbox = new VBox();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(new Group());
        primaryStage.setTitle("SecureChat");
        primaryStage.setWidth(640);
        primaryStage.setHeight(400);

        Label serverIpLabel = new Label("Server IP: ");
        //serverIpLabel.setFont(new Font("Arial", 12));
        serverIpField = new TextField();
        serverIpField.setPromptText("Server IP");
        serverIpField.setEditable(true);

        portNo = new TextField();
        Label portLabel = new Label("Port: ");
        //portLabel.setFont(new Font("Arial", 12));
        portNo.setPromptText("Port number");
        portNo.setEditable(true);

        userName = new TextField();
        Label userLabel = new Label("Name: ");
        //userLabel.setFont(new Font("Arial", 12));
        userName.setPromptText("Alias");
        userName.setEditable(true);

        loginButton = new Button("Login");
        this.login = loginButton;
        loginButton.setOnAction((event) -> {
            String username = userName.getText().trim();
            // empty username ignore it
            if(username.length() == 0){
                return;
            }
            // empty serverAddress ignore it
            String server = serverIpField.getText().trim();
            if(server.length() == 0){
                return;
            }
            String portNumber = portNo.getText().trim();
            // empty or invalid port number, ignore it
            if(portNumber.length() == 0){
                return;
            }
            int port = 0;
            try {
                port = Integer.parseInt(portNumber);
            }
            catch(Exception ex) {
                return;   // nothing I can do if port number is not valid
            }
            // try creating a new Client with GUI
            client = new Client(server, port, username, this);
            // test if we can start the Client
            if(!client.start())
                System.out.println("!client.start() in secureChatUI");
            return;
        });

        logoutButton = new Button("logout");
        logoutButton.setOnAction((event) -> {
            ChatMessage message = new ChatMessage(2, chatMessage.getText());
            this.client.sendMessage(message);
            this.client.disconnect();
        });

        startServer = new Button("Start Server");
        startServer.setOnAction((event) -> {
            serverGUI = new ServerGUI();
            serverGUI.start();
        });

        final Button whoIsIn = new Button("whoIsIn");
        this.whoIsIn = whoIsIn;
        whoIsIn.setOnAction((event) ->{
            ChatMessage message = new ChatMessage(0, chatMessage.getText());
            this.client.sendMessage(message);
        });

        final Button sendButton = new Button("Send");
        sendButton.setOnAction((event) -> {
            ChatMessage message = new ChatMessage(1, chatMessage.getText());
            this.client.sendMessage(message);
            chatMessage.setText("");
        });

        chatMessage = new TextField();
        chatMessage.setPromptText("Enter Your Chat Message Here");
        chatMessage.setPrefWidth(300);

        chatRoom = new TextArea();
        chatRoom.setPromptText("Welcome to the Chat room");
        chatRoom.setEditable(false);

        //chatRoom.setScrollTop(100);

        hbConnection.setSpacing(5);
        hbConnection.setPadding(new Insets(10, 0, 30, 10));
        hbConnection.getChildren().addAll(serverIpLabel, serverIpField, portLabel, portNo, userLabel, userName);
        hbConnection.setCenterShape(true);

        hbBottom.setSpacing(5);
        hbBottom.setPadding(new Insets(25, 0, 0, 10));
        hbBottom.getChildren().addAll(hbInput, loginButton, logoutButton, whoIsIn);

        hbInput.setSpacing(5);
        hbInput.setPadding(new Insets(0, 50, 0, 10));
        hbInput.getChildren().addAll(chatMessage, sendButton);

        hbStartSever.setSpacing(5);
        hbStartSever.setPadding(new Insets(0, 50, 0, 10));
        hbStartSever.getChildren().addAll(startServer);

        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(hbConnection, hbStartSever, chatRoom, hbBottom);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // called by the Client to append text in the TextArea
    void append(String str) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoom.appendText(str + "\n");
                chatRoom.positionCaret(chatRoom.getText().length() - 1);
            }
        });
    }
}
