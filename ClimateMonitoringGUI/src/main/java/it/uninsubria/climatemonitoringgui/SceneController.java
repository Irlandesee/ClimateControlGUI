package it.uninsubria.climatemonitoringgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.HashMap;


public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public static LoginViewController getLoginSceneController(){
        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource("login-scene.fxml"));
        try {
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

    public static MainWindowController getMainSceneController(){
        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource("main-scene.fxml"));
        try{
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

    public static RegistrazioneController getRegistrazioneController(){
        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource("registrazione-scene.fxml"));
        try{
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

    public void switchToMainScene(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("main-scene.fxml")); //watch out for this line of code
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLoginScene(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("login-scene.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToRegOp(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("registrazione-scene.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
