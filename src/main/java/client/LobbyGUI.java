package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The GUI that shows the users connected to the chat(port).
 * Created by Michael on 2016-05-08.
 */
public class LobbyGUI {
    // The main GUI that starts this one.
    private SecureChatUI secureChatUI;

    // The textarea of the lobby.
    private TextArea lobbyArea;

    // Button to update lobby.
    private Button updateLobby;

    // Constructor
    public LobbyGUI(SecureChatUI secureChatUI){
        this.secureChatUI = secureChatUI;
    }

    // Starts this GUI.
    public void start(){
        // Creates the Stage, Scene and sets Title.
        Stage primaryStage = new Stage();
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("Lobby");

        // The textarea of the lobby settings.
        lobbyArea = new TextArea();
        lobbyArea.setPromptText("Lobby");
        lobbyArea.getStyleClass().add("textArea");
        lobbyArea.setEditable(false);

        // The button that updates the lobby.
        updateLobby = new Button("Update Lobby");
        updateLobby.setCenterShape(true);
        updateLobby.setDefaultButton(true);
        updateLobby.getStyleClass().add("updateLobbyButton");

        // Handles the update lobby event.
        updateLobby.setOnAction((event) ->{
            if(this.secureChatUI.getClient() != null){
                clearLobby();
                ChatMessage message = new ChatMessage(0, "text");
                this.secureChatUI.getClient().sendMessage(message);
            }else{
                clearLobby();
                appendToLobby("You are not connected to a server.");
            }
        });

        // Handles the close GUI event.
        primaryStage.setOnCloseRequest(event -> {
            this.secureChatUI.setLobbyGUI(null);
            primaryStage.close();
        });

        // HBox and VBox settings.
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        vBox.setSpacing(3);
        vBox.getChildren().addAll(lobbyArea, updateLobby);
        vBox.setCenterShape(true);
        hBox.getStyleClass().add("hboxLobby");
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(25, 25, 25, 25));
        hBox.getChildren().addAll(vBox);
        hBox.setCenterShape(true);

        // Adds children to the scene.
        ((Group) scene.getRoot()).getChildren().addAll(hBox);
        primaryStage.setScene(scene);

        // Add stylesheet to the scene.
        scene.getStylesheets().add("myCSS.css");

        // Shows primaryStage.
        primaryStage.show();
    }

    // Clears the lobby.
    void clearLobby(){
        lobbyArea.clear();
    }

    // Appends text to the lobby textarea.
    void appendToLobby(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lobbyArea.appendText(str + "\n");
            }
        });
    }
}
