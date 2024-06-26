package it.uninsubria.controller.dialog;

import it.uninsubria.controller.operatore.OperatoreViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateCentroDialog {
    @FXML
    private TextField inputNomeCentroField;
    @FXML
    private TextField inputComuneCentroField;
    @FXML
    private TextField inputStatoCmField;
    @FXML
    private TextField inputAreaTextField;
    @FXML
    private TextArea areeInteresseBox;
    private OperatoreViewController operatoreViewController;
    public UpdateCentroDialog(OperatoreViewController operatoreViewController){
        inputNomeCentroField = new TextField("Nome del centro");
        inputComuneCentroField = new TextField("Comune del centro");
        inputStatoCmField = new TextField("Stato del centro");
        inputAreaTextField = new TextField("Area associata al centro");
        areeInteresseBox = new TextArea();

        inputNomeCentroField.setOnMouseClicked((mouseClicked) -> this.inputNomeCentroField.clear());
        inputComuneCentroField.setOnMouseClicked((mouseClicked) -> this.inputComuneCentroField.clear());
        inputStatoCmField.setOnMouseClicked((mouseClicked) -> this.inputStatoCmField.clear());
        inputAreaTextField.setOnMouseClicked((mouseClicked) -> this.inputAreaTextField.clear());


        this.operatoreViewController = operatoreViewController;
    }

    @FXML
    public void handleAddAreaToBox(ActionEvent actionEvent){
        String nomeArea = inputAreaTextField.getText();
        if(!nomeArea.isEmpty()){
            String text = areeInteresseBox.getText();
            text += nomeArea + "\n";
            areeInteresseBox.setText(text);
        }else{
            new Alert(Alert.AlertType.ERROR,"Il nome dell'area non puo' essere vuota").showAndWait();
        }
    }

    @FXML
    public void handleRemoveAreaFromBox(ActionEvent actionEvent){
        String nomeAreaDaRimuovere = inputAreaTextField.getText();
        if(!nomeAreaDaRimuovere.isEmpty()){
            List<String> nomiAree = Arrays.stream(areeInteresseBox.getText().split("\n")).collect(Collectors.toList());
            if(nomiAree.contains(nomeAreaDaRimuovere)){
                nomiAree = nomiAree.stream().filter(nome -> !nome.equals(nomeAreaDaRimuovere)).toList();
                areeInteresseBox.clear();
                StringBuilder text = new StringBuilder();
                for(String s : nomiAree) text.append(s).append("\n");
                areeInteresseBox.setText(text.toString());
            }
        }

    }
    @FXML
    public void clearFields(){
        inputNomeCentroField.clear();
        inputComuneCentroField.clear();
        inputStatoCmField.clear();
        inputAreaTextField.clear();
        areeInteresseBox.clear();

    }

    @FXML
    public void handleDone(){
        String nomeCentro = inputNomeCentroField.getText();
        String comuneCentro = inputComuneCentroField.getText();
        String statoCentro = inputStatoCmField.getText();
        String areeAssociate = areeInteresseBox.getText();

        if(nomeCentro.isEmpty() || comuneCentro.isEmpty() || statoCentro.isEmpty()
                || nomeCentro.equals("Nome del centro")
                || comuneCentro.equals("Comune del centro")
                || statoCentro.equals("Area associata al centro")){
            new Alert(Alert.AlertType.ERROR, "Stringa invalida").showAndWait();
        }

        operatoreViewController.executeInsertCMQuery(nomeCentro, comuneCentro, statoCentro, areeAssociate);
    }

}
