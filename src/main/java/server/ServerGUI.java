package server;

import client.SecureChatUI;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main GUI for the server.
 * Created by Michael on 2016-04-01.
 */
public class ServerGUI {
    // To format date/time.
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    // HBox and VBox for GUI.
    final private HBox hb = new HBox();
    final private VBox vbox = new VBox();
    final private VBox vbox2 = new VBox();

    // The button that toggles start/stop server.
    private Button startButton;

    // TextArea for the chatArea room and the events
    private TextArea chatArea;
    private TextArea eventLog;

    // The port number
    private TextField portNo;

    // The server that this GUI controls.
    private Server server;

    // This GUI starts the ServerGUI.
    private SecureChatUI secureChatUI;

    // Constructor
    public ServerGUI(SecureChatUI secureChatUI){
        this.secureChatUI = secureChatUI;
    }

    // Starts the primaryStage
    public void start(){

        // Starts the Stage, Scene and sets title.
        Stage primaryStage = new Stage();
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("Server");

        // Label, textfield, style etc for the port.
        portNo = new TextField();
        portNo.getStyleClass().add("textField");
        Label portLabel = new Label("Port Number:");
        portLabel.setFont(new Font("Arial", 12));
        portNo.setPromptText("Port No ");
        portNo.setEditable(true);

        // The button that starts the server.
        startButton = new Button("Start");
        startButton.setDefaultButton(true);
        startButtonStyleClass();
        startButton.setOnAction((event) ->{
            startStopButton();
        });

        // Handles Stage is closed event.
        primaryStage.setOnCloseRequest(event -> {
            if(this.server != null){
                this.server.stop();
            }
            this.secureChatUI.setServerGUI(null);
            primaryStage.close();
        });

        // The server chatarea.
        chatArea = new TextArea();
        chatArea.setPromptText("Chatroom");
        chatArea.getStyleClass().add("textArea");
        chatArea.setEditable(false);

        // The server eventlogarea.
        eventLog = new TextArea();
        eventLog.setPromptText("Eventlog");
        eventLog.getStyleClass().add("textArea");
        eventLog.setEditable(false);
        eventLog.setScrollTop(100);

        // HBox
        hb.setSpacing(3);
        hb.getChildren().addAll(portLabel, portNo, startButton);
        hb.setCenterShape(true);

        // VBox
        vbox.setSpacing(5);
        vbox.getChildren().addAll(chatArea, eventLog);
        vbox.setCenterShape(true);

        // VBox2
        vbox2.getStyleClass().add("vboxServer");
        vbox2.setSpacing(5);
        vbox2.setPadding(new Insets(25, 25, 25, 25));
        vbox2.getChildren().addAll(hb,vbox);
        vbox2.setCenterShape(true);

        // Add children to root.
        ((Group) scene.getRoot()).getChildren().addAll(vbox2);

        // Set the Scene of the PrimaryStage.
        primaryStage.setScene(scene);

        // Add stylesheets to the Scene.
        scene.getStylesheets().add("myCSS.css");

        // Show the primaryStage.
        primaryStage.show();
    }


    // Starts or stops the server.
    public void startStopButton(){
        // If the server is running and we want to stop it.
        if(server != null) {
            server.stop();
            server = null;
            startButton.setText("Start");
            startButtonStyleClass();
            portNo.setEditable(true);
            appendEvent(simpleDateFormat.format(new Date()) + " Server stopped!");
            return;
        }
        // If the server is started. Use the port in the portNo textField.
        int port;
        try {
            port = Integer.parseInt(portNo.getText().trim());
        }
        catch(Exception ex) {
            appendEvent("Invalid port number");
            return;
        }
        // Create the server with selected port.
        server = new Server(port, this);

        // Change text and style of button.
        startButton.setText("Stop");
        startButtonStyleClass();
        portNo.setEditable(false);
    }

    // Append to the server chatArea.
    void appendRoom(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatArea.appendText(str + "\n");
            }
        });
    }

    // Append to the server eventlog.
    void appendEvent(String event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                eventLog.appendText(event + "\n");
            }
        });
    }

    // Handles the style of the button depending on whether the server is started or not.
    void startButtonStyleClass(){
        startButton.getStyleClass().clear();
        startButton.getStyleClass().add("button");
        if(startButton.getText().equals("Start")){
            startButton.getStyleClass().add("startButton");
        }else{
            startButton.getStyleClass().add("stopButton");
        }
    }
}
