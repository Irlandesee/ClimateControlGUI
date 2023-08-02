package it.uninsubria.climatemonitoringgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrazioneController {
    public void registraOp(ActionEvent actionEvent) {
        //For now it goes back to the main scene
        try{
            Parent root = FXMLLoader.load(getClass().getResource("main-scene.fxml")); //watch out for this line of code
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }
}
