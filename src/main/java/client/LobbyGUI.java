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
 * Created by Michael on 2016-05-08.
 */
public class LobbyGUI {

    SecureChatUI secureChatUI;

    TextArea lobbyArea;
    Button updateLobby;

    public LobbyGUI(SecureChatUI secureChatUI){
        this.secureChatUI = secureChatUI;
    }

    public void start(){

        Stage primaryStage = new Stage();
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("Lobby");

        HBox hBox = new HBox();
        VBox vBox = new VBox();

        lobbyArea = new TextArea();
        lobbyArea.setPromptText("Lobby");
        lobbyArea.setEditable(false);

        updateLobby = new Button("Start");
        updateLobby.setDefaultButton(true);
        //startButtonStyleClass();
        updateLobby.setOnAction((event) ->{

        });

        vBox.setSpacing(3);
        vBox.getChildren().addAll(lobbyArea, updateLobby);
        vBox.setCenterShape(true);

        hBox.getStyleClass().add("hboxLobby");
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(25, 25, 25, 25));
        hBox.getChildren().addAll(vBox);
        hBox.setCenterShape(true);

        ((Group) scene.getRoot()).getChildren().addAll(hBox);

        primaryStage.setScene(scene);

        scene.getStylesheets().add("myCSS.css");

        primaryStage.show();
    }

    void clearLobby(){
        lobbyArea.clear();
    }

    void appendToLobby(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lobbyArea.appendText(str + "\n");
            }
        });
    }
}
