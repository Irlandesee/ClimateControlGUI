package it.uninsubria.controller.dialog;

import it.uninsubria.graphbuilder.GraphBuilder;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.time.Month;
import java.util.*;

public class GraphDialog {

    public Label graphName;
    public VBox contentBox;
    public Button closeButton;
    public Button ventoButton;
    public Button umiditaButton;
    public Button pressioneButton;
    public Button precipitazioniButton;
    public Button altitudineButton;
    public Button massaButton;
    public Button temperaturaButton;
    //private final SceneController sceneController;
    private final QueryHandler queryHandler;

    private LineChart<String, Number> ventoChart;
    private LineChart<String, Number> umiditaChart;
    private LineChart<String, Number> pressioneChart;
    private LineChart<String, Number> temperaturaChart;
    private LineChart<String, Number> precipitazioniChart;
    private LineChart<String, Number> altitudineChart;
    private LineChart<String, Number> massaChart;

    private List<ParametroClimatico> params;
    public GraphDialog(QueryHandler queryHandler, List<ParametroClimatico> params){
        //this.sceneController = sceneController;
        this.queryHandler = queryHandler;
        this.params = params;
    }

    @FXML
    public void initialize(){
        //init the graphs
        ventoChart = GraphBuilder.getBasicLineChart(GraphBuilder.Resource.wind);
        umiditaChart = GraphBuilder.getBasicLineChart(GraphBuilder.Resource.umidity);
        temperaturaChart = GraphBuilder.getBasicLineChart(GraphBuilder.Resource.temperature);
        pressioneChart = GraphBuilder.getBasicLineChart(GraphBuilder.Resource.atmPressure);
        precipitazioniChart = GraphBuilder.getBasicLineChart(GraphBuilder.Resource.rainfall);
        altitudineChart = GraphBuilder.getBasicLineChart(GraphBuilder.Resource.glacierAlt);
        massaChart = GraphBuilder.getBasicLineChart(GraphBuilder.Resource.glacierMass);

        contentBox
                .getChildren()
                .addAll(temperaturaChart); //Default chart?
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String denomArea = queryHandler.selectObjectWithCond(
                "denominazione",
                QueryHandler.tables.AREA_INTERESSE,
                "areaid",
                params.get(0).getAreaInteresseId()).get(0);
        series.setName(denomArea);

        /**
         * Note to self:
         * Si assume che venga passata una lista che rappresenta l'interita
         * dei parametri climatici registrati per quell'area e verranno
         * rappresentati sul grafico.
         */
        List<Pair<Number, String>> data = calculateAverageTemp(params);
        data.forEach(coppia -> {
            String month = coppia.getValue();
            Number avgTemp = coppia.getKey();
            System.out.println(month + " -> " + avgTemp);
            series.getData().add(
                    new XYChart.Data<>(month, avgTemp));
        });


        temperaturaChart.getData().add(series);


    }

    private List<Pair<Number, String>> calculateAverageTemp(List<ParametroClimatico> params){
        List<Pair<Number, String>> res = new LinkedList<Pair<Number, String>>();
        List<Pair<Month, List<ParametroClimatico>>> filteredParams = new LinkedList<Pair<Month, List<ParametroClimatico>>>();
        List<Month> months = Arrays.stream(Month.values()).toList();

        for(Month m : months){
            System.out.println("filtering month: " + m);
            List<ParametroClimatico> monthParameters = params.stream()
                    .filter(param -> param.getPubDate().getMonth().equals(m))
                    .toList();
            filteredParams.add(new Pair<Month, List<ParametroClimatico>>(m, monthParameters));
            params.removeAll(monthParameters);
        }

        for(Pair<Month, List<ParametroClimatico>> pair : filteredParams){
            System.out.println("printing list that corresponds to: " + pair.getKey());
            List<ParametroClimatico> values = pair.getValue();
            values.forEach(System.out::println);

            res.add(new Pair<Number, String>(
                    getMonthAverageTemp(values), GraphBuilder.getLocaleMonth(pair.getKey().getValue())));
        }



        return res;
    }

    private Number getMonthAverageTemp(List<ParametroClimatico> monthParameters){
        int averageTemperature = 0;
        for(ParametroClimatico p : monthParameters){
            averageTemperature += p.getTemperaturaValue();
        }
        return averageTemperature / monthParameters.size();
    }



    @FXML
    public void addVento(){
        boolean isPresent = contentBox.getChildren().contains(ventoChart);
        if(isPresent) {
            System.out.println("Removing wind chart");
            contentBox.getChildren().remove(ventoChart);
        }
        else {
            System.out.println("Adding wind chart");
            contentBox.getChildren().add(ventoChart);
        }
    }

    @FXML
    public void addUmidita(){
        boolean isPresent = contentBox.getChildren().contains(umiditaChart);
        if(isPresent){
            System.out.println("Removing umidity chart");
            contentBox.getChildren().remove(umiditaChart);
        }
        else {
            System.out.println("Adding umidity chart");
            contentBox.getChildren().add(umiditaChart);
        }
    }

    @FXML
    public void addPressione(){
        boolean isPresent = contentBox.getChildren().contains(pressioneChart);
        if(isPresent) {
            System.out.println("Removing pressure chart");
            contentBox.getChildren().remove(pressioneChart);
        }
        else {
            System.out.println("Adding pressure chart");
            contentBox.getChildren().add(pressioneChart);
        }
    }

    @FXML
    public void addTemperatura(){

    }

    @FXML
    public void addPrecipitazioni(){
        boolean isPresent = contentBox.getChildren().contains(precipitazioniChart);
        if(isPresent) {
            System.out.println("Removing temperature chart");
            contentBox.getChildren().remove(precipitazioniChart);
        }
        else {
            System.out.println("Adding temperature chart");
            contentBox.getChildren().add(precipitazioniChart);
        }
    }

    @FXML
    public void addAltitudine(){
        boolean isPresent = contentBox.getChildren().contains(altitudineChart);
        if(isPresent) {
            System.out.println("Removing altitude chart");
            contentBox.getChildren().remove(altitudineChart);
        }
        else {
            System.out.println("Adding altitude chart");
            contentBox.getChildren().add(altitudineChart);
        }
    }

    @FXML
    public void addMassa(){
        boolean isPresent = contentBox.getChildren().contains(massaChart);
        if(isPresent) {
            System.out.println("Removing glacier mass chart");
            contentBox.getChildren().remove(massaChart);
        }
        else {
            System.out.println("Adding glacier mass chart");
            contentBox.getChildren().add(massaChart);
        }
    }

    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }

}
