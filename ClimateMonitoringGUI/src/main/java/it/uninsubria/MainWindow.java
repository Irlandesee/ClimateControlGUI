package it.uninsubria;
import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.util.IDGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

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
    //ip address iphone: 172.20.10.3

    public static void main(String[] args) {
        launch();
    }
}