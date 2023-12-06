package it.uninsubria.controller.loginview;

import it.uninsubria.MainWindow;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.operatore.OperatoreViewController;
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

    public void login(ActionEvent actionEvent) {
        String userID = useridField.getText();
        //TODO: hash the password for security reasons
        String password = passwordField.getText();
        if(userID.isEmpty() && password.isEmpty()) {invalidUserNameOrPassword.showAndWait();}
        else{
            try{
                boolean loginResult = mainWindowController.onExecuteLoginQuery(userID, password);
                if(loginResult){
                    loggedIn.showAndWait();
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
