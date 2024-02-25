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
        final Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");
        Client client = new Client(IDGenerator.generateID());
        MainWindowController mainWindowController = new MainWindowController(stage, client);
        fxmlLoader.setController(mainWindowController);
        Scene scene = new Scene(fxmlLoader.load(),800, 800);
        stage.setTitle("ClimateMonitoringApp!");
        stage.setScene(scene);
        stage.show();
        /**
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/operatore-scene.fxml"));
        Client client = new Client(IDGenerator.generateID());
        OperatoreViewController operatoreViewController = new OperatoreViewController(stage, client);
        fxmlLoader.setController(operatoreViewController);
        Scene scene = new Scene(fxmlLoader.load(), 800, 1200);
        stage.setTitle("ClimateMonitoringApp!");
        stage.setScene(scene);
        stage.show();
         **/
    }

    public static void main(String[] args) {
        launch();
    }
}