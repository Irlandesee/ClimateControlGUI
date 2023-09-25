package it.uninsubria.controller.loginview;

import it.uninsubria.MainWindow;
import it.uninsubria.controller.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
            Parent root = FXMLLoader.load(MainWindow.class.getResource("fxml/registrazione-scene.fxml")); //watch out for this line of code
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public void login(ActionEvent actionEvent) {
        String userID = useridField.getText();
        String password = passwordField.getText();
        if(userID.isEmpty() && password.isEmpty()) {invalidUserNameOrPassword.showAndWait();}
        else{
            String query = userID + password;
            System.out.println(query);
            try{
                boolean loggedIN = SceneController
                        .getMainSceneController()
                        .executeLoginQuery(userID, password);
                if(loggedIN) {
                    loggedIn.showAndWait();
                    Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                    if(s != null)
                        s.close();

                }else{
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
