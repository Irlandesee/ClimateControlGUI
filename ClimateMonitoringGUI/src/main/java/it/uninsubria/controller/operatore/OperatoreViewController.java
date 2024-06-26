package it.uninsubria.controller.operatore;

import it.uninsubria.MainWindow;
import it.uninsubria.clientCm.ClientProxy;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.datamodel.areaInteresse.AreaInteresse;
import it.uninsubria.datamodel.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.datamodel.city.City;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.dialog.*;
import it.uninsubria.controller.parametroclimatico.ParametroClimaticoController;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.datamodel.operatore.OperatoreAutorizzato;
import it.uninsubria.datamodel.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.PredefinedRequest;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import it.uninsubria.tableViewBuilder.TableViewBuilder;
import it.uninsubria.util.IDGenerator;
import it.uninsubria.util.Util;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static it.uninsubria.controller.mainscene.MainWindowController.*;

public class OperatoreViewController {


    public TableView tableView;
    public BorderPane borderPane;
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

    //update - delete buttons
    private Button buttonRimuoviAreaInteresse;
    private Button buttonRimuoviCentroMonitoraggio;
    private Button buttonRimuoviParametroClimatico;
    private Button buttonAggiornaParametroClimatico;
    private Button buttonAggiornaAreaInteresse;
    private Button buttonAggiornaCentroMonitoraggio;

    private Properties props;

    private Stage operatoreWindowStage;
    private Client client;
    private ClientProxy clientProxy;

    private final ParametroClimaticoController parametroClimaticoController;
    private final Logger logger;
    private ServerInterface.Tables tableShown;

    public OperatoreViewController(Stage operatoreWindowStage, Client client, ClientProxy clientProxy, String userId, String password) {
        this.client = client;
        this.clientProxy = clientProxy;
        this.operatoreWindowStage = operatoreWindowStage;

        int width = (int) Screen.getPrimary().getVisualBounds().getWidth();
        int height = (int) Screen.getPrimary().getVisualBounds().getHeight() - 50;
        this.operatoreWindowStage.setMinWidth(width);
        this.operatoreWindowStage.setMaxWidth(width);
        this.operatoreWindowStage.setMinHeight(height);
        this.operatoreWindowStage.setMaxHeight(height);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        this.operatoreWindowStage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - this.operatoreWindowStage.getWidth()) / 2);
        this.operatoreWindowStage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - this.operatoreWindowStage.getHeight()) / 2);

        this.operatoreWindowStage.setOnCloseRequest(e -> {
            if (client != null) {
                this.client.getClientProxy().sendQuitRequest();
                this.client.setRunCondition(false);
            }
            System.out.println("Exiting");
            Platform.exit();
        });
        this.logger = Logger.getLogger("OperatoreWindow");
        this.parametroClimaticoController = new ParametroClimaticoController(this);
        props = new Properties();
        props.put("user", userId);
        props.put("password", password);
    }

    /**
     * Gestisce la richiesta di uscita da parte dell'utente, disconnettendosi dal server, ritornando poi
     * alla schermata comune.
     *
     * @param actionEvent
     */
    @FXML
    public void exit(ActionEvent actionEvent) {

        client.getClientProxy().sendLogoutRequest();
        Stage operatoreStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        //go back to the main window stage
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/main-scene.fxml"));
            Stage mainWindowStage = new Stage();
            fxmlLoader.setController(new MainWindowController(mainWindowStage));
            Scene scene = new Scene(fxmlLoader.load(), 800, 800);
            mainWindowStage.setScene(scene);
            mainWindowStage.setTitle("ClimateMonitoringApp");
            operatoreStage.close();
            mainWindowStage.show();
        } catch (IOException ioe) {
        }
    }

    /**
     * Gestisce la richiesta di nuova connessione a un server
     */
    @FXML
    public void handleNewConnection() {
        System.out.println("New connection");
        try {
            Stage connectionStage = new Stage();
            NewConnectionDialog connectionDialog = new NewConnectionDialog(this, connectionStage);
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/new-connection-scene.fxml"));
            fxmlLoader.setController(connectionDialog);
            Scene dialogScene = new Scene(fxmlLoader.load());
            connectionStage.setScene(dialogScene);
            connectionStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Gestisce la richiesta di disconnessione dal server
     */
    @FXML
    public void handleDisconnect() {
        if (client != null) {
            System.out.printf("Disconnecting from: %s:%s\n", client.getClientProxy().getIpAddr(), client.getClientProxy().getPortNumber());
            client.getClientProxy().sendQuitRequest();
            client.setRunCondition(false);
            client = null;

            if (paramBox != null) {
                paramBox.getChildren().clear();
            }
            if (tableView != null) {
                tableView.getColumns().clear();
                tableView.getItems().clear();
            }

            clientHasDisconnected.showAndWait();
        } else {
            clientNotConnected.showAndWait();
        }
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientProxy getClientProxy() {
        return this.clientProxy;
    }

    public void setClientProxy(ClientProxy clientProxy) {
        this.clientProxy = clientProxy;
    }


    /**
     * Prepara la tabella per la visualizzazione di oggetti di tipo City
     */
    private void prepTableCity() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.CITY;

        tableView.getColumns().addAll(TableViewBuilder.getColumnsCity());
        tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryPrepTableCity(tDenominazione, tStato, tLatitudine, tLongitudine));
    }

    /**
     * Prepara la tabella per la visualizzazione di oggetti di tipo CentroMonitoraggio
     */
    private void prepTableCentroMonitoraggio() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.setRowFactory(null);
        tableView.refresh();
        tableShown = ServerInterface.Tables.CENTRO_MONITORAGGIO;
        tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());

        tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryPrepTableCentroMonitoraggio(nomeCentroField, comuneField, statoCMField));

    }

    /**
     * Prepara la tabella per la visualizzazione di oggetti di tipo Operatore
     */
    private void prepTableOperatore() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableView.setRowFactory(null);
        tableView.getColumns().addAll(TableViewBuilder.getColumnsOp());

    }

    /**
     * Set up della schermata per la visualizzazione e ricerca di AreaInteresse
     *
     * @param actionEvent
     */
    public void handleRicercaAreaInteresse(ActionEvent actionEvent) {
        if (client != null) {
            prepTableAreaInteresse();
            this.paramBox = new VBox(10);
            paramBox.getStyleClass().add("param-box");
            //denominazione, stato, latitudine, longitudine
            this.tDenominazione = new TextField("nome");
            this.tDenominazione.setOnMouseClicked((event) -> {
                this.tDenominazione.clear();
            });
            this.tStato = new TextField("stato ");
            this.tStato.setOnMouseClicked((event) -> {
                this.tStato.clear();
            });
            this.tLatitudine = new TextField("latidudine");
            this.tLatitudine.setOnMouseClicked((event) -> {
                this.tLatitudine.clear();
            });
            this.tLongitudine = new TextField("longitudine");
            this.tLongitudine.setOnMouseClicked((event) -> {
                this.tLongitudine.clear();
            });
            this.btnRicercaAreaPerDenom = new Button("Ricerca per nome");
            this.btnRicercaAreaPerStato = new Button("Ricerca per stato");
            this.btnRicercaAreaCoord = new Button("Ricerca Coord");

            this.btnRicercaAreaPerDenom.setOnAction(event -> {
                handleRicercaAreaPerDenominazione();
            });
            this.btnRicercaAreaPerStato.setOnAction(event -> {
                handleRicercaAreaPerStato();
            });
            this.btnRicercaAreaCoord.setOnAction(event -> {
                handleRicercaAreaPerCoordinate();
            });
            paramBox.getChildren().add(tDenominazione);
            paramBox.getChildren().add(tStato);
            paramBox.getChildren().add(tLatitudine);
            paramBox.getChildren().add(tLongitudine);
            paramBox.getChildren().add(btnRicercaAreaPerDenom);
            paramBox.getChildren().add(btnRicercaAreaPerStato);
            paramBox.getChildren().add(btnRicercaAreaCoord);
            this.borderPane.setRight(paramBox);
        } else {
            clientNotConnected.showAndWait();
        }

    }

    /**
     * Prepara la tabella per la visualizzazione di oggetti di tipo AreaInteresse
     */
    private void prepTableAreaInteresse() {
        if (paramBox != null && !paramBox.getChildren().isEmpty())
            paramBox.getChildren().clear();
        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.AREA_INTERESSE;

        tableView.getColumns().addAll(TableViewBuilder.getColumnsAi());

        tableView.setRowFactory(tv -> TableViewBuilder.getRowAi(client));
        tableView.refresh(); //forces the tableview to refresh the listeners
    }


    private void showAreeInserite() {
        Request request = null;
        try {
            request = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.AREA_INTERESSE,
                    null);//select all does not need parameters
        } catch (MalformedRequestException mre) {
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        client.addRequest(request);
        //get response
        Response response = client.getResponse();

        if (response.getResponseType() == ServerInterface.ResponseType.Error) {
            resErrorAlert.showAndWait();
            return;
        }

        List<AreaInteresse> res = (List<AreaInteresse>) response.getResult();
        res.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
    }


    /**
     * Gestisce la ricerca di oggetti di tipo AreaInteresse tramite denominazione
     */
    private void handleRicercaAreaPerDenominazione() {
        tableView.getItems().clear();
        String denom = this.tDenominazione.getText();
        if (!denom.isEmpty() && !(denom.equals("nome"))) {
            try {
                Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "denominazione", denom);
                Request request = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        params);
                client.addRequest(request);
                System.out.println("Build request: " + request);
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            Response response = client.getResponse();
            if (response.getResponseType() == ServerInterface.ResponseType.Error || response.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                resNoSuchElementAlert.showAndWait();
            } else {
                List<AreaInteresse> areeInteresseRichieste = (List<AreaInteresse>) response.getResult();
                areeInteresseRichieste.forEach((areaInteresse -> {
                    tableView.getItems().add(areaInteresse);
                }));
            }

        } else {
            denomAlert.showAndWait();
        }
    }

    /**
     * Gestisce la ricerca di oggetti di tipo AreaInteresse tramite Stato
     */
    private void handleRicercaAreaPerStato() {
        tableView.getItems().clear();
        String stato = this.tStato.getText();
        if (!stato.isEmpty() && !(stato.equals("stato"))) {
            try {
                Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "stato", stato);
                Request request = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        params);
                client.addRequest(request);
            } catch (MalformedRequestException mre) {
                logger.info(mre.getMessage());
            }

            Response response = client.getResponse();

            if (response.getResponseType() == ServerInterface.ResponseType.Error
                    || response.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                resNoSuchElementAlert.showAndWait();
            } else {
                List<AreaInteresse> queryResult = (List<AreaInteresse>) response.getResult();
                queryResult.removeIf(areaInteresse -> !areaInteresse.getStato().equals(stato));
                queryResult.forEach(areaInteresse -> tableView.getItems().add(areaInteresse));
            }

        } else {
            statoAlert.showAndWait();
        }
    }

    /**
     * Gestisce la ricerca di oggetti di tipo AreaInteresse tramite coordinate
     */
    private void handleRicercaAreaPerCoordinate() {
        String longi = this.tLongitudine.getText();
        String lati = this.tLatitudine.getText();
        String query;
        if ((longi.isEmpty() || lati.isEmpty()) ||
                (longi.equals("longitudine") || lati.equals("latitudine"))) {
            coordAlert.showAndWait();
        } else {
            float lo = Float.parseFloat(longi);
            float la = Float.parseFloat(lati);

            try {
                Request request = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectAll,
                        ServerInterface.Tables.AREA_INTERESSE,
                        null);
                client.addRequest(request);
            } catch (MalformedRequestException mre) {
                logger.info(mre.getMessage());
            }
            List<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
            Response response = client.getResponse();
            if (response.getResponseType() == ServerInterface.ResponseType.Error ||
                    response.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                resNoSuchElementAlert.showAndWait();
            } else {
                areeInteresse = (LinkedList<AreaInteresse>) response.getResult();
                List<AreaInteresse> areeVicine = new LinkedList<AreaInteresse>();
                areeInteresse.forEach(area -> {
                    float distance = Util.haversineDistance(lo, la, area.getLongitudine(), area.getLatitudine());
                    //50 km
                    if (distance < 50) areeVicine.add(area);
                });
                tableView.getItems().clear();
                areeVicine.forEach(area -> tableView.getItems().add(area));

            }

        }
    }

    /**
     * Prepara la tabella per la visualizzazione di oggetti di tipo ParametroClimatico
     */
    private void prepTableParamClimatici() {
        System.out.println("preparo tabella per parametri climatici");
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.refresh();
        tableShown = ServerInterface.Tables.PARAM_CLIMATICO;

        tableView.getColumns().add(TableViewBuilder.getDateColumn());
        tableView.getColumns().addAll(TableViewBuilder.getColumnsPc());
        tableView.setRowFactory(tv -> TableViewBuilder.getRowPc(client));
        tableView.refresh(); //forces the tableview to refresh the listeners
    }

    /**
     * @param actionEvent Set up della schermata per la visualizzazione di oggetti di tipo ParametroClimatico
     */
    public void handleVisualizzaParametriClimatici(ActionEvent actionEvent) {
        if (client != null) {
            tableView.getColumns().clear();
            tableView.getItems().clear();
            prepTableParamClimatici();

            if (paramBox != null && !paramBox.getChildren().isEmpty()) paramBox.getChildren().clear();
            this.paramBox = new VBox(10);
            this.paramBox.getStyleClass().add("param-box");
            this.tAreaInteresse = new TextField("AreaInteresse");
            this.tAreaInteresse.setOnMouseClicked((event) -> this.tAreaInteresse.clear());
            this.tglDatePicker = new ToggleButton("Ricerca con data");
            this.startDatePicker = new DatePicker();
            this.endDatePicker = new DatePicker();
            this.btnRicercaPcArea = new Button("Ricerca per area");
            this.btnRicercaPcArea.setOnAction(this::handleRicercaPc);

            paramBox.getChildren().add(tAreaInteresse);
            paramBox.getChildren().add(tglDatePicker);
            paramBox.getChildren().add(startDatePicker);
            paramBox.getChildren().add(endDatePicker);
            paramBox.getChildren().add(btnRicercaPcArea);

            this.borderPane.setRight(paramBox);

        } else {
            clientNotConnected.showAndWait();
        }

    }

    /**
     * @param event Gestisce la ricerca di oggetti ParametroClimatico
     */
    private void handleRicercaPc(ActionEvent event) {
        String denomAiCercata = tAreaInteresse.getText();
        LocalDate canonicalStartDate = LocalDate.of(1900, 1, 1);
        LocalDate canonicalEndDate = LocalDate.of(2100, 1, 1);
        boolean ricercaPerData = false;
        LocalDate startDate = canonicalStartDate;
        LocalDate endDate = canonicalEndDate;

        if (tglDatePicker.isSelected()) {
            if (startDatePicker.getValue() == null && endDatePicker.getValue() == null) {
                invalidDateAlert.show();
            }
            startDate = startDatePicker.getValue();
            endDate = endDatePicker.getValue();
            if (startDate.isBefore(canonicalStartDate)
                    || endDate.isAfter(canonicalEndDate)
                    || startDate.isEqual(endDate)) {
                invalidDateAlert.show();
            }
            ricercaPerData = true;
        }

        //ricerca area
        if (event.getSource().equals(btnRicercaPcArea)) {
            if (denomAiCercata.isEmpty() || denomAiCercata.equals("AreaInteresse")) {
                areaInteresseAlert.showAndWait();
            } else {
                try {
                    Map<String, String> reqAreaIdParams = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectObjWithCond, "areaid", "denominazione", denomAiCercata);
                    Request requestAreaId = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectObjWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            reqAreaIdParams);
                    client.addRequest(requestAreaId);
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                Response resAreaId = client.getResponse();

                if (resAreaId.getResponseType() == ServerInterface.ResponseType.Error) {
                    resErrorAlert.showAndWait();
                    return;
                }
                if (resAreaId.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                    resNoSuchElementAlert.showAndWait();
                    return;
                }

                String areaInteresseId = resAreaId.getResult().toString();

                Request requestParamClimatici = null;
                try {
                    Map<String, String> reqParamClimatici = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", areaInteresseId);
                    requestParamClimatici = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            reqParamClimatici
                    );
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                client.addRequest(requestParamClimatici);
                Response responseParametriClimatici = client.getResponse();

                if (responseParametriClimatici.getResponseType() == ServerInterface.ResponseType.Error) {
                    resErrorAlert.showAndWait();
                    return;
                }
                if (responseParametriClimatici.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                    resNoSuchElementAlert.showAndWait();
                    return;
                }

                List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>) responseParametriClimatici.getResult();
                tableView.getItems().clear();
                if (ricercaPerData) {
                    LocalDate finalStartDate = startDate;
                    LocalDate finalEndDate = endDate;
                    parametriClimatici.removeIf((pc) -> Util.isBetweenDates(finalStartDate, finalEndDate, pc.getPubDate()));
                }
                parametriClimatici.forEach((pc) -> {
                    System.out.println(pc);
                    tableView.getItems().add(pc);
                });
            }
        }
    }

    /**
     * Gestisce l'inserimento di una nuova area d'interesse
     */
    @FXML
    public void handleInserisciAreaInteresse() {
        if (client != null) {
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

        } else {
            resErrorAlert.showAndWait();
        }
    }

    /**
     * Gestisce la richiesta di visualizzazione di oggetti di tipo CentroMonitoraggio
     *
     * @param event
     */
    private void visualizeCmData(ActionEvent event) {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.setRowFactory(null);
        tableView.refresh();
        Request cmRequest;
        String country = tFilterCountry.getText();
        if (country.isEmpty()) {
            try {
                cmRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectAll,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        null
                );
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                return;
            }
        } else {
            try {
                Map<String, String> cmParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "country", country);
                cmRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        cmParams
                );
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                return;
            }
        }
        client.addRequest(cmRequest);
        Response response = client.getResponse();

        if (response.getResponseType() == ServerInterface.ResponseType.Error) {
            resErrorAlert.showAndWait();
            return;
        }
        if (response.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
            resNoSuchElementAlert.showAndWait();
            return;
        }

        List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) response.getResult();
        tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());
        centri.forEach(centro -> tableView.getItems().add(centro));
    }

    /**
     * @param event Gestisce la visualizzazione di oggetti di tipo City, CentroMonitoraggio e AreaInteresse
     */
    private void visualizeData(ActionEvent event) {
        tableView.getItems().clear();
        Request request;
        Request requestAi;
        Object source = event.getSource();
        if (source == visualizeCityData) {
            if (tableShown != ServerInterface.Tables.CITY) {
                prepTableCity();
            }
            if (tFilterCountry.getText().isEmpty() || tFilterCountry.getText().equals("Filtra per stato")) {
                try {
                    request = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CITY,
                            null);
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
            } else {
                try {
                    String statoDaFiltrare = tFilterCountry.getText();
                    Map<String, String> params = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "country", statoDaFiltrare);
                    request = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.CITY,
                            params);
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(request);
                Response responseCity = client.getResponse();
                String statoDaFiltrare = tFilterCountry.getText();
                try {
                    Map<String, String> paramsAiRequest = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "stato", statoDaFiltrare);
                    requestAi = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            paramsAiRequest
                    );
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(requestAi);
                Response responseAi = client.getResponse();
                List<City> cities = (List<City>) responseCity.getResult();
                List<AreaInteresse> areeInteresse = (List<AreaInteresse>) responseAi.getResult();
                for (AreaInteresse area : areeInteresse) {
                    cities = cities
                            .stream()
                            .filter(city -> !city.getAsciiName().equals(area.getDenominazione()))
                            .collect(Collectors.toList());
                }
                cities.forEach(city -> tableView.getItems().add(city));
            }
        } else if (source == visualizeAiData) {
            if (tableShown != ServerInterface.Tables.AREA_INTERESSE) {
                prepTableAreaInteresse();
            }
            if (tFilterCountry.getText().isEmpty() || tFilterCountry.getText().equals("Filtra per stato")) {
                try {
                    request = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.AREA_INTERESSE,
                            null
                    );
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
            } else {
                String statoDaFiltrare = tFilterCountry.getText();
                try {
                    Map<String, String> paramsCityRequest = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "stato", statoDaFiltrare);
                    request = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.AREA_INTERESSE,
                            paramsCityRequest
                    );
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;

                }
                client.addRequest(request);
                Response responseAreaInteresse = client.getResponse();
                List<AreaInteresse> areeInteresse = (List<AreaInteresse>) responseAreaInteresse.getResult();
                areeInteresse.forEach(ai -> tableView.getItems().add(ai));
            }
        } else if (source == visualizeCmData) {
            prepTableCity();
            tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryVisualizeCmData(nomeCentroField, comuneField, statoCMField));
            if (tFilterCountry.getText().isEmpty() || tFilterCountry.getText().equals("Filtra per stato")) {
                Request citiesRequest;
                Request centriMonitoraggioRequest;
                try {
                    citiesRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CITY,
                            null);
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage());
                    return;
                }

                client.addRequest(citiesRequest);
                Response responseCities = client.getResponse();

                try {
                    centriMonitoraggioRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            null
                    );

                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(centriMonitoraggioRequest);
                Response responseCentriMonitoraggio = client.getResponse();

                List<City> citiesWithoutCm = new LinkedList<City>();
                List<City> cities = (List<City>) responseCities.getResult();
                List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>) responseCentriMonitoraggio.getResult();
                for (CentroMonitoraggio cm : centriMonitoraggio) {
                    citiesWithoutCm = cities.stream().filter(city -> !city.getAsciiName().equals(cm.getComune())).collect(Collectors.toList());
                }

                citiesWithoutCm.forEach(city -> tableView.getItems().add(city));
            } else {
                Request centriMonitoraggioRequest;
                try {
                    centriMonitoraggioRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            null
                    );

                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(centriMonitoraggioRequest);
                Response responseCentriMonitoraggio = client.getResponse();
                List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>) responseCentriMonitoraggio.getResult();
                String statoDaFiltrare = tFilterCountry.getText();
                try {
                    Map<String, String> params = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "country", statoDaFiltrare);
                    request = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.CITY,
                            params);
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    return;
                }
                client.addRequest(request);
                Response responseCity = client.getResponse();
                List<City> cities = (List<City>) responseCity.getResult();
                List<City> citiesWithoutCm = new LinkedList<City>();
                for (CentroMonitoraggio cm : centriMonitoraggio) {
                    citiesWithoutCm = cities.stream().filter(city -> !city.getAsciiName().equals(cm.getComune())).collect(Collectors.toList());
                }

                citiesWithoutCm.forEach(city -> tableView.getItems().add(city));

            }
        }
    }

    /**
     * Gestisce l'inserimento di una nuova area interesse
     *
     * @param event
     */
    private void executeInsertAreaInteresse(ActionEvent event) {
        String denom = tDenominazione.getText();
        String stato = tStato.getText();
        String latitudine = tLatitudine.getText();
        String longitudine = tLongitudine.getText();

        if (denom.isEmpty() || denom.equals("Denominazione")) {
            new Alert(Alert.AlertType.ERROR, "Denominazione non valida!");
        } else if (stato.isEmpty() || stato.equals("Stato")) {
            new Alert(Alert.AlertType.ERROR, "Stato non valido!");
        } else if (latitudine.isEmpty() || latitudine.equals("Latitudine")) {
            new Alert(Alert.AlertType.ERROR, "Latitudine non valida!");
        } else if (longitudine.isEmpty() || longitudine.equals("Longitudine")) {
            new Alert(Alert.AlertType.ERROR, "Longitudine non valida!");
        }

        Map<String, String> insertParams;
        Request insertRequest;
        try {
            insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.AREA_INTERESSE,
                    IDGenerator.generateID(), denom, stato, latitudine, longitudine);
            insertRequest = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.AREA_INTERESSE,
                    insertParams
            );
        } catch (MalformedRequestException mre) {
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            return;
        }
        client.addRequest(insertRequest);
        Response insertResponse = client.getResponse();
        if (insertResponse.getResponseType() == ServerInterface.ResponseType.Error) {
            resErrorAlert.showAndWait();
            return;
        }

        Object obj = insertResponse.getResult();
        if (obj instanceof String) {
            new Alert(Alert.AlertType.ERROR, ServerInterface.DUPLICATE_ITEM).showAndWait();
        } else {
            boolean res = (boolean) insertResponse.getResult();
            if (res) {
                new Alert(Alert.AlertType.CONFIRMATION, "Inserimento avvenuto con successo.").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Fallimento dell'operazione!").showAndWait();
            }
        }
        tDenominazione.setText("Denominazione");
        tStato.setText("Stato");
        tLatitudine.setText("Latitudine");
        tLongitudine.setText("Longitudine");
    }

    /**
     * Set up della schermata per l'inserimento di un nuovo parametro climatico
     *
     * @param actionEvent
     */
    @FXML
    public void handleInserisciParametriClimatici(ActionEvent actionEvent) {
        if (client != null) {
            paramBox = new VBox();
            paramBox.getStyleClass().add("param-box");
            tableView.getColumns().clear();
            tableView.getItems().clear();
            tableView.getColumns().add(TableViewBuilder.getDateColumn());
            tableView.getColumns().addAll(TableViewBuilder.getColumnsPc());
            tableView.setRowFactory(tv -> TableViewBuilder.getRowPc(client));

            TextField tRicercaArea = new TextField("Denominazione area");
            tRicercaArea.setOnMouseClicked(e -> tRicercaArea.clear());
            Button bRicercaPcArea = new Button("Ricerca");
            bRicercaPcArea.setOnAction(e -> {
                        String denom = tRicercaArea.getText();
                        if (denom.equals("Denominazione area") || denom.isEmpty()) {
                            new Alert(Alert.AlertType.ERROR, "Denominazione non valida!").showAndWait();
                        } else {
                            try {
                                Map<String, String> reqAreaIdParams = RequestFactory.buildParams(
                                        ServerInterface.RequestType.selectObjWithCond,
                                        "areaid",
                                        "denominazione",
                                        denom);
                                Request areaIdRequest = RequestFactory.buildRequest(
                                        client.getHostName(),
                                        ServerInterface.RequestType.selectObjWithCond,
                                        ServerInterface.Tables.AREA_INTERESSE,
                                        reqAreaIdParams
                                );
                                client.addRequest(areaIdRequest);
                            } catch (MalformedRequestException mre) {
                                logger.info(mre.getMessage());
                            }
                            Response areaIdResponse = client.getResponse();
                            if (areaIdResponse.getResponseType() == ServerInterface.ResponseType.Error ||
                                    areaIdResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                                resNoSuchElementAlert.showAndWait();
                            } else {
                                String areaId = areaIdResponse.getResult().toString();
                                try {
                                    Map<String, String> reqParams = RequestFactory.buildParams(
                                            ServerInterface.RequestType.selectAllWithCond, "areaid", areaId);
                                    Request pcRequest = RequestFactory.buildRequest(
                                            client.getHostName(),
                                            ServerInterface.RequestType.selectAllWithCond,
                                            ServerInterface.Tables.PARAM_CLIMATICO,
                                            reqParams
                                    );
                                    client.addRequest(pcRequest);
                                } catch (MalformedRequestException mre) {
                                    logger.info(mre.getMessage());

                                }
                                Response pcResponse = client.getResponse();
                                if (pcResponse.getResponseType() == ServerInterface.ResponseType.Error
                                        || pcResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                                    resNoSuchElementAlert.showAndWait();
                                } else {
                                    List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>) pcResponse.getResult();
                                    parametriClimatici.forEach(pc -> tableView.getItems().add(pc));
                                }
                            }

                        }

                    }
            );

            this.paramBox.getChildren().add(tRicercaArea);
            this.paramBox.getChildren().add(bRicercaPcArea);
            this.borderPane.setRight(paramBox);

            try {
                FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("fxml/parametro_climatico-scene.fxml"));
                loader.setController(parametroClimaticoController);
                Stage pcStage = new Stage();
                Scene scene = new Scene(loader.load(), 800, 400);
                pcStage.setScene(scene);
                pcStage.show();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } else {
            clientNotConnected.showAndWait();
        }

    }


    /**
     * Gestisce l'inserimento del parametro climatico
     *
     * @param parameterId
     * @param nomeArea
     * @param centroMon
     * @param pubdate
     * @param paramValues
     * @param notaId
     * @param notaInsertParams
     */
    public void executeInsertPCQuery(String parameterId, String nomeArea, String centroMon, LocalDate pubdate, Map<String, String> paramValues, String notaId, Map<String, String> notaInsertParams) {
        paramValues.forEach((key, value) -> {
            logger.info(key + ": " + value);
        });

        try {
            Map<String, String> reqAreaIdParams = RequestFactory
                    .buildParams(
                            ServerInterface.RequestType.selectObjWithCond,
                            "areaid",
                            "denominazione",
                            nomeArea);
            Request requestAreaId = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    reqAreaIdParams);
            client.addRequest(requestAreaId);
        } catch (MalformedRequestException mre) {
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
        }

        Response respAreaId = client.getResponse();

        if (respAreaId.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
            resNoSuchElementAlert.showAndWait();
            return;
        }
        if (respAreaId.getResponseType() == ServerInterface.ResponseType.Error) {
            resErrorAlert.showAndWait();
            return;
        }

        try {
            Map<String, String> reqCentroIdParams = RequestFactory
                    .buildParams(
                            ServerInterface.RequestType.selectObjWithCond,
                            "centroid",
                            "nomecentro",
                            centroMon);
            Request requestCentroId = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    reqCentroIdParams
            );

            client.addRequest(requestCentroId);
        } catch (MalformedRequestException mre) {
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
        }
        Response respCentroId = client.getResponse();
        if (respCentroId.getResponseType() == ServerInterface.ResponseType.Error) {
            resErrorAlert.showAndWait();
            return;
        }
        if (respCentroId.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
            resNoSuchElementAlert.showAndWait();
            return;
        }

        String areaId = respAreaId.getResult().toString();
        String centroId = respCentroId.getResult().toString();

        notaInsertParams.put(RequestFactory.notaIdKey, notaId);
        try {
            Request insertNotaRequest = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.NOTA_PARAM_CLIMATICO,
                    notaInsertParams);
            client.addRequest(insertNotaRequest);
        } catch (MalformedRequestException mre) {
            logger.info(mre.getMessage());
        }
        Response responseNota = client.getResponse();
        if (responseNota.getResponseType() == ServerInterface.ResponseType.Error ||
                responseNota.getResponseType() == ServerInterface.ResponseType.insertKo) {
            new Alert(Alert.AlertType.ERROR, "errore inserimento nota").showAndWait();
        }

        try {
            Map<String, String> insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.PARAM_CLIMATICO,
                    parameterId, centroId, areaId, pubdate.toString(), notaId,
                    paramValues.get(RequestFactory.valoreVentoKey),
                    paramValues.get(RequestFactory.valoreUmiditaKey),
                    paramValues.get(RequestFactory.valorePressioneKey),
                    paramValues.get(RequestFactory.valorePrecipitazioniKey),
                    paramValues.get(RequestFactory.valoreTemperaturaKey),
                    paramValues.get(RequestFactory.valoreAltGhiacciaiKey),
                    paramValues.get(RequestFactory.valoreMassaGhiacciaiKey));
            Request insertPcRequest = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.PARAM_CLIMATICO,
                    insertParams
            );
            client.addRequest(insertPcRequest);
        } catch (MalformedRequestException mre) {
            logger.info(mre.getMessage());
        }
        Response insertPcResponse = client.getResponse();

        if (
                insertPcResponse.getResponseType() == ServerInterface.ResponseType.Error
                        || insertPcResponse.getResponseType() == ServerInterface.ResponseType.insertKo) {
            new Alert(Alert.AlertType.ERROR, "Errore inserimento del parametro").showAndWait();
        } else {
            if ((boolean) insertPcResponse.getResult()) {
                new Alert(Alert.AlertType.CONFIRMATION, "Successo nell'inserimento!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Errore inserimento del parametro").showAndWait();
            }
        }
    }

    /**
     * Set up della schermata per la visualizzazione di oggetti di tipo CentroMonitoraggio
     *
     * @param actionEvent
     */
    public void handleInserisciCentroMonitoraggio(ActionEvent actionEvent) {
        if (client != null) {
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

        } else {
            clientNotConnected.showAndWait();
        }

    }

    private void addAreaToBox() {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/update-centro-dialog.fxml"));
        UpdateCentroDialog addAreaController = new UpdateCentroDialog(this);
        fxmlLoader.setController(addAreaController);
        try {
            Scene scene = new Scene(fxmlLoader.load(), 400, 600);
            stage.setTitle("Aggiungi area a centro");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Gestisce l'esecuzione della richiesta d'inserimento di un nuovo CentroMonitoraggio
     *
     * @param nomeCentro
     * @param comuneCentro
     * @param statoCentro
     * @param areeAssociate
     */
    public void executeInsertCMQuery(String nomeCentro, String comuneCentro, String statoCentro, String areeAssociate) {

        List<String> l = new LinkedList<String>();
        for (String nome : areeAssociate.split("\n")) {
            l.add(nome.trim());
        }

        StringBuilder areaList = new StringBuilder();
        if (l.size() == 1) {
            areaList.append(l.get(0));
        } else {
            for (int i = 0; i < l.size(); i++) {
                if (i == l.size() - 1)
                    areaList.append(l.get(i));
                else
                    areaList.append(l.get(i)).append(",");
            }
        }
        //Controlla che il comune inserito sia associato allo stato corretto

        try {
            Map<String, String> comuneParams = RequestFactory
                    .buildParams(ServerInterface.RequestType.selectAllWithCond, "ascii_name", comuneCentro);
            Request reqStato = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.selectAllWithCond,
                    ServerInterface.Tables.CITY,
                    comuneParams);

            client.addRequest(reqStato);
        } catch (MalformedRequestException mre) {
            logger.info(mre.getMessage());
        }
        Response responseStato = client.getResponse();
        List<City> cities = (List<City>) responseStato.getResult();
        System.out.println(cities.size());
        if (cities.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Stato non corrisponde").showAndWait();
        } else {
            City c = cities.get(0);
            System.out.println(c);
            if (!c.getCountry().equals(statoCentro)) {
                new Alert(Alert.AlertType.CONFIRMATION, "Stato non corrisponde").showAndWait();
                return;
            }
        }

        try {
            Map<String, String> insertParams = RequestFactory.buildInsertParams(ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    IDGenerator.generateID(), nomeCentro, comuneCentro, statoCentro, areaList.toString());
            Request insertCmRequest = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.insert,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                    insertParams);
            client.addRequest(insertCmRequest);
        } catch (MalformedRequestException mre) {
            logger.info(mre.getMessage());
        }
        Response res = client.getResponse();
        boolean result = (boolean) res.getResult();
        if (result) {
            new Alert(Alert.AlertType.CONFIRMATION, "inserimento completato").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "errore nell'inserimento ").showAndWait();
        }
    }

    /**
     * Set up della schermata per l'abilitazione di un nuovo operatore
     *
     * @param actionEvent
     */
    public void handleAbilitaNuovoOperatore(ActionEvent actionEvent) {
        if (client != null) {

            tableView.getColumns().clear();
            tableView.getItems().clear();
            tableView.refresh();

            tEmailField = new TextField("email");
            tCodFiscField = new TextField("codice fiscale");
            buttonAbilitaOp = new Button("Abilita");
            buttonAbilitaOp.setOnAction(e -> executeAbilitaNuovoOperatore());

            tableView.getColumns().addAll(TableViewBuilder.getColumnsOp());
            tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryOpAbilitaOperatore(tEmailField, tCodFiscField));

            paramBox = new VBox();
            paramBox.getStyleClass().add("param-box");
            paramBox.getChildren().add(tEmailField);
            paramBox.getChildren().add(tCodFiscField);
            paramBox.getChildren().add(buttonAbilitaOp);

            this.borderPane.setRight(paramBox);

            try {
                Request opAutorizzatiRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectAll,
                        ServerInterface.Tables.OP_AUTORIZZATO,
                        null
                );
                client.addRequest(opAutorizzatiRequest);
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
            }

            Response response = client.getResponse();
            List<OperatoreAutorizzato> opAutorizzati = (List<OperatoreAutorizzato>) response.getResult();

            opAutorizzati.forEach(op -> tableView.getItems().add(op));

        } else {
            clientNotConnected.showAndWait();
        }

    }

    /**
     * Gestisce la richiesta di inserimento di un nuovo operatore
     */
    private void executeAbilitaNuovoOperatore() {
        String email = tEmailField.getText();
        String codFisc = tCodFiscField.getText();
        if (email.isEmpty() || codFisc.isEmpty() || email.equals("email") || codFisc.equals("codice fiscale")) {
            new Alert(Alert.AlertType.ERROR, "Campo non valido").showAndWait();
        } else {
            try {
                Map<String, String> insertAuthOpParams = RequestFactory.buildInsertParams(ServerInterface.Tables.OP_AUTORIZZATO, email, codFisc);
                Request insertAuthOpRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.insert,
                        ServerInterface.Tables.OP_AUTORIZZATO,
                        insertAuthOpParams);
                client.addRequest(insertAuthOpRequest);
            } catch (MalformedRequestException mre) {
                logger.info(mre.getMessage());
            }
            Response res = client.getResponse();
            if (res.getResponseType() == ServerInterface.ResponseType.Error) {
                resErrorAlert.showAndWait();
                return;
            }
            if (res.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                resNoSuchElementAlert.showAndWait();
                return;
            }
            if ((boolean) res.getResult()) {
                new Alert(Alert.AlertType.CONFIRMATION, "L'inserimento ha avuto successo").showAndWait();
                try {
                    Request opAutorizzatiRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAll,
                            ServerInterface.Tables.OP_AUTORIZZATO,
                            null
                    );
                    client.addRequest(opAutorizzatiRequest);
                } catch (MalformedRequestException mre) {
                    logger.info(mre.getMessage());
                }

                Response response = client.getResponse();
                List<OperatoreAutorizzato> opAutorizzati = (List<OperatoreAutorizzato>) response.getResult();
                tableView.getItems().clear();
                opAutorizzati.forEach(op -> tableView.getItems().add(op));

            } else {
                new Alert(Alert.AlertType.ERROR, "L'inserimento non ha avuto successo").showAndWait();
            }
        }

    }


    /**
     * Set up della schermata per la visualizzazione di grafici
     *
     * @param event
     */
    @FXML
    public void handleVisualizzaGrafici(ActionEvent event) {
        if (client != null) {
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

        } else {
            clientNotConnected.showAndWait();
        }


    }

    /**
     * Richiesta dati e creazione della finestra per la visualizzazione grafici per l'area richiesta
     */
    private void createChart() {
        String nomeArea = tAreaInteresse.getText();
        if (nomeArea.isEmpty() || nomeArea.equals("Nome Area")) {
            new Alert(Alert.AlertType.ERROR, "nome area non valido").showAndWait();
            return;
        }
        System.out.println("Creating chart for" + nomeArea);
        try {
            Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond,
                    "areaid", "denominazione", nomeArea);
            Request request = RequestFactory.buildRequest(
                    client.getHostName(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    params
            );
            client.addRequest(request);
        } catch (MalformedRequestException mre) {
            logger.info(mre.getMessage());
        }
        Response response = client.getResponse();
        String areaId = "";
        if (response.getResponseType() == ServerInterface.ResponseType.Error ||
                response.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
            new Alert(Alert.AlertType.ERROR, response.getResponseType().label).showAndWait();
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/graph-dialog.fxml"));
                fxmlLoader.setController(new GraphDialog(client, areaId));
                Stage chartStage = new Stage();
                Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
                chartStage.setScene(scene);
                chartStage.show();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }


    }

    /**
     * Gestisce la richiesta di visualizzazione di oggetti di tipo CentroMonitoraggio
     */
    @FXML
    public void handleVisualizzaCentri() {
        if (client != null) {
            if (paramBox != null && !paramBox.getChildren().isEmpty())
                paramBox.getChildren().clear();

            tableView.getColumns().clear();
            tableView.getItems().clear();
            tableView.refresh();

            Request requestCentro;
            try {
                requestCentro = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectAll,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        null);
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage());
                mre.printStackTrace();
                return;
            }
            client.addRequest(requestCentro);
            Response responseCentriMonitoraggio = client.getResponse();

            List<CentroMonitoraggio> centriMonitoraggio = (List<CentroMonitoraggio>) responseCentriMonitoraggio.getResult();

            tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());

            centriMonitoraggio.forEach(cm -> {
                tableView.getItems().add(cm);
            });

            tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryHandleVisualizzaCentri(client));

            tableView.refresh();

        } else {
            clientNotConnected.showAndWait();
        }

    }

    /**
     * Gestisce la richiesta di eliminazione di AreaInteresse
     *
     * @param denominazioneAreaDaRimuovere
     */
    private void rimuoviAreaPerDenom(String denominazioneAreaDaRimuovere) {
        if (denominazioneAreaDaRimuovere.isEmpty() || denominazioneAreaDaRimuovere.equals("Nome")) {
            new Alert(Alert.AlertType.ERROR, "Denominazione non valida").showAndWait();
        } else {
            logger.info("Removing: " + denominazioneAreaDaRimuovere);
            try {
                Map<String, String> requestParams = RequestFactory.buildParams(
                        ServerInterface.RequestType.selectObjWithCond,
                        "areaid",
                        "denominazione",
                        denominazioneAreaDaRimuovere);
                Request areaIdRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectObjWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        requestParams);
                client.addRequest(areaIdRequest);
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            }
            Response areaIdResponse = client.getResponse();
            if ((areaIdResponse.getResponseType() == ServerInterface.ResponseType.Error)
                    || (areaIdResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement)) {
                resNoSuchElementAlert.showAndWait();
            } else {
                String areaId = areaIdResponse.getResult().toString();
                try {
                    Map<String, String> deleteParams = RequestFactory.buildDeleteParams(
                            ServerInterface.Tables.AREA_INTERESSE, areaId);
                    Request deleteRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.executeDelete,
                            ServerInterface.Tables.AREA_INTERESSE,
                            deleteParams);
                    logger.info(deleteRequest.toString());
                    client.addRequest(deleteRequest);
                } catch (MalformedRequestException mre) {
                    logger.info(mre.getMessage());
                }
                Response deleteResponse = client.getResponse();
                if (deleteResponse.getResponseType() == ServerInterface.ResponseType.deleteOk) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Elemento eliminato con successo").showAndWait();
                    tableView.getItems().clear();
                    showAreeInserite();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Fallimento dell'operazione!").showAndWait();
                }
            }
        }
    }

    /**
     * Set up della schermata per la rimozione di AreaInteresse
     *
     * @param event
     */
    @FXML
    public void handleRimuoviAreaInteresse(ActionEvent event) {
        if (client != null) {
            if (paramBox != null && !paramBox.getChildren().isEmpty())
                paramBox.getChildren().clear();
            this.paramBox = new VBox(2);
            paramBox.getStyleClass().add("param-box");
            TextField tDenomAreaDaRimuovere = new TextField("Denominazione area da rimuovere");
            tDenomAreaDaRimuovere.setOnMouseClicked(e -> tDenomAreaDaRimuovere.clear());
            Button btnRimuoviArea = new Button("Rimuovi Area");
            btnRimuoviArea.setOnAction(e -> rimuoviAreaPerDenom(tDenomAreaDaRimuovere.getText()));
            paramBox.getChildren().add(tDenomAreaDaRimuovere);
            paramBox.getChildren().add(btnRimuoviArea);

            //Set up tableView
            tableView.getItems().clear();
            tableView.getColumns().clear();
            tableView.getColumns().addAll(TableViewBuilder.getColumnsAi());
            tableView.setRowFactory(tv -> {
                TableRow row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && (!row.isEmpty())) {
                        AreaInteresse ai = (AreaInteresse) row.getItem();
                        tDenomAreaDaRimuovere.setText(ai.getDenominazione());
                    }
                });
                return row;
            });
            tableView.refresh();

            showAreeInserite();
            this.borderPane.setRight(paramBox);

        } else {
            clientNotConnected.showAndWait();
        }
    }

    /**
     * Gestisce la rimozione di CentroMonitoraggio tramite denominazione
     *
     * @param nomeCentroDaRimuovere
     */
    private void rimuoviCentroPerDenom(String nomeCentroDaRimuovere) {
        if (nomeCentroDaRimuovere.isEmpty() || nomeCentroDaRimuovere.equals("Nome")) {
            new Alert(Alert.AlertType.ERROR, "Denominazione non valida").showAndWait();
        } else {
            try {
                Map<String, String> requestParams = RequestFactory.buildParams(
                        ServerInterface.RequestType.selectObjWithCond,
                        "centroid",
                        "nomecentro",
                        nomeCentroDaRimuovere);
                Request centroIdRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectObjWithCond,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        requestParams);
                client.addRequest(centroIdRequest);
            } catch (MalformedRequestException mre) {
                logger.info(mre.getMessage());
            }
            Response centroIdResponse = client.getResponse();
            if ((centroIdResponse.getResponseType() == ServerInterface.ResponseType.Error) ||
                    (centroIdResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement)) {
                resNoSuchElementAlert.showAndWait();
            } else {
                String centroId = centroIdResponse.getResult().toString();
                logger.info("CentroID: " + centroId);
                //check parametri climatici
                try {
                    Map<String, String> pcParams = RequestFactory.buildParams(
                            ServerInterface.RequestType.selectAllWithCond,
                            "centroid", centroId);
                    Request pcRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            pcParams);
                    client.addRequest(pcRequest);
                    Response pcResponse = client.getResponse();

                    if (pcResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                        new Alert(Alert.AlertType.ERROR, "Errore nell'oggetto risposta").showAndWait();
                    }
                    //Non esistono parametri climatici associati a questo centro di monitoraggio
                    if (pcResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                        try {
                            Map<String, String> deleteParams = RequestFactory.buildDeleteParams(
                                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                                    centroIdResponse.getResult().toString());
                            Request deleteRequest = RequestFactory.buildRequest(
                                    client.getHostName(),
                                    ServerInterface.RequestType.executeDelete,
                                    ServerInterface.Tables.CENTRO_MONITORAGGIO,
                                    deleteParams);
                            client.addRequest(deleteRequest);
                        } catch (MalformedRequestException mre) {
                            logger.info(mre.getMessage());
                        }
                        Response deleteResponse = client.getResponse();
                        if (deleteResponse.getResponseType() == ServerInterface.ResponseType.deleteOk) {
                            new Alert(Alert.AlertType.CONFIRMATION, "Elemento eliminato con successo").showAndWait();
                            tableView.getItems().clear();
                            client.addRequest(PredefinedRequest.getRequestCm(client.getHostName()));
                            Response resCentri = client.getResponse();
                            if (resCentri.getResponseType() != ServerInterface.ResponseType.Error) {
                                List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) resCentri.getResult();
                                centri.forEach(centro -> tableView.getItems().add(centro));
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Errore nell'ogettto risposta").showAndWait();
                            }
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Fallimento dell'operazione!").showAndWait();
                        }

                    } else {
                        List<ParametroClimatico> pc = (List<ParametroClimatico>) pcResponse.getResult();
                        if (!pc.isEmpty()) {
                            new Alert(Alert.AlertType.ERROR, "Centro non eliminato: presenza di parametri climatici associati")
                                    .showAndWait();
                        }
                    }
                } catch (MalformedRequestException mre) {
                    logger.info(mre.getMessage());
                }
            }
        }
    }

    /**
     * Setup della schermata principale per la rimozione di CentroMonitoraggio
     *
     * @param event
     */
    @FXML
    public void handleRimuoviCentroMonitoraggio(ActionEvent event) {
        if (client != null) {
            if (paramBox != null && !paramBox.getChildren().isEmpty()) paramBox.getChildren().clear();
            this.paramBox = new VBox(2);
            paramBox.getStyleClass().add("param-box");

            TextField tNomeCentro = new TextField("Nome Centro");
            tNomeCentro.setOnMouseClicked(e -> tNomeCentro.clear());

            Button btnVisualizzaCentri = new Button("Visualizza centri");
            btnVisualizzaCentri.setOnAction(e -> {
                tableView.getItems().clear();
                Request centriRequest = PredefinedRequest.getRequestCm(client.getHostName());
                client.addRequest(centriRequest);
                Response resCentri = client.getResponse();
                if (resCentri.getResponseType() == ServerInterface.ResponseType.Error) {
                    logger.info("Errore nell'oggetto risposta");
                }
                List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) resCentri.getResult();
                centri.forEach(centro -> tableView.getItems().add(centro));
            });

            Button btnRimuoviCentro = new Button("Rimuovi Centro");
            btnRimuoviCentro.setOnAction(e -> rimuoviCentroPerDenom(tNomeCentro.getText()));

            paramBox.getChildren().add(tNomeCentro);
            paramBox.getChildren().add(btnVisualizzaCentri);
            paramBox.getChildren().add(btnRimuoviCentro);

            //set up tableView
            tableView.getItems().clear();
            tableView.getColumns().clear();
            tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());
            tableView.setRowFactory(tv -> {
                TableRow row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && (!row.isEmpty())) {
                        CentroMonitoraggio cm = (CentroMonitoraggio) row.getItem();
                        tNomeCentro.setText(cm.getDenominazione());
                    }
                });
                return row;
            });
            tableView.refresh();
            this.borderPane.setRight(paramBox);

        } else {
            clientNotConnected.showAndWait();
        }
    }

    /**
     * Set up della schermata per la rimozione di ParametroClimatico
     *
     * @param event
     */
    @FXML
    public void handleRimuoviParametroClimatico(ActionEvent event) {
        if (client != null) {
            if (paramBox != null && !paramBox.getChildren().isEmpty()) paramBox.getChildren().clear();
            this.paramBox = new VBox(5);
            paramBox.getStyleClass().add("param-box");
            this.tDenominazione = new TextField("Nome");
            this.tDenominazione.setOnMouseClicked(e -> this.tDenominazione.clear());
            TextField tIdArea = new TextField("");
            tIdArea.setEditable(false);
            this.startDatePicker = new DatePicker();
            this.btnRicercaAreaPerDenom = new Button("Cerca");
            Button deleteButton = new Button("Elimina");
            deleteButton.setOnAction(e -> {
                String parameterId = tIdArea.getText();
                LocalDate date = startDatePicker.getValue();
                LocalDate canonicalStartDate = LocalDate.of(1900, 1, 1);
                LocalDate canonicalEndDate = LocalDate.of(2100, 1, 1);
                if (date.isBefore(canonicalStartDate) || date.isAfter(canonicalEndDate)) {
                    new Alert(Alert.AlertType.ERROR, "Data inserita non valida").showAndWait();
                }
                //delete query
                try {
                    Map<String, String> deleteParams = RequestFactory.buildDeleteParams(ServerInterface.Tables.PARAM_CLIMATICO, parameterId, date.toString());
                    Request deleteRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.executeDelete,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            deleteParams
                    );
                    client.addRequest(deleteRequest);
                } catch (MalformedRequestException mre) {
                    mre.printStackTrace();
                }
                Response deleteResponse = client.getResponse();
                if (deleteResponse.getResponseType() == ServerInterface.ResponseType.deleteOk) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Operazione avvenuta con successo").showAndWait();
                    Request requestAreaId;
                    try {
                        Map<String, String> params = RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond, "areaid", "denominazione", tDenominazione.getText());
                        requestAreaId = RequestFactory.buildRequest(client.getHostName(), ServerInterface.RequestType.selectObjWithCond, ServerInterface.Tables.AREA_INTERESSE, params);
                    } catch (MalformedRequestException mre) {
                        mre.printStackTrace();
                        return;
                    }
                    client.addRequest(requestAreaId);
                    Response areaIdResponse = client.getResponse();
                    String areaId = areaIdResponse.getResult().toString();
                    Request paramClimaticiRequest = PredefinedRequest.getRequestPc(client.getHostName(), "areaid", areaId);
                    client.addRequest(paramClimaticiRequest);
                    Response paramClimaticiResponse = client.getResponse();
                    List<ParametroClimatico> paramClimatici = (List<ParametroClimatico>) paramClimaticiResponse.getResult();
                    tableView.getItems().clear();
                    paramClimatici.forEach(pc -> tableView.getItems().add(pc));
                } else {
                    new Alert(Alert.AlertType.ERROR, "Fallimento dell'operazione di eliminazione").showAndWait();
                }
                tableView.refresh();
            });
            this.btnRicercaAreaPerDenom.setOnAction(e -> {
                String areaDenom = tDenominazione.getText();
                if (areaDenom.isEmpty() || areaDenom.equals("Cerca")) {
                    new Alert(Alert.AlertType.ERROR, "Nome area non valido").showAndWait();
                } else {
                    try {
                        Map<String, String> areaIdReqParams =
                                RequestFactory.buildParams(
                                        ServerInterface.RequestType.selectObjWithCond,
                                        "areaid",
                                        "denominazione",
                                        areaDenom);
                        Request areaIdRequest = RequestFactory.buildRequest(
                                client.getHostName(),
                                ServerInterface.RequestType.selectObjWithCond,
                                ServerInterface.Tables.AREA_INTERESSE,
                                areaIdReqParams
                        );
                        client.addRequest(areaIdRequest);
                    } catch (MalformedRequestException mre) {
                        mre.printStackTrace();
                    }
                    Response areaIdResponse = client.getResponse();
                    if ((areaIdResponse.getResponseType() == ServerInterface.ResponseType.Error)
                            || (areaIdResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement)) {
                        resNoSuchElementAlert.showAndWait();
                    } else {
                        String areaId = areaIdResponse.getResult().toString();
                        Request paramClimaticiRequest = PredefinedRequest.getRequestPc(client.getHostName(), "areaid", areaId);
                        client.addRequest(paramClimaticiRequest);
                        Response paramClimaticiResponse = client.getResponse();
                        if (paramClimaticiResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                            resErrorAlert.showAndWait();
                        } else if (paramClimaticiResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                            resNoSuchElementAlert.showAndWait();
                        } else {
                            List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>) paramClimaticiResponse.getResult();
                            parametriClimatici.forEach(pc -> tableView.getItems().add(pc));
                        }
                    }
                }
            });

            //set up tableView
            tableView.getItems().clear();
            tableView.getColumns().clear();
            tableView.getColumns().add(TableViewBuilder.getDateColumn());
            tableView.getColumns().addAll(TableViewBuilder.getColumnsPc());
            tableView.setRowFactory(tv -> {
                TableRow row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && (!row.isEmpty())) {
                        ParametroClimatico pc = (ParametroClimatico) row.getItem();
                        tIdArea.setText(pc.getParameterId());
                        startDatePicker.setValue(pc.getPubDate());
                    }
                });
                return row;
            });

            tableView.refresh();
            paramBox.getChildren().add(tDenominazione);
            paramBox.getChildren().add(tIdArea);
            paramBox.getChildren().add(startDatePicker);
            paramBox.getChildren().add(btnRicercaAreaPerDenom);
            paramBox.getChildren().add(deleteButton);

            this.borderPane.setRight(paramBox);

        } else {
            clientNotConnected.showAndWait();
        }

    }

    /**
     * Gestisce l'aggiornamento di un centro monitoraggio tramite denominazione
     *
     * @param nuovaDenominazione
     * @param centroId
     */
    private void updateDenomCentro(String nuovaDenominazione, String centroId) {
        if (nuovaDenominazione.isEmpty() || nuovaDenominazione.equals("Nuova denominazione")) {
            new Alert(Alert.AlertType.ERROR, "Denominazione non valida").showAndWait();
        } else {
            try {
                Map<String, String> updateParams = RequestFactory.buildParams(
                        ServerInterface.RequestType.executeUpdate,
                        "nomecentro",
                        nuovaDenominazione,
                        centroId
                );
                Request updateDenomRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.executeUpdate,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        updateParams
                );
                client.addRequest(updateDenomRequest);
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            }
            Response updateResponse = client.getResponse();
            if (updateResponse.getResponseType() == ServerInterface.ResponseType.updateOk) {
                new Alert(Alert.AlertType.CONFIRMATION, "Aggiornamento avvenuto con successo!").showAndWait();
                tableView.getItems().clear();
                client.addRequest(PredefinedRequest.getRequestCm(client.getHostName()));
                Response resCm = client.getResponse();
                List<CentroMonitoraggio> cm = (List<CentroMonitoraggio>) resCm.getResult();
                cm.forEach(c -> tableView.getItems().add(c));
            } else {
                new Alert(Alert.AlertType.ERROR, "Aggiornamento fallito").showAndWait();
            }
        }
    }

    /**
     * Set up della schermata per l'aggiornamento di un CentroMonitoraggio
     *
     * @param event
     */
    @FXML
    public void handleAggiornaCentroMonitoraggio(ActionEvent event) {
        if (client != null) {
            if (paramBox != null && !paramBox.getChildren().isEmpty()) paramBox.getChildren().clear();
            this.paramBox = new VBox();
            this.paramBox.getStyleClass().add("param-box");
            tableView.getColumns().clear();
            tableView.getItems().clear();
            tableView.refresh();

            TextField tRicercaCentro = new TextField("Nome del centro");
            tRicercaCentro.setOnMouseClicked(e -> tRicercaCentro.clear());
            TextField tUpdateDenominazione = new TextField("Nuova denominazione");
            tUpdateDenominazione.setOnMouseClicked(e -> tUpdateDenominazione.clear());
            TextField tUpdateAreeAssociate = new TextField("area da aggiungere");
            tUpdateAreeAssociate.setOnMouseClicked(e -> tUpdateAreeAssociate.clear());
            TextField tRemoveAreaAssociata = new TextField("area da rimuovere");
            tRemoveAreaAssociata.setOnMouseClicked(e -> tRemoveAreaAssociata.clear());

            Button bVisualizzaAree = new Button("Visualizza aree");
            bVisualizzaAree.setOnAction(e -> {
                tableView.getColumns().clear();
                tableView.getItems().clear();
                tableView.getColumns().addAll(TableViewBuilder.getColumnsAi());
                tableView.setRowFactory(tv -> TableViewBuilder.getRowAi(client));
                tableView.refresh();

                Request reqAi = PredefinedRequest.getRequestAi(client.getHostName());
                client.addRequest(reqAi);
                Response resAi = client.getResponse();
                if (resAi.getResponseType() == ServerInterface.ResponseType.Error) {
                    resErrorAlert.showAndWait();
                } else if (resAi.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                    resNoSuchElementAlert.showAndWait();
                } else {
                    List<AreaInteresse> aree = (List<AreaInteresse>) resAi.getResult();
                    aree.forEach(area -> tableView.getItems().add(area));
                }
            });
            Button bVisualizzaCentri = new Button("Visualizza centri");
            bVisualizzaCentri.setOnAction(e -> {
                tableView.getColumns().clear();
                tableView.getItems().clear();
                tableView.getColumns().addAll(TableViewBuilder.getColumnsCm());
                tableView.setRowFactory(tv -> TableViewBuilder.getRowFactoryHandleVisualizzaCentri(client));
                tableView.refresh();

                Request reqCm = PredefinedRequest.getRequestCm(client.getHostName());
                client.addRequest(reqCm);
                Response resCm = client.getResponse();
                if (resCm.getResponseType() == ServerInterface.ResponseType.Error) {
                    resErrorAlert.showAndWait();
                } else if (resCm.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                    resNoSuchElementAlert.showAndWait();
                } else {
                    List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) resCm.getResult();
                    centri.forEach(centro -> tableView.getItems().add(centro));
                }
            });

            Button ricercaCentro = new Button("Ricerca centro");
            ricercaCentro.setOnAction(e -> {
                String nomeCentro = tRicercaCentro.getText();
                if (nomeCentro.isEmpty() || nomeCentro.equals("Nome del centro: ")) {
                    new Alert(Alert.AlertType.ERROR, "Nome del centro non valido!").showAndWait();
                } else {
                    try {
                        Map<String, String> reqCmParams = RequestFactory.buildParams(
                                ServerInterface.RequestType.selectAllWithCond,
                                "nomecentro",
                                nomeCentro
                        );
                        Request reqCm = RequestFactory.buildRequest(
                                client.getHostName(),
                                ServerInterface.RequestType.selectAllWithCond,
                                ServerInterface.Tables.CENTRO_MONITORAGGIO,
                                reqCmParams
                        );
                        client.addRequest(reqCm);
                    } catch (MalformedRequestException mre) {
                        new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    }
                    Response resCm = client.getResponse();
                    if (resCm.getResponseType() == ServerInterface.ResponseType.Error) {
                        resErrorAlert.showAndWait();
                    } else if (resCm.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                        resNoSuchElementAlert.showAndWait();
                    } else {
                        List<CentroMonitoraggio> result = (List<CentroMonitoraggio>) resCm.getResult();
                        result.forEach(cm -> tableView.getItems().add(cm));
                    }
                }
            });

            Button bUpdateDenom = new Button("Aggiorna denominazione");
            bUpdateDenom.setOnAction(e -> {
                String nomeCentro = tRicercaCentro.getText();
                try {
                    Map<String, String> reqCmIdParams = RequestFactory.buildParams(
                            ServerInterface.RequestType.selectObjWithCond,
                            "centroid",
                            "nomecentro",
                            nomeCentro);
                    Request centroIdRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectObjWithCond,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            reqCmIdParams);
                    client.addRequest(centroIdRequest);
                } catch (MalformedRequestException mre) {
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                }
                Response centroIdResponse = client.getResponse();
                if (centroIdResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                    resErrorAlert.showAndWait();
                } else if (centroIdResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                    resNoSuchElementAlert.showAndWait();
                } else {
                    String centroId = centroIdResponse.toString();
                    updateDenomCentro(tUpdateDenominazione.getText(), centroId);
                }
            });

            Button bAggiungiAreaAssociata = new Button("Aggiungi area associata");
            bAggiungiAreaAssociata.setOnAction(e -> aggiungiAreaCentro(
                    tUpdateAreeAssociate.getText(),
                    tRicercaCentro.getText()));

            Button bRimuoviAreaAssociata = new Button("Rimuovi area associata");
            bRimuoviAreaAssociata.setOnAction(e -> {
                String nomeAreaDaEliminare = tRemoveAreaAssociata.getText();
                String nomeCentro = tRicercaCentro.getText();
                if ((!nomeAreaDaEliminare.isEmpty() && !nomeAreaDaEliminare.equals("area da rimuovere"))
                        && (!nomeCentro.isEmpty() && !nomeCentro.equals("Nome del centro"))) {
                    try {
                        Map<String, String> reqAreaIdParams = RequestFactory.buildParams(
                                ServerInterface.RequestType.selectObjWithCond,
                                "areaid",
                                "denominazione",
                                nomeAreaDaEliminare
                        );
                        Request areaIdRequest = RequestFactory.buildRequest(
                                client.getHostName(),
                                ServerInterface.RequestType.selectObjWithCond,
                                ServerInterface.Tables.AREA_INTERESSE,
                                reqAreaIdParams
                        );
                        client.addRequest(areaIdRequest);
                    } catch (MalformedRequestException mre) {
                        logger.info(mre.getMessage());
                    }
                    Response areaIdResponse = client.getResponse();
                    try {
                        Map<String, String> reqCmIdParams = RequestFactory.buildParams(
                                ServerInterface.RequestType.selectObjWithCond,
                                "centroid",
                                "nomecentro",
                                nomeCentro);
                        Request centroIdRequest = RequestFactory.buildRequest(
                                client.getHostName(),
                                ServerInterface.RequestType.selectObjWithCond,
                                ServerInterface.Tables.CENTRO_MONITORAGGIO,
                                reqCmIdParams);
                        client.addRequest(centroIdRequest);
                    } catch (MalformedRequestException mre) {
                        logger.info(mre.getMessage());
                    }
                    Response centroIdResponse = client.getResponse();
                    if (centroIdResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                        resErrorAlert.showAndWait();
                    } else if (centroIdResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                        resNoSuchElementAlert.showAndWait();
                    } else {
                        String centroId = centroIdResponse.getResult().toString();
                        String areaId = areaIdResponse.getResult().toString();
                        rimuoviAreaDaCentro(areaId, centroId);
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Nome dell'area o del centro non valida!").showAndWait();
                }
            });

            this.paramBox.getChildren()
                    .addAll(
                            bVisualizzaAree,
                            bVisualizzaCentri,
                            tRicercaCentro,
                            tUpdateDenominazione,
                            tUpdateAreeAssociate,
                            tRemoveAreaAssociata,
                            bUpdateDenom,
                            bAggiungiAreaAssociata,
                            bRimuoviAreaAssociata);
            this.borderPane.setRight(paramBox);
        } else {
            clientNotConnected.showAndWait();
        }
    }

    /**
     * Gestisce la rimozione di un AreaInteresse da un centro di monitoraggio
     *
     * @param areaId
     * @param centroId
     */
    private void rimuoviAreaDaCentro(String areaId, String centroId) {
        if (centroId.isEmpty() || areaId.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "id del centro o dell'area non valido").showAndWait();
        } else {
            try {
                Map<String, String> deleteParams = RequestFactory.buildDeleteAiCmParams(areaId, centroId);
                Request removeAreaRequest = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.executeDelete,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        deleteParams
                );
                client.addRequest(removeAreaRequest);
            } catch (MalformedRequestException mre) {
                logger.info(mre.getMessage());
            }
            Response deleteAreaResponse = client.getResponse();
            if (deleteAreaResponse.getResponseType() == ServerInterface.ResponseType.deleteOk) {
                new Alert(Alert.AlertType.CONFIRMATION, "Area eliminata con successo").showAndWait();
                tableView.getItems().clear();
                client.addRequest(PredefinedRequest.getRequestCm(client.getHostName()));
                Response resCm = client.getResponse();
                List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) resCm.getResult();
                centri.forEach(cm -> tableView.getItems().add(cm));
            } else {
                new Alert(Alert.AlertType.ERROR, "Fallimento dell'operazione di eliminazione").showAndWait();
            }
        }
    }


    /**
     * Gestisce l'aggiunta di un AreaInteresse a un CentroMonitoraggio
     *
     * @param denominazioneAreaInteresse
     * @param denominazioneCentro
     */
    private void aggiungiAreaCentro(String denominazioneAreaInteresse, String denominazioneCentro) {
        if (denominazioneAreaInteresse.isEmpty() || denominazioneCentro.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Stringhe inserite non valide!");
        } else {
            //check se l'area esiste
            try {
                Map<String, String> paramsCheckAi = RequestFactory
                        .buildParams(ServerInterface.RequestType.selectObjWithCond,
                                "areaid", "denominazione", denominazioneAreaInteresse);
                Request requestAi = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectObjWithCond,
                        ServerInterface.Tables.AREA_INTERESSE,
                        paramsCheckAi);
                client.addRequest(requestAi);
            } catch (MalformedRequestException mre) {
                logger.info(mre.getMessage());
            }
            Response responseAi = client.getResponse();
            String areaId = responseAi.getResult().toString();
            if (areaId.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Area non esistente").showAndWait();
                return;
            }
            //check se il centro esiste
            try {
                Map<String, String> paramsCheckCm = RequestFactory
                        .buildParams(ServerInterface.RequestType.selectObjWithCond, "centroid",
                                "nomecentro", denominazioneCentro);
                Request requestCm = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectObjWithCond,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        paramsCheckCm
                );
                client.addRequest(requestCm);
            } catch (MalformedRequestException mre) {
                logger.info(mre.getMessage());
            }
            Response responseCm = client.getResponse();
            String centroId = responseCm.getResult().toString();
            if (centroId.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Centro non esistente").showAndWait();
                return;
            }
            //Check se l'area sia gia presente nell'array del centro
            try {
                Map<String, String> paramsCheckAiInCm = RequestFactory
                        .buildParams(ServerInterface.RequestType.selectObjWithCond, "aree_interesse_ids",
                                "centroid", centroId);
                Request req = RequestFactory.buildRequest(
                        client.getHostName(),
                        ServerInterface.RequestType.selectObjWithCond,
                        ServerInterface.Tables.CENTRO_MONITORAGGIO,
                        paramsCheckAiInCm
                );
                client.addRequest(req);
            } catch (MalformedRequestException mre) {
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                return;
            }
            Response response = client.getResponse();
            String areeAssociateAlCentro = response.getResult().toString();
            if (areeAssociateAlCentro.contains(areaId)) {
                new Alert(Alert.AlertType.INFORMATION, "area già associata al centro").showAndWait();
            } else {
                //area non è associata al centro -> si aggiunge l'area al array associato al centro
                try {
                    Map<String, String> updateParams = RequestFactory.buildParams(ServerInterface.RequestType.executeUpdate, "aree_interesse_ids", areaId, centroId);
                    Request insertAreaRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.executeUpdate,
                            ServerInterface.Tables.CENTRO_MONITORAGGIO,
                            updateParams
                    );
                    client.addRequest(insertAreaRequest);
                } catch (MalformedRequestException mre) {
                    logger.info(mre.getMessage());
                }
                Response updateAi = client.getResponse();
                if (updateAi.getResponseType() == ServerInterface.ResponseType.updateOk) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Area aggiunta al centro").showAndWait();
                    tableView.getItems().clear();
                    client.addRequest(PredefinedRequest.getRequestCm(client.getHostName()));
                    Response cmRes = client.getResponse();
                    List<CentroMonitoraggio> centri = (List<CentroMonitoraggio>) cmRes.getResult();
                    centri.forEach(cm -> tableView.getItems().add(cm));
                } else {
                    new Alert(Alert.AlertType.ERROR, "Errore nell'aggiunta dell'area").showAndWait();
                }
            }
        }

    }

    /**
     * Set up della schermata e gestione della richiesta di aggiornamento di un ParametroClimatico
     *
     * @param event
     */
    @FXML
    public void handleAggiornaParametroClimatico(ActionEvent event) {
        if (client != null) {
            if (paramBox != null && !paramBox.getChildren().isEmpty()) paramBox.getChildren().clear();

            tableView.getColumns().clear();
            tableView.getItems().clear();
            tableView.refresh();
            tableView.getColumns().add(TableViewBuilder.getDateColumn());
            tableView.getColumns().addAll(TableViewBuilder.getColumnsPc());
            tableView.setRowFactory(tv -> TableViewBuilder.getRowPc(client));

            this.paramBox = new VBox();
            this.paramBox.getStyleClass().add("param-box");
            TextField tArea = new TextField("Nome dell'area");
            Button bRicercaArea = new Button("Ricerca area");
            bRicercaArea.setOnMouseClicked(e -> {
                String nomeArea = tArea.getText();
                if (nomeArea.isEmpty() || nomeArea.equalsIgnoreCase("Nome dell'area"))
                    new Alert(Alert.AlertType.ERROR, "Nome dell'area non valido").showAndWait();
                else {
                    try {
                        Map<String, String> areaIdReqParams =
                                RequestFactory.buildParams(ServerInterface.RequestType.selectObjWithCond, "areaid", "denominazione", nomeArea);
                        Request areaIdRequest = RequestFactory.buildRequest(
                                client.getHostName(),
                                ServerInterface.RequestType.selectObjWithCond,
                                ServerInterface.Tables.AREA_INTERESSE,
                                areaIdReqParams);
                        client.addRequest(areaIdRequest);
                    } catch (MalformedRequestException mre) {
                        logger.info(mre.getMessage());
                    }
                    Response areaIdResponse = client.getResponse();
                    if (areaIdResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                        resErrorAlert.showAndWait();
                    } else if (areaIdResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                        resNoSuchElementAlert.showAndWait();
                    } else {
                        String areaId = areaIdResponse.getResult().toString();
                        try {
                            Map<String, String> params = RequestFactory.buildParams(
                                    ServerInterface.RequestType.selectAllWithCond,
                                    "areaid",
                                    areaId);
                            Request paramClimaticiRequest = RequestFactory.buildRequest(
                                    client.getHostName(),
                                    ServerInterface.RequestType.selectAllWithCond,
                                    ServerInterface.Tables.PARAM_CLIMATICO,
                                    params);
                            client.addRequest(paramClimaticiRequest);
                        } catch (MalformedRequestException mre) {
                            logger.info(mre.getMessage());
                        }
                        Response paramClimaticiResponse = client.getResponse();
                        if (paramClimaticiResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                            resErrorAlert.showAndWait();
                        } else if (paramClimaticiResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                            resNoSuchElementAlert.showAndWait();
                        } else {

                            List<ParametroClimatico> paramClimatici = (List<ParametroClimatico>) paramClimaticiResponse.getResult();
                            paramClimatici.forEach(pc -> tableView.getItems().add(pc));
                        }

                    }


                }
            });

            tArea.setOnMouseClicked(e -> tArea.clear());
            ComboBox<String> itemBox = new ComboBox<String>();
            itemBox.getStyleClass().add("combo-box");
            ComboBox<Integer> valueBox = new ComboBox<Integer>();
            valueBox.getStyleClass().add("combo-box");
            String[] items = {"vento", "umidita", "pressione", "temperatura", "precipitazioni", "alt. ghiacciai", "m. ghiacciai"};
            itemBox.getItems().addAll(items);
            for (int i = 1; i < 6; i++) valueBox.getItems().add(i);

            Button aggiornaPc = new Button("Aggiorna");
            aggiornaPc.setOnMouseClicked(e -> {
                String item = itemBox.getSelectionModel().getSelectedItem();
                Integer value = valueBox.getSelectionModel().getSelectedItem();
                ParametroClimatico pc = (ParametroClimatico) tableView.getSelectionModel().getSelectedItem();
                if (item != null && pc != null && value != null) {
                    String columnToUpdate = mapString(item);
                    try {
                        Map<String, String> updateParams = RequestFactory.buildParams(
                                ServerInterface.RequestType.executeUpdate,
                                columnToUpdate,
                                value.toString(),
                                pc.getParameterId()
                        );
                        Request updateRequest = RequestFactory.buildRequest(
                                client.getHostName(),
                                ServerInterface.RequestType.executeUpdate,
                                ServerInterface.Tables.PARAM_CLIMATICO,
                                updateParams
                        );
                        client.addRequest(updateRequest);
                    } catch (MalformedRequestException mre) {
                        logger.info(mre.getMessage());
                    }
                    Response updateResponse = client.getResponse();
                    if (updateResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                        new Alert(Alert.AlertType.ERROR, updateResponse.getResponseType().label).showAndWait();
                    }
                    if (updateResponse.getResponseType() == ServerInterface.ResponseType.updateOk) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Update avvenuto con successo").showAndWait();
                        tableView.getItems().clear();
                        try {
                            Map<String, String> params = RequestFactory.buildParams(
                                    ServerInterface.RequestType.selectAllWithCond,
                                    "areaid",
                                    pc.getAreaInteresseId());
                            Request paramClimaticiRequest = RequestFactory.buildRequest(
                                    client.getHostName(),
                                    ServerInterface.RequestType.selectAllWithCond,
                                    ServerInterface.Tables.PARAM_CLIMATICO,
                                    params);
                            client.addRequest(paramClimaticiRequest);
                        } catch (MalformedRequestException mre) {
                            logger.info(mre.getMessage());
                        }
                        Response paramClimaticiResponse = client.getResponse();
                        if (paramClimaticiResponse.getResponseType() == ServerInterface.ResponseType.Error) {
                            resErrorAlert.showAndWait();
                        } else if (paramClimaticiResponse.getResponseType() == ServerInterface.ResponseType.NoSuchElement) {
                            resNoSuchElementAlert.showAndWait();
                        } else {
                            List<ParametroClimatico> paramClimatici = (List<ParametroClimatico>) paramClimaticiResponse.getResult();
                            paramClimatici.forEach(paramClimatico -> tableView.getItems().add(paramClimatico));
                        }
                    } else if (updateResponse.getResponseType() == ServerInterface.ResponseType.updateKo) {
                        new Alert(Alert.AlertType.ERROR, "Operazione di update fallita").showAndWait();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Selezionare dei valori validi!").showAndWait();
                }
            });

            paramBox.getChildren().addAll(tArea, bRicercaArea, itemBox, valueBox, aggiornaPc);
            this.borderPane.setRight(paramBox);

        } else {
            clientNotConnected.showAndWait();
        }
    }


    private String mapString(String item) {
        String columnToUpdate = "";
        switch (item) {
            case "vento" -> columnToUpdate = RequestFactory.valoreVentoKey;
            case "umidita" -> columnToUpdate = RequestFactory.valoreUmiditaKey;
            case "pressione" -> columnToUpdate = RequestFactory.valorePressioneKey;
            case "temperatura" -> columnToUpdate = RequestFactory.valoreTemperaturaKey;
            case "precipitazioni" -> columnToUpdate = RequestFactory.valorePrecipitazioniKey;
            case "alt. ghiacciai" -> columnToUpdate = RequestFactory.valoreAltGhiacciaiKey;
            case "m. ghiacciai" -> columnToUpdate = RequestFactory.valoreMassaGhiacciaiKey;
        }
        return columnToUpdate;
    }
}