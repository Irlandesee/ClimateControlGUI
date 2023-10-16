package it.uninsubria.controller.dialog;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class AIDialog {

    private final SceneController sceneController;
    private final AreaInteresse ai;
    private List<ParametroClimatico> parameters;
    public Label nomeLabel;
    public Label denominazioneLabel;
    public Label statoLabel;
    public Label latitudineLabel;
    public Label longitudineLabel;

    public ListView paramClimaticiList;
    public AIDialog(SceneController sceneController, AreaInteresse ai, List<ParametroClimatico> parameters){
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
        if(parameters.size() > 0){
            for(ParametroClimatico cp: parameters){
                Label l = new Label(String.valueOf(cp.getPubDate()));
                paramClimaticiList.getItems().add(l);
            }

        }
    }

    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }

}
