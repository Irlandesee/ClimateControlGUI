package it.uninsubria.controller.operatore;

import it.uninsubria.MainWindow;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.dialog.AiDialog;
import it.uninsubria.controller.dialog.CmDialog;
import it.uninsubria.controller.dialog.GraphDialog;
import it.uninsubria.controller.dialog.PcDialog;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import it.uninsubria.util.IDGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.protocol.RequestExpectContinue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class OperatoreViewController {

    public Button buttonRicercaAreaInteresse;
    public Button buttonVisualizzaParametri;
    public Button buttonInserisciParametri;
    public Button buttonInserisciCentroMonitoraggio;
    public Button buttonRegistraOp;

    public TableView tableView;
    public BorderPane borderPane;
    public VBox contentBox;
    public VBox paramBox;
    //Inserimento area interesse
    private TextField tFilterCountry;
    private Button visualizeCityData;
    private Button visualizeAiData;
    private Button visualizeCmData;

    //per area interesse
    private TextField tDenominazione;
    private TextField tStato;
    private TextField tLatitudine;
    private TextField tLongitudine;
    private Button btnRicercaAreaPerDenom;
    private Button btnRicercaAreaPerStato;
    private Button btnRicercaAreaCoord;

    //Visualizzazione parametri climatici
    private TextField tAreaInteresse;
    private TextField tCentroMonitoraggio;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button btnRicercaPC;
    private ToggleButton tglDatePicker;
    private ToggleButton tglRicercaAreaCm;
    private Button btnRicercaPcArea;
    private Button btnRicercaPcCm;

    //Per centro monitoraggio
    private TextField nomeCentroField;
    private TextField comuneField;
    private TextField statoCMField;
    private TextField areaInteresseCMField;
    private TextArea areeInteresseBox;
    private Button inserisciArea;
    private Button inserisciCM;
    private Button clearCM;

    //visualizzazione grafici
    private Button btnRicercaArea;
    //abilita utente
    private TextField tEmailField;
    private TextField tCodFiscField;
    private Button buttonAbilitaOp; //abilita alla registrazione

    //alerts
    private Alert coordAlert;
    private Alert denomAlert;
    private Alert statoAlert;
    private Alert pcAlert;
    private Alert areaInteresseAlert;
    private Alert invalidDateAlert;
    private Alert centroMonitoraggioAlert;

    private Properties props;
    private final String url = "jdbc:postgresql://192.168.1.26/postgres";
    //private final String url = "jdbc:postgresql://localhost/postgres";

    private Stage mainWindowStage;
    private Stage operatoreWindowStage;
    private final Client client;
    private final MainWindowController mainWindowController;

    private final ParametroClimaticoController parametroClimaticoController;
    //private final RegistrazioneController registrazioneController;
    private final Logger logger;
    private ServerInterface.Tables tableShown;

    public OperatoreViewController(Stage mainWindowStage, Stage operatoreWindowStage, MainWindowController mainWindowController, Client client){
        this.mainWindowController = mainWindowController;
        this.mainWindowStage = mainWindowStage;
        this.operatoreWindowStage = operatoreWindowStage;
        this.client = client;

        this.logger = Logger.getLogger("OperatoreWindow");

        this.operatoreWindowStage.setWidth(1200);
        this.parametroClimaticoController = new ParametroClimaticoController(this);
        //this.registrazioneController = new RegistrazioneController(this);

        props = new Properties();
        props.put("user", "postgres");
        props.put("password", "qwerty");
    }


    @FXML
    public void initialize(){
        initAlerts();
        //show aree interesse presenti
        showAreeInserite();
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

    }

    @FXML
    public void exit(ActionEvent actionEvent){
        if(operatoreWindowStage != null) operatoreWindowStage.close();
        //go back to the main window stage
        if(mainWindowStage != null){
            mainWindowStage.show();
        }
    }

    private void prepTableCity(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.CITY;


        TableColumn nomeColumn = new TableColumn("denominazione");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<City, String>("asciiName"));
        TableColumn countryColumn = new TableColumn("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<City, String>("country"));
        TableColumn latitudineColumn = new TableColumn("latitudine");
        latitudineColumn.setCellValueFactory(new PropertyValueFactory<City, String>("latitude"));
        TableColumn longitudineColumn = new TableColumn("longitudine");
        longitudineColumn.setCellValueFactory(new PropertyValueFactory<City, String>("longitude"));
        tableView.getColumns().addAll(nomeColumn, countryColumn, latitudineColumn, longitudineColumn);

        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    City c = (City) row.getItem();
                    tDenominazione.setText(c.getAsciiName());
                    tStato.setText(c.getCountry());
                    tLatitudine.setText(String.valueOf(c.getLatitude()));
                    tLongitudine.setText(String.valueOf(c.getLongitude()));
                }
            });

            return row;
        });
    }

    private void prepTableCentroMonitoraggio(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.CENTRO_MONITORAGGIO;

        TableColumn nomeColumn = new TableColumn("denominazione");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<City, String>("asciiName"));
        TableColumn countryColumn = new TableColumn("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<City, String>("country"));
        tableView.getColumns().addAll(nomeColumn, countryColumn);

        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    City c = (City) row.getItem();
                    nomeCentroField.setText(c.getAsciiName()+"Centro");
                    comuneField.setText(c.getAsciiName());
                    statoCMField.setText(c.getCountry());
                }
            });
            return row;
        });

    }

    private void prepTableOperatore(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        TableColumn<OperatoreAutorizzato, String> userColumn = new TableColumn<OperatoreAutorizzato, String>("email");
        userColumn.setCellValueFactory(new PropertyValueFactory<OperatoreAutorizzato, String>("email"));
        TableColumn<OperatoreAutorizzato, String> codFiscColumn = new TableColumn<OperatoreAutorizzato, String>("codice fiscale");
        codFiscColumn.setCellValueFactory(new PropertyValueFactory<OperatoreAutorizzato, String>("codFiscale"));

        tableView.setRowFactory(null);
        tableView.getColumns().addAll(userColumn, codFiscColumn);

    }


    public void handleRicercaAreaInteresse(ActionEvent actionEvent){
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
        this.btnRicercaAreaPerDenom.setOnAction(this::visualizeAiData);
        this.btnRicercaAreaPerStato = new Button("Ricerca per stato");
        this.btnRicercaAreaPerStato.setOnAction(this::visualizeAiData);
        this.btnRicercaAreaCoord = new Button("Ricerca Coord");
        this.btnRicercaAreaCoord.setOnAction(this::visualizeAiData);

        this.btnRicercaAreaPerDenom.setOnAction(event -> {ricercaAreaPerDenom();});
        btnRicercaAreaPerStato.setOnAction(event -> {handleRicercaAreaPerStato();});

        this.btnRicercaAreaCoord.setOnAction(event -> {handleRicercaAreaPerCoordinate();});
        paramBox.getChildren().add(tDenominazione);
        paramBox.getChildren().add(tStato);
        paramBox.getChildren().add(tLatitudine);
        paramBox.getChildren().add(tLongitudine);
        paramBox.getChildren().add(btnRicercaAreaPerDenom);
        paramBox.getChildren().add(btnRicercaAreaPerStato);
        paramBox.getChildren().add(btnRicercaAreaCoord);
        this.borderPane.setRight(paramBox);

    }

    private void prepTableAreaInteresse(){
        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.AREA_INTERESSE;

        TableColumn<AreaInteresse, String> denomColumn = new TableColumn<AreaInteresse, String>("denominazione");
        denomColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("denominazione"));
        denomColumn.setMinWidth(120);
        TableColumn<AreaInteresse, String> countryColumn = new TableColumn<AreaInteresse, String>("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("stato"));
        countryColumn.setMinWidth(100);
        TableColumn<AreaInteresse, String> latColumn = new TableColumn<AreaInteresse, String>("latitudine");
        latColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("latitudine"));
        latColumn.setMinWidth(100);
        TableColumn<AreaInteresse, String> longColumn = new TableColumn<AreaInteresse, String>("longitudine");
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
                    Request req;
                    try{
                        req = RequestFactory.buildRequest(
                                client.getClientId(),
                                ServerInterface.RequestType.selectAllWithCond,
                                ServerInterface.Tables.PARAM_CLIMATICO,
                                requestParams);

                    }catch(MalformedRequestException mre){
                        new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                        mre.printStackTrace();
                        return;
                    }
                    client.addRequest(req);

                    //get response
                    Response res;
                    res = client.getResponse(req.getRequestId());
                    if(res == null){
                        new Alert(Alert.AlertType.ERROR, "Error in response object").showAndWait();
                        return;
                    }
                    List<ParametroClimatico> params = new LinkedList<ParametroClimatico>();
                    if(res.getTable() == ServerInterface.Tables.PARAM_CLIMATICO
                            && res.getRespType() == ServerInterface.ResponseType.List){
                        params = (List<ParametroClimatico>)res.getResult();
                    }
                    params.forEach(System.out::println);

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
            Request request;
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
            List<AreaInteresse> areeInteresseRichieste = (List<AreaInteresse>) response.getResult();
            areeInteresseRichieste.forEach((areaInteresse -> {
                tableView.getItems().add(areaInteresse);
            }));
        }
        else{
            denomAlert.showAndWait();
        }
    }

    private void handleRicercaAreaPerStato(){
        tableView.getItems().clear();
        String stato = this.tStato.getText();
        if(!stato.isEmpty() && !(stato.equals("stato"))){
            Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
            params.replace(RequestFactory.condKey, "stato");
            params.replace(RequestFactory.fieldKey, stato);
            Request request;
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

    private void handleRicercaAreaPerCoordinate(){
        String longi = this.tLongitudine.getText();
        String lati = this.tLatitudine.getText();
        String query;
        if((longi.isEmpty() || lati.isEmpty()) ||
                (longi.equals("longitudine") || lati.equals("latitudine"))){
            this.coordAlert.showAndWait();
        }else{
            float lo = Float.parseFloat(longi);
            float la = Float.parseFloat(lati);

            Request request;
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
                float distance = MainWindowController.haversineDistance(lo, la, area.getLongitudine(), area.getLatitudine());
                if(distance < 50) { //50 km
                    System.out.println(area);
                    areeVicine.add(area);
                }
            });
            tableView.getItems().clear();
            areeVicine.forEach(area -> tableView.getItems().add(area));
        }
    }

    private void prepTableParamClimatici(){
        System.out.println("preparo tabella per parametri climatici");
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.PARAM_CLIMATICO;


        TableColumn<ParametroClimatico, LocalDate> dateColumn = new TableColumn<ParametroClimatico, LocalDate>("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, LocalDate>("pubDate"));
        TableColumn<ParametroClimatico, Short> ventoColumn = new TableColumn<ParametroClimatico, Short>("Vento");
        ventoColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("ventoValue"));
        TableColumn<ParametroClimatico, Short> umiditaColumn = new TableColumn<ParametroClimatico, Short>("Umidita");
        umiditaColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("umiditaValue"));
        TableColumn<ParametroClimatico, Short> pressioneColumn = new TableColumn<ParametroClimatico, Short>("Pressione");
        pressioneColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("pressioneValue"));
        TableColumn<ParametroClimatico, Short> temperaturaColumn = new TableColumn<ParametroClimatico, Short>("Temperatura");
        temperaturaColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("temperaturaValue"));
        TableColumn<ParametroClimatico, Short> precipitazioniColumn = new TableColumn<ParametroClimatico, Short>("Precipitazioni");
        precipitazioniColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("precipitazioniValue"));
        TableColumn<ParametroClimatico, Short> altitudineColumn = new TableColumn<ParametroClimatico, Short>("Altitudine ghiacciai");
        altitudineColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>("altitudineValue"));
        TableColumn<ParametroClimatico, Short> massaColumn = new TableColumn<ParametroClimatico, Short>("Massa ghiacciai");
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
                                paramsReqDenom.replace(RequestFactory.condKey, "parameterid");
                                paramsReqDenom.replace(RequestFactory.fieldKey, pc.getParameterId());
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
                                paramsReqNomeCentro.replace(RequestFactory.condKey, "parameterid");
                                paramsReqNomeCentro.replace(RequestFactory.fieldKey, pc.getParameterId());
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

                            if(respDenom.getRespType().equals(ServerInterface.ResponseType.Object) &&
                                    respDenom.getTable().equals(ServerInterface.Tables.AREA_INTERESSE)){
                                nomeArea = respDenom.getResult().toString();
                            }else{
                                nomeArea = "Error while retrieving denominazione area";
                            }
                            if(respNomeCentro.getRespType().equals(ServerInterface.ResponseType.Object)
                                    && respNomeCentro.getTable().equals(ServerInterface.Tables.CENTRO_MONITORAGGIO)){
                                nomeCentro = respNomeCentro.getResult().toString();
                            }else{
                                nomeCentro = "Error while retrieving denominazione centro";
                            }

                            try{
                                Stage pcDialogStage = new Stage();
                                PcDialog pcDialogController = new PcDialog(pc, nomeCentro, nomeArea);
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

    public void handleVisualizzaParametriClimatici(ActionEvent actionEvent){
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
        this.btnRicercaPcArea.setOnAction(this::handleRicercaPc);
        this.btnRicercaPcCm = new Button("Ricerca Per Cm");
        this.btnRicercaPcCm.setOnAction(this::handleRicercaPc);

        paramBox.getChildren().add(tAreaInteresse);
        paramBox.getChildren().add(tCentroMonitoraggio);
        paramBox.getChildren().add(tglDatePicker);
        paramBox.getChildren().add(startDatePicker);
        paramBox.getChildren().add(endDatePicker);
        paramBox.getChildren().add(tglRicercaAreaCm);
        paramBox.getChildren().add(btnRicercaPcArea);
        paramBox.getChildren().add(btnRicercaPcCm);

        this.borderPane.setRight(paramBox);

    }



    private void handleRicercaPc(ActionEvent event){

        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.refresh();

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
                Request requestAreaId;
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
                String areaInteresseId = resAreaId.getResult().toString();

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
                        parametriClimatici.removeIf((pc) -> MainWindowController.isBetweenDates(finalStartDate, finalEndDate, pc.getPubDate()));
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
                requestCentroIdParams.replace(RequestFactory.fieldKey, denomCmCercato);
                Request requestCentroId;
                try{
                    requestCentroId = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectObjJoinWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO, //join pc -> cm
                            requestCentroIdParams);

                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                client.addRequest(requestCentroId);
                Response responseCentroId = client.getResponse(requestCentroId.getRequestId());
                List<String> result = (List<String>)responseCentroId.getResult(); //returns multiple...
                String centroId = result.get(0);
                System.out.println(centroId);

                Map<String, String> requestParametriClimaticiParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                requestParametriClimaticiParams.replace(RequestFactory.condKey, "centroid");
                requestParametriClimaticiParams.replace(RequestFactory.fieldKey, centroId);

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
                            return MainWindowController.isBetweenDates(finalStartDate, finalEndDate, pc.getPubDate());
                        });
                    });
                }
                parametriClimatici.forEach((pc) -> tableView.getItems().add(pc));
            }
        }

    }

    @FXML
    public void handleInserisciAreaInteresse(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.CITY;
        prepTableCity();

        tDenominazione = new TextField("Denominazione");
        tDenominazione.setOnMouseClicked(e -> tDenominazione.clear());
        tStato = new TextField("Stato");
        tStato.setOnMouseClicked(e -> tStato.clear());
        tLatitudine = new TextField("Latitudine");
        tLatitudine.setOnMouseClicked(e -> tLatitudine.clear());
        tLongitudine = new TextField("Longitudine");
        tLongitudine.setOnMouseClicked(e -> tLongitudine.clear());
        Button inserisciAreaButton = new Button("inserisciArea");
        inserisciAreaButton.setOnAction(this::executeInsertAreaInteresse);
        tFilterCountry = new TextField("Filtra per stato");
        tFilterCountry.setOnMouseClicked(mouseClicked -> tFilterCountry.clear());
        visualizeCityData = new Button("Visualizza data");
        visualizeCityData.setOnAction(this::visualizeData);

        paramBox = new VBox(4);
        paramBox.getChildren().add(tDenominazione);
        paramBox.getChildren().add(tStato);
        paramBox.getChildren().add(tLatitudine);
        paramBox.getChildren().add(tLongitudine);
        paramBox.getChildren().add(inserisciAreaButton);
        paramBox.getChildren().add(tFilterCountry);
        paramBox.getChildren().add(visualizeCityData);
        this.borderPane.setRight(paramBox);


    }

    //TODO: group visualize data methods in a better way?
    private void visualizeAiData(ActionEvent event){
        Object source = event.getSource();
        if(source == btnRicercaAreaPerDenom){

        }else if(source == btnRicercaAreaPerStato){

        }else if(source == btnRicercaAreaCoord){

        }
    }

    private void visualizeCmData(ActionEvent event){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.setRowFactory(null);
        tableView.refresh();
        Request cmRequest;
        String country = tFilterCountry.getText();
        if(country.isEmpty()){
            try{
                cmRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    null
            );
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                return;
            }
        }else{
            try{
                Map<String, String> cmParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                cmParams.replace(RequestFactory.condKey, "country");
                cmParams.replace(RequestFactory.fieldKey, country);
                cmRequest = RequestFactory.buildRequest(
                        client.getClientId(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        cmParams
                );
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                return;
            }
        }
        client.addRequest(cmRequest);
        Response response = client.getResponse(client.getClientId());
        List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) response.getResult();
        TableColumn<CentroMonitoraggio, String> nomeCentroColumn = new TableColumn<CentroMonitoraggio, String>("Nome");
        nomeCentroColumn.setCellValueFactory(new PropertyValueFactory<CentroMonitoraggio, String>("denominazione"));
        TableColumn<CentroMonitoraggio, String> statoCentroColumn = new TableColumn<CentroMonitoraggio, String>("Stato");
        statoCentroColumn.setCellValueFactory(new PropertyValueFactory<CentroMonitoraggio, String>("country"));
        tableView.getColumns().addAll(nomeCentroColumn, statoCentroColumn);

        centri.forEach(centro -> tableView.getItems().add(centro));
    }

    private void visualizeData(ActionEvent event){
        tableView.getItems().clear();
        Request request;
        Request requestAi;
        Object source = event.getSource();
        if(source == visualizeCityData){
            if(tableShown != ServerInterface.Tables.CITY){
                prepTableCity();
            }
            if(tFilterCountry.getText().isEmpty() || tFilterCountry.getText().equals("Filtra per stato")){
                try{
                    request = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CITY,
                            null);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
            }else{
                try{
                    Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                    params.replace(RequestFactory.condKey, "country");
                    params.replace(RequestFactory.fieldKey, tFilterCountry.getText());
                    request = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.CITY,
                            params);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(request);
                Response responseCity = client.getResponse(request.getRequestId());
                Map<String, String> paramsAiRequest = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                paramsAiRequest.replace(RequestFactory.condKey, "stato");
                paramsAiRequest.replace(RequestFactory.fieldKey, tFilterCountry.getText());
                try{
                    requestAi  = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            paramsAiRequest
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(requestAi);
                Response responseAi = client.getResponse(requestAi.getRequestId());
                List<City> cities = (List<City>) responseCity.getResult();
                List<AreaInteresse> areeInteresse = (List<AreaInteresse>) responseAi.getResult();
                for(AreaInteresse area : areeInteresse){
                    cities = cities
                                .stream()
                                .filter(city -> !city.getAsciiName().equals(area.getDenominazione()))
                                .toList();
                }
                cities.forEach(city -> tableView.getItems().add(city));
            }
        }else if(source == visualizeAiData){
            if(tableShown != ServerInterface.Tables.AREA_INTERESSE){
                prepTableAreaInteresse();
            }
            if(tFilterCountry.getText().isEmpty() || tFilterCountry.getText().equals("Filtra per stato")){
                try{
                    request = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.AREA_INTERESSE,
                            null
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
            }else{
                try{
                    Map<String, String> paramsCityRequest = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                    paramsCityRequest.replace(RequestFactory.condKey, "stato");
                    paramsCityRequest.replace(RequestFactory.fieldKey, tFilterCountry.getText());
                    request = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            paramsCityRequest
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;

                }
                client.addRequest(request);
                Response responseAreaInteresse = client.getResponse(request.getRequestId());
                List<AreaInteresse> areeInteresse = (List<AreaInteresse>) responseAreaInteresse.getResult();
                areeInteresse.forEach(ai -> tableView.getItems().add(ai));
            }
        }else if(source == visualizeCmData){
            if(tableShown != ServerInterface.Tables.CENTRO_MONITORAGGIO){
                prepTableCentroMonitoraggio();
            }
            if(tFilterCountry.getText().isEmpty() || tFilterCountry.getText().equals("Filtra per stato")){
                Request citiesRequest;
                Request centriMonitoraggioRequest;
                try{
                    citiesRequest = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CITY,
                            null);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage());
                    return;
                }

                client.addRequest(citiesRequest);
                Response responseCities = client.getResponse(citiesRequest.getRequestId());

                try{
                    centriMonitoraggioRequest = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            null
                    );

                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(centriMonitoraggioRequest);
                Response responseCentriMonitoraggio = client.getResponse(centriMonitoraggioRequest.getRequestId());

                List<City> citiesWithoutCm = new LinkedList<City>();
                List<City> cities = (List<City>) responseCities.getResult();
                List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>) responseCentriMonitoraggio.getResult();
                for(CentroMonitoraggio cm : centriMonitoraggio){
                    citiesWithoutCm = cities.stream().filter(city -> !city.getAsciiName().equals(cm.getComune())).toList();
                }

                citiesWithoutCm.forEach(city -> tableView.getItems().add(city));
            }else{
                Request centriMonitoraggioRequest;
                try{
                    centriMonitoraggioRequest = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            null
                    );

                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(centriMonitoraggioRequest);
                Response responseCentriMonitoraggio = client.getResponse(centriMonitoraggioRequest.getRequestId());
                List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>)responseCentriMonitoraggio.getResult();
                try{
                    Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                    params.replace(RequestFactory.condKey, "country");
                    params.replace(RequestFactory.fieldKey, tFilterCountry.getText());
                    request = RequestFactory.buildRequest(
                            client.getClientId(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.CITY,
                            params);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(request);
                Response responseCity = client.getResponse(request.getRequestId());
                List<City> cities = (List<City>)responseCity.getResult();
                List<City> citiesWithoutCm = new LinkedList<City>();
                for(CentroMonitoraggio cm : centriMonitoraggio){
                    citiesWithoutCm = cities.stream().filter(city -> !city.getAsciiName().equals(cm.getComune())).toList();
                }

                citiesWithoutCm.forEach(city -> tableView.getItems().add(city));

            }
        }
    }

    private void executeInsertAreaInteresse(ActionEvent event){
        String denom = tDenominazione.getText();
        String stato = tStato.getText();
        String latitudine = tLatitudine.getText();
        String longitudine = tLongitudine.getText();

        if(denom.isEmpty() || denom.equals("Denominazione")){
            new Alert(Alert.AlertType.ERROR, "Denominazione non valida!");
        }else if(stato.isEmpty() || stato.equals("Stato")){
            new Alert(Alert.AlertType.ERROR, "Stato non valido!");
        }else if(latitudine.isEmpty() || latitudine.equals("Latitudine")){
            new Alert(Alert.AlertType.ERROR, "Latitudine non valida!");
        }else if(longitudine.isEmpty() || longitudine.equals("Longitudine")){
            new Alert(Alert.AlertType.ERROR, "Longitudine non valida!");
        }

        Map<String, String> insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.AREA_INTERESSE);
        insertParams.replace(RequestFactory.areaIdKey, IDGenerator.generateID());
        insertParams.replace(RequestFactory.denominazioneAreaKey, denom);
        insertParams.replace(RequestFactory.statoAreaKey, stato);
        insertParams.replace(RequestFactory.latitudineKey, latitudine);
        insertParams.replace(RequestFactory.longitudineKey, longitudine);
        Request insertRequest;
        try{
            insertRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.AREA_INTERESSE,
                    insertParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        client.addRequest(insertRequest);
        Response insertResponse = client.getResponse(insertRequest.getClientId());
        Object obj = insertResponse.getResult();
        if(obj instanceof String){
            new Alert(Alert.AlertType.ERROR, ServerInterface.DUPLICATE_ITEM).showAndWait();
        }else{
            boolean res = (boolean)insertResponse.getResult();
            if(res){
                new Alert(Alert.AlertType.CONFIRMATION, ServerInterface.SUCCESSFULL_INSERT).showAndWait();
            }else{
                new Alert(Alert.AlertType.ERROR, ServerInterface.UNSUCCESSFULL_INSERT).showAndWait();
            }
        }
        tDenominazione.setText("Denominazione");
        tStato.setText("Stato");
        tLatitudine.setText("Latitudine");
        tLongitudine.setText("Longitudine");
    }

    @FXML
    public void handleInserisciParametriClimatici(ActionEvent actionEvent){
        //TODO: Aggiungere un modo per vedere i centri di monitoraggio:
        //magari con un bottone ceh switcha attraverso le tableview?
        //Un bottone fa visualizzare i centri, schiacciando l'altro si vedono
        //le area di interesse

        paramBox = new VBox();
        tFilterCountry = new TextField("Filtra per stato");
        tFilterCountry.setOnMouseClicked(e -> tFilterCountry.clear());
        visualizeAiData = new Button("Visualizza aree");
        visualizeAiData.setOnAction(this::visualizeData);
        visualizeCmData = new Button("Visualizza centri");
        visualizeCmData.setOnAction(this::visualizeCmData);
        paramBox.getChildren().add(tFilterCountry);
        paramBox.getChildren().add(visualizeAiData);
        paramBox.getChildren().add(visualizeCmData);
        this.borderPane.setRight(paramBox);

        prepTableAreaInteresse();
        try{
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("fxml/parametro_climatico-scene.fxml"));
            loader.setController(parametroClimaticoController);
            Stage pcStage = new Stage();
            Scene scene = new Scene(loader.load(), 800, 400);
            pcStage.setScene(scene);
            pcStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}

    }


    public void executeInsertPCQuery(String parameterId, String nomeArea, String centroMon, LocalDate pubdate, Map<String, String> paramValues, String notaId, Map<String, String> notaInsertParams){
        logger.info("nome area: "+ nomeArea);
        logger.info("Noem centro: " + centroMon);
        logger.info("Parameterid: " +parameterId);
        logger.info("Notaid:  "+ notaId);

        Map<String, String> reqAreaIdParams = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond);
        reqAreaIdParams.replace(RequestFactory.objectKey, "areaid");
        reqAreaIdParams.replace(RequestFactory.condKey, "denominazione");
        reqAreaIdParams.replace(RequestFactory.fieldKey, nomeArea);
        Request requestAreaId;
        try{
            requestAreaId = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    reqAreaIdParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        client.addRequest(requestAreaId);
        Response respAreaId = client.getResponse(requestAreaId.getRequestId());

        Map<String, String> reqCentroIdParams = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond);
        reqCentroIdParams.replace(RequestFactory.objectKey, "centroid");
        reqCentroIdParams.replace(RequestFactory.condKey, "nomecentro");
        reqCentroIdParams.replace(RequestFactory.fieldKey, centroMon);
        Request requestCentroId;
        try{
            requestCentroId = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    reqCentroIdParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        client.addRequest(requestCentroId);
        Response respCentroId = client.getResponse(requestCentroId.getRequestId());
        /**
         * TODO:
         * NullPointerException se non c'e' risultato... add checks...
         * **/
        if(respAreaId.getRespType() != ServerInterface.ResponseType.Object){
            ServerInterface.ResponseType responseType = respAreaId.getRespType();
            if(responseType == ServerInterface.ResponseType.NoSuchElement){
                new Alert(Alert.AlertType.ERROR, ServerInterface.ResponseType.NoSuchElement.label).showAndWait();
            }else if(responseType == ServerInterface.ResponseType.Error){
                new Alert(Alert.AlertType.ERROR, ServerInterface.ResponseType.Error.label).showAndWait();
            }
            return;
        }
        if(respCentroId.getRespType() != ServerInterface.ResponseType.Object){
            ServerInterface.ResponseType responseType = respAreaId.getRespType();
            if(responseType == ServerInterface.ResponseType.NoSuchElement){
                new Alert(Alert.AlertType.ERROR, ServerInterface.ResponseType.NoSuchElement.label).showAndWait();
            }else if(responseType == ServerInterface.ResponseType.Error){
                new Alert(Alert.AlertType.ERROR, ServerInterface.ResponseType.Error.label).showAndWait();
            }
            return;
        }
        String areaId = respAreaId.getResult().toString();
        String centroId = respCentroId.getResult().toString();
        logger.info(areaId);
        logger.info(centroId);

        Request insertNotaRequest;
        notaInsertParams.replace(RequestFactory.notaIdKey, notaId);
        try{
            insertNotaRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.NOTA_PARAM_CLIMATICO,
                    notaInsertParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            logger.info("Mre costruzione insertNota");
            return;
        }
        client.addRequest(insertNotaRequest);
        Response responseNota = client.getResponse(insertNotaRequest.getRequestId());
        if(responseNota.getRespType() == ServerInterface.ResponseType.insertKo){
            new Alert(Alert.AlertType.ERROR, "errore inserimento nota").showAndWait();
            return;
        }

        Map<String, String> insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.PARAM_CLIMATICO);
        insertParams.replace(RequestFactory.parameterIdKey, parameterId);
        insertParams.replace(RequestFactory.areaIdKey, areaId);
        insertParams.replace(RequestFactory.centroIdKey, centroId);
        insertParams.replace(RequestFactory.pubDateKey, pubdate.toString());
        insertParams.replace(RequestFactory.notaIdKey, notaId);
        insertParams.replace(RequestFactory.valoreVentoKey, paramValues.get(RequestFactory.valoreVentoKey));
        insertParams.replace(RequestFactory.valoreUmiditaKey, paramValues.get(RequestFactory.valoreUmiditaKey));
        insertParams.replace(RequestFactory.valorePressioneKey, paramValues.get(RequestFactory.valorePressioneKey));
        insertParams.replace(RequestFactory.valorePrecipitazioniKey, paramValues.get(RequestFactory.valorePrecipitazioniKey));
        insertParams.replace(RequestFactory.valoreTemperaturaKey, paramValues.get(RequestFactory.valoreTemperaturaKey));
        insertParams.replace(RequestFactory.valoreAltGhiacciaiKey, paramValues.get(RequestFactory.valoreAltGhiacciaiKey));
        insertParams.replace(RequestFactory.valoreMassaGhiacciaiKey, paramValues.get(RequestFactory.valoreMassaGhiacciaiKey));

        Request insertPcRequest;
        try{
            insertPcRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.PARAM_CLIMATICO,
                    insertParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            logger.info("Mre costruzione insertPc");
            return;
        }
        client.addRequest(insertPcRequest);
        Response insertPcResponse = client.getResponse(insertPcRequest.getRequestId());
        if(insertPcResponse.getRespType() == ServerInterface.ResponseType.insertKo){
            new Alert(Alert.AlertType.ERROR, "Errore inserimento del parametro").showAndWait();
        }else{
            if((boolean) insertPcResponse.getResult()){
                new Alert(Alert.AlertType.CONFIRMATION, ServerInterface.SUCCESSFULL_INSERT).showAndWait();
            }else{
                new Alert(Alert.AlertType.ERROR, "Errore inserimento del parametro").showAndWait();
            }
        }
    }

    public void handleInserisciCentroMonitoraggio(ActionEvent actionEvent){
        prepTableCentroMonitoraggio();

        this.paramBox = new VBox();
        nomeCentroField = new TextField("Nome centro");
        nomeCentroField.setOnMouseClicked((event) -> nomeCentroField.clear());
        comuneField = new TextField("Comune centro");
        comuneField.setOnMouseClicked((event) -> comuneField.clear());
        statoCMField = new TextField("Stato centro");
        statoCMField.setOnMouseClicked((event) -> statoCMField.clear());
        areaInteresseCMField = new TextField("Area interesse");
        areaInteresseCMField.setOnMouseClicked((event) -> areaInteresseCMField.clear());
        areeInteresseBox = new TextArea();
        areeInteresseBox.setEditable(false);
        inserisciArea = new Button("Inserisci Area");
        inserisciArea.setOnAction((event) -> addAreaToBox());
        inserisciCM = new Button("Inserisci CM");
        inserisciCM.setOnAction((event) -> executeInsertCMQuery());
        clearCM = new Button("Pulisci");
        clearCM.setOnAction((event) -> clearCMFields());
        tFilterCountry = new TextField("Filtra per stato");
        tFilterCountry.setOnMouseClicked(e -> tFilterCountry.clear());
        visualizeCmData = new Button("Visualizza data");
        visualizeCmData.setOnAction(this::visualizeData);

        paramBox.getChildren().add(nomeCentroField);
        paramBox.getChildren().add(comuneField);
        paramBox.getChildren().add(statoCMField);
        paramBox.getChildren().add(areaInteresseCMField);
        paramBox.getChildren().add(areeInteresseBox);
        paramBox.getChildren().add(inserisciArea);
        paramBox.getChildren().add(inserisciCM);
        paramBox.getChildren().add(clearCM);
        paramBox.getChildren().add(tFilterCountry);
        paramBox.getChildren().add(visualizeCmData);


        this.borderPane.setRight(paramBox);

    }

    private void addAreaToBox(){
        String nomeArea = areaInteresseCMField.getText();
        if(!nomeArea.isEmpty()){
            String text = areeInteresseBox.getText();
            text += nomeArea + "\n";
            areeInteresseBox.setText(text);
        }
    }

    private void clearCMFields(){
        nomeCentroField.clear();
        comuneField.clear();
        statoCMField.clear();
        areaInteresseCMField.clear();
    }

    private void executeInsertCMQuery(){
        String nomeCentro = nomeCentroField.getText();
        String comuneCentro = comuneField.getText();
        String statoCentro = statoCMField.getText();
        //Area interesse è campo particolare, si possono inserire una quantita
        //indefinita di aree di interesse -> si cancella in automatico
        //solo areaInteresseCentro, per pulire tutto si usa clearCMFields()

        if(nomeCentro.isEmpty() || comuneCentro.isEmpty() || statoCentro.isEmpty()){centroMonitoraggioAlert.showAndWait(); return;}

        List<String> l = new LinkedList<String>();
        for(String nome: areeInteresseBox.getText().split("\n")){
            l.add(nome.trim());
        }

        StringBuilder areaList = new StringBuilder();
        if(l.size() == 1){
            areaList.append(l.get(0));
        }else{
            for(int i = 0; i < l.size(); i++){
                if(i == l.size() - 1)
                    areaList.append(l.get(i));
                else
                    areaList.append(l.get(i)).append(",");
            }
        }

        String params = "{%s}, {%s}, {%s}".formatted(nomeCentro, comuneCentro, statoCentro);
        logger.info(params);
        logger.info(areaList.toString());
        areaInteresseCMField.clear();
        Map<String, String> insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.CENTRO_MONITORAGGIO);
        insertParams.replace(RequestFactory.nomeCentroKey, nomeCentro);
        insertParams.replace(RequestFactory.comuneCentroKey, comuneCentro);
        insertParams.replace(RequestFactory.countryCentroKey, statoCentro);
        insertParams.replace(RequestFactory.listAiKey, areaList.toString());
        Request insertCmRequest;
        try{
            insertCmRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    insertParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        client.addRequest(insertCmRequest);
        Response res = client.getResponse(insertCmRequest.getRequestId());
        boolean result = (boolean)res.getResult();
        if(result){
            new Alert(Alert.AlertType.CONFIRMATION, "inserimento completato").showAndWait();
        }
        else{
            new Alert(Alert.AlertType.ERROR, "errore nell'inserimento ").showAndWait();
        }
        clearCMFields();
    }

    public void handleAbilitaNuovoOperatore(ActionEvent actionEvent){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();

        tEmailField = new TextField("email");
        tCodFiscField = new TextField("codice fiscale");
        buttonAbilitaOp = new Button("Abilita");
        buttonAbilitaOp.setOnAction(this::executeAbilitaNuovoOperatore);

        TableColumn<OperatoreAutorizzato, String> emailColumn = new TableColumn<OperatoreAutorizzato, String>();
        emailColumn.setCellValueFactory(new PropertyValueFactory<OperatoreAutorizzato, String>("email"));
        TableColumn<OperatoreAutorizzato, String> codiceFiscaleColumn = new TableColumn<OperatoreAutorizzato, String>();
        codiceFiscaleColumn.setCellValueFactory(new PropertyValueFactory<OperatoreAutorizzato, String>("codFiscale"));
        tableView.getColumns().addAll(emailColumn, codiceFiscaleColumn);
        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    OperatoreAutorizzato op = (OperatoreAutorizzato) row.getItem();
                    tEmailField.setText(op.getEmail());
                    tCodFiscField.setText(op.getCodFiscale());
                }
            });
            return row;
        });

        paramBox = new VBox();
        paramBox.getChildren().add(tEmailField);
        paramBox.getChildren().add(tCodFiscField);
        paramBox.getChildren().add(buttonAbilitaOp);

        this.borderPane.setRight(paramBox);

        Request opAutorizzatiRequest;
        try{
            opAutorizzatiRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.OP_AUTORIZZATO,
                    null
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage());
            return;
        }

        client.addRequest(opAutorizzatiRequest);
        Response response = client.getResponse(opAutorizzatiRequest.getRequestId());
        List<OperatoreAutorizzato> opAutorizzati = (List<OperatoreAutorizzato>)response.getResult();

        opAutorizzati.forEach(op -> tableView.getItems().add(op));

    }

    private void executeAbilitaNuovoOperatore(ActionEvent event){
        String email = tEmailField.getText();
        String codFisc = tCodFiscField.getText();
        if(email.isEmpty() || codFisc.isEmpty() || email.equals("email") || codFisc.equals("codice fiscale")){
            new Alert(Alert.AlertType.ERROR, "Campo non valido").showAndWait();
            return;
        }
        Map<String, String> insertAuthOpParams = RequestFactory.buildInsertParams(ServerInterface.Tables.OP_AUTORIZZATO);
        insertAuthOpParams.replace(RequestFactory.emailOpKey, email);
        insertAuthOpParams.replace(RequestFactory.codFiscOpKey, codFisc);
        Request insertAuthOpRequest;
        try{
            insertAuthOpRequest = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.OP_AUTORIZZATO,
                    insertAuthOpParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        client.addRequest(insertAuthOpRequest);
        Response res = client.getResponse(insertAuthOpRequest.getRequestId());
        //TODO: Test, add better checks, refresh table view in order to show the newly inserted op
        if((boolean)res.getResult()){
            new Alert(Alert.AlertType.CONFIRMATION, "L'inserimento ha avuto successo").showAndWait();
        }else{
            new Alert(Alert.AlertType.ERROR, "L'inserimento non ha avuto successo").showAndWait();
        }

    }


    /**
     * TODO:
     * -> inserire area esistente a un centro monitoraggio esistente, bottone?
     * -> Completare ricerca area interesse...
     * -> Completare visualizza parametro climatico
     * -> tableview builder?
     */

    public void handleVisualizzaGrafici(ActionEvent event){
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
        Request request;
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

    @FXML
    public void handleVisualizzaCentri(){
        paramBox.getChildren().clear();
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        //tableView.setRowFactory(null);
        Request requestCentro;
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
                        List<AreaInteresse> responseAree = (List<AreaInteresse>)responseAi.getResult();
                        responseAree.forEach(area -> areeInteresseAssociateAlCentro.add(area.getDenominazione()));
                    }
                    try{
                        Stage cmDialogStage = new Stage();
                        CmDialog cmDialogController = new CmDialog(areeInteresseAssociateAlCentro);
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

}
