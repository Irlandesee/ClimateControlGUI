package it.uninsubria;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.controller.dialog.GraphDialog;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.scene.SceneController;
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
        /**
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/main-scene.fxml"));
        MainWindowController mainWindowController = new MainWindowController();
        fxmlLoader.setController(mainWindowController);
         **/
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/graph-dialog.fxml"));
        final String url = "jdbc:postgresql://localhost/postgres";
        final Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");
        QueryHandler queryHandler = new QueryHandler(url, props);
        List<ParametroClimatico> params = queryHandler.selectAllWithCond(
                QueryHandler.tables.PARAM_CLIMATICO,
                "areaid",
                "372be289572b5a6408c39baacd66758e");
        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator(queryHandler);
        List<ParametroClimatico> randomParams = fakeDataGenerator.generateParamClimatici(100);
        randomParams.forEach(System.out::println);
        GraphDialog graphDialog = new GraphDialog(queryHandler, params);
        fxmlLoader.setController(graphDialog);
        Scene scene = new Scene(fxmlLoader.load(),800, 480);
        stage.setTitle("ClimateMonitoringApp!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}