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
        final String url = "jdbc:postgresql://192.168.1.26/postgres";
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
        String idHunterTown = "1a4957fdc6382209d4d59bc2469722e5";
        List<ParametroClimatico> params = queryHandler.selectAllWithCond(
                QueryHandler.Tables.PARAM_CLIMATICO,
                "areaid",
                idHunterTown);
        int year = 2008;
        int july = 7;
        int october = 10;
        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator(queryHandler);
        String api_key = "b1579b15-ecb5-47c3-bcb8-9548ee05f230";

        GraphDialog graphDialog = new GraphDialog(queryHandler, idHunterTown, params);
        fxmlLoader.setController(graphDialog);
        Scene scene = new Scene(fxmlLoader.load(),800, 800);
        stage.setTitle("ClimateMonitoringApp!");
        stage.setScene(scene);
        stage.show();
         **/
    }

    public static void main(String[] args) {
        launch();
    }
}