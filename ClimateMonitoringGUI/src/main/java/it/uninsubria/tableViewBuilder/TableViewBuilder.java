package it.uninsubria.tableViewBuilder;

import it.uninsubria.MainWindow;
import it.uninsubria.datamodel.areaInteresse.AreaInteresse;
import it.uninsubria.datamodel.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.datamodel.city.City;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.dialog.AiDialog;
import it.uninsubria.controller.dialog.CmDialog;
import it.uninsubria.controller.dialog.PcDialog;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.datamodel.operatore.Operatore;
import it.uninsubria.datamodel.operatore.OperatoreAutorizzato;
import it.uninsubria.datamodel.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableViewBuilder {
    static short minWidth = 100;

    /**
     * Prepara le colonne della tabella per la visualizzazione di oggetti di tipo AreaInteresse
     * @return
     */
    public static List<TableColumn<AreaInteresse, String>> getColumnsAi(){
        List<TableColumn<AreaInteresse, String>> res = new LinkedList<TableColumn<AreaInteresse, String>>();
        String[] columnNames = {"denominazione", "stato", "latitudine", "longitudine"};
        for (String columnName : columnNames) {
            TableColumn<AreaInteresse, String> column = new TableColumn<AreaInteresse, String>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<AreaInteresse, String>(columnName));
            column.setMinWidth(minWidth);
            res.add(column);
        }
        return res;
    }

    /**
     * Prepara le colonne della tabella per la visualizzazione di oggetti di tipo CentroMonitoraggio
     * @return
     */
    public static List<TableColumn<CentroMonitoraggio, String>> getColumnsCm(){
        List<TableColumn<CentroMonitoraggio, String>> res = new LinkedList<TableColumn<CentroMonitoraggio, String>>();
        String[] columnNames = {"denominazione", "stato"};
        String[] propertyNames = {"denominazione", "country"};
        int i = 0;
        for(String columnName : columnNames){
            TableColumn<CentroMonitoraggio, String> column = new TableColumn<CentroMonitoraggio, String>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<CentroMonitoraggio, String>(propertyNames[i]));
            column.setMinWidth(minWidth);
            res.add(column);
            i++;
        }
        return res;
    }

    /**
     * Prepara le colonne della tabella per la visualizzazione di oggetti di tipo City
     * @return
     */
    public static List<TableColumn<City, String>> getColumnsCity(){
        List<TableColumn<City, String>> res = new LinkedList<TableColumn<City, String>>();
        String[] columnNames = {"asciiName", "country", "latitude", "longitude"};
        String[] propertyNames = {"asciiName", "country", "latitude", "longitude"};
        int i = 0;
        for(String columnName : columnNames){
            TableColumn<City, String> column = new TableColumn<City, String>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<City, String>(propertyNames[i]));
            column.setMinWidth(minWidth);
            res.add(column);
            i++;
        }
        return res;
    }

    /**
     * Prepara le colonne della tabella per la visualizzazione di oggetti di tipo Operatore
     * @return
     */
    public static List<TableColumn<Operatore, String>> getColumnsOp(){
        List<TableColumn<Operatore, String>> res = new LinkedList<TableColumn<Operatore, String>>();
        String[] columnNames = {"email", "codice fiscale"};
        String[] propertyNames = {"email", "codFiscale"};
        int i = 0;
        for(String columnName : columnNames){
            TableColumn<Operatore, String> column = new TableColumn<Operatore, String>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<Operatore, String>(propertyNames[i]));;
            column.setMinWidth(minWidth);
            res.add(column);
            i++;
        }
        return res;
    }

    /**
     * Crea una colonna per la visualizzazione di LocalDate combinato a oggetti di tipo ParametroClimatico
     * @return
     */
    public static TableColumn<ParametroClimatico, LocalDate> getDateColumn(){
        TableColumn<ParametroClimatico, LocalDate> dateColumn = new TableColumn<ParametroClimatico, LocalDate>("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, LocalDate>("pubDate"));
        dateColumn.setMinWidth(minWidth);
        return dateColumn;
    }

    /**
     * Prepara le colonne della tabella per la visualizzazione di oggetti di tipo ParametroClimatico
     * @return
     */
    public static List<TableColumn<ParametroClimatico, Short>> getColumnsPc(){
        List<TableColumn<ParametroClimatico, Short>> res = new LinkedList<TableColumn<ParametroClimatico, Short>>();
        String[] columnNames = {"Vento", "Umidita", "Pressione", "Temperatura", "Precipitazioni", "Altitudine ghiacciai", "Massa ghiacciai"};
        String[] propertyNames = {"ventoValue", "umiditaValue", "pressioneValue", "temperaturaValue", "precipitazioniValue", "altitudineValue", "massaValue"};
        int i = 0;
        for(String columnName : columnNames){
            TableColumn<ParametroClimatico, Short> column = new TableColumn<ParametroClimatico, Short>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, Short>(propertyNames[i]));
            column.setMinWidth(minWidth);
            res.add(column);
            i++;
        }
        return res;
    }

    /**
     * Prepara le righe della tabella, rendendole "Interattive" per lavorare con oggetti di tipo AreaInteresse
     * @param client
     * @return
     */
    public static TableRow getRowAi(Client client){
        TableRow row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && (!row.isEmpty())){
                AreaInteresse a = (AreaInteresse) row.getItem();
                Request request = null;
                try{
                    Map<String, String> requestParams = RequestFactory
                            .buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", a.getAreaid());
                    request = RequestFactory.buildRequest(
                            client.getHostName(),
                            ServerInterface.RequestType.selectAllWithCond,
                            ServerInterface.Tables.PARAM_CLIMATICO,
                            requestParams
                    );
                }catch(MalformedRequestException mre){
                    new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                    mre.printStackTrace();
                    return;
                }
                client.addRequest(request);

                //get response
                Response response = null;
                response = client.getResponse();

                if(response.getResponseType() == ServerInterface.ResponseType.Error){
                    new Alert(Alert.AlertType.ERROR, "Errore in risposta").showAndWait();
                    return;
                }
                if(response.getResponseType() == ServerInterface.ResponseType.NoSuchElement){
                    new Alert(Alert.AlertType.INFORMATION, "Area senza prametri Climatici").showAndWait();
                    return;
                }

                List<ParametroClimatico> parametriClimatici = (List<ParametroClimatico>) response.getResult();
                try{
                    Stage aiDialogStage = new Stage();
                    AiDialog aiDialogController = new AiDialog(aiDialogStage, client, a, parametriClimatici);

                    FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/ai-dialog.fxml"));

                    fxmlLoader.setController(aiDialogController);
                    Scene dialogScene = new Scene(fxmlLoader.load(), 400, 400);
                    aiDialogStage.setScene(dialogScene);
                    aiDialogStage.show();

                }catch(IOException ioe){ioe.printStackTrace();}


            }
        });

        return row;
    }

    /**
     * Prepara le righe della tabella, rendendole "interattive" per lavorare con oggetti di tipo ParametroClimatico
     * @param client
     * @return
     */
    public static TableRow getRowPc(Client client){
        TableRow row = new TableRow<>();
        row.setOnMouseClicked(
                event -> {
                    if(event.getClickCount() == 2 && (!row.isEmpty())){
                        ParametroClimatico pc = (ParametroClimatico) row.getItem();


                        Request requestDenominazione = null;
                        try {
                            Map<String, String> paramsReqDenom = RequestFactory
                                    .buildParams(ServerInterface.RequestType.selectObjJoinWithCond,
                                            "denominazione", ServerInterface.Tables.AREA_INTERESSE.label,
                                            "parameterid", pc.getParameterId());
                            requestDenominazione = RequestFactory
                                    .buildRequest(
                                            client.getHostName(),
                                            ServerInterface.RequestType.selectObjJoinWithCond,
                                            ServerInterface.Tables.PARAM_CLIMATICO,
                                            paramsReqDenom);
                        } catch (MalformedRequestException e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                            e.printStackTrace();
                            return;
                        }

                        Request requestNomeCentro = null;
                        try{
                            Map<String, String> paramsReqNomeCentro = RequestFactory
                                    .buildParams(ServerInterface.RequestType.selectObjJoinWithCond,
                                            "nomecentro",
                                            ServerInterface.Tables.CENTRO_MONITORAGGIO.label,
                                            "parameterid",
                                            pc.getParameterId());
                            requestNomeCentro = RequestFactory
                                    .buildRequest(
                                            client.getHostName(),
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
                            respDenom = client.getResponse();
                            respNomeCentro = client.getResponse();

                        }

                        if(respDenom == null || respNomeCentro == null){
                            new Alert(Alert.AlertType.ERROR, "Error in response object").showAndWait();
                            return;
                        }

                        String nomeArea = "";
                        String nomeCentro = "";

                        if(respDenom.getResponseType().equals(ServerInterface.ResponseType.Object) &&
                                respDenom.getTable().equals(ServerInterface.Tables.AREA_INTERESSE)){
                            nomeArea = respDenom.getResult().toString();
                        }else{
                            nomeArea = "Error while retrieving denominazione area";
                        }
                        if(respNomeCentro.getResponseType().equals(ServerInterface.ResponseType.Object)
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
    }

    /**
     * Prepara le righe della tabella per l'abilitazione di un operatore
     * @param tEmailField
     * @param tCodFiscField
     * @return
     */
    public static TableRow getRowFactoryOpAbilitaOperatore(TextField tEmailField, TextField tCodFiscField){
        TableRow row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 &&  (!row.isEmpty())){
                OperatoreAutorizzato op = (OperatoreAutorizzato) row.getItem();
                tEmailField.setText(op.getEmail());
                tCodFiscField.setText(op.getCodFiscale());
            }
        });
        return row;
    }

    /**
     * Prepara le righe della tabella per la visualizzazione di centri di monitoraggio
     * @param client
     * @return
     */
    public static TableRow getRowFactoryHandleVisualizzaCentri(Client client){
        TableRow row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && !(row.isEmpty())){
                CentroMonitoraggio cm = (CentroMonitoraggio) row.getItem();
                List<String> areeId = cm.getAreeInteresseIdAssociate();
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
                    Response responseAi = client.getResponse();
                    if(responseAi == null){
                        new Alert(Alert.AlertType.ERROR, "Error in response object").showAndWait();
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
    }

    /**
     * Prepara le righe della tabella per interagire con TextField
     * @param tDenominazione
     * @param tStato
     * @param tLatitudine
     * @param tLongitudine
     * @return
     */
    public static TableRow getRowFactoryPrepTableCity(TextField tDenominazione, TextField tStato, TextField tLatitudine, TextField tLongitudine){
        TableRow row = new TableRow<>();
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
    }

    public static TableRow getRowFactoryPrepTableCentroMonitoraggio(TextField nomeCentroField, TextField comuneField, TextField statoCmField){
        TableRow row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && (!row.isEmpty())){
                City c = (City) row.getItem();
                nomeCentroField.setText(c.getAsciiName()+"Centro");
                comuneField.setText(c.getAsciiName());
                statoCmField.setText(c.getCountry());
            }
        });
        return row;
    }

    public static TableRow getRowFactoryVisualizeCmData(TextField nomeCentroField, TextField comuneField, TextField statoCMField){
        TableRow row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && (!row.isEmpty())){
                City c = (City) row.getItem();
                nomeCentroField.setText(c.getAsciiName()+"Centro");
                comuneField.setText(c.getAsciiName());
                statoCMField.setText(c.getCountry());
            }
        });
        return row;
    }



}
