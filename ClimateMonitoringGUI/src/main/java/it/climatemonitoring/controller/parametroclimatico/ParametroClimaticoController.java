package it.climatemonitoring.controller.parametroclimatico;

import it.climatemonitoring.controller.operatore.OperatoreViewController;
import it.climatemonitoring.factories.RequestFactory;
import it.climatemonitoring.util.IDGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ParametroClimaticoController {


    public TextField areaInteresseField;
    public TextField cmField;
    public DatePicker pubDatePicker;
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
    private OperatoreViewController operatoreViewController;
    public ParametroClimaticoController(OperatoreViewController operatoreViewController){
        this.operatoreViewController = operatoreViewController;
    }
    
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

    /**
     * Lettura dei campi dei parametri climatici inseriti e creazione di una richiesta di inserimento
     * @param actionEvent
     */
    public void insericiPC(ActionEvent actionEvent) {
        String nomeArea = areaInteresseField.getText();
        String centroMon = cmField.getText();
        LocalDate pubDate = pubDatePicker.getValue();
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
            return;
        }
        LocalDate startDateTmp = LocalDate.of(1900, 1, 1);
        LocalDate endDateTmp = LocalDate.of(2100, 1, 1);
        //pubdate check
        if(pubDate != null){
            if(pubDate.isBefore(startDateTmp) || pubDate.isAfter(endDateTmp)){
                invalidDateError.showAndWait();
                return;
            }
        }else{
            invalidDateError.showAndWait();
            return;
        }
        try{
            if(!ventoTmp.isEmpty()){
                ventoValue = Short.parseShort(ventoTmp);
                if(ventoValue <= 0 || ventoValue > 5){
                    pcAlert.showAndWait();
                    return;
                }
            }else{
                ventoValue = Short.MIN_VALUE;
            }
            if(!umiditaTmp.isEmpty()){
                umiditaValue = Short.parseShort(umiditaTmp);
                if(umiditaValue <= 0 || umiditaValue > 5){
                    pcAlert.showAndWait();
                    return;
                }
            }else{
                umiditaValue = Short.MIN_VALUE;
            }
            if(!pressioneTmp.isEmpty()){
                pressioneValue = Short.parseShort(pressioneTmp);
                if(pressioneValue <= 0 || pressioneValue > 5){
                    pcAlert.showAndWait();
                    return;
                }
            }else{
                pressioneValue = Short.MIN_VALUE;
            }
            if(!precipitazioniTmp.isEmpty()){
                precipitazioniValue = Short.parseShort(precipitazioniTmp);
                if(precipitazioniValue <= 0 || precipitazioniValue > 5){
                    pcAlert.showAndWait();
                    return;
                }
            }else{
                precipitazioniValue = Short.MIN_VALUE;
            }
            if(!temperaturaTmp.isEmpty()){
                tempValue = Short.parseShort(temperaturaTmp);
                if(tempValue < 0 || tempValue > 5){
                    pcAlert.showAndWait();
                    return;
                }
            }else{
                tempValue = Short.MIN_VALUE;
            }
            if(!altGhiacciaiTmp.isEmpty()){
                altGhiacciaiValue = Short.parseShort(altGhiacciaiTmp);
                if(altGhiacciaiValue < 0 || altGhiacciaiValue > 5){
                    pcAlert.showAndWait();
                    return;
                }
            }else{
                altGhiacciaiValue = Short.MIN_VALUE;
            }
            if(!massaGhiacciaiTmp.isEmpty()){
                massaGhiacciaiValue = Short.parseShort(massaGhiacciaiTmp);
                if(massaGhiacciaiValue <= 0 || massaGhiacciaiValue > 5){
                    pcAlert.showAndWait();
                    return;
                }
            }else{
                massaGhiacciaiValue = Short.MIN_VALUE;
            }
        }catch(NumberFormatException nfe){
            nfe.printStackTrace();
            pcAlert.showAndWait();
            return;
        }
        Map<String, String> paramValues = new HashMap<String, String>();
        Map<String, String> notes = new HashMap<String, String>();
        paramValues.put(RequestFactory.valoreVentoKey, String.valueOf(ventoValue));
        paramValues.put(RequestFactory.valoreUmiditaKey, String.valueOf(umiditaValue));
        paramValues.put(RequestFactory.valorePressioneKey, String.valueOf(pressioneValue));
        paramValues.put(RequestFactory.valorePrecipitazioniKey, String.valueOf(precipitazioniValue));
        paramValues.put(RequestFactory.valoreTemperaturaKey, String.valueOf(tempValue));
        paramValues.put(RequestFactory.valoreAltGhiacciaiKey, String.valueOf(altGhiacciaiValue));
        paramValues.put(RequestFactory.valoreMassaGhiacciaiKey, String.valueOf(massaGhiacciaiValue));

        notes.put(RequestFactory.notaVentoKey, notaVento);
        notes.put(RequestFactory.notaUmiditaKey, notaUmidita);
        notes.put(RequestFactory.notaPressioneKey, notaPressione);
        notes.put(RequestFactory.notaPrecipitazioniKey, notaPrecipitazioni);
        notes.put(RequestFactory.notaTemperaturaKey, notaTemperatura);
        notes.put(RequestFactory.notaAltGhiacciaiKey, notaAltGhiacciai);
        notes.put(RequestFactory.notaMassaGhiacciaiKey, notaMassaGhiacciai);

        String parameterId = IDGenerator.generateID();
        String notaId = IDGenerator.generateID();
        operatoreViewController.executeInsertPCQuery(parameterId, nomeArea, centroMon, pubDate, paramValues, notaId, notes);
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
        pubDatePicker.getEditor().clear();
        pubDatePicker.setValue(null);
        clearValoriFields();
    }


   @FXML
    public void cancel(ActionEvent actionEvent) {
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }
}
