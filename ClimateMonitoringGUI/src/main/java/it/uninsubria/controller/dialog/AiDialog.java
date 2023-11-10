package it.uninsubria.controller.dialog;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AiDialog {

    private final SceneController sceneController;
    private final AreaInteresse ai;
    private List<ParametroClimatico> parameters;
    public Label nomeLabel;
    public Label denominazioneLabel;
    public Label statoLabel;
    public Label latitudineLabel;
    public Label longitudineLabel;

    public TableView paramClimaticiTableView;
    private final QueryHandler queryHandler;
    public AiDialog(SceneController sceneController, QueryHandler queryHandler, AreaInteresse ai, List<ParametroClimatico> parameters){
        this.sceneController = sceneController;
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

        TableColumn<LocalDate, String> pubDateColumn = new TableColumn<LocalDate, String>("Data pubblicazione");
        TableColumn<String, String> nomeCentroColumn = new TableColumn<String, String>("Nome Centro");

        pubDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
        nomeCentroColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        //paramClimaticiTableView.getColumns().addAll(pubDateColumn, nomeCentroColumn);
        paramClimaticiTableView.getColumns().add(pubDateColumn);

        paramClimaticiTableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    String pubDateSelected = row.getItem().toString();
                    ParametroClimatico pc = parameters
                            .stream()
                            .filter(p -> p.getPubDate() != LocalDate.parse(pubDateSelected))
                            .toList()
                            .get(0);
                    String areaId = pc.getAreaInteresseId();
                    String centroId = pc.getIdCentro();
                    String denomArea = queryHandler
                            .selectObjectWithCond("denominazione", QueryHandler.tables.AREA_INTERESSE, "areaid", areaId)
                            .get(0);
                    String denomCentro = queryHandler
                            .selectObjectWithCond("nomecentro", QueryHandler.tables.CENTRO_MONITORAGGIO, "centroid", centroId)
                            .get(0);
                    System.out.println(pc);
                    System.out.println(denomArea);
                    System.out.println(denomCentro);
                }
            });
            return row;
        });

        if(parameters.size() > 0){
            parameters.forEach(pc -> {
                paramClimaticiTableView.getItems().add(pc.getPubDate());
            });
        }
    }

    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
        }


}
