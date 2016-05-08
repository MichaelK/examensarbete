package client;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by Michael on 2016-05-08.
 */
public class LobbyGUI {

    SecureChatUI secureChatUI;

    public LobbyGUI(SecureChatUI secureChatUI){
        this.secureChatUI = secureChatUI;
    }

    public void start(){

        Stage primaryStage = new Stage();
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("Lobby");

        HBox hb = new HBox();

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridPane");
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Label lblAlias = new Label("Alias");
        Label lblConnectedSince = new Label("Connected since");
        Label lblone = new Label("One");
        Label lbltwo = new Label("Two");

        gridPane.add(lblAlias, 0, 0);
        gridPane.add(lblConnectedSince, 1, 0);
        gridPane.add(lblone, 0, 1);
        gridPane.add(lbltwo, 1, 1);

        hb.getChildren().addAll(gridPane);
        
        ((Group) scene.getRoot()).getChildren().addAll(hb);

        primaryStage.setScene(scene);

        scene.getStylesheets().add("myCSS.css");

        primaryStage.show();
    }
}
