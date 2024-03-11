package it.uninsubria.controller.operatore;

import it.uninsubria.MainWindow;
import it.uninsubria.clientCm.ClientProxy;
import it.uninsubria.datamodel.areaInteresse.AreaInteresse;
import it.uninsubria.datamodel.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.datamodel.city.City;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.dialog.*;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.datamodel.operatore.OperatoreAutorizzato;
import it.uninsubria.datamodel.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import it.uninsubria.tableViewBuilder.TableViewBuilder;
import it.uninsubria.util.IDGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Inet4Address;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class OperatoreViewController {

    public Button buttonRicercaAreaInteresse;
    public Button buttonVisualizzaParametri;
    public Button buttonInserisciParametri;
    public Button buttonInserisciCentroMonitoraggio;
    public Button buttonAggiungiArea;
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
    private Button btnAggiungiAreaACentro;

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
    private Button inserisciCentro;
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
    private Alert resErrorAlert;
    private Alert resNoSuchElementAlert;
    private Properties props;
    private Stage mainWindowStage;
    private Stage operatoreWindowStage;
    private final Client operatoreClient;
    private final MainWindowController mainWindowController;

    private final ParametroClimaticoController parametroClimaticoController;
    private final Logger logger;
    private ServerInterface.Tables tableShown;

    public OperatoreViewController(Stage mainWindowStage, Stage operatoreWindowStage, MainWindowController mainWindowController, String hostname, Inet4Address ipv4Address, int portNumber, String userId, String password){
        this.mainWindowController = mainWindowController;
        this.mainWindowStage = mainWindowStage;
        this.operatoreWindowStage = operatoreWindowStage;
        this.operatoreClient = new Client();
        ClientProxy operatoreProxy = new ClientProxy(operatoreClient, hostname);
        operatoreProxy.setIpAddr(ipv4Address);
        operatoreProxy.setPortNumber(portNumber);
        operatoreProxy.init();
        operatoreClient.setClientProxy(operatoreProxy);

        this.logger = Logger.getLogger("OperatoreWindow");

        this.operatoreWindowStage.setMinHeight(800);
        this.operatoreWindowStage.setWidth(1200);
        this.parametroClimaticoController = new ParametroClimaticoController(this);

        props = new Properties();
        props.put("user", userId);
        props.put("password", password);
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

        this.resErrorAlert = new Alert(Alert.AlertType.ERROR);
        this.resErrorAlert.setHeaderText("Errore in risposta");
        this.resErrorAlert.setContentText("Errore nell'oggetto risposta");

        this.resNoSuchElementAlert = new Alert(Alert.AlertType.ERROR);
        this.resNoSuchElementAlert.setHeaderText("Oggetto inesistente");
        this.resNoSuchElementAlert.setContentText("L'oggetto richiesto non esiste!");
    }

    @FXML
    public void exit(ActionEvent actionEvent){

        try{
            Request logoutRequest = RequestFactory.buildRequest(operatoreClient.getHostName(), ServerInterface.RequestType.executeLogout, null, null);
            operatoreClient.addRequest(logoutRequest);
            Response response = operatoreClient.getResponse(logoutRequest.getRequestId());
            System.out.println(response.getResponseType());
            if(response.getResponseType() == ServerInterface.ResponseType.logoutOk){
                new Alert(Alert.AlertType.CONFIRMATION, "Logout avvenuto con successo!").showAndWait();
            }else{
                new Alert(Alert.AlertType.ERROR, "Errore durante il logout!").showAndWait();
            }
        }catch(MalformedRequestException mre){}

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

        tableView.getColumns().addAll(TableViewBuilder.getColumnsCity());
        tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryPrepTableCity(tDenominazione, tStato, tLatitudine, tLongitudine));
    }

    private void prepTableCentroMonitoraggio(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.setRowFactory(null);
        tableView.refresh();
        tableShown = ServerInterface.Tables.CENTRO_MONITORAGGIO;
        tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());

        tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryPrepTableCentroMonitoraggio(nomeCentroField, comuneField, statoCMField));

    }

    private void prepTableOperatore(){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableView.setRowFactory(null);
        tableView.getColumns().addAll(TableViewBuilder.getColumnsOp());

    }


    public void handleRicercaAreaInteresse(ActionEvent actionEvent){
        prepTableAreaInteresse();
        this.paramBox = new VBox(10);
        paramBox.getStyleClass().add("param-box");
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

        this.btnRicercaAreaPerDenom.setOnAction(event -> {handleRicercaAreaPerDenominazione();});
        this.btnRicercaAreaPerStato.setOnAction(event -> {handleRicercaAreaPerStato();});
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

        tableView.getColumns().addAll(TableViewBuilder.getColumnsAi());

        tableView.setRowFactory(tv -> TableViewBuilder.getRowAi(operatoreClient));
        tableView.refresh(); //forces the tableview to refresh the listeners
    }


    private void showAreeInserite(){
        Request request = null;
        try{
            request = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.AREA_INTERESSE,
                    new HashMap<>());//select all does not need parameters
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        operatoreClient.addRequest(request);
        //get response
        Response response = operatoreClient.getResponse(request.getRequestId());

        if(response.getResponseType() == ServerInterface.ResponseType.Error){
            resErrorAlert.showAndWait();
            return;
        }

        List<AreaInteresse> res = (List<AreaInteresse>)response.getResult();
        prepTableAreaInteresse();
        res.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
    }

    private void showCentriInseriti(){
        Request request = null;
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.setRowFactory(null);
        tableView.refresh();
        try{
            request = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    null
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        operatoreClient.addRequest(request);
        Response response = operatoreClient.getResponse(request.getRequestId());

        if(response.getResponseType() == ServerInterface.ResponseType.Error){
            resErrorAlert.showAndWait();
            return;
        }

        List<CentroMonitoraggio> res = (List<CentroMonitoraggio>) response.getResult();
        tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());
        res.forEach(centroMonitoraggio -> tableView.getItems().add(centroMonitoraggio));

    }

    private void handleRicercaAreaPerDenominazione(){
        tableView.getItems().clear();
        String denom = this.tDenominazione.getText();
        if(!denom.isEmpty() && !(denom.equals("nome"))){
            Request request;
            try{
                Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "denominazione", denom);
                request = RequestFactory.buildRequest(
                        operatoreClient.getHostName(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        params);
                System.out.println("Build request: " + request);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            operatoreClient.addRequest(request);
            Response response = operatoreClient.getResponse(request.getRequestId());

            if(response.getResponseType() == ServerInterface.ResponseType.Error){
                resErrorAlert.showAndWait();
                return;
            }
            if(response.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                resNoSuchElementAlert.showAndWait();
                return;
            }

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
            Request request;
            try{
                Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "stato", stato);
                request = RequestFactory.buildRequest(
                        operatoreClient.getHostName(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        params);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }

            operatoreClient.addRequest(request);
            Response response = operatoreClient.getResponse(request.getRequestId());

            if(response.getResponseType() == ServerInterface.ResponseType.Error){
                resErrorAlert.showAndWait();
                return;
            }
            if(response.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                resNoSuchElementAlert.showAndWait();
                return;
            }
            List<AreaInteresse> queryResult = (List<AreaInteresse>)response.getResult();
            queryResult.removeIf(areaInteresse -> !areaInteresse.getStato().equals(stato));
            queryResult.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));

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
                        operatoreClient.getHostName(),
                        ServerInterface.RequestType.selectAll,
                        ServerInterface.Tables.AREA_INTERESSE,
                        null);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            operatoreClient.addRequest(request);
            List<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
            Response response = operatoreClient.getResponse(request.getRequestId());
            if(response.getResponseType() == ServerInterface.ResponseType.Error){
                resErrorAlert.showAndWait();
                return;
            }
            if(response.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                resNoSuchElementAlert.showAndWait();
                return;
            }

            areeInteresse = (LinkedList<AreaInteresse>) response.getResult();
            List<AreaInteresse> areeVicine = new LinkedList<AreaInteresse>();
            areeInteresse.forEach(area -> {
                float distance = MainWindowController.haversineDistance(lo, la, area.getLongitudine(), area.getLatitudine());
                //50 km
                if(distance < 50) areeVicine.add(area);
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

        tableView.getColumns().add(TableViewBuilder.getDateColumn());
        tableView.getColumns().addAll(TableViewBuilder.getColumnsPc());
        tableView.setRowFactory(tv -> TableViewBuilder.getRowPc(operatoreClient));
        tableView.refresh(); //forces the tableview to refresh the listeners
    }

    public void handleVisualizzaParametriClimatici(ActionEvent actionEvent){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        prepTableParamClimatici();

        this.paramBox = new VBox(10);
        this.paramBox.getStyleClass().add("param-box");
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
                Request requestAreaId;
                try{
                    Map<String, String> reqAreaIdParams = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectObjWithCond, "areaid", "denominazione", denomAiCercata);
                    requestAreaId = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectObjWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            reqAreaIdParams);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                operatoreClient.addRequest(requestAreaId);
                Response resAreaId = operatoreClient.getResponse(requestAreaId.getRequestId());

                if(resAreaId.getResponseType() == ServerInterface.ResponseType.Error){
                    resErrorAlert.showAndWait();
                    return;
                }
                if(resAreaId.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                    resNoSuchElementAlert.showAndWait();
                    return;
                }

                String areaInteresseId = resAreaId.getResult().toString();

                Request requestParamClimatici = null;
                try{
                    Map<String, String> reqParamClimatici = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", areaInteresseId);
                    requestParamClimatici = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            reqParamClimatici
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                operatoreClient.addRequest(requestParamClimatici);
                Response responseParametriClimatici = operatoreClient.getResponse(requestParamClimatici.getRequestId());

                if(responseParametriClimatici.getResponseType() == ServerInterface.ResponseType.Error){
                    resErrorAlert.showAndWait();
                    return;
                }
                if(responseParametriClimatici.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                    resNoSuchElementAlert.showAndWait();
                    return;
                }

                List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>)responseParametriClimatici.getResult();
                tableView.getItems().clear();
                if(ricercaPerData){
                    LocalDate finalStartDate = startDate;
                    LocalDate finalEndDate = endDate;
                    parametriClimatici.removeIf((pc) -> MainWindowController.isBetweenDates(finalStartDate, finalEndDate, pc.getPubDate()));
                }
                parametriClimatici.forEach((pc) -> {
                    System.out.println(pc);
                    tableView.getItems().add(pc);
                });
            }
        }
        //ricerca cm
        else if(event.getSource().equals(btnRicercaPcCm)){
            if(denomCmCercato.isEmpty() || denomCmCercato.equals("CentroMonitoraggio")){
                this.centroMonitoraggioAlert.showAndWait();
            }else{
                Request requestCentroId;
                try{
                    Map<String, String> requestCentroIdParams = RequestFactory
                            .buildParams(
                                    ServerInterface.RequestType.selectObjJoinWithCond,
                                    "centroid",
                                    ServerInterface.Tables.CENTRO_MONITORAGGIO.label,
                                    "nomecentro",
                                    denomCmCercato
                            );
                    requestCentroId = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectObjJoinWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO, //join pc -> cm
                            requestCentroIdParams);

                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                operatoreClient.addRequest(requestCentroId);
                Response responseCentroId = operatoreClient.getResponse(requestCentroId.getRequestId());
                if(responseCentroId.getResponseType() == ServerInterface.ResponseType.Error){
                    resErrorAlert.showAndWait();
                    return;
                }
                if(responseCentroId.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                    resNoSuchElementAlert.showAndWait();
                    return;
                }

                String centroId = responseCentroId.getResult().toString();

                Request requestParametriClimatici;
                try{
                    Map<String, String> requestParametriClimaticiParams = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "centroid", centroId);
                    requestParametriClimatici = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            requestParametriClimaticiParams);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                operatoreClient.addRequest(requestParametriClimatici);
                Response responseParametriClimatici = operatoreClient.getResponse(requestParametriClimatici.getRequestId());
                List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>)responseParametriClimatici.getResult();

                tableView.getItems().clear();
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
        paramBox.getStyleClass().add("param-box");
        paramBox.getChildren().add(tDenominazione);
        paramBox.getChildren().add(tStato);
        paramBox.getChildren().add(tLatitudine);
        paramBox.getChildren().add(tLongitudine);
        paramBox.getChildren().add(inserisciAreaButton);
        paramBox.getChildren().add(tFilterCountry);
        paramBox.getChildren().add(visualizeCityData);
        this.borderPane.setRight(paramBox);
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
                    operatoreClient.getHostName(),
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
                Map<String, String> cmParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "country", country);
                cmRequest = RequestFactory.buildRequest(
                        operatoreClient.getHostName(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        cmParams
                );
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                return;
            }
        }
        operatoreClient.addRequest(cmRequest);
        Response response = operatoreClient.getResponse(operatoreClient.getHostName());

        if(response.getResponseType() == ServerInterface.ResponseType.Error){
            resErrorAlert.showAndWait();
            return;
        }
        if(response.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
            resNoSuchElementAlert.showAndWait();
            return;
        }

        List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) response.getResult();
        tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());
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
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CITY,
                            null);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
            }else{
                try{
                    String statoDaFiltrare = tFilterCountry.getText();
                    Map<String, String> params = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "country", statoDaFiltrare);
                    request = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.CITY,
                            params);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                operatoreClient.addRequest(request);
                Response responseCity = operatoreClient.getResponse(request.getRequestId());
                String statoDaFiltrare = tFilterCountry.getText();
                try{
                    Map<String, String> paramsAiRequest = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "stato", statoDaFiltrare);
                    requestAi  = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            paramsAiRequest
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                operatoreClient.addRequest(requestAi);
                Response responseAi = operatoreClient.getResponse(requestAi.getRequestId());
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
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.AREA_INTERESSE,
                            null
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
            }else{
                String statoDaFiltrare = tFilterCountry.getText();
                try{
                    Map<String, String> paramsCityRequest = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "stato", statoDaFiltrare);
                    request = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            paramsCityRequest
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;

                }
                operatoreClient.addRequest(request);
                Response responseAreaInteresse = operatoreClient.getResponse(request.getRequestId());
                List<AreaInteresse> areeInteresse = (List<AreaInteresse>) responseAreaInteresse.getResult();
                areeInteresse.forEach(ai -> tableView.getItems().add(ai));
            }
        }else if(source == visualizeCmData){
            prepTableCity();
            tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryVisualizeCmData(nomeCentroField, comuneField, statoCMField));
            if(tFilterCountry.getText().isEmpty() || tFilterCountry.getText().equals("Filtra per stato")){
                Request citiesRequest;
                Request centriMonitoraggioRequest;
                try{
                    citiesRequest = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CITY,
                            null);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage());
                    return;
                }

                operatoreClient.addRequest(citiesRequest);
                Response responseCities = operatoreClient.getResponse(citiesRequest.getRequestId());

                try{
                    centriMonitoraggioRequest = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            null
                    );

                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                operatoreClient.addRequest(centriMonitoraggioRequest);
                Response responseCentriMonitoraggio = operatoreClient.getResponse(centriMonitoraggioRequest.getRequestId());

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
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            null
                    );

                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                operatoreClient.addRequest(centriMonitoraggioRequest);
                Response responseCentriMonitoraggio = operatoreClient.getResponse(centriMonitoraggioRequest.getRequestId());
                List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>)responseCentriMonitoraggio.getResult();
                String statoDaFiltrare = tFilterCountry.getText();
                try{
                    Map<String, String> params = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "country", statoDaFiltrare);
                    request = RequestFactory.buildRequest(
                            operatoreClient.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.CITY,
                            params);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                operatoreClient.addRequest(request);
                Response responseCity = operatoreClient.getResponse(request.getRequestId());
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

        Map<String, String> insertParams;
        Request insertRequest;
        try{
            insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.AREA_INTERESSE,
                    IDGenerator.generateID(), denom, stato, latitudine, longitudine);
            insertRequest = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.AREA_INTERESSE,
                    insertParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(insertRequest);
        Response insertResponse = operatoreClient.getResponse(insertRequest.getClientId());
        if(insertResponse.getResponseType() == ServerInterface.ResponseType.Error){
            resErrorAlert.showAndWait();
            return;
        }

        Object obj = insertResponse.getResult();
        if(obj instanceof String){
            new Alert(Alert.AlertType.ERROR, ServerInterface.DUPLICATE_ITEM).showAndWait();
        }else{
            boolean res = (boolean) insertResponse.getResult();
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
        paramBox = new VBox();
        paramBox.getStyleClass().add("param-box");
        tFilterCountry = new TextField("Filtra per stato");
        tFilterCountry.setOnMouseClicked(e -> tFilterCountry.clear());
        visualizeAiData = new Button("Visualizza aree");
        visualizeAiData.setOnAction(this::visualizeData);
        visualizeCmData = new Button("Visualizza centri");
        visualizeCmData.setOnAction(this::visualizeCmData);
        paramBox.getChildren().addAll(tFilterCountry, visualizeAiData, visualizeCmData);
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

        Request requestAreaId;
        try{
            Map<String, String> reqAreaIdParams = RequestFactory
                    .buildParams(
                            ServerInterface.RequestType.selectObjWithCond,
                            "areaid",
                            "denominazione",
                            nomeArea);
            requestAreaId = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    reqAreaIdParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(requestAreaId);
        Response respAreaId = operatoreClient.getResponse(requestAreaId.getRequestId());

        Request requestCentroId;
        try{
            Map<String, String> reqCentroIdParams = RequestFactory
                    .buildParams(
                            ServerInterface.RequestType.selectObjWithCond,
                            "centroid",
                            "nomecentro",
                            centroMon);
            requestCentroId = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    reqCentroIdParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(requestCentroId);
        Response respCentroId = operatoreClient.getResponse(requestCentroId.getRequestId());
        /**
         * TODO:
         * NullPointerException se non c'e' risultato... add checks...
         * **/
        if(respAreaId.getResponseType() != ServerInterface.ResponseType.Object){
            ServerInterface.ResponseType responseType = respAreaId.getResponseType();
            if(responseType == ServerInterface.ResponseType.NoSuchElement){
                new Alert(Alert.AlertType.ERROR, ServerInterface.ResponseType.NoSuchElement.label).showAndWait();
            }else if(responseType == ServerInterface.ResponseType.Error){
                new Alert(Alert.AlertType.ERROR, ServerInterface.ResponseType.Error.label).showAndWait();
            }
            return;
        }
        if(respCentroId.getResponseType() != ServerInterface.ResponseType.Object){
            ServerInterface.ResponseType responseType = respAreaId.getResponseType();
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
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.NOTA_PARAM_CLIMATICO,
                    notaInsertParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            logger.info("Mre costruzione insertNota");
            return;
        }
        operatoreClient.addRequest(insertNotaRequest);
        Response responseNota = operatoreClient.getResponse(insertNotaRequest.getRequestId());
        if(responseNota.getResponseType() == ServerInterface.ResponseType.insertKo){
            new Alert(Alert.AlertType.ERROR, "errore inserimento nota").showAndWait();
            return;
        }

        Map<String, String> insertParams;
        Request insertPcRequest;
        try{
            insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.PARAM_CLIMATICO,
                    parameterId, areaId, centroId, pubdate.toString(), notaId,
                    paramValues.get(RequestFactory.valoreVentoKey), paramValues.get(RequestFactory.valoreUmiditaKey),
                    paramValues.get(RequestFactory.valorePressioneKey), paramValues.get(RequestFactory.valorePrecipitazioniKey),
                    paramValues.get(RequestFactory.valoreTemperaturaKey), paramValues.get(RequestFactory.valoreAltGhiacciaiKey),
                    paramValues.get(RequestFactory.valoreMassaGhiacciaiKey));
            insertPcRequest = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.PARAM_CLIMATICO,
                    insertParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            logger.info("Mre costruzione insertPc");
            return;
        }
        operatoreClient.addRequest(insertPcRequest);
        Response insertPcResponse = operatoreClient.getResponse(insertPcRequest.getRequestId());
        if(insertPcResponse.getResponseType() == ServerInterface.ResponseType.insertKo){
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
        paramBox.getStyleClass().add("param-box");
        tFilterCountry = new TextField("Filtra per stato");
        tFilterCountry.setOnMouseClicked(e -> tFilterCountry.clear());
        inserisciCentro = new Button("Aggiungi centro");
        inserisciCentro.setOnAction((event) -> addAreaToBox());
        visualizeCmData = new Button("Visualizza data");
        visualizeCmData.setOnAction(this::visualizeData);

        paramBox.getChildren().add(inserisciCentro);
        paramBox.getChildren().add(tFilterCountry);
        paramBox.getChildren().add(visualizeCmData);

        this.borderPane.setRight(paramBox);

    }

    private void addAreaToBox(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/add-area-centro-scene.fxml"));
        AddAreaToCentroBoxDialog addAreaController = new AddAreaToCentroBoxDialog(this);
        fxmlLoader.setController(addAreaController);
        try{
            Scene scene = new Scene(fxmlLoader.load(), 400, 600);
            stage.setTitle("Aggiungi area a centro");
            stage.setScene(scene);
            stage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public void executeInsertCMQuery(String nomeCentro, String comuneCentro, String statoCentro, String areeAssociate){

        List<String> l = new LinkedList<String>();
        for(String nome: areeAssociate.split("\n")){
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
        //Controlla che il comune inserito sia associato allo stato corretto

        Request reqStato = null;
        try{
            Map<String, String> comuneParams = RequestFactory
                    .buildParams(ServerInterface.RequestType.selectAllWithCond, "ascii_name", comuneCentro);
            reqStato = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectAllWithCond,
                    ServerInterface.Tables.CITY,
                    comuneParams);

        }catch(MalformedRequestException mre){
            mre.printStackTrace();
            return;
        }
        operatoreClient.addRequest(reqStato);
        Response responseStato = operatoreClient.getResponse(reqStato.getRequestId());
        List<City> cities = (List<City>) responseStato.getResult();
        System.out.println(cities.size());
        if(cities.isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Stato non corrisponde").showAndWait();
        }else{
            City c = cities.get(0);
            System.out.println(c);
            if(!c.getCountry().equals(statoCentro)){
                new Alert(Alert.AlertType.CONFIRMATION, "Stato non corrisponde").showAndWait();
                return;
            }
        }

        String params = "{%s}, {%s}, {%s}".formatted(nomeCentro, comuneCentro, statoCentro);
        logger.info(params);
        logger.info(areaList.toString());
        Map<String, String> insertParams;
        Request insertCmRequest;
        try{
            insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.CENTRO_MONITORAGGIO,
                nomeCentro, comuneCentro, statoCentro, areaList.toString());
            insertCmRequest = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    insertParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(insertCmRequest);
        Response res = operatoreClient.getResponse(insertCmRequest.getRequestId());
        boolean result = (boolean)res.getResult();
        if(result){
            new Alert(Alert.AlertType.CONFIRMATION, "inserimento completato").showAndWait();
        }
        else{
            new Alert(Alert.AlertType.ERROR, "errore nell'inserimento ").showAndWait();
        }
    }

    public void handleAbilitaNuovoOperatore(ActionEvent actionEvent){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();

        tEmailField = new TextField("email");
        tCodFiscField = new TextField("codice fiscale");
        buttonAbilitaOp = new Button("Abilita");
        buttonAbilitaOp.setOnAction(this::executeAbilitaNuovoOperatore);

        tableView.getColumns().addAll(TableViewBuilder.getColumnsOp());
        tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryOpAbilitaOperatore(tEmailField, tCodFiscField));

        paramBox = new VBox();
        paramBox.getStyleClass().add("param-box");
        paramBox.getChildren().add(tEmailField);
        paramBox.getChildren().add(tCodFiscField);
        paramBox.getChildren().add(buttonAbilitaOp);

        this.borderPane.setRight(paramBox);

        Request opAutorizzatiRequest;
        try{
            opAutorizzatiRequest = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.OP_AUTORIZZATO,
                    null
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage());
            return;
        }

        operatoreClient.addRequest(opAutorizzatiRequest);
        Response response = operatoreClient.getResponse(opAutorizzatiRequest.getRequestId());
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
        Map<String, String> insertAuthOpParams;
        Request insertAuthOpRequest;
        try{
            insertAuthOpParams = RequestFactory.buildInsertParams(ServerInterface.Tables.OP_AUTORIZZATO, email, codFisc);
            insertAuthOpRequest = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.OP_AUTORIZZATO,
                    insertAuthOpParams);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(insertAuthOpRequest);
        Response res = operatoreClient.getResponse(insertAuthOpRequest.getRequestId());
        //TODO: Test, add better checks, refresh table view in order to show the newly inserted op
        if((boolean)res.getResult()){
            new Alert(Alert.AlertType.CONFIRMATION, "L'inserimento ha avuto successo").showAndWait();
        }else{
            new Alert(Alert.AlertType.ERROR, "L'inserimento non ha avuto successo").showAndWait();
        }

    }

    @FXML
    public void handleAggiungiAreaACentro(ActionEvent event){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        this.paramBox = new VBox();
        paramBox.getStyleClass().add("param-box");
        this.tAreaInteresse = new TextField("Nome Area");
        this.tAreaInteresse.setOnMouseClicked(e -> this.tAreaInteresse.clear());
        this.tCentroMonitoraggio = new TextField("Nome Centro");
        this.visualizeAiData = new Button("Visualizza aree");
        this.visualizeAiData.setOnAction(e -> {showAreeInserite();});
        this.visualizeCmData = new Button("Visualizza centri");
        this.visualizeCmData.setOnAction(e -> {
            showCentriInseriti();
        });
        this.tCentroMonitoraggio.setOnMouseClicked(e -> this.tCentroMonitoraggio.clear());
        btnAggiungiAreaACentro = new Button("Aggiungi");
        btnAggiungiAreaACentro.setOnAction(this::aggiungiAreaCentro);

        this.paramBox.getChildren().addAll(tAreaInteresse, tCentroMonitoraggio, visualizeAiData, visualizeCmData, btnAggiungiAreaACentro);
        this.borderPane.setRight(paramBox);

    }

    private void aggiungiAreaCentro(ActionEvent event) {
        String denomAi = tAreaInteresse.getText();
        String denomCm = tCentroMonitoraggio.getText();
        if (denomAi.isEmpty() || denomCm.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Stringhe inserite non valide!");
            return;
        }
        //check se l'area esiste
        Request requestAi;
        try {
            Map<String, String> paramsCheckAi = RequestFactory
                    .buildParams(ServerInterface.RequestType.selectObjWithCond, "areaid", "denominazione", denomAi);
            requestAi = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    paramsCheckAi);
        } catch (MalformedRequestException mre) {
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(requestAi);
        Response responseAi = operatoreClient.getResponse(requestAi.getRequestId());
        String areaId = responseAi.getResult().toString();
        if (areaId.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Area non esistente").showAndWait();
            return;
        }

        //check se il centro esiste
        Request requestCm;
        try {
            Map<String, String> paramsCheckCm = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond, "centroid", "nomecentro", denomCm);
            requestCm = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    paramsCheckCm
            );
        } catch (MalformedRequestException mre) {
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(requestCm);
        Response responseCm = operatoreClient.getResponse(requestCm.getRequestId());
        String centroId = responseCm.getResult().toString();
        if (centroId.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Centro non esistente").showAndWait();
            return;
        }

        //Check se l'area sia gia presente nell'array del centro
        Request req;
        try {
            Map<String, String> paramsCheckAiInCm = RequestFactory
                    .buildParams(ServerInterface.RequestType.selectObjWithCond, "aree_interesse_ids", "centroid", centroId);
            req = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    paramsCheckAiInCm
            );
        } catch (MalformedRequestException mre) {
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        operatoreClient.addRequest(req);
        Response response = operatoreClient.getResponse(req.getRequestId());
        String areeAssociateAlCentro = response.getResult().toString();
        if (areeAssociateAlCentro.contains(areaId)) {
            new Alert(Alert.AlertType.INFORMATION, "area già associata al centro").showAndWait();
            return;
        } else { //area non è associata al centro -> si aggiunge l'area al array associato al centro
            //requires an UPDATE statement
            Request insertAreaRequest = null;
            try{
                Map<String, String> updateParams = RequestFactory.buildParams(ServerInterface.RequestType.executeUpdateAi, areaId, centroId);
                insertAreaRequest = RequestFactory.buildRequest(
                        operatoreClient.getHostName(),
                        ServerInterface.RequestType.executeUpdateAi,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        updateParams
                );
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            operatoreClient.addRequest(insertAreaRequest);
            Response updateAi = operatoreClient.getResponse(insertAreaRequest.getRequestId());
            if(updateAi.getResponseType() == ServerInterface.ResponseType.updateOk){
                new Alert(Alert.AlertType.CONFIRMATION, "Area aggiunta al centro").showAndWait();
            }else{
                new Alert(Alert.AlertType.ERROR, "Errore nell'aggiunta dell'area").showAndWait();
            }
        }
    }

    @FXML
    public void handleVisualizzaGrafici(ActionEvent event){
        tableView.getColumns().clear();
        tableView.getItems().clear();
        prepTableAreaInteresse();
        showAreeInserite();
        this.paramBox = new VBox(2);
        paramBox.getStyleClass().add("param-box");
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
        Request request;
        try{
            Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond,
                    "areaid", "denominazione", nomeArea);
            request = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    params
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        operatoreClient.addRequest(request);
        if(request == null){return;}
        Response response = operatoreClient.getResponse(request.getRequestId());
        String areaId = "";
        if(response.getResponseType() == ServerInterface.ResponseType.Object
                && response.getTable() == ServerInterface.Tables.AREA_INTERESSE){
            areaId = response.getResult().toString();
        }

        System.out.println("areaid ->" + areaId);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/graph-dialog.fxml"));
            fxmlLoader.setController(new GraphDialog(operatoreClient, areaId));
            Stage chartStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
            chartStage.setScene(scene);
            chartStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}

    }

    @FXML
    public void handleVisualizzaCentri(){
        //Test for a NullPointerException when paramBox is not initialized
        if(paramBox != null && !paramBox.getChildren().isEmpty())
            paramBox.getChildren().clear();

        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();

        Request requestCentro;
        try{
            requestCentro = RequestFactory.buildRequest(
                    operatoreClient.getHostName(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    null);
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage());
            mre.printStackTrace();
            return;
        }
        operatoreClient.addRequest(requestCentro);
        Response responseCentriMonitoraggio = operatoreClient.getResponse(requestCentro.getClientId());

        List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>) responseCentriMonitoraggio.getResult();

        tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());

        centriMonitoraggio.forEach(cm -> {
            tableView.getItems().add(cm);
        });

        tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryHandleVisualizzaCentri(operatoreClient));

        tableView.refresh();

    }

}
