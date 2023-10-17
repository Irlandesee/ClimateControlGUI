package it.uninsubria.controller.dialog;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.parametroClimatico.ParametroClimatico;
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
    public AiDialog(SceneController sceneController, AreaInteresse ai, List<ParametroClimatico> parameters){
        this.sceneController = sceneController;
        this.ai = ai;
        this.parameters = parameters;
    }

    @FXML
    public void initialize(){
        nomeLabel.setText(ai.getDenominazione());
        denominazioneLabel.setText(ai.getDenominazione());
        statoLabel.setText(ai.getStato());
        latitudineLabel.setText(String.valueOf(ai.getLatitudine()));
        longitudineLabel.setText(String.valueOf(ai.getLongitudine()));

        TableColumn pubDateColumn = new TableColumn("Data pubblicazione");
        pubDateColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, String>("pubDate"));
        TableColumn nomeCentroColumn = new TableColumn("Nome Centro");
        nomeCentroColumn.setCellValueFactory(new PropertyValueFactory<ParametroClimatico, String>("denominazione"));

        paramClimaticiTableView.getColumns().addAll(pubDateColumn, nomeCentroColumn);

        paramClimaticiTableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    String pubDate = row.getItem().toString();
                    System.out.println(pubDate);
                }
            });
            return row;
        });

        if(parameters.size() > 0){
            parameters.forEach(pc -> paramClimaticiTableView.getItems().add(pc));
        }
    }

    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }


}
