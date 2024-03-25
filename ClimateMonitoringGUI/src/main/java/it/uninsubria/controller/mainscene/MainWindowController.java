package it.uninsubria.controller.mainscene;
import it.uninsubria.MainWindow;
import it.uninsubria.datamodel.areaInteresse.AreaInteresse;
import it.uninsubria.datamodel.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.clientCm.Client;
import it.uninsubria.clientCm.ClientProxy;
import it.uninsubria.controller.dialog.AiDialog;
import it.uninsubria.controller.dialog.CmDialog;
import it.uninsubria.controller.dialog.GraphDialog;
import it.uninsubria.controller.dialog.NewConnectionDialog;
import it.uninsubria.controller.loginview.LoginViewController;
import it.uninsubria.controller.operatore.OperatoreViewController;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.controller.registrazione.RegistrazioneController;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.datamodel.operatore.OperatoreAutorizzato;
import it.uninsubria.datamodel.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import it.uninsubria.tableViewBuilder.TableViewBuilder;
import it.uninsubria.util.Util;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

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

    private Button buttonNewConnection;
    private Button buttonDisconnect;

    //alerts
    private Alert coordAlert;
    private Alert denomAlert;
    private Alert statoAlert;
    private Alert pcAlert;
    private Alert areaInteresseAlert;
    private Alert centroMonitoraggioAlert;
    private Alert invalidDateAlert;
    private Alert cmAlert;

    private Alert resErrorAlert;
    private Alert resNoSuchElementAlert;
    private Alert areaWithNoPc;

    private Alert clientNotConnected;
    private Alert clientHasDisconnected;
    private Alert serverHasDisconnected;
    private Alert disconnectSuccessful;

    private Stage mainWindowStage;
    private Stage loginStage;
    private Stage registrazioneStage;
    private Stage operatoreStage;
    private LoginViewController loginViewController;
    private RegistrazioneController registrazioneController;
    private ParametroClimaticoController parametroClimaticoController;
    private OperatoreViewController operatoreViewController;

    private Client client;
    private ClientProxy clientProxy;

    public MainWindowController(Stage stage){

        this.mainWindowStage = stage;
        this.mainWindowStage.setOnCloseRequest(e -> {
            if(client != null){
                this.client.getClientProxy().sendQuitRequest();
                this.client.setRunCondition(false);
            }
            System.out.println("Exiting");
            Platform.exit();
        });

        int width = (int) Screen.getPrimary().getVisualBounds().getWidth();
        int height = (int) Screen.getPrimary().getVisualBounds().getHeight() - 50;
        mainWindowStage.setMinWidth(width);
        mainWindowStage.setMaxWidth(width);
        mainWindowStage.setMinHeight(height);
        mainWindowStage.setMaxHeight(height);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        mainWindowStage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - mainWindowStage.getWidth()) / 2);
        mainWindowStage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - mainWindowStage.getHeight()) / 2);
        createControllers();
        initAlerts();
    }

    private void createControllers(){
        loginViewController = new LoginViewController(this);
        registrazioneController = new RegistrazioneController(this);
    }

    public LoginViewController getLoginViewController(){
        return this.loginViewController;
    }

    public RegistrazioneController getRegistrazioneController(){
        return this.registrazioneController;
    }

    public Client getClient(){
        return this.client;
    }

    public void setClient(Client client){
        this.client = client;
    }

    public ClientProxy getClientProxy(){
        return this.clientProxy;
    }

    public void setClientProxy(ClientProxy clientProxy){
        this.clientProxy= clientProxy;
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

        this.resErrorAlert = new Alert(Alert.AlertType.ERROR);
        this.resErrorAlert.setHeaderText("Errore in risposta");
        this.resErrorAlert.setContentText("Errore nell'oggetto risposta");

        this.resNoSuchElementAlert = new Alert(Alert.AlertType.ERROR);
        this.resNoSuchElementAlert.setHeaderText("Oggetto inesistente");
        this.resNoSuchElementAlert.setContentText("L'oggetto richiesto non esiste!");

        this.clientNotConnected = new Alert(Alert.AlertType.ERROR);
        this.clientNotConnected.setHeaderText("Nessuna connessione!");
        this.clientNotConnected.setContentText("L'applicazione non e' connessa a nessun server!");

        this.clientHasDisconnected = new Alert(Alert.AlertType.CONFIRMATION);
        this.clientHasDisconnected.setHeaderText("Disconnessione effettuata.");
        this.clientHasDisconnected.setContentText("Disconessione dal server effettuata con successo!");

        this.serverHasDisconnected = new Alert(Alert.AlertType.INFORMATION);
        this.serverHasDisconnected.setHeaderText("Server disconnesso.");
        this.serverHasDisconnected.setContentText("Il server si e' disconnesso, tentare una nuova connessione oppure connettersi a un nuovo server.");

        this.areaWithNoPc = new Alert(Alert.AlertType.INFORMATION);
        this.areaWithNoPc.setHeaderText("Area senza parametri climatici.");
        this.areaWithNoPc.setContentText("L'area selezionata non presenta alcun parametro climatico associato.");

    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        if(client != null){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/login-scene.fxml"));
                fxmlLoader.setController(getLoginViewController());
                loginStage = new Stage();
                loginStage.initOwner((Stage)((Node) actionEvent.getSource()).getScene().getWindow());
                loginStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(fxmlLoader.load(), 400, 300);
                loginStage.setScene(scene);
                loginStage.show();
            }catch(IOException ioe){ioe.printStackTrace();}
        }else{
            clientNotConnected.showAndWait();
        }
    }


    public void handleRicercaAreaInteresse(ActionEvent actionEvent) {
        if(client != null){
            tableView.getColumns().clear();
            tableView.getItems().clear();
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

            this.btnRicercaAreaPerDenom.setOnAction(event -> {
                handleRicercaAreaDenom();
            });

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
        }else{
            clientNotConnected.showAndWait();
        }
    }

    private void prepTableParamClimatici(){
        System.out.println("preparo tabella per parametri climatici");
        tableView.getColumns().clear();
        tableView.getItems().clear();

        tableView.getColumns().add(TableViewBuilder.getDateColumn());
        tableView.getColumns().addAll(TableViewBuilder.getColumnsPc());
        tableView.setRowFactory(tv -> TableViewBuilder.getRowPc(client));

        tableView.refresh(); //forces the tableview to refresh the listeners
    }

    private void prepTableAreaInteresse(){
        tableView.getItems().clear();
        tableView.getColumns().clear();
        System.out.println("table view column size in prepAreaInteresse: "+ tableView.getColumns().size());

        TableColumn<AreaInteresse, String> denomColumn = new TableColumn("denominazione");
        denomColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("denominazione"));
        denomColumn.setMinWidth(120);
        TableColumn<AreaInteresse, String> countryColumn = new TableColumn("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("stato"));
        countryColumn.setMinWidth(100);
        TableColumn<AreaInteresse, String> latColumn = new TableColumn("latitudine");
        latColumn.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>("latitudine"));
        latColumn.setMinWidth(100);
        TableColumn<AreaInteresse, String> longColumn = new TableColumn("longitudine");
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
                    Request req;
                    try{
                        Map<String, String> requestParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", a.getAreaid());
                        req = RequestFactory.buildRequest(client.getHostName(), ServerInterface.RequestType.selectAllWithCond, ServerInterface.Tables.PARAM_CLIMATICO, requestParams);
                    }catch(MalformedRequestException mre){
                        new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                        mre.printStackTrace();
                        return;
                    }
                    client.addRequest(req);

                    //get response
                    Response res;
                    res = client.getResponse(req.getRequestId());

                    //Check Response for errors
                    if(res.getResponseType() == ServerInterface.ResponseType.Error){
                        resErrorAlert.showAndWait();
                        return;
                    }
                    if(res.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                        areaWithNoPc.showAndWait();
                        return;
                    }
                    List<ParametroClimatico> params = (List<ParametroClimatico>) res.getResult();

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
                    client.getHostName(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.AREA_INTERESSE,
                    null);//select all does not need parameters
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        client.addRequest(request);
        //get response
        Response response = client.getResponse(request.getRequestId());
        //Check response
        if(response.getResponseType() == ServerInterface.ResponseType.List && response.getTable() == ServerInterface.Tables.AREA_INTERESSE){
            prepTableAreaInteresse();
            List<AreaInteresse> res = (List<AreaInteresse>)response.getResult();
            res.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
        }
    }

    private void handleRicercaAreaDenom(){
        tableView.getItems().clear();
        String denom = this.tDenominazione.getText();
        if(!denom.isEmpty() && !(denom.equals("nome"))){
            Request request;
            try{
                Map<String, String> params = RequestFactory
                        .buildParams(ServerInterface.RequestType.selectAllWithCond, "denominazione", denom);
                request = RequestFactory.buildRequest(
                        client.getHostName(),
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
        String stato = this.tStato.getText().trim();
        if(!stato.isEmpty() && !(stato.equals("stato"))){
            Request request;
            try{
                Map<String, String> params = RequestFactory
                        .buildParams(ServerInterface.RequestType.selectAllWithCond, "stato", stato);
                request = RequestFactory.buildRequest(
                        client.getHostName(),
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
                        client.getHostName(),
                        ServerInterface.RequestType.selectAll,
                        ServerInterface.Tables.AREA_INTERESSE,
                        null);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            client.addRequest(request);
            Response response = client.getResponse(request.getRequestId());

            if(response.getResponseType() == ServerInterface.ResponseType.Error){
                resErrorAlert.showAndWait();
                return;
            }
            if(response.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                resNoSuchElementAlert.showAndWait();
                return;
            }

            List<AreaInteresse> areeInteresse = (LinkedList<AreaInteresse>) response.getResult();
            List<AreaInteresse> areeVicine = new LinkedList<AreaInteresse>();
            areeInteresse.forEach(area -> {
                float distance = Util.haversineDistance(lo, la, area.getLongitudine(), area.getLatitudine());
                if(distance < 50) { //50 km
                    System.out.println(area);
                    areeVicine.add(area);
                }
            });
            tableView.getItems().clear();
            areeVicine.forEach(area -> tableView.getItems().add(area));

        }

    }

    public void handleVisualizzaParametriClimatici(ActionEvent actionEvent) {
        if(client != null){
            tableView.getColumns().clear();
            tableView.getItems().clear();
            prepTableParamClimatici();

            this.paramBox = new VBox(10);
            paramBox.getStyleClass().add("param-box");
            this.tAreaInteresse = new TextField("AreaInteresse");
            this.tAreaInteresse.setOnMouseClicked((event) -> this.tAreaInteresse.clear());
            this.tCentroMonitoraggio = new TextField("CentroMonitoraggio");
            this.tCentroMonitoraggio.setOnMouseClicked((event) -> this.tCentroMonitoraggio.clear());
            this.tglDatePicker = new ToggleButton("Ricerca con data");
            this.startDatePicker = new DatePicker();
            this.endDatePicker = new DatePicker();
            this.btnRicercaPcArea = new Button("Ricerca per area");
            this.btnRicercaPcArea.setOnAction(this::handleRicercaPc);
            this.btnRicercaPcCm = new Button("Ricerca Per Cm");
            this.btnRicercaPcCm.setOnAction(this::handleRicercaPc);

            paramBox.getChildren().add(tAreaInteresse);
            paramBox.getChildren().add(tCentroMonitoraggio);
            paramBox.getChildren().add(tglDatePicker);
            paramBox.getChildren().add(startDatePicker);
            paramBox.getChildren().add(endDatePicker);
            paramBox.getChildren().add(btnRicercaPcArea);
            paramBox.getChildren().add(btnRicercaPcCm);
            this.borderPane.setRight(paramBox);
        }else{
            clientNotConnected.showAndWait();
        }
    }

    @FXML
    public void handleVisualizzaGrafici(){
        if(client != null){
            tableView.getColumns().clear();
            tableView.getItems().clear();
            prepTableAreaInteresse();
            showAreeInserite();
            this.paramBox = new VBox(2);
            this.paramBox.getStyleClass().add("param-box");
            this.tAreaInteresse = new TextField("Nome Area");
            this.tAreaInteresse.setOnMouseClicked(e -> this.tAreaInteresse.clear());
            this.btnRicercaArea = new Button("Ricerca area");
            this.btnRicercaArea.setOnAction(e -> createChart());

            this.paramBox.getChildren().add(tAreaInteresse);
            this.paramBox.getChildren().add(btnRicercaArea);
            this.borderPane.setRight(paramBox);

        }else{
            clientNotConnected.showAndWait();
        }
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
            Map<String, String> params = RequestFactory
                    .buildParams(ServerInterface.RequestType.selectObjWithCond, "areaid", "denominazione", nomeArea);
            request = RequestFactory.buildRequest(
                    client.getHostName(),
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
        Response response = client.getResponse(request.getRequestId());

        if(response.getResponseType() == ServerInterface.ResponseType.Error){
            resErrorAlert.showAndWait();
            return;
        }
        if(response.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
            resNoSuchElementAlert.showAndWait();
            return;
        }

        String areaId = response.getResult().toString();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/graph-dialog.fxml"));
            fxmlLoader.setController(new GraphDialog(client, areaId));
            Stage chartStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
            chartStage.setScene(scene);
            chartStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}

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
                            client.getHostName(),
                            ServerInterface.RequestType.selectObjWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            reqAreaIdParams);
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                client.addRequest(requestAreaId);
                Response resAreaId = client.getResponse(requestAreaId.getRequestId());

                if(resAreaId.getResponseType() == ServerInterface.ResponseType.Error){
                    resErrorAlert.showAndWait();
                    return;
                }
                if(resAreaId.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                    resNoSuchElementAlert.showAndWait();
                    return;
                }

                String areaInteresseId = resAreaId.getResult().toString();
                Request requestParamClimatici;
                try{
                    Map<String, String> reqParamClimatici = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", areaInteresseId);
                    requestParamClimatici = RequestFactory.buildRequest(
                            client.getHostName(),
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
                    parametriClimatici.removeIf(parametroClimatico -> Util.isBetweenDates(finalStartDate, finalEndDate, parametroClimatico.getPubDate()));
                }
                parametriClimatici.forEach((pc) -> tableView.getItems().add(pc));
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
                            .buildParams(ServerInterface.RequestType.selectObjJoinWithCond,
                                    "centroid",
                                    ServerInterface.Tables.CENTRO_MONITORAGGIO.label,
                                    "nomecentro",
                                    denomCmCercato
                            );
                    requestCentroId = RequestFactory.buildRequest(
                            client.getHostName(),
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

                if(responseCentroId.getResponseType() == ServerInterface.ResponseType.Error){
                    resErrorAlert.showAndWait();
                    return;
                }
                if(responseCentroId.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                    resNoSuchElementAlert.showAndWait();
                    return;
                }

                /**
                List<String> result = (List<String>)responseCentroId.getResult(); //returns multiple...
                String centroId = result.get(0);
                 **/
                String centroId = responseCentroId.getResult().toString();
                System.out.println(centroId);

                Request requestParametriClimatici;
                try{
                    Map<String, String> requestParametriClimaticiParams = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "centroid", centroId);
                    requestParametriClimatici = RequestFactory.buildRequest(
                            client.getHostName(),
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
                    parametriClimatici.forEach((param) -> {
                        parametriClimatici.removeIf((pc) -> {
                            return Util.isBetweenDates(finalStartDate, finalEndDate, pc.getPubDate());
                        });
                    });
                }
                parametriClimatici.forEach((pc) -> tableView.getItems().add(pc));
            }
        }
    }

    @FXML
    public void handleVisualizzaCentri(){
        if(client != null){
            tableView.getColumns().clear();
            tableView.getItems().clear();
            if(paramBox != null)
                if(!paramBox.getChildren().isEmpty()) paramBox.getChildren().clear();
            Request requestCentro = null;
            try{
                requestCentro = RequestFactory.buildRequest(
                        client.getHostName(),
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

            if(responseCentriMonitoraggio.getResponseType() == ServerInterface.ResponseType.Error){
                resErrorAlert.showAndWait();
                return;
            }
            if(responseCentriMonitoraggio.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                resNoSuchElementAlert.showAndWait();
                return;
            }

            List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>) responseCentriMonitoraggio.getResult();
            TableColumn<CentroMonitoraggio, String> denomCentro = new TableColumn("Denominazione");
            denomCentro.setCellValueFactory(new PropertyValueFactory<CentroMonitoraggio, String>("denominazione"));
            tableView.getColumns().add(denomCentro);

            centriMonitoraggio.forEach(cm -> {tableView.getItems().add(cm);});

            tableView.setRowFactory(tv -> {
                TableRow row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if(event.getClickCount() == 2 && !(row.isEmpty())){
                        CentroMonitoraggio c = (CentroMonitoraggio) row.getItem();
                        //System.out.println("Item double clicked: " + c);
                        List<String> areeId = c.getAreeInteresseIdAssociate();
                        List<String> areeInteresseAssociateAlCentro = new LinkedList<String>();
                        for(String areaId : areeId){
                            Request requestAi;
                            try{
                                Map<String, String> reqAiParams = RequestFactory
                                        .buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", areaId);
                                requestAi = RequestFactory.buildRequest(
                                        client.getHostName(),
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

                            if(responseAi.getResponseType() == ServerInterface.ResponseType.Error){
                                resErrorAlert.showAndWait();
                                return;
                            }
                            if(responseAi.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                                resNoSuchElementAlert.showAndWait();
                                return;
                            }

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
        }else{
            clientNotConnected.showAndWait();
        }
    }

    public boolean onExecuteLoginQuery(String userID, String password){
        System.out.printf("Userid & password: %s %s\n", userID, password);

        Request loginRequest;
        try{
            Map<String, String> loginParams = RequestFactory
                    .buildParams(ServerInterface.RequestType.executeLogin, userID, password);
            loginRequest = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.executeLogin,
                    ServerInterface.Tables.OPERATORE,
                    loginParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return false;
        }
        System.out.println(loginRequest);
        client.addRequest(loginRequest);
        Response response = client.getResponse(loginRequest.getRequestId());
        if(response.getResponseType() == ServerInterface.ResponseType.loginKo) return false;
        else{
            mainWindowStage.close();
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/operatore-scene.fxml"));
                operatoreStage = new Stage();
                fxmlLoader.setController(new OperatoreViewController(operatoreStage, client, clientProxy, userID, password));
                Scene scene = new Scene(fxmlLoader.load(), 800, 1200);
                operatoreStage.setScene(scene);
                operatoreStage.setTitle("operatoreView");
                operatoreStage.show();
            }catch(IOException ioe){ioe.printStackTrace();}
            return true;
        }
    }

    private boolean requestSignUp(String codFisc, String email){
        Request request;
        try{
            request = RequestFactory.buildRequest(
                    client.getHostName(),
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
            new Alert(Alert.AlertType.ERROR, "Operatore non abilitato alla registrazione.").showAndWait();
            return false;
        }else{
            Request requestCentroId;
            try{
                Map<String, String> reqCmIdParams = RequestFactory
                        .buildParams(ServerInterface.RequestType.selectObjWithCond, "centroid", "comune", centroAfferenza);
                requestCentroId = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO, reqCmIdParams);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return false;
            }
            client.addRequest(requestCentroId);
            Response responseCmId = client.getResponse(requestCentroId.getRequestId());
            String centroId = responseCmId.getResult().toString();

            if(responseCmId.getResponseType() == ServerInterface.ResponseType.Error){
                resErrorAlert.showAndWait();
                return false;
            }
            if(responseCmId.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                resNoSuchElementAlert.showAndWait();
                return false;
            }

            System.out.println(centroId);
            Request signUpRequest;
            try{
                Map<String, String> requestSignUpParams = RequestFactory
                        .buildParams(ServerInterface.RequestType.executeSignUp, nomeOp, cognomeOp, codFisc, userID, email, password, centroId);
                signUpRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.executeSignUp,
                        ServerInterface.Tables.OPERATORE,
                        requestSignUpParams);
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return false;
            }
            System.out.println(signUpRequest);
            client.addRequest(signUpRequest);
            Response responseSignUp = client.getResponse(signUpRequest.getRequestId());
            System.out.println(responseSignUp);
            return (boolean) responseSignUp.getResult();
        }
    }

    @FXML
    public void handleNewConnection(){
        System.out.println("New connection");
        try{
            Stage connectionStage = new Stage();
            NewConnectionDialog connectionDialog = new NewConnectionDialog(this);
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/new-connection-scene.fxml"));
            fxmlLoader.setController(connectionDialog);
            Scene dialogScene = new Scene(fxmlLoader.load());
            connectionStage.setScene(dialogScene);
            connectionStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    @FXML
    public void handleDisconnect(){
        if(client != null){
            System.out.printf("Disconnecting from: %s:%s\n",  client.getClientProxy().getIpAddr(), client.getClientProxy().getPortNumber());
            client.getClientProxy().sendQuitRequest();
            client.setRunCondition(false);
            client = null;

            if(paramBox != null){
                paramBox.getChildren().clear();
            }
            if(tableView != null){
                tableView.getColumns().clear();
                tableView.getItems().clear();
            }

            this.clientHasDisconnected.showAndWait();
        }
        else{
            clientNotConnected.showAndWait();
        }
    }

    public void handleServerDisconnect(){
        serverHasDisconnected.showAndWait();
    }

}