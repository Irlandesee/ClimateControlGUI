package it.uninsubria.climatemonitoringgui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;

public class MainWindowController{
    public Button buttonRicercaAreaInteresse;
    public Button buttonVisualizzaParametri;
    public Button buttonInserisciParametri;
    public Button buttonInserisciCentroMonitoraggio;
    public Button buttonRegistraOp;
    public TableView tableView;
    public Button loginButton;

    public BorderPane borderPane;

    public VBox contentBox;
    public VBox paramBox;
    public Label loggedInLabel;

    //per area interesse
    private TextField tDenominazione;
    private TextField tStato;
    private TextField tLatitudine;
    private TextField tLongitudine;
    private Button btnRicercaAreaPerDenom;
    private Button btnRicercaAreaCoord;

    //per visualizzazione parametri climatici
    private TextField tAreaInteresse;
    private TextField tCentroMonitoraggio;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button btnRicercaPC;

    //per inserimento
    private DatePicker pubdate;
    private TextField ventoField;
    private TextField umiditaField;
    private TextField pressioneField;
    private TextField precipitazioniField;
    private TextField tempField;
    private TextField altGhiacciaiField;
    private TextField massaGhiacciaiField;

    private Button inserisciPC;

    //per centro monitoraggio
    private TextField nomeCentroField;
    private TextField comuneField;
    private TextField statoCMField;
    private TextField areaInteresseCMField;
    private Button inserisciCM;

    //per operatore
    private TextField nomeOp;
    private TextField cognomeOp;
    private TextField codFiscOp;
    private TextField emailOp;
    private TextField useridOp;
    private PasswordField passwordOp;
    private TextField centroID;
    private Button btnRegistraOp;

    //alerts
    private Alert coordAlert;
    private Alert denomAlert;
    private Alert statoAlert;
    private Alert pcAlert;
    private Alert areaInteresseAlert;
    private Alert invalidDateAlert;
    private Alert cmAlert;

    private SceneController sceneController;

    public void setLoggedInLabel(String text){
        loggedInLabel.setText(text);
    }


    @FXML
    private void initialize(){
        //Init the sceneController
        sceneController = new SceneController();

        //line chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Num items");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("time");

        LineChart lineChart = new LineChart(xAxis, yAxis);
        contentBox.getChildren().add(lineChart);

        initAlerts();
    }

    private void initAlerts(){

        this.denomAlert = new Alert(Alert.AlertType.ERROR);
        this.denomAlert.setHeaderText("Input non valido");
        this.denomAlert.setContentText("denom non valida");

        this.coordAlert = new Alert(Alert.AlertType.ERROR);
        this.coordAlert.setHeaderText("Input not valido");
        this.coordAlert.setContentText("coordinate devono essere positive!");

        this.statoAlert = new Alert(Alert.AlertType.ERROR);
        this.statoAlert.setHeaderText("Input non valido");
        this.statoAlert.setContentText("Stato inserito non valido");

        this.pcAlert = new Alert(Alert.AlertType.ERROR);
        this.pcAlert.setHeaderText("input pc non valido");
        this.pcAlert.setContentText("PC non valido!");

        this.areaInteresseAlert = new Alert(Alert.AlertType.ERROR);
        this.areaInteresseAlert.setHeaderText("Area interesse non valida");
        this.areaInteresseAlert.setContentText("Input non valido");

        this.invalidDateAlert = new Alert(Alert.AlertType.ERROR);
        this.invalidDateAlert.setHeaderText("Invalid date");
        this.invalidDateAlert.setContentText("data input non valida");

        this.cmAlert = new Alert(Alert.AlertType.ERROR);
        this.cmAlert.setHeaderText("Invalid cm");
        this.cmAlert.setContentText("centro in input non valido");
    }


    @FXML
    public void login(ActionEvent actionEvent) {
        try {
            sceneController.switchToLoginScene(actionEvent);
        }catch(IOException ioe){ioe.printStackTrace();}
    }


    private void addNodesToParamBox(LinkedList<Node> nodes){
        nodes.forEach((node) -> paramBox.getChildren().add(node));
    }

    public void ricercaAreaInteresse(ActionEvent actionEvent) {
        this.paramBox = new VBox(10);
        //denominazione, stato, latitudine, longitudine
        this.tDenominazione = new TextField("nome");
        this.tDenominazione.setOnMouseClicked((event) -> {this.tDenominazione.clear();});
        this.tStato = new TextField("stato ");
        this.tStato.setOnMouseClicked((event) -> {this.tStato.clear();});
        this.tLatitudine = new TextField("latidudine");
        this.tLatitudine.setOnMouseClicked((event) -> {this.tLatitudine.clear();});
        this.tLongitudine = new TextField("longitudine");
        this.tLongitudine.setOnMouseClicked((event) -> {this.tLongitudine.clear();});
        this.btnRicercaAreaPerDenom = new Button("Ricerca per nome");
        this.btnRicercaAreaCoord = new Button("Ricerca Coord");

        this.btnRicercaAreaPerDenom.setOnAction(event -> {
            ricercaAreaPerDenom();
        });

        this.btnRicercaAreaCoord.setOnAction(event -> {ricercaPerCoord();});

        LinkedList<Node> nodesToAdd = new LinkedList<Node>();

        nodesToAdd.add(tDenominazione);
        nodesToAdd.add(tStato);
        nodesToAdd.add(tLatitudine);
        nodesToAdd.add(tLongitudine);
        nodesToAdd.add(btnRicercaAreaPerDenom);
        nodesToAdd.add(btnRicercaAreaCoord);

        addNodesToParamBox(nodesToAdd);

        this.borderPane.setRight(paramBox);
    }

    private void ricercaAreaPerDenom(){
        String denom = this.tDenominazione.getText();
        String stato = this.tStato.getText();
        String query;

        if(denom.isEmpty() || denom.equals("nome")){
            this.denomAlert.showAndWait();
        }

        if(!stato.isEmpty() && !(stato.equals("stato"))){
            query = denom + stato;
        }else{
            //query the db without using stato
            query = denom;
        }
        System.out.println(query);
    }

    private void ricercaPerCoord(){
        String stato = this.tStato.getText();
        String longi = this.tLongitudine.getText();
        String lati = this.tLatitudine.getText();
        String query;
        if((longi.isEmpty() || lati.isEmpty()) ||
                (longi.equals("longitudine") || lati.equals("latitudine"))){
            this.coordAlert.showAndWait();
        }else{
            float lo = Float.parseFloat(longi);
            float la = Float.parseFloat(lati);
            if(stato.isEmpty()){//Query the db without using stato
                query = lo + ":" +la;
                System.out.println(query);
            }
            else{
                query = lo + ":" +la + "=" +stato;
                System.out.println(query);
            }
        }

    }

    public void visualizzaParametriClimatici(ActionEvent actionEvent) {
        this.paramBox = new VBox(10);
        this.tAreaInteresse = new TextField("AreaInteresse");
        this.tAreaInteresse.setOnMouseClicked((event) -> this.tAreaInteresse.clear());
        this.tCentroMonitoraggio = new TextField("CentroMonitoraggio");
        this.tCentroMonitoraggio.setOnMouseClicked((event) -> this.tCentroMonitoraggio.clear());
        this.startDatePicker = new DatePicker();
        this.endDatePicker = new DatePicker();
        this.btnRicercaPC = new Button("Ricerca");
        this.btnRicercaPC.setOnAction((event) -> {visualizzaPC();});

        LinkedList<Node> notesToAdd = new LinkedList<Node>();
        notesToAdd.add(tAreaInteresse);
        notesToAdd.add(tCentroMonitoraggio);
        notesToAdd.add(startDatePicker);
        notesToAdd.add(endDatePicker);
        notesToAdd.add(btnRicercaPC);

        addNodesToParamBox(notesToAdd);
        this.borderPane.setRight(paramBox);

    }

    private void visualizzaPC(){
        String areaInteresseCercata = tAreaInteresse.getText();
        String centroMonitoraggioCercato = tCentroMonitoraggio.getText();
        LocalDate startDateTmp = LocalDate.of(1900, 1, 1);
        LocalDate endDateTmp = LocalDate.of(2100, 1, 1);
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if(areaInteresseCercata.isEmpty()){
            this.areaInteresseAlert.showAndWait();
        }
        if(startDate.isBefore(startDateTmp) || endDate.isAfter(endDateTmp))
            this.invalidDateAlert.showAndWait();

        if(!centroMonitoraggioCercato.isEmpty()){
            if(!startDate.isEqual(endDate)) {
                String params = areaInteresseCercata + centroMonitoraggioCercato + startDate + endDate;
                System.out.println(params);
            }else{
                String params = areaInteresseCercata + centroMonitoraggioCercato + startDate;
                System.out.println(params);
            }
        }else{//Formulo query senza centromonitoraggio
            if(!startDate.isEqual(endDate)){
                String params = areaInteresseCercata + startDate + endDate;
                System.out.println(params);
            }else{
                String params = areaInteresseCercata + startDate;
                System.out.println(params);
            }
        }
    }

    public void inserisciParametriClimatici(ActionEvent actionEvent) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("parametro_climatico-scene.fxml")); //watch out for this line of code
            //Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Stage pcStage = new Stage();
            Scene scene = new Scene(root);
            pcStage.setScene(scene);
            pcStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public void inserisciCentroMonitoraggio(ActionEvent actionEvent) {
        this.paramBox = new VBox(10);
        nomeCentroField = new TextField("Nome centro");
        nomeCentroField.setOnMouseClicked((event) -> nomeCentroField.clear());
        comuneField = new TextField("Comune centro");
        comuneField.setOnMouseClicked((event) -> comuneField.clear());
        statoCMField = new TextField("Stato centro");
        statoCMField.setOnMouseClicked((event) -> statoCMField.clear());
        areaInteresseCMField = new TextField("Area interesse");
        areaInteresseCMField.setOnMouseClicked((event) -> areaInteresseCMField.clear());
        inserisciCM = new Button("Inserisci CM");
        inserisciCM.setOnAction((event) -> inserisciCM());

        LinkedList<Node> nodesToAdd = new LinkedList<Node>();
        nodesToAdd.add(nomeCentroField);
        nodesToAdd.add(comuneField);
        nodesToAdd.add(statoCMField);
        nodesToAdd.add(areaInteresseCMField);
        nodesToAdd.add(inserisciCM);

        addNodesToParamBox(nodesToAdd);

        this.borderPane.setRight(paramBox);

    }

    private void inserisciCM(){
        //TODO
        String nomeCentro = nomeCentroField.getText();
        String comuneCentro = comuneField.getText();
        String statoCentro = statoCMField.getText();
        //Area interesse è campo particolare, si possono inserire una quantita
        //indefinita di aree di interesse
        String areaInteresseCentro = areaInteresseCMField.getText();

        if(nomeCentro.isEmpty() || comuneCentro.isEmpty() || statoCentro.isEmpty()){cmAlert.showAndWait();}

        String query = nomeCentro + comuneCentro + statoCentro;
        System.out.println(query);

    }

    public void registraOperatore(ActionEvent actionEvent) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("registrazione-scene.fxml")); //watch out for this line of code
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

}