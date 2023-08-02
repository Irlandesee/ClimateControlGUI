package it.uninsubria.climatemonitoringgui.controller.scene;

import it.uninsubria.climatemonitoringgui.controller.mainscene.MainWindowController;
import it.uninsubria.climatemonitoringgui.controller.loginview.LoginViewController;
import it.uninsubria.climatemonitoringgui.controller.registrazione.RegistrazioneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public static LoginViewController getLoginSceneController(){
        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource("fxml/login-scene.fxml"));
        try {
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

    public static MainWindowController getMainSceneController(){
        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource("fxml/main-scene.fxml"));
        try{
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

    public static RegistrazioneController getRegistrazioneController(){
        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource("fxml/registrazione-scene.fxml"));
        try{
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

}
