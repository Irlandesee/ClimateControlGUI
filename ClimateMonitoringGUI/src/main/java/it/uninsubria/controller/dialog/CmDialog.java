package it.uninsubria.controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class CmDialog {

    private final List<String> nomiAree;
    //public VBox areeBox;
    public ListView listAree;
    public CmDialog(List<String> nomiAree){
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
