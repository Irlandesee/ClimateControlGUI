package it.uninsubria.controller.mainscene;
import it.uninsubria.MainWindow;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.graphbuilder.GraphBuilder;
import it.uninsubria.parametroClimatico.ClimateParameter;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.geom.Area;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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

    //per area interesse
    private TextField tDenominazione;
    private TextField tStato;
    private TextField tLatitudine;
    private TextField tLongitudine;
    private Button btnRicercaAreaPerDenom;
    private Button btnRicercaAreaPerStato;
    private Button btnRicercaAreaCoord;

    //per visualizzazione parametri climatici
    private TextField tAreaInteresse;
    private TextField tCentroMonitoraggio;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button btnRicercaPC;


    //per centro monitoraggio
    private TextField nomeCentroField;
    private TextField comuneField;
    private TextField statoCMField;
    private TextField areaInteresseCMField;
    private Button inserisciCM;
    private Button clearCM;

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

    private QueryHandler queryHandler;

    private final String url = "jdbc:postgresql://localhost/postgres";
    private Properties props;


    @FXML
    private void initialize(){
        //Init the sceneController
        sceneController = new SceneController();


        //init the query handler
        props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");
        queryHandler = new QueryHandler(url, props);

        //show aree interesse presenti
        showAreeInserite();

        //line chart
        contentBox.getChildren().add(GraphBuilder.getBasicLineChart(GraphBuilder.Resource.wind));

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
        try{
            Parent root = FXMLLoader.load(MainWindow.class.getResource("fxml/login-scene.fxml")); //watch out for this line of code
            //Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Stage registrazioneStage = new Stage();
            Scene scene = new Scene(root);
            registrazioneStage.setScene(scene);
            registrazioneStage.show();
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
        this.btnRicercaAreaPerStato = new Button("Ricerca per stato");
        this.btnRicercaAreaCoord = new Button("Ricerca Coord");

        this.btnRicercaAreaPerDenom.setOnAction(event -> {
            ricercaAreaPerDenom();
        });

        btnRicercaAreaPerStato.setOnAction(event -> {ricercaAreaPerStato();});

        this.btnRicercaAreaCoord.setOnAction(event -> {ricercaPerCoord();});

        LinkedList<Node> nodesToAdd = new LinkedList<Node>();

        nodesToAdd.add(tDenominazione);
        nodesToAdd.add(tStato);
        nodesToAdd.add(tLatitudine);
        nodesToAdd.add(tLongitudine);
        nodesToAdd.add(btnRicercaAreaPerDenom);
        nodesToAdd.add(btnRicercaAreaPerStato);
        nodesToAdd.add(btnRicercaAreaCoord);

        addNodesToParamBox(nodesToAdd);

        this.borderPane.setRight(paramBox);
    }

    private void prepTableAreaInteresse(){
        TableColumn keyColumn = new TableColumn("areaID");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("areaid"));
        TableColumn denomColumn = new TableColumn("denominazione");
        denomColumn.setCellValueFactory(new PropertyValueFactory<>("denominazione"));
        TableColumn countryColumn = new TableColumn("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
        TableColumn latColumn = new TableColumn("latitudine");
        latColumn.setCellValueFactory(new PropertyValueFactory<>("latitudine"));
        TableColumn longColumn = new TableColumn("longitudine");
        longColumn.setCellValueFactory(new PropertyValueFactory<>("longitudine"));

        tableView.getColumns().addAll(keyColumn, denomColumn, countryColumn, latColumn, longColumn);
    }

    private void showAreeInserite(){
        prepTableAreaInteresse();
        List<AreaInteresse> res = queryHandler.selectAll(QueryHandler.tables.AREA_INTERESSE);
        res.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
    }

    private void ricercaAreaPerDenom(){
        String denom = this.tDenominazione.getText();
        prepTableAreaInteresse();
        if(!denom.isEmpty() && !(denom.equals("nome"))){
            LinkedList<AreaInteresse> res = queryHandler.selectAllWithCond(QueryHandler.tables.AREA_INTERESSE, "denominazione", denom);

            //res.forEach(System.out::println);
            tableView.getItems().clear();
            res.forEach((areaInteresse -> {
                tableView.getItems().add(areaInteresse);
            }));
        }
        else{
            denomAlert.showAndWait();
        }
    }

    private void ricercaAreaPerStato(){
        String stato = this.tStato.getText();
        prepTableAreaInteresse();
        if(!stato.isEmpty() && !(stato.equals("stato"))){
            LinkedList<AreaInteresse> res = queryHandler.selectAllWithCond(QueryHandler.tables.AREA_INTERESSE, "stato", stato);
            res.removeIf(areaInteresse -> !areaInteresse.getStato().equals(stato));
            res.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
        }else{
            statoAlert.showAndWait();
        }

    }

    //TODO
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

    private void prepTablePC(){
        TableColumn keyColumn = new TableColumn("parameterId");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("parameterId"));;
        TableColumn centroColumn = new TableColumn("idCentro");
        centroColumn.setCellValueFactory(new PropertyValueFactory<>("idCentro"));
        TableColumn areaColumn = new TableColumn("areaInteresse");
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("areaInteresse"));
        TableColumn dateColumn = new TableColumn("pubDate");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("pubDate"));
        TableColumn ventoColumn = new TableColumn("ventoValue");
        ventoColumn.setCellValueFactory(new PropertyValueFactory<>("ventoValue"));
        TableColumn umiditaColumn = new TableColumn("umiditaValue");
        umiditaColumn.setCellValueFactory(new PropertyValueFactory<>("umiditaValue"));
        TableColumn pressioneColumn = new TableColumn("pressioneValue");
        pressioneColumn.setCellValueFactory(new PropertyValueFactory<>("pressioneValue"));
        TableColumn temperaturaColumn = new TableColumn("temperaturaValue");
        temperaturaColumn.setCellValueFactory(new PropertyValueFactory<>("temperaturaValue"));
        TableColumn precipitazioniColumn = new TableColumn("precipitazioniValue");
        precipitazioniColumn.setCellValueFactory(new PropertyValueFactory<>("precipitazioniValue"));
        TableColumn altitudineColumn = new TableColumn("altitudineValue");
        altitudineColumn.setCellValueFactory(new PropertyValueFactory<>("altitudineValue"));
        TableColumn massaColumn = new TableColumn("massaValue");
        massaColumn.setCellValueFactory(new PropertyValueFactory<>("massaValue"));

    }

    private void visualizzaPC(){
        String areaInteresseCercata = tAreaInteresse.getText();
        String centroMonitoraggioCercato = tCentroMonitoraggio.getText();
        LocalDate startDateTmp = LocalDate.of(1900, 1, 1);
        LocalDate endDateTmp = LocalDate.of(2100, 1, 1);
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        prepTablePC();

        if(areaInteresseCercata.isEmpty()){
            this.areaInteresseAlert.showAndWait();
        }//TODO: add check dates != null
        if(startDate.isBefore(startDateTmp) || endDate.isAfter(endDateTmp) || startDate.isEqual(endDate))
            this.invalidDateAlert.showAndWait();

        LinkedList<CentroMonitoraggio> cms = new LinkedList<>();
        LinkedList<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
        areeInteresse = queryHandler.selectAllWithCond(QueryHandler.tables.AREA_INTERESSE, "denominazione", areaInteresseCercata);
        String areaID = areeInteresse.pop().getAreaid();
        LinkedList<ClimateParameter> parametriClimatici = queryHandler.selectAllWithCond(QueryHandler.tables.PARAM_CLIMATICO, "areaid", areaID);
        if(!centroMonitoraggioCercato.isEmpty()){
            if(!centroMonitoraggioCercato.equals("CentroMonitoraggio")){
                cms = queryHandler.selectAllWithCond(QueryHandler.tables.CENTRO_MONITORAGGIO, "nomecentro", centroMonitoraggioCercato);
                String centroCercatoId = cms.pop().getCentroID();
                //filtro via i risultati non appertenti al cm cercato
                tableView.getItems().clear();
                parametriClimatici.forEach((pc) -> {
                    parametriClimatici.removeIf((id) -> !pc.getIdCentro().equals(centroCercatoId));
                    tableView.getItems().add(pc);
                });
            }else{//Formulo query senza centromonitoraggio
                tableView.getItems().clear();
                parametriClimatici.forEach((pc) -> {
                    tableView.getItems().add(pc);
                });
            }
        }
    }

    /**
     * select ai.denominazione, cm.nomecentro, pc.pubdate, pc.valore_vento, pc.valore_umidita, pc.valore_pressione, pc.valore_temperatura, pc.valore_alt_ghiacciai, pc.valore_massa_ghiacciai
     * from parametro_climatico pc join centro_monitoraggio cm on pc.idcentro = cm.centroid join area_interesse ai on pc.areaid = ai.areaid
     * where pc.pubdate > '2011-1-1' and pc.pubdate < '2021-1-1'
     */

    public void inserisciParametriClimatici(ActionEvent actionEvent) {
        try{
            Parent root = FXMLLoader.load(MainWindow.class.getResource("fxml/parametro_climatico-scene.fxml")); //watch out for this line of code
            //Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Stage pcStage = new Stage();
            Scene scene = new Scene(root);
            pcStage.setScene(scene);
            pcStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public void executeInsertPCQuery(String nomeArea, String centroMon, LocalDate pubdate, short[] paramValues, String[] notes){
        //TODO
    }

    public void executeRegistraOpQuery(String nomeOp, String cognomeOp, String codFisc, String email, String password, String centroAfferenza){
        //TODO
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
        clearCM = new Button("Pulisci");
        clearCM.setOnAction((event) -> clearCMFields());

        LinkedList<Node> nodesToAdd = new LinkedList<Node>();
        nodesToAdd.add(nomeCentroField);
        nodesToAdd.add(comuneField);
        nodesToAdd.add(statoCMField);
        nodesToAdd.add(areaInteresseCMField);
        nodesToAdd.add(inserisciCM);
        nodesToAdd.add(clearCM);

        addNodesToParamBox(nodesToAdd);

        this.borderPane.setRight(paramBox);

    }

    private void clearCMFields(){
        nomeCentroField.clear();
        comuneField.clear();
        statoCMField.clear();
        areaInteresseCMField.clear();
    }

    private void inserisciCM(){
        String nomeCentro = nomeCentroField.getText();
        String comuneCentro = comuneField.getText();
        String statoCentro = statoCMField.getText();
        //Area interesse è campo particolare, si possono inserire una quantita
        //indefinita di aree di interesse -> si cancella in automatico
        //solo areaInteresseCentro, per pulire tutto si usa clearCMFields()
        String areaInteresseCentro = areaInteresseCMField.getText();

        if(nomeCentro.isEmpty() || comuneCentro.isEmpty() || statoCentro.isEmpty()){cmAlert.showAndWait();}

        areaInteresseCMField.clear();
        //TODO
    }

    public void registraOperatore(ActionEvent actionEvent) {
        try{
            Parent root = FXMLLoader.load(MainWindow.class.getResource("fxml/registrazione-scene.fxml")); //watch out for this line of code
            //Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Stage registrazioneStage = new Stage();
            Scene scene = new Scene(root);
            registrazioneStage.setScene(scene);
            registrazioneStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

}