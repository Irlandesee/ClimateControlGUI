package it.uninsubria.controller.mainscene;
import it.uninsubria.MainWindow;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.dialog.AiDialog;
import it.uninsubria.controller.dialog.CmDialog;
import it.uninsubria.controller.dialog.GraphDialog;
import it.uninsubria.controller.dialog.PcDialog;
import it.uninsubria.controller.loginview.LoginViewController;
import it.uninsubria.controller.operatore.OperatoreViewController;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.controller.registrazione.RegistrazioneController;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import javafx.beans.property.SimpleStringProperty;
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
import org.apache.http.protocol.RequestUserAgent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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

    //private final String url = "jdbc:postgresql://localhost/postgres";
    private final String url = "jdbc:postgresql://192.168.1.26/postgres";
    private Properties props;

    private Stage mainWindowStage;
    private SceneController sceneController;
    private final Client client;

    public MainWindowController(Stage stage, Client client){
        this.client = client;
        //set up the controllers
        sceneController = new SceneController(this);
        sceneController.setLoginViewController(new LoginViewController(sceneController));
        sceneController.setOperatoreViewController(new OperatoreViewController(sceneController));
        sceneController.setParametroClimaticoController(new ParametroClimaticoController(sceneController));
        sceneController.setRegistrazioneController(new RegistrazioneController(sceneController));

        this.mainWindowStage = stage;
        mainWindowStage.setMinHeight(800);
        mainWindowStage.setMinWidth(1200);

        props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");

        client.start();

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
                            /**
                             * Params (From: ParametroClimatico)
                             * "denominazione"
                             * "AREA_INTERESSE"
                             * "areaid"
                             * areaid: String
                             */
                            Map<String, String> paramsReqDenom =
                                    RequestFactory.buildParams(ServerInterface.RequestType.selectObjJoinWithCond);
                            if(paramsReqDenom != null){
                                paramsReqDenom.replace(RequestFactory.objectKey, "denominazione");
                                paramsReqDenom.replace(RequestFactory.joinKey, ServerInterface.Tables.AREA_INTERESSE.label);
                                paramsReqDenom.replace(RequestFactory.condKey, "areaid");
                                paramsReqDenom.replace(RequestFactory.fieldKey, pc.getAreaInteresseId());
                            }

                            Request requestDenominazione = null;
                            try {
                                requestDenominazione = RequestFactory
                                        .buildRequest(
                                                client.getClientId(),
                                                ServerInterface.RequestType.selectObjJoinWithCond,
                                                ServerInterface.Tables.PARAM_CLIMATICO,
                                                paramsReqDenom);
                            } catch (MalformedRequestException e) {
                                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                                e.printStackTrace();
                                return;
                            }
                            /**
                             * Params(From: ParametroClimatico)
                             * "nomecentro"
                             * "CENTRO_MONITORAGGIO"
                             * "centroid"
                             * centroid: String
                             */
                            Map<String, String> paramsReqNomeCentro = RequestFactory.buildParams(ServerInterface.RequestType.selectObjJoinWithCond);
                            if(paramsReqNomeCentro != null){
                                paramsReqNomeCentro.replace(RequestFactory.objectKey, "nomecentro");
                                paramsReqNomeCentro.replace(RequestFactory.joinKey, ServerInterface.Tables.CENTRO_MONITORAGGIO.label);
                                paramsReqNomeCentro.replace(RequestFactory.condKey, "centroid");
                                paramsReqNomeCentro.replace(RequestFactory.fieldKey, pc.getIdCentro());
                            }
                            Request requestNomeCentro = null;
                            try{
                                requestNomeCentro = RequestFactory
                                        .buildRequest(
                                                client.getClientId(),
                                                ServerInterface.RequestType.selectObjJoinWithCond,
                                                ServerInterface.Tables.PARAM_CLIMATICO,
                                                paramsReqNomeCentro
                                        );

                            }catch(MalformedRequestException e){
                                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                                e.printStackTrace();
                                return;

                            }

                            client.addRequest(requestDenominazione);
                            client.addRequest(requestNomeCentro);

                            Response respDenom = null;
                            Response respNomeCentro = null;
                            if(requestDenominazione != null && requestNomeCentro != null){
                                respDenom = client.getResponse(requestDenominazione.getRequestId());
                                respNomeCentro = client.getResponse(requestNomeCentro.getRequestId());

                            }

                            if(respDenom == null || respNomeCentro == null){
                                new Alert(Alert.AlertType.ERROR, "Error in response object").showAndWait();
                                return;
                            }

                            String nomeArea = "";
                            String nomeCentro = "";

                            if(respDenom.getRespType().equals(ServerInterface.ResponseType.Object) && respDenom.getTable().equals(ServerInterface.Tables.AREA_INTERESSE)){
                                nomeArea = respDenom.getResult().toString();
                            }else{
                                nomeArea = "Error while retrieving denominazione area";
                            }
                            if(respNomeCentro.getRespType().equals(ServerInterface.ResponseType.Object) && respNomeCentro.getTable().equals(ServerInterface.Tables.CENTRO_MONITORAGGIO)){
                                nomeCentro = respNomeCentro.getResult().toString();
                            }else{
                                nomeCentro = "Error while retrieving denominazione centro";
                            }

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
                    Map<String, String> requestParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                    if(requestParams != null){
                        requestParams.replace(RequestFactory.condKey, "areaid");
                        requestParams.replace(RequestFactory.fieldKey, a.getAreaid());
                    }
                    Request req = null;
                    try{
                        req = RequestFactory.buildRequest(
                                client.getClientId(),
                                ServerInterface.RequestType.selectAllWithCond,
                                ServerInterface.Tables.AREA_INTERESSE,
                                requestParams);

                    }catch(MalformedRequestException mre){
                        new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                        mre.printStackTrace();
                        return;
                    }
                    client.addRequest(req);

                    //get response
                    Response res = null;
                    if(req != null){
                        res = client.getResponse(req.getRequestId());
                    }
                    if(res == null){
                        new Alert(Alert.AlertType.ERROR, "Error in response object").showAndWait();
                        return;
                    }
                    List<ParametroClimatico> params = new LinkedList<ParametroClimatico>();
                    if(res.getTable() == ServerInterface.Tables.PARAM_CLIMATICO
                            && res.getRespType() == ServerInterface.ResponseType.List){
                        params = (List<ParametroClimatico>)res.getResult();
                    }

                    try{
                        Stage aiDialogStage = new Stage();
                        AiDialog aiDialogController = new AiDialog(aiDialogStage, client, a, params);

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
        Request request = null;
        try{
            request = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.AREA_INTERESSE,
                    new HashMap<>());//select all does not need parameters
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        client.addRequest(request);
        //get response
        Response response = client.getResponse(request.getRequestId());
        if(response.getRespType() == ServerInterface.ResponseType.List
                && response.getTable() == ServerInterface.Tables.AREA_INTERESSE){
            List<AreaInteresse> res = (List<AreaInteresse>)response.getResult();
            prepTableAreaInteresse();
            res.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
        }
    }

    private void ricercaAreaPerDenom(){
        tableView.getItems().clear();
        String denom = this.tDenominazione.getText();
        if(!denom.isEmpty() && !(denom.equals("nome"))){
            Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
            params.replace(RequestFactory.condKey, "denominazione");
            params.replace(RequestFactory.fieldKey, denom);
            Request request = null;
            try{
                request = RequestFactory.buildRequest(
                        client.getClientId(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        params);
                System.out.println("Build request: " + request);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            client.addRequest(request);
            Response response = client.getResponse(request.getRequestId());
            List<AreaInteresse> l = (List<AreaInteresse>) response.getResult();
            //res.forEach(System.out::println);
            l.forEach((areaInteresse -> {
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
            Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
            params.replace(RequestFactory.condKey, "stato");
            params.replace(RequestFactory.fieldKey, stato);
            Request request = null;
            try{
                request = RequestFactory.buildRequest(
                        client.getClientId(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        params);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }

            client.addRequest(request);
            Response response = client.getResponse(request.getRequestId());

            if(response.getRespType() == ServerInterface.ResponseType.List &&
                response.getTable() == ServerInterface.Tables.AREA_INTERESSE){
                List<AreaInteresse> queryResult = (List<AreaInteresse>)response.getResult();
                queryResult.removeIf(areaInteresse -> !areaInteresse.getStato().equals(stato));
                queryResult.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
            }
        }else{
            statoAlert.showAndWait();
        }

    }

    //Calculate the parameter passed in radians
    private Float toRad(Float value){
        return (float) (value * Math.PI / 180);
    }

    private Float haversineDistance(Float latFirstPoint, Float longFirstPoint, Float latSecondPoint, Float longSecondPoint){
        final int earthRadius = 6731; // in kms
        float latDistance = toRad(latSecondPoint - latFirstPoint);
        float longDistance = toRad(longSecondPoint - longFirstPoint);

        float a = (float) (Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                        Math.cos(toRad(latFirstPoint)) * Math.cos(toRad(latSecondPoint)) *
                                Math.sin(toRad(longDistance / 2))  * Math.sin(longDistance / 2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));

        return earthRadius * c;
    }

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

            Request request = null;
            try{
                request = RequestFactory.buildRequest(
                        client.getClientId(),
                        ServerInterface.RequestType.selectAll,
                        ServerInterface.Tables.AREA_INTERESSE,
                        null);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            client.addRequest(request);
            List<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
            Response response = client.getResponse(request.getRequestId());
            if(response.getRespType() == ServerInterface.ResponseType.List
                    && response.getTable() == ServerInterface.Tables.AREA_INTERESSE){
                areeInteresse = (LinkedList<AreaInteresse>) response.getResult();

            }

            List<AreaInteresse> areeVicine = new LinkedList<AreaInteresse>();
            areeInteresse.forEach(area -> {
                float distance = haversineDistance(lo, la, area.getLongitudine(), area.getLatitudine());
                if(distance < 50) { //50 km
                    System.out.println(area);
                    areeVicine.add(area);
                }
            });
            tableView.getItems().clear();
            areeVicine.forEach(area -> tableView.getItems().add(area));

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

        Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond);
        if(params == null){return;}
        params.replace(RequestFactory.objectKey, "areaid");
        params.replace(RequestFactory.condKey, "denominazione");
        params.replace(RequestFactory.fieldKey, nomeArea);
        Request request = null;
        try{
            request = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    params
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        client.addRequest(request);
        if(request == null){return;}
        Response response = client.getResponse(request.getRequestId());
        String areaId = "";
        if(response.getRespType() == ServerInterface.ResponseType.Object
                && response.getTable() == ServerInterface.Tables.AREA_INTERESSE){
            areaId = response.getResult().toString();
        }

        System.out.println("areaid ->" + areaId);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/graph-dialog.fxml"));
            fxmlLoader.setController(new GraphDialog(client, areaId));
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
                Map<String, String> reqAreaIdParams = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond);
                reqAreaIdParams.replace(RequestFactory.objectKey, "areaid");
                reqAreaIdParams.replace(RequestFactory.condKey, "denominazione");
                reqAreaIdParams.replace(RequestFactory.fieldKey, denomAiCercata);
                Request requestAreaId = null;
                try{
                    requestAreaId = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectObjWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            reqAreaIdParams);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                client.addRequest(requestAreaId);
                Response resAreaId = client.getResponse(requestAreaId.getRequestId()); //should wait for the response
                String areaInteresseId = resAreaId.toString();

                Map<String, String> reqParamClimatici = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                reqParamClimatici.replace(RequestFactory.condKey, "areaid");
                reqParamClimatici.replace(RequestFactory.fieldKey, areaInteresseId);
                Request requestParamClimatici = null;
                try{
                    requestParamClimatici = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            reqParamClimatici
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                client.addRequest(requestParamClimatici);
                Response responseParametriClimatici = client.getResponse(requestParamClimatici.getRequestId());


                List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>)responseParametriClimatici.getResult();
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
                Map<String, String> requestCentroIdParams = RequestFactory.buildParams(ServerInterface.RequestType.selectObjJoinWithCond);
                requestCentroIdParams.replace(RequestFactory.objectKey, "centroid");
                requestCentroIdParams.replace(RequestFactory.joinKey, ServerInterface.Tables.CENTRO_MONITORAGGIO.label);
                requestCentroIdParams.replace(RequestFactory.condKey, "nomecentro");
                requestCentroIdParams.replace(RequestFactory.objectKey, denomCmCercato);
                Request requestCentroId;
                try{
                    requestCentroId = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectObjJoinWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            requestCentroIdParams);

                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;

                }
                client.addRequest(requestCentroId);
                Response responseCentroId = client.getResponse(requestCentroId.getRequestId());
                String centroId = responseCentroId.getResult().toString();

                Map<String, String> requestParametriClimaticiParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                requestParametriClimaticiParams.replace(RequestFactory.condKey, "centroid");
                requestParametriClimaticiParams.replace(RequestFactory.objectKey, centroId);

                Request requestParametriClimatici;
                try{
                    requestParametriClimatici = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            requestParametriClimaticiParams);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                client.addRequest(requestParametriClimatici);
                Response responseParametriClimatici = client.getResponse(requestParametriClimatici.getRequestId());
                List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>)responseParametriClimatici.getResult();

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
        Request requestCentro = null;
        try{
            requestCentro = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    null);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage());
            mre.printStackTrace();
            return;
        }
        client.addRequest(requestCentro);
        Response responseCentriMonitoraggio = client.getResponse(requestCentro.getClientId());

        List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>) responseCentriMonitoraggio.getResult();
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
                        Map<String, String> reqAiParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                        reqAiParams.replace(RequestFactory.condKey, "areaid");
                        reqAiParams.replace(RequestFactory.fieldKey, areaId);
                        Request requestAi;
                        try{
                            requestAi = RequestFactory.buildRequest(
                                    client.getClientId(),
                                    ServerInterface.RequestType.selectAllWithCond,
                                    ServerInterface.Tables.AREA_INTERESSE,
                                    reqAiParams
                            );
                        }catch(MalformedRequestException mre){
                            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                            mre.printStackTrace();
                            return;
                        }
                        client.addRequest(requestAi);
                        Response responseAi = client.getResponse(requestAi.getRequestId());
                        AreaInteresse ai = (AreaInteresse) responseAi.getResult();
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
        Map<String, String> loginParams = RequestFactory.buildParams(ServerInterface.RequestType.executeLogin);
        loginParams.replace(RequestFactory.userKey, userID);
        loginParams.replace(RequestFactory.passwordKey, password);

        Request loginRequest;
        try{
            loginRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.executeLogin,
                    ServerInterface.Tables.OPERATORE,
                    loginParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return false;
        }
        client.addRequest(loginRequest);
        Response response = client.getResponse(loginRequest.getRequestId());
        Operatore o = (Operatore) response.getResult();
        if(o != null){
            mainWindowStage.close();
            return true;
        }

        return false;
    }

    private boolean requestSignUp(String codFisc, String email){
        Request request;
        try{
            request = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.OP_AUTORIZZATO,
                    null);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return false;
        }
        client.addRequest(request);
        Response response = client.getResponse(request.getClientId());
        List<OperatoreAutorizzato> operatoriAutorizzati = (List<OperatoreAutorizzato>) response.getResult();
        for(OperatoreAutorizzato op : operatoriAutorizzati){
            if(op.getEmail().equals(email) && op.getCodFiscale().equals(codFisc))
                return true;
        }
        return false;
    }


    public boolean onExecuteRegistraOpQuery(String nomeOp, String cognomeOp, String codFisc, String userID, String email, String password, String centroAfferenza){
        if(!requestSignUp(codFisc, email)){
            System.out.println("Operatore inesistente");
            return false;
        }else{//
            Map<String, String> reqCmIdParams = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond);
            reqCmIdParams.replace(RequestFactory.objectKey, "centroid");
            reqCmIdParams.replace(RequestFactory.condKey, "comune");
            reqCmIdParams.replace(RequestFactory.fieldKey, centroAfferenza);
            Request requestCentroId;
            try{
                requestCentroId = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO, reqCmIdParams);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                mre.printStackTrace();
                return false;
            }
            client.addRequest(requestCentroId);
            Response responseCmId = client.getResponse(requestCentroId.getRequestId());
            String centroID = responseCmId.getResult().toString();

            Map<String, String> requestSignUpParams = RequestFactory.buildParams(ServerInterface.RequestType.executeSignUp);
            Request signUpRequest;
            try{
                signUpRequest = RequestFactory.buildRequest(
                        client.getClientId(),
                        ServerInterface.RequestType.executeSignUp,
                        ServerInterface.Tables.OPERATORE,
                        requestSignUpParams);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                mre.printStackTrace();
                return false;
            }

            client.addRequest(signUpRequest);
            Response responseSignUp = client.getResponse(signUpRequest.getRequestId());
            return (boolean) responseSignUp.getResult();
        }
    }


}