package it.climatemonitoring.controller.loginview;

import it.climatemonitoring.MainWindow;
import it.climatemonitoring.controller.mainscene.MainWindowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    public Button cancelButton;

    private Alert invalidUserNameOrPassword;
    private Alert loggedIn;
    private final MainWindowController mainWindowController;
    public LoginViewController(MainWindowController mainWindowController){
        this.mainWindowController = mainWindowController;
    }

    @FXML
    public void initialize(){
        invalidUserNameOrPassword = new Alert(Alert.AlertType.ERROR);
        invalidUserNameOrPassword.setHeaderText("Login Error");
        invalidUserNameOrPassword.setContentText("Invalid user name or password");

        loggedIn = new Alert(Alert.AlertType.CONFIRMATION);
        loggedIn.setHeaderText("Logged In");
        loggedIn.setContentText("Logged in successfully");
    }


    /**
     * Creazione di una nuova finestra di registrazione utente
     * @param actionEvent
     */
    public void registra(ActionEvent actionEvent) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/registrazione-scene.fxml"));
            fxmlLoader.setController(mainWindowController.getRegistrazioneController());
            Scene scene = new Scene(fxmlLoader.load(), 800, 400);
            Stage registrazioneStage = (Stage)((Node) actionEvent
                    .getSource())
                    .getScene()
                    .getWindow();
            registrazioneStage.setScene(scene);
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    /**
     * Creazione di una richiesta di login. In caso positivo, si passa automaticamente alla schermata per operatori
     * @param actionEvent
     */
    public void login(ActionEvent actionEvent) {
        String userID = useridField.getText();
        String password = passwordField.getText();
        if(userID.isEmpty() && password.isEmpty()) {invalidUserNameOrPassword.showAndWait();}
        else{
            try{
                boolean loginResult = mainWindowController.onExecuteLoginQuery(userID, password);
                if(loginResult){
                    loggedIn.showAndWait();
                    Stage s = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                    if(s != null) s.close();
                }
                else{
                    invalidUserNameOrPassword.showAndWait();
                }
            }catch(NullPointerException npe){npe.printStackTrace();}
        }
    }

    public void cancel(ActionEvent actionEvent) {
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }

}
