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
 * The LoginGUI for the application.
 * Created by Michael on 2016-05-03.
 */
public class LoginGUI {
    // The main GUI that starts this one.
    SecureChatUI secureChatUI;

    // Constructor
    public LoginGUI(SecureChatUI secureChatUI){
        this.secureChatUI = secureChatUI;
    }

    // Starts the LoginGUI.
    public void start(){
        // Create primaryStage, Scene and set title.
        Stage primaryStage = new Stage();
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("Login");

        // HBox and GridPane
        HBox hb = new HBox();
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridPane");
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Labels, textFields etc for login values.
        Label lblAlias = new Label("Alias");
        final TextField txtAlias = new TextField();
        txtAlias.getStyleClass().add("textField");
        txtAlias.setPromptText("Alias");
        Label lblServerIp = new Label("Server IP");
        final TextField txtServerIp = new TextField();
        txtServerIp.getStyleClass().add("textField");
        txtServerIp.setPromptText("Server IP");
        Label lblPort = new Label("Port No");
        final TextField txtPort = new TextField();
        txtPort.getStyleClass().add("textField");
        txtPort.setPromptText("Port No");
        Label lblPassword = new Label("Password");
        final PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("textField");
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        loginButton.getStyleClass().add("loginButton");

        // Add to the GridPane.
        gridPane.add(lblAlias, 0, 0);
        gridPane.add(txtAlias, 1, 0);
        gridPane.add(lblServerIp, 0, 1);
        gridPane.add(txtServerIp, 1, 1);
        gridPane.add(lblPort, 0, 2);
        gridPane.add(txtPort, 1, 2);
        gridPane.add(lblPassword, 0, 3);
        gridPane.add(passwordField, 1, 3);
        gridPane.add(loginButton, 1, 4);

        // Handles the login button event.
        loginButton.setOnAction((event) ->{
            // Try to login. If it works then close the LoginGUI.
            try{
                this.secureChatUI.login(txtAlias.getText(), txtServerIp.getText(), Integer.parseInt(txtPort.getText()), passwordField.getText());
                this.secureChatUI.setLoginGUI(null);
                primaryStage.close();
            // If not all fields are input correctly then login fails with this message.
            }catch(Exception e) {
                this.secureChatUI.append("Login failed. All fields need to be input correctly.");
            }
        });

        // Handles the close primaryStage event.
        primaryStage.setOnCloseRequest(event -> {
            this.secureChatUI.setLoginGUI(null);
            primaryStage.close();
        });

        // Add children to the Scene.
        hb.getChildren().addAll(gridPane);
        ((Group) scene.getRoot()).getChildren().addAll(hb);
        primaryStage.setScene(scene);

        // Adds CSS to the Scene.
        scene.getStylesheets().add("myCSS.css");

        // Show LoginGUI.
        primaryStage.show();
    }
}
