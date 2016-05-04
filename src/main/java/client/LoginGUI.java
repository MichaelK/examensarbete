package client;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by Michael on 2016-05-03.
 */
public class LoginGUI {

    SecureChatUI secureChatUI;

    public LoginGUI(SecureChatUI secureChatUI){
        this.secureChatUI = secureChatUI;
    }

    public void start(){

        Stage primaryStage = new Stage();
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("Login GUI");

        HBox hb = new HBox();
        //hb.setPadding(new Insets(20,20,20,20));

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridPane");
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Label lblAlias = new Label("Alias");
        final TextField txtAlias = new TextField();
        txtAlias.setPromptText("Alias");
        Label lblServerIp = new Label("Server IP");
        final TextField txtServerIp = new TextField();
        txtServerIp.setPromptText("Server IP");
        Label lblPort = new Label("Port No");
        final TextField txtPort = new TextField();
        txtPort.setPromptText("Port No");
        Label lblPassword = new Label("Password");
        final PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("loginButton");

        gridPane.add(lblAlias, 0, 0);
        gridPane.add(txtAlias, 1, 0);
        gridPane.add(lblServerIp, 0, 1);
        gridPane.add(txtServerIp, 1, 1);
        gridPane.add(lblPort, 0, 2);
        gridPane.add(txtPort, 1, 2);
        gridPane.add(lblPassword, 0, 3);
        gridPane.add(passwordField, 1, 3);
        gridPane.add(loginButton, 1, 4);

        loginButton.setOnAction((event) ->{
            this.secureChatUI.login(txtAlias.getText(), txtServerIp.getText(), Integer.parseInt(txtPort.getText()), passwordField.getText());
            primaryStage.close();
        });

        hb.getChildren().addAll(gridPane);

        ((Group) scene.getRoot()).getChildren().addAll(hb);

        primaryStage.setScene(scene);

        scene.getStylesheets().add("myCSS.css");

        primaryStage.show();
    }
}
