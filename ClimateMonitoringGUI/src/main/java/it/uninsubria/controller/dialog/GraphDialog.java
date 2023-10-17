package it.uninsubria.controller.dialog;

import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GraphDialog {

    public Label graphName;
    public VBox contentBox;
    public Button closeButton;
    public SceneController sceneController;
    private ArrayList<ParametroClimatico> params;
    public GraphDialog(SceneController sceneController){
        this.sceneController = sceneController;
        params = new ArrayList<ParametroClimatico>();
    }

    @FXML
    public void initialize(){

    }

    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }

}
