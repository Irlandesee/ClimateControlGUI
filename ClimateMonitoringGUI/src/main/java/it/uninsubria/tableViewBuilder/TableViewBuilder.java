package it.uninsubria.tableViewBuilder;

import it.uninsubria.MainWindow;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.dialog.AiDialog;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableViewBuilder {
    static short minWidth = 100;

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

    public static List<TableColumn<CentroMonitoraggio, String>> getColumnsCm(){
        List<TableColumn<CentroMonitoraggio, String>> res = new LinkedList<TableColumn<CentroMonitoraggio, String>>();
        String[] columnNames = {"denominazione", "stato"};
        String[] propertyNames = {"asciiName", "country"};
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

    public static List<TableColumn<City, String>> getColumnsCity(){
        List<TableColumn<City, String>> res = new LinkedList<TableColumn<City, String>>();
        String[] columnNames = {"denominazione", "stato", "latitudine", "longitudine"};
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

    public static TableColumn<ParametroClimatico, LocalDate> getDateColumn(){
        TableColumn<ParametroClimatico, LocalDate> dateColumn = new TableColumn<ParametroClimatico, LocalDate>("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, LocalDate>("pubDate"));
        dateColumn.setMinWidth(minWidth);
        return dateColumn;
    }
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

    public static TableRow getRowAi(Client client){
        TableRow row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && (!row.isEmpty())){
                AreaInteresse a = (AreaInteresse) row.getItem();
                Map<String, String> requestParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond);
                if(requestParams != null){
                    requestParams.replace(RequestFactory.condKey, "areaid");
                    requestParams.replace(RequestFactory.fieldKey, a.getAreaid());
                }
                Request request = null;
                try{
                    request = RequestFactory.buildRequest(
                            client.getClientId(),
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
                response = client.getResponse(request.getRequestId());
                if(response == null){
                    new Alert(Alert.AlertType.ERROR, "Error in the response object").showAndWait();
                    return;
                }
                List<ParametroClimatico> parametriClimatici = new LinkedList<ParametroClimatico>();
                if(response.getTable() == ServerInterface.Tables.PARAM_CLIMATICO
                    && response.getRespType() == ServerInterface.ResponseType.List){
                    parametriClimatici = (List<ParametroClimatico>) response.getResult();
                }


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


}
