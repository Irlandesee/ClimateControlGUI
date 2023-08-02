package it.uninsubria.climatemonitoringgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {
    public TextField useridField;
    public PasswordField passwordField;
    public Button buttonRegistra;
    public Button buttonLogin;
    public Button backToMainScene;


    public void registra(ActionEvent actionEvent) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("registrazione-scene.fxml")); //watch out for this line of code
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public void login(ActionEvent actionEvent) {
    }

    public void backToMainScene(ActionEvent actionEvent) {
        MainWindowController mainWindowController = SceneController.getMainSceneController();
        mainWindowController.setLoggedInLabel("Logged IN!");
        try{
            Parent root = FXMLLoader.load(getClass().getResource("main-scene.fxml")); //watch out for this line of code
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch(IOException ioe){ioe.printStackTrace();}

    }
}
