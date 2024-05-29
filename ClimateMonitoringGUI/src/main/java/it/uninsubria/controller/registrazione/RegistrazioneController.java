package it.uninsubria.controller.registrazione;

import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.operatore.OperatoreViewController;
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
    private Alert registrationFailed;
    private Alert registrationSuccess;

    private final MainWindowController mainWindowController;

    public RegistrazioneController(MainWindowController mainWindowController){
        this.mainWindowController = mainWindowController;
    }

    @FXML
    public void initialize(){
        invalidFieldAlert = new Alert(Alert.AlertType.ERROR);
        invalidFieldAlert.setHeaderText("Invalid field");
        invalidFieldAlert.setContentText("Campo inserito non valido");

        registrationFailed = new Alert(Alert.AlertType.ERROR);
        registrationFailed.setHeaderText("Fallimento Registrazione");
        registrationFailed.setContentText("Campo Inserito non valido oppure utente non abilitato alla registrazione!");

        registrationSuccess = new Alert(Alert.AlertType.CONFIRMATION);
        registrationSuccess.setHeaderText("Registrazione Successo");
        registrationSuccess.setHeaderText("Registrazione avvenuta con successo!");
        nomeOpField.setText("Nicole");
        cognomeField.setText("Bagattini");
        codFiscaleField.setText("UHUT3VQHGPHFE2I3");
        userIDField.setText("bagatins");
        emailField.setText("prova1234@gmail.com");
        passwordField.setText("elocin");
        centroField.setText("Parma");
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
            boolean resultRegistrazione = mainWindowController.onExecuteRegistraOpQuery(nomeOp, cognomeOp, codFiscOp, userID, email, password, centroAfferenza);
            if(resultRegistrazione){
                clearFields();
                registrationSuccess.showAndWait();
                Stage registrazioneStage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
                registrazioneStage.close();
            }else{
                clearFields();
                registrationFailed.showAndWait();
            }
        }catch(NullPointerException npe){
            System.out.println("Null Pointer exception while executing registra op");
        }
    }

    private void clearFields(){
        nomeOpField.clear();
        cognomeField.clear();
        codFiscaleField.clear();
        userIDField.clear();
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
