package it.uninsubria.controller.parametroclimatico;

import it.uninsubria.controller.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ParametroClimaticoController {


    public TextField areaInteresseField;
    public TextField cmField;
    public DatePicker pubDate;
    public TextField ventoField;
    public TextField notaVentoField;
    public TextField umiditaField;
    public TextField notaUmiditaField;
    public TextField pressioneField;
    public TextField notaPressioneField;
    public TextField precipitazioniField;
    public TextField notaPrecipitazioniField;
    public TextField temperaturaField;
    public TextField notaTemperaturaField;
    public TextField altGhiacciaiField;
    public TextField notaAltGhiacciaiField;
    public TextField massaField;
    public TextField notaMassaField;
    public Button cancelButton;
    public Button InserisciPC;
    public Button clearButton;

    private Alert nomeOrCentroError;
    private Alert invalidDateError;
    private Alert pcAlert;
    
    @FXML
    public void initialize(){
        nomeOrCentroError = new Alert(Alert.AlertType.ERROR);
        nomeOrCentroError.setHeaderText("Input error");
        nomeOrCentroError.setContentText("Nome area o nome centro non validi");
        
        invalidDateError = new Alert(Alert.AlertType.ERROR);
        invalidDateError.setHeaderText("Date error");
        invalidDateError.setContentText("Data invalida");
        
        pcAlert = new Alert(Alert.AlertType.ERROR);
        pcAlert.setHeaderText("Valore pc non valido!");
        pcAlert.setContentText("valore non valido");
        
    }

    public void insericiPC(ActionEvent actionEvent) {
        String nomeArea = areaInteresseField.getText();
        String centroMon = cmField.getText();
        LocalDate pubdate = pubDate.getValue();
        String ventoTmp = ventoField.getText();
        String notaVento = notaVentoField.getText();
        String umiditaTmp = umiditaField.getText();
        String notaUmidita = notaUmiditaField.getText();
        String pressioneTmp = pressioneField.getText();
        String notaPressione = notaPressioneField.getText();
        String precipitazioniTmp = precipitazioniField.getText();
        String notaPrecipitazioni = notaPrecipitazioniField.getText();
        String temperaturaTmp = temperaturaField.getText();
        String notaTemperatura = notaTemperaturaField.getText();
        String altGhiacciaiTmp = altGhiacciaiField.getText();
        String notaAltGhiacciai = notaAltGhiacciaiField.getText();
        String massaGhiacciaiTmp = massaField.getText();
        String notaMassaGhiacciai = notaMassaField.getText();

        short ventoValue;
        short umiditaValue;
        short pressioneValue;
        short precipitazioniValue;
        short tempValue;
        short altGhiacciaiValue;
        short massaGhiacciaiValue;

        if(nomeArea.isEmpty() || centroMon.isEmpty()){
            nomeOrCentroError.showAndWait();
        }
        LocalDate startDateTmp = LocalDate.of(1900, 1, 1);
        LocalDate endDateTmp = LocalDate.of(2100, 1, 1);
        //pubdate check
        if(pubdate.isBefore(startDateTmp) || pubdate.isAfter(endDateTmp))
            invalidDateError.showAndWait();
        if(!ventoTmp.isEmpty()){
            ventoValue = Short.parseShort(ventoTmp);
            if(ventoValue <= 0 || ventoValue > 5)
                pcAlert.showAndWait();
        }else{
            ventoValue = Short.MIN_VALUE;
        }
        if(!umiditaTmp.isEmpty()){
            umiditaValue = Short.parseShort(umiditaTmp);
            if(umiditaValue <= 0 || umiditaValue > 5)
                pcAlert.showAndWait();
        }else{
            umiditaValue = Short.MIN_VALUE;
        }
        if(!pressioneTmp.isEmpty()){
            pressioneValue = Short.parseShort(pressioneTmp);
            if(pressioneValue <= 0 || pressioneValue > 5)
                pcAlert.showAndWait();
        }else{
            pressioneValue = Short.MIN_VALUE;
        }
        if(!precipitazioniTmp.isEmpty()){
            precipitazioniValue = Short.parseShort(precipitazioniTmp);
            if(precipitazioniValue <= 0 || precipitazioniValue > 5)
                pcAlert.showAndWait();
        }else{
            precipitazioniValue = Short.MIN_VALUE;
        }
        if(!temperaturaTmp.isEmpty()){
            tempValue = Short.parseShort(temperaturaTmp);
            if(tempValue < 0 || tempValue > 5)
                pcAlert.showAndWait();
        }else{
            tempValue = Short.MIN_VALUE;
        }
        if(!altGhiacciaiTmp.isEmpty()){
            altGhiacciaiValue = Short.parseShort(altGhiacciaiTmp);
            if(altGhiacciaiValue < 0 || altGhiacciaiValue > 5)
                pcAlert.showAndWait();
        }else{
            altGhiacciaiValue = Short.MIN_VALUE;
        }
        if(!massaGhiacciaiTmp.isEmpty()){
            massaGhiacciaiValue = Short.parseShort(massaGhiacciaiTmp);
            if(massaGhiacciaiValue <= 0 || massaGhiacciaiValue > 5)
                pcAlert.showAndWait();
        }else{
            massaGhiacciaiValue = Short.MIN_VALUE;
        }
        short[] paramValues = new short[7];
        String[] notes = new String[7];
        paramValues[0] = ventoValue;
        paramValues[1] = umiditaValue;
        paramValues[2] = pressioneValue;
        paramValues[3] = precipitazioniValue;
        paramValues[4] = tempValue;
        paramValues[5] = altGhiacciaiValue;
        paramValues[6] = massaGhiacciaiValue;
        notes[0] = notaVento;
        notes[1] = notaUmidita;
        notes[2] = notaPressione;
        notes[3] = notaPrecipitazioni;
        notes[4] = notaTemperatura;
        notes[5] = notaAltGhiacciai;
        notes[6] = notaMassaGhiacciai;

        //query the db
        try {
            SceneController
                    .getMainSceneController()
                    .executeInsertPCQuery(nomeArea, centroMon, pubdate, paramValues, notes);
        }catch(NullPointerException npe){System.out.println("NullPointerException while executing insertPC query");}
        clearValoriFields();
    }

    private void clearValoriFields(){
        ventoField.clear();
        notaVentoField.clear();
        umiditaField.clear();
        notaUmiditaField.clear();
        pressioneField.clear();
        notaPressioneField.clear();
        precipitazioniField.clear();
        notaPrecipitazioniField.clear();
        temperaturaField.clear();
        notaTemperaturaField.clear();
        altGhiacciaiField.clear();
        notaAltGhiacciaiField.clear();
        massaField.clear();
        notaMassaField.clear();

    }

    @FXML
    private void clearAllFields(){
        areaInteresseField.clear();
        cmField.clear();
        pubDate.getEditor().clear();
        pubDate.setValue(null);
        clearValoriFields();
    }


    public void cancel(ActionEvent actionEvent) {
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }
}
