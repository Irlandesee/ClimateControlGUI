package it.uninsubria.controller.dialog;

import it.uninsubria.datamodel.areaInteresse.AreaInteresse;
import it.uninsubria.clientCm.Client;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.datamodel.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class AiDialog {

    private Stage stage;
    private final AreaInteresse ai;
    private List<ParametroClimatico> parameters;
    public Label nomeLabel;
    public Label denominazioneLabel;
    public Label statoLabel;
    public Label latitudineLabel;
    public Label longitudineLabel;

    private static final int MAX_WINDOW_SIZE = 800;

    public TableView paramClimaticiTableView;
    private final Client client;

    public AiDialog(Stage stage, Client client, AreaInteresse ai, List<ParametroClimatico> parameters){
        this.stage = stage;
        this.ai = ai;
        this.parameters = parameters;
        this.client = client;
    }

    /**
     * Prepara la finestra per la visualizzazione dei dati
     */
    @FXML
    public void initialize(){
        nomeLabel.setText(ai.getDenominazione());
        denominazioneLabel.setText(ai.getDenominazione());
        statoLabel.setText(ai.getStato());
        latitudineLabel.setText(String.valueOf(ai.getLatitudine()));
        longitudineLabel.setText(String.valueOf(ai.getLongitudine()));

        TableColumn<ParametroClimatico, String> pubDateColumn = new TableColumn<ParametroClimatico, String>("Data pubblicazione");
        TableColumn<ParametroClimatico, Short> tempColumn = new TableColumn<ParametroClimatico, Short>("temperatura");
        TableColumn<ParametroClimatico, Short> umidityColumn = new TableColumn<ParametroClimatico, Short>("umidità");
        TableColumn<ParametroClimatico, Short> pressureColumn = new TableColumn<ParametroClimatico, Short>("pressione");
        TableColumn<ParametroClimatico, Short> windColumn = new TableColumn<ParametroClimatico, Short>("vento");
        TableColumn<ParametroClimatico, Short> rainfallColumn = new TableColumn<ParametroClimatico, Short>("precipitazioni");
        TableColumn<ParametroClimatico, Short> altColumn = new TableColumn<ParametroClimatico, Short>("altitudine g.");
        TableColumn<ParametroClimatico, Short> massColumn = new TableColumn<ParametroClimatico, Short>("massa g.");

        pubDateColumn.setCellValueFactory(new PropertyValueFactory<>("pubDate"));
        tempColumn.setCellValueFactory(new PropertyValueFactory<>("temperaturaValue"));
        umidityColumn.setCellValueFactory(new PropertyValueFactory<>("umiditaValue"));
        pressureColumn.setCellValueFactory(new PropertyValueFactory<>("pressioneValue"));
        windColumn.setCellValueFactory(new PropertyValueFactory<>("ventoValue"));
        rainfallColumn.setCellValueFactory(new PropertyValueFactory<>("precipitazioniValue"));
        altColumn.setCellValueFactory(new PropertyValueFactory<>("altitudineValue"));
        massColumn.setCellValueFactory(new PropertyValueFactory<>("massaValue"));
        paramClimaticiTableView.getColumns().add(pubDateColumn);

        paramClimaticiTableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    ParametroClimatico pcSelected = (ParametroClimatico) row.getItem();
                    String centroId = pcSelected.getIdCentro();
                    Request request = null;
                    try{
                        Map<String, String> params = RequestFactory
                                .buildParams(ServerInterface.RequestType.selectObjWithCond, "nomecentro",
                                        "centroid", centroId);
                        request = RequestFactory.buildRequest(client.getHostName(),
                                ServerInterface.RequestType.selectObjWithCond,
                                ServerInterface.Tables.CENTRO_MONITORAGGIO,
                                params);
                    }catch(MalformedRequestException mre){
                        new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                        mre.printStackTrace();
                        return;
                    }
                    client.addRequest(request);

                    Response res = client.getResponse();
                    String denomCentro = "";
                    if(res.getResponseType() == ServerInterface.ResponseType.Object){
                        denomCentro = res.getResult().toString();
                    }

                    if(stage.getWidth() < MAX_WINDOW_SIZE) stage.setWidth(MAX_WINDOW_SIZE);

                    if(!paramClimaticiTableView.getColumns().contains(tempColumn))
                        paramClimaticiTableView.getColumns().add(tempColumn);
                    if(!paramClimaticiTableView.getColumns().contains(umidityColumn))
                        paramClimaticiTableView.getColumns().add(umidityColumn);
                    if(!paramClimaticiTableView.getColumns().contains(pressureColumn))
                        paramClimaticiTableView.getColumns().add(pressureColumn);
                    if(!paramClimaticiTableView.getColumns().contains(windColumn))
                        paramClimaticiTableView.getColumns().add(windColumn);
                    if(!paramClimaticiTableView.getColumns().contains(rainfallColumn))
                        paramClimaticiTableView.getColumns().add(rainfallColumn);
                    if(!paramClimaticiTableView.getColumns().contains(altColumn))
                        paramClimaticiTableView.getColumns().add(altColumn);
                    if(!paramClimaticiTableView.getColumns().contains(massColumn))
                        paramClimaticiTableView.getColumns().add(massColumn);



                    System.out.println(pcSelected);
                    System.out.println(denomCentro);
                }
            });
            return row;
        });

        if(!parameters.isEmpty()){
            parameters.forEach(pc -> {
                paramClimaticiTableView.getItems().add(pc);
            });
        }
    }

    /**
     * Chiude la finestra associata all'evento
     * @param actionEvent
     */
    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }
}
