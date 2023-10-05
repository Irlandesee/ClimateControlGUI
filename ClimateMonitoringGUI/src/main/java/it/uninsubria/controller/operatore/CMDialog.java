package it.uninsubria.controller.operatore;

import it.uninsubria.controller.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;

public class CMDialog {

    private final SceneController sceneController;
    private final LinkedList<String> nomiAree;
    //public VBox areeBox;
    public ListView listAree;
    public CMDialog(SceneController sceneController, LinkedList<String> nomiAree){
        this.sceneController = sceneController;
        this.nomiAree = nomiAree;
    }

    @FXML
    public void initialize(){
        for(String nome: nomiAree){
            listAree.getItems().add(new Label(nome));
        }
    }

    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }

}
