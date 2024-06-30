package it.climatemonitoring;
import it.climatemonitoring.controller.mainscene.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/main-scene.fxml"));
        //final String url = "jdbc:postgresql://192.168.1.26/postgres";
        MainWindowController mainWindowController = new MainWindowController(stage);
        fxmlLoader.setController(mainWindowController);
        Scene scene = new Scene(fxmlLoader.load(),800, 800);
        stage.setTitle("ClimateMonitoringApp!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}