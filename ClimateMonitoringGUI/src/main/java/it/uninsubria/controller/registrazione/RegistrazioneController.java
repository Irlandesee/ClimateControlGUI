package it.uninsubria.controller.registrazione;

import it.uninsubria.controller.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrazioneController {
    public TextField nomeOpField;
    public TextField cognomeField;
    public TextField codFiscaleField;
    public TextField userIDField;
    public TextField emailField;
    public PasswordField passwordField;
    public TextField centroField;
    public Button btnRegistra;
    public Button cancelButton;

    private Alert invalidFieldAlert;

    private SceneController sceneController;
    public RegistrazioneController(SceneController sceneController){
        this.sceneController = sceneController;
    }

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
        String userID = userIDField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String centroAfferenza = centroField.getText();

        if(nomeOp.isEmpty() || cognomeOp.isEmpty() || codFiscOp.isEmpty() ||
                userID.isEmpty() || email.isEmpty() || password.isEmpty() || centroAfferenza.isEmpty()){
            invalidFieldAlert.showAndWait();
        }
        try{
            sceneController
                    .getMainWindowController()
                    .onExecuteRegistraOpQuery(nomeOp, cognomeOp, codFiscOp, userID, email, password, centroAfferenza);
        }catch(NullPointerException npe){
            System.out.println("Null Pointer exception while executing registra op");
        }
        clearFields();
    }

    private void clearFields(){
        nomeOpField.clear();
        cognomeField.clear();
        codFiscaleField.clear();
        emailField.clear();
        passwordField.clear();
        centroField.clear();
    }

    public void cancel(ActionEvent actionEvent) {
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }
}
