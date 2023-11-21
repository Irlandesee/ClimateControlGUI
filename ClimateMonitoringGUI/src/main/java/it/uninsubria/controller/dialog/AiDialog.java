package it.uninsubria.controller.dialog;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

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
    private final QueryHandler queryHandler;
    public AiDialog(Stage stage, QueryHandler queryHandler, AreaInteresse ai, List<ParametroClimatico> parameters){
        this.stage = stage;
        this.ai = ai;
        this.parameters = parameters;
        this.queryHandler = queryHandler;
    }

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
                    String denomCentro = queryHandler
                            .selectObjectWithCond("nomecentro", QueryHandler.Tables.CENTRO_MONITORAGGIO, "centroid", centroId)
                            .get(0);
                    System.out.println("Aumento dimensioni");
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

    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }
}
