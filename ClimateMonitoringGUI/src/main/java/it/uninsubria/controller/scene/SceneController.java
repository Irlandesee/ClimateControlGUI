package it.uninsubria.controller.scene;

import it.uninsubria.MainWindow;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.loginview.LoginViewController;
import it.uninsubria.controller.registrazione.RegistrazioneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SceneController {
    public static LoginViewController getLoginSceneController(){
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("fxml/login-scene.fxml"));
        try {
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

    public static MainWindowController getMainSceneController(){
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("fxml/main-scene.fxml"));
        try{
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

    public static RegistrazioneController getRegistrazioneController(){
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("fxml/registrazione-scene.fxml"));
        try{
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

}
