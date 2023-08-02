package it.uninsubria.climatemonitoringgui;

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

public class RegistrazioneController {
    public TextField nomeOpField;
    public TextField cognomeField;
    public TextField codFiscaleField;
    public TextField emailField;
    public PasswordField passwordField;
    public TextField centroField;
    public Button btnRegistra;
    public Button cancelButton;

    private Alert invalidFieldAlert;

    @FXML
    public void initialize(){
        invalidFieldAlert = new Alert(Alert.AlertType.ERROR);
        invalidFieldAlert.setHeaderText("Invalid field");
        invalidFieldAlert.setContentText("Campo inserito non valido");
    }

    public void registraOp(ActionEvent actionEvent) {
        String nomeOp = nomeOpField.getText();
        String cognomeOp = cognomeField.getText();
        String codFiscOp = codFiscaleField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String centroAfferenza = centroField.getText();

        if(nomeOp.isEmpty() || cognomeOp.isEmpty() || codFiscOp.isEmpty() ||
            email.isEmpty() || password.isEmpty() || centroAfferenza.isEmpty()){
            invalidFieldAlert.showAndWait();
        }
        String query = nomeOp + cognomeOp + codFiscOp + email + password + centroAfferenza;
        System.out.println(query);
    }

    public void cancel(ActionEvent actionEvent) {
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }
}
