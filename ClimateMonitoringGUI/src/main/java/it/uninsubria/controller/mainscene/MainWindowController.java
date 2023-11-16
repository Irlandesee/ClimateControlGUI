package it.uninsubria.controller.mainscene;
import it.uninsubria.MainWindow;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.controller.dialog.AiDialog;
import it.uninsubria.controller.dialog.CmDialog;
import it.uninsubria.controller.dialog.GraphDialog;
import it.uninsubria.controller.dialog.PcDialog;
import it.uninsubria.controller.loginview.LoginViewController;
import it.uninsubria.controller.operatore.OperatoreViewController;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.controller.registrazione.RegistrazioneController;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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
import javafx.util.Pair;

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
    private Button btnRicercaPcArea;
    private Button btnRicercaPcCm;
    private Button btnRicercaArea;
    private ToggleButton tglDatePicker;
    private ToggleButton tglRicercaAreaCm;

    //alerts
    private Alert coordAlert;
    private Alert denomAlert;
    private Alert statoAlert;
    private Alert pcAlert;
    private Alert areaInteresseAlert;
    private Alert centroMonitoraggioAlert;
    private Alert invalidDateAlert;
    private Alert cmAlert;

    private QueryHandler queryHandler;

    //private final String url = "jdbc:postgresql://localhost/postgres";
    private final String url = "jdbc:postgresql://192.168.1.26/postgres";
    private Properties props;

    private Stage mainWindowStage;
    private SceneController sceneController;

    public MainWindowController(Stage stage){
        //set up the controllers
        sceneController = new SceneController(this);
        sceneController.setLoginViewController(new LoginViewController(sceneController));
        sceneController.setOperatoreViewController(new OperatoreViewController(sceneController));
        sceneController.setParametroClimaticoController(new ParametroClimaticoController(sceneController));
        sceneController.setRegistrazioneController(new RegistrazioneController(sceneController));

        this.mainWindowStage = stage;
        mainWindowStage.setMinHeight(800);
        mainWindowStage.setMinWidth(1200);

        //init the query handler
        props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");
        queryHandler = new QueryHandler(url, props);

        initAlerts();
    }

    @FXML
    public void initialize(){
        //table view
        showAreeInserite();
    }

    static TableView<String[]> createTable(String[] columnNames){
        TableView<String[]> table = new TableView<String[]>();
        for(int i = 0; i < columnNames.length; i++){
            final int index = i;
            TableColumn<String[], String> column = new TableColumn<String[], String>(columnNames[i]);
            column.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[index]));
            table.getColumns().add(column);
        }
        return table;
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

        this.centroMonitoraggioAlert = new Alert(Alert.AlertType.ERROR);
        this.centroMonitoraggioAlert.setHeaderText("Centro Monitoraggio non valido");
        this.centroMonitoraggioAlert.setContentText("Input non valido");

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
        tableView.getColumns().clear();
        tableView.getItems().clear();
        prepTableAreaInteresse();
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

    private void prepTableParamClimatici(){
        System.out.println("preparo tabella per parametri climatici");
        tableView.getColumns().clear();
        tableView.getItems().clear();


        TableColumn dateColumn = new TableColumn("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, LocalDate>("pubDate"));
        TableColumn ventoColumn = new TableColumn("Vento");
        ventoColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("ventoValue"));
        TableColumn umiditaColumn = new TableColumn("Umidita");
        umiditaColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("umiditaValue"));
        TableColumn pressioneColumn = new TableColumn("Pressione");
        pressioneColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("pressioneValue"));
        TableColumn temperaturaColumn = new TableColumn("Temperatura");
        temperaturaColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("temperaturaValue"));
        TableColumn precipitazioniColumn = new TableColumn("Precipitazioni");
        precipitazioniColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("precipitazioniValue"));
        TableColumn altitudineColumn = new TableColumn("Altitudine ghiacciai");
        altitudineColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("altitudineValue"));
        TableColumn massaColumn = new TableColumn("Massa ghiacciai");
        massaColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("massaValue"));

        tableView.getColumns().addAll(dateColumn, ventoColumn, umiditaColumn,
                pressioneColumn, temperaturaColumn, precipitazioniColumn, altitudineColumn, massaColumn);

        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(
                    event -> {
                        if(event.getClickCount() == 2 && (!row.isEmpty())){
                            ParametroClimatico pc = (ParametroClimatico) row.getItem();
                            System.out.println("item clicked" + pc.getPubDate());
                            String nomeArea = queryHandler.selectObjectJoinWithCond("denominazione",
                                    QueryHandler.tables.PARAM_CLIMATICO, QueryHandler.tables.AREA_INTERESSE,
                                    "areaid", pc.getAreaInteresseId()).get(0);
                            String nomeCentro = queryHandler.selectObjectJoinWithCond(
                                    "nomecentro",
                                    QueryHandler.tables.PARAM_CLIMATICO, QueryHandler.tables.CENTRO_MONITORAGGIO,
                                    "centroid", pc.getIdCentro()).get(0);
                            try{
                               Stage pcDialogStage = new Stage();
                               PcDialog pcDialogController = new PcDialog(sceneController, pc, nomeCentro, nomeArea);
                               FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/pc-dialog.fxml"));
                               fxmlLoader.setController(pcDialogController);
                               Scene dialogScene = new Scene(fxmlLoader.load(), 400, 400);
                               pcDialogStage.setScene(dialogScene);
                               pcDialogStage.show();
                            }catch(IOException ioe){ioe.printStackTrace();}
                        }
                    }
            );
            return row;
        });
        tableView.refresh(); //forces the tableview to refresh the listeners
    }

    private void prepTableAreaInteresse(){
        tableView.getItems().clear();
        tableView.getColumns().clear();
        System.out.println("table view column size in prepAreaInteresse: "+ tableView.getColumns().size());

        TableColumn denomColumn = new TableColumn("denominazione");
        denomColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("denominazione"));
        denomColumn.setMinWidth(120);
        TableColumn countryColumn = new TableColumn("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("stato"));
        countryColumn.setMinWidth(100);
        TableColumn latColumn = new TableColumn("latitudine");
        latColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("latitudine"));
        latColumn.setMinWidth(100);
        TableColumn longColumn = new TableColumn("longitudine");
        longColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("longitudine"));
        longColumn.setMinWidth(100);

        tableView.getColumns().addAll(denomColumn, countryColumn, latColumn, longColumn);

        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    AreaInteresse a = (AreaInteresse) row.getItem();

                    System.out.println("Item double Clicked: "+ a);
                    //get cp associated with this area interesse
                    List<ParametroClimatico> params = queryHandler.selectAllWithCond(QueryHandler.tables.PARAM_CLIMATICO, "areaid", a.getAreaid());
                    try{
                        Stage aiDialogStage = new Stage();
                        AiDialog aiDialogController = new AiDialog(aiDialogStage, queryHandler, a, params);

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
        tableView.refresh(); //forces the tableview to refresh the listeners
    }

    private void showAreeInserite(){
        List<AreaInteresse> res = queryHandler.selectAll(QueryHandler.tables.AREA_INTERESSE);
        prepTableAreaInteresse();
        res.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
    }

    private void ricercaAreaPerDenom(){
        tableView.getItems().clear();
        String denom = this.tDenominazione.getText();
        if(!denom.isEmpty() && !(denom.equals("nome"))){
            List<AreaInteresse> res = queryHandler.selectAllWithCond(QueryHandler.tables.AREA_INTERESSE, "denominazione", denom);
            //res.forEach(System.out::println);
            res.forEach((areaInteresse -> {
                tableView.getItems().add(areaInteresse);
            }));
        }
        else{
            denomAlert.showAndWait();
        }
    }

    private void ricercaAreaPerStato(){
        tableView.getItems().clear();
        String stato = this.tStato.getText();
        if(!stato.isEmpty() && !(stato.equals("stato"))){
            List<AreaInteresse> res = queryHandler.selectAllWithCond(QueryHandler.tables.AREA_INTERESSE, "stato", stato);
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
        tableView.getColumns().clear();
        tableView.getItems().clear();
        prepTableParamClimatici();

        this.paramBox = new VBox(10);
        this.tAreaInteresse = new TextField("AreaInteresse");
        this.tAreaInteresse.setOnMouseClicked((event) -> this.tAreaInteresse.clear());
        this.tCentroMonitoraggio = new TextField("CentroMonitoraggio");
        this.tCentroMonitoraggio.setOnMouseClicked((event) -> this.tCentroMonitoraggio.clear());
        this.tglDatePicker = new ToggleButton("Ricerca con data");
        this.startDatePicker = new DatePicker();
        this.endDatePicker = new DatePicker();
        this.tglRicercaAreaCm = new ToggleButton("Ricerca entrambi");
        this.btnRicercaPcArea = new Button("Ricerca per area");
        this.btnRicercaPcArea.setOnAction(this::visualizzaPC);
        this.btnRicercaPcCm = new Button("Ricerca Per Cm");
        this.btnRicercaPcCm.setOnAction(this::visualizzaPC);

        LinkedList<Node> nodesToAdd = new LinkedList<Node>();
        nodesToAdd.add(tAreaInteresse);
        nodesToAdd.add(tCentroMonitoraggio);
        nodesToAdd.add(tglDatePicker);
        nodesToAdd.add(startDatePicker);
        nodesToAdd.add(endDatePicker);
        nodesToAdd.add(tglRicercaAreaCm);
        nodesToAdd.add(btnRicercaPcArea);
        nodesToAdd.add(btnRicercaPcCm);
        addNodesToParamBox(nodesToAdd);
        this.borderPane.setRight(paramBox);

    }

    @FXML
    public void visualizzaGrafici(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        prepTableAreaInteresse();
        showAreeInserite();
        this.paramBox = new VBox(2);
        this.tAreaInteresse = new TextField("Nome Area");
        this.tAreaInteresse.setOnMouseClicked(e -> this.tAreaInteresse.clear());
        this.btnRicercaArea = new Button("Ricerca area");
        this.btnRicercaArea.setOnAction(e -> createChart());

        this.paramBox.getChildren().add(tAreaInteresse);
        this.paramBox.getChildren().add(btnRicercaArea);
        this.borderPane.setRight(paramBox);

    }

    private void createChart(){
        String nomeArea = tAreaInteresse.getText();
        if(nomeArea.isEmpty() || nomeArea.equals("Nome Area")){
            new Alert(Alert.AlertType.ERROR, "nome area non valido").showAndWait();
            return;
        }
        System.out.println("Creating chart for" + nomeArea);
        String areaid = queryHandler
                .selectObjectWithCond("areaid", QueryHandler.tables.AREA_INTERESSE, "denominazione", nomeArea)
                .get(0);
        System.out.println("areaid ->" + areaid);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/graph-dialog.fxml"));
            fxmlLoader.setController(new GraphDialog(queryHandler, areaid));
            Stage chartStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
            chartStage.setScene(scene);
            chartStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}

    }


    private void visualizzaPC(ActionEvent event){
        String denomAiCercata = tAreaInteresse.getText();
        String denomCmCercato = tCentroMonitoraggio.getText();
        LocalDate canonicalStartDate = LocalDate.of(1900, 1, 1);
        LocalDate canonicalEndDate = LocalDate.of(2100, 1, 1);
        boolean ricercaPerData = false;
        LocalDate startDate = canonicalStartDate;
        LocalDate endDate = canonicalEndDate;

        if(tglDatePicker.isSelected()){
            if(startDatePicker.getValue() == null && endDatePicker.getValue() == null){
                invalidDateAlert.show();
            }
            startDate = startDatePicker.getValue();
            endDate = endDatePicker.getValue();
            if(startDate.isBefore(canonicalStartDate)
                    || endDate.isAfter(canonicalEndDate)
                    || startDate.isEqual(endDate)){
                invalidDateAlert.show();
            }
            ricercaPerData = true;
        }

        //ricerca area
        if(event.getSource().equals(btnRicercaPcArea)){
            if(denomAiCercata.isEmpty() || denomAiCercata.equals("AreaInteresse")){
                this.areaInteresseAlert.showAndWait();
            }else{
                String areaInteresseId = queryHandler
                        .selectObjectWithCond("areaid", QueryHandler.tables.AREA_INTERESSE, "denominazione", denomAiCercata)
                        .get(0);
                List<ParametroClimatico> parametriClimatici = queryHandler
                        .selectAllWithCond(QueryHandler.tables.PARAM_CLIMATICO, "areaid", areaInteresseId);
                tableView
                        .getItems()
                        .clear();
                if(ricercaPerData){
                    LocalDate finalStartDate = startDate;
                    LocalDate finalEndDate = endDate;
                    parametriClimatici.forEach((param) -> {
                        parametriClimatici.removeIf((pc) -> {
                            return isBetweenDates(finalStartDate, finalEndDate, pc.getPubDate());
                        });
                    });
                }
                parametriClimatici.forEach((pc) -> tableView.getItems().add(pc));
            }
        }
        //ricerca cm
        else if(event.getSource().equals(btnRicercaPcCm)){
            if(denomCmCercato.isEmpty() || denomCmCercato.equals("CentroMonitoraggio")){
                this.centroMonitoraggioAlert.showAndWait();
            }else{
                String centroId = queryHandler
                        .selectObjectJoinWithCond("centroid",
                                QueryHandler.tables.PARAM_CLIMATICO,
                                QueryHandler.tables.CENTRO_MONITORAGGIO,
                                "nomecentro",
                                denomCmCercato)
                        .get(0);
                List<ParametroClimatico> parametriClimatici = queryHandler
                        .selectAllWithCond(
                                QueryHandler.tables.PARAM_CLIMATICO,
                                "centroid",
                                centroId);
                tableView
                        .getItems()
                        .clear();
                if(ricercaPerData){
                    LocalDate finalStartDate = startDate;
                    LocalDate finalEndDate = endDate;
                    parametriClimatici.forEach((param) -> {
                        parametriClimatici.removeIf((pc) -> {
                            return isBetweenDates(finalStartDate, finalEndDate, pc.getPubDate());
                        });
                    });
                }
                parametriClimatici.forEach((pc) -> tableView.getItems().add(pc));
            }
        }
    }

    @FXML
    public void visualizzaCentri(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        //tableView.setRowFactory(null);

        List<CentroMonitoraggio> centriMonitoraggio = queryHandler.selectAll(QueryHandler.tables.CENTRO_MONITORAGGIO);

        //TODO: Show the areaInteresse for each cm?
        TableColumn denomCentro = new TableColumn("Denominazione");
        denomCentro.setCellValueFactory(new PropertyValueFactory<CentroMonitoraggio, String>("denominazione"));
        tableView.getColumns().add(denomCentro);

        centriMonitoraggio.forEach(cm -> {
            tableView.getItems().add(cm);
        });

        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !(row.isEmpty())){
                    CentroMonitoraggio c = (CentroMonitoraggio) row.getItem();
                    System.out.println("Item double clicked: " + c);
                    List<String> areeId = c.getAreeInteresseIdAssociate();
                    List<String> areeInteresseAssociateAlCentro = new LinkedList<String>();
                    for(String areaId : areeId){
                        AreaInteresse ai = (AreaInteresse) queryHandler.selectAllWithCond(QueryHandler.tables.AREA_INTERESSE, "areaid", areaId).get(0);
                        areeInteresseAssociateAlCentro.add(ai.getDenominazione());
                    }
                    try{
                        Stage cmDialogStage = new Stage();
                        CmDialog cmDialogController = new CmDialog(sceneController, areeInteresseAssociateAlCentro);
                        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/cm-dialog.fxml"));
                        fxmlLoader.setController(cmDialogController);
                        Scene dialogScene = new Scene(fxmlLoader.load());
                        cmDialogStage.setScene(dialogScene);
                        cmDialogStage.show();
                    }catch(IOException ioe){ioe.printStackTrace();}
                }
            });

            return row;
        });

        tableView.refresh();
    }

    public boolean isBetweenDates(LocalDate startDate, LocalDate endDate, LocalDate inputDate){
        return inputDate.isAfter(startDate) && inputDate.isBefore(endDate);
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
            String centroID = queryHandler.selectObjectWithCond("centroid", QueryHandler.tables.CENTRO_MONITORAGGIO, "comune", centroAfferenza)
                    .get(0);
            return queryHandler.executeSignUp(nomeOp, cognomeOp, codFisc, userID, email, password, centroID);
        }
    }


}