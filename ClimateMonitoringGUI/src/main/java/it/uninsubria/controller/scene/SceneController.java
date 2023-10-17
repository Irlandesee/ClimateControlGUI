package it.uninsubria.controller.scene;

import it.uninsubria.MainWindow;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.loginview.LoginViewController;
import it.uninsubria.controller.operatore.OperatoreViewController;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.controller.registrazione.RegistrazioneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SceneController {

    private LoginViewController loginViewController;
    private MainWindowController mainWindowController;
    private OperatoreViewController operatoreViewController;
    private RegistrazioneController registrazioneController;
    private ParametroClimaticoController parametroClimaticoController;

    public SceneController(MainWindowController mainWindowController){
        this.mainWindowController = mainWindowController;
    }

    public MainWindowController getMainWindowController(){
        return this.mainWindowController;
    }

    public LoginViewController getLoginViewController(){
        return this.loginViewController;
    }

    public OperatoreViewController getOperatoreViewController(){
        return this.operatoreViewController;
    }

    public RegistrazioneController getRegistrazioneController(){
        return this.registrazioneController;
    }

    public ParametroClimaticoController getParametroClimaticoController(){
        return this.parametroClimaticoController;
    }

    public void setMainWindowController(MainWindowController mainWindowController){
        this.mainWindowController = mainWindowController;
    }

    public void setLoginViewController(LoginViewController loginViewController){
        this.loginViewController = loginViewController;
    }

    public void setOperatoreViewController(OperatoreViewController operatoreViewController){
        this.operatoreViewController = operatoreViewController;
    }

    public void setRegistrazioneController(RegistrazioneController registrazioneController){
        this.registrazioneController = registrazioneController;
    }

    public void setParametroClimaticoController(ParametroClimaticoController parametroClimaticoController){
        this.parametroClimaticoController = parametroClimaticoController;
    }

    /**
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

    public static OperatoreViewController getOperatoreViewController(){
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("fxml/operatore-scene.fxml"));
        try{
            Parent root = loader.load();
            return loader.getController();
        }catch(IOException ioe){ioe.printStackTrace(); return null;}
    }

     **/

}
