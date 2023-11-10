package it.uninsubria;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.controller.dialog.GraphDialog;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.parametroClimatico.NotaParametro;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import it.uninsubria.util.FakeDataGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Collectors;

public class MainWindow extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/main-scene.fxml"));
        final String url = "jdbc:postgresql://192.168.1.26/postgres";
        final Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");
        //QueryHandler queryHandler = new QueryHandler(url, props);
        MainWindowController mainWindowController = new MainWindowController();
        fxmlLoader.setController(mainWindowController);
        Scene scene = new Scene(fxmlLoader.load(),800, 800);
        stage.setTitle("ClimateMonitoringApp!");
        stage.setScene(scene);
        stage.show();
        /**
        String idHunterTown = "1a4957fdc6382209d4d59bc2469722e5";
        List<ParametroClimatico> params = queryHandler.selectAllWithCond(
                QueryHandler.tables.PARAM_CLIMATICO,
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