package server;

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

/**
 * Created by Michael on 2016-02-27.
 */
public class ServerGUI {

    final HBox hb = new HBox();

    final VBox vbox = new VBox();
    final VBox vbox2 = new VBox();

    private Button stopStartButton;
    // TextArea for the chatArea room and the events
    // The port number
    private TextField portNo;

    // my server
    private Server server;

    TextArea chatArea;
    TextArea eventLog;

    public void start(){

        Stage primaryStage = new Stage();
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("Server GUI");
        //primaryStage.setWidth(530);
        //primaryStage.setHeight(490);

        portNo = new TextField();
        Label portLabel = new Label("Port Number:");
        portLabel.setFont(new Font("Arial", 12));
        portNo.setPromptText("Port No ");
        portNo.setEditable(true);

        stopStartButton = new Button("Start");
        stopStartButton.getStyleClass().add("stopStartButton");
        stopStartButton.setOnAction((event) ->{
            startStopButton();
        });

        chatArea = new TextArea();
        chatArea.setPromptText("Chatroom");
        chatArea.setEditable(false);

        eventLog = new TextArea();
        eventLog.setPromptText("Eventlog");
        eventLog.setEditable(false);
        eventLog.setScrollTop(100);

        hb.setSpacing(3);
        //hb.setPadding(new Insets(10, 0, 0, 10));
        hb.getChildren().addAll(portLabel, portNo, stopStartButton);
        hb.setCenterShape(true);

        vbox.setSpacing(5);
        //vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(chatArea, eventLog);
        vbox.setCenterShape(true);

        vbox2.getStyleClass().add("vbox2");
        vbox2.setSpacing(5);
        vbox2.setPadding(new Insets(25, 25, 25, 25));
        vbox2.getChildren().addAll(hb,vbox);
        vbox2.setCenterShape(true);

        ((Group) scene.getRoot()).getChildren().addAll(vbox2);

        primaryStage.setScene(scene);

        scene.getStylesheets().add("myCSS.css");

        primaryStage.show();
    }


    // start or stop where clicked
    public void startStopButton(){
        // if running we have to stop
        if(server != null) {
            server.stop();
            server = null;
            stopStartButton.setText("Start");
            return;
        }
        // OK start the server
        int port;
        try {
            port = Integer.parseInt(portNo.getText().trim());
        }
        catch(Exception ex) {
            appendEvent("Invalid port number");
            System.out.println("Invalid port number");
            return;
        }
        // ceate a new Server
        server = new Server(port, this);
        // and start it as a thread
        new ServerRunning().start();
        stopStartButton.setText("Stop");
        portNo.setEditable(false);
    }


    void appendRoom(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatArea.appendText(str);
                chatArea.positionCaret(chatArea.getText().length() - 1);
            }
        });
    }

    void appendEvent(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                eventLog.appendText(str);
                eventLog.positionCaret(eventLog.getText().length() - 1);
            }
        });
    }

    public Server getServer() {
        return server;
    }

    // A thread to run the Server
    class ServerRunning extends Thread {
        public void run() {
            //Start server and keep it alive until manually stopped
            server.start();
            //Server has either crashed or been manually stopped
            stopStartButton.setText("Start");
            portNo.setEditable(true);
            appendEvent("Server stopped!\n");
            server = null;
        }
    }

}
