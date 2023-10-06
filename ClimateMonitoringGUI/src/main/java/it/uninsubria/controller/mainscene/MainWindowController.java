package it.uninsubria.controller.mainscene;
import it.uninsubria.MainWindow;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.controller.dialog.AIDialog;
import it.uninsubria.controller.loginview.LoginViewController;
import it.uninsubria.controller.operatore.OperatoreViewController;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.controller.registrazione.RegistrazioneController;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.graphbuilder.GraphBuilder;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class MainWindowController{
    public Button buttonRicercaAreaInteresse;
    public Button buttonVisualizzaParametri;
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

    //alerts
    private Alert coordAlert;
    private Alert denomAlert;
    private Alert statoAlert;
    private Alert pcAlert;
    private Alert areaInteresseAlert;
    private Alert invalidDateAlert;
    private Alert cmAlert;

    private QueryHandler queryHandler;

    private final String url = "jdbc:postgresql://localhost/postgres";
    private Properties props;

    //set login status to false
    //private boolean loggedIN = false;
    private BooleanProperty loggedIN;

    private Stage mainWindowStage;
    private SceneController sceneController;

    public MainWindowController(){
        //set up the controllers
        sceneController = new SceneController(this);
        sceneController.setLoginViewController(new LoginViewController(sceneController));
        sceneController.setOperatoreViewController(new OperatoreViewController(sceneController));
        sceneController.setParametroClimaticoController(new ParametroClimaticoController(sceneController));
        sceneController.setRegistrazioneController(new RegistrazioneController(sceneController));

        loggedIN = new SimpleBooleanProperty(false);


        //init the query handler
        props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");
        queryHandler = new QueryHandler(url, props);


        /**
        loggedIN.addListener((listener, oldValue, newValue) -> sceneController
                .getLoginViewController().setLoggedIn(newValue));
         **/

        initAlerts();


    }

    @FXML
    public void initialize(){
        //table view
        showAreeInserite();
        //line chart
        contentBox.getChildren().add(GraphBuilder.getBasicLineChart(GraphBuilder.Resource.wind));

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
            mainWindowStage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/login-scene.fxml"));
            fxmlLoader.setController(sceneController.getLoginViewController());
            Stage loginStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            loginStage.setScene(scene);
            loginStage.show();
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
        /**
        TableColumn keyColumn = new TableColumn("areaID");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("areaid"));
         **/
        TableColumn denomColumn = new TableColumn("denominazione");
        denomColumn.setCellValueFactory(new PropertyValueFactory<>("denominazione"));
        TableColumn countryColumn = new TableColumn("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
        TableColumn latColumn = new TableColumn("latitudine");
        latColumn.setCellValueFactory(new PropertyValueFactory<>("latitudine"));
        TableColumn longColumn = new TableColumn("longitudine");
        longColumn.setCellValueFactory(new PropertyValueFactory<>("longitudine"));

        tableView.getColumns().addAll(denomColumn, countryColumn, latColumn, longColumn);

        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    AreaInteresse a = (AreaInteresse) row.getItem();
                    System.out.println("Item double Clicked: "+a);
                    //get cp associated with this area interesse
                    LinkedList<ParametroClimatico> params = queryHandler.selectAllWithCond(QueryHandler.tables.PARAM_CLIMATICO, "areaid", a.getAreaid());
                    try{
                        Stage aiDialogStage = new Stage();
                        AIDialog aiDialogController = new AIDialog(sceneController, a, params);
                        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/ai-dialog.fxml"));
                        fxmlLoader.setController(aiDialogController);
                        Scene dialogScene = new Scene(fxmlLoader.load(), 400, 400);
                        aiDialogStage.setScene(dialogScene);
                        aiDialogStage.show();

                    }catch(IOException ioe){ioe.printStackTrace();}
                }
            });
            return row;
        });
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
        String longi = this.tLongitudine.getText();
        String lati = this.tLatitudine.getText();
        String query;
        if((longi.isEmpty() || lati.isEmpty()) ||
                (longi.equals("longitudine") || lati.equals("latitudine"))){
            this.coordAlert.showAndWait();
        }else{
            float lo = Float.parseFloat(longi);
            float la = Float.parseFloat(lati);
            //Sperical law of cosines?

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
        /**
        TableColumn keyColumn = new TableColumn("parameterId");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("parameterId"));;
         **/
        TableColumn centroColumn = new TableColumn("idCentro");
        centroColumn.setCellValueFactory(new PropertyValueFactory<>("idcentro"));
        TableColumn areaColumn = new TableColumn("areaInteresse");
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("areaid"));
        TableColumn dateColumn = new TableColumn("pubDate");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("pubdate"));
        TableColumn ventoColumn = new TableColumn("ventoValue");
        ventoColumn.setCellValueFactory(new PropertyValueFactory<>(""));
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
        tableView.getColumns().addAll(centroColumn, areaColumn, dateColumn, ventoColumn, umiditaColumn, pressioneColumn,
                temperaturaColumn, precipitazioniColumn, altitudineColumn, massaColumn);

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
            return;
        }//TODO: add check dates != null
        if(startDate == null || endDate == null){
            this.invalidDateAlert.showAndWait();
            return;
        }
        if(startDate.isBefore(startDateTmp) || endDate.isAfter(endDateTmp) || startDate.isEqual(endDate)) {
            this.invalidDateAlert.showAndWait();
            return;
        }

        LinkedList<CentroMonitoraggio> cms = new LinkedList<>();
        LinkedList<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
        areeInteresse = queryHandler.selectAllWithCond(QueryHandler.tables.AREA_INTERESSE, "denominazione", areaInteresseCercata);
        String areaID = areeInteresse.pop().getAreaid();
        LinkedList<ParametroClimatico> parametriClimatici = queryHandler.selectAllWithCond(QueryHandler.tables.PARAM_CLIMATICO, "areaid", areaID);
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

    public boolean onExecuteLoginQuery(String userID, String password){
        Operatore o = queryHandler.executeLogin(userID, password);
        if(o != null){
            mainWindowStage.close();
            return true;
        }
        return false;
    }


    public boolean onExecuteRegistraOpQuery(String nomeOp, String cognomeOp, String codFisc, String userID, String email, String password, String centroAfferenza){
        if(!queryHandler.requestSignUp(codFisc, email)){
            System.out.println("Operatore inesistente");
            return false;
        }else{//
            String centroID = queryHandler.selectObjectWithCond("centroid", QueryHandler.tables.CENTRO_MONITORAGGIO, "comune", centroAfferenza).get(0);
            return queryHandler.executeSignUp(nomeOp, cognomeOp, codFisc, userID, email, password, centroID);
        }
    }


}