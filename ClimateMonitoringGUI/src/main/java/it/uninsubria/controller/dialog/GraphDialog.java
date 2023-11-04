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
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.control.ToggleSwitch;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

public class GraphDialog {

    private enum ParameterType{
        vento,
        temperatura,
        umidita,
        pressione,
        precipitazioni,
        alt_ghiacciai,
        massa_ghiacciai

    };

    private enum monthlyChart{
        monthlyWindChart,
        monthlyUmidityChart,
        monthlyTemperatureChart,
        monthlyPressureChart,
        monthlyRainfallChart,
        monthlyAltChart,
        monthlyMassChart,
    };

    private enum dailyChart{
        dailyWindChart,
        dailyUmidityChart,
        dailyTemperatureChart,
        dailyPressureChart,
        dailyRainfallChart,
        dailyAltChart,
        dailyMassChart,
    }

    public Label graphName;
    public VBox contentBox;

    @FXML
    public ToggleSwitch switchMode; //if true -> show montly view
    @FXML
    public Button closeButton;
    @FXML
    public Button ventoButton;
    @FXML
    public Button umiditaButton;
    @FXML
    public Button pressioneButton;
    @FXML
    public Button precipitazioniButton;
    @FXML
    public Button altitudineButton;
    @FXML
    public Button massaButton;
    @FXML
    public Button temperaturaButton;
    @FXML
    public Button btnFilterYear;
    @FXML
    public Button btnFilterMonth;

    @FXML
    public TextField tfYearFilter;
    @FXML
    public TextField tfMonthFilter;

    private final QueryHandler queryHandler;

    private LineChart<String, Number> ventoChart;
    private LineChart<String, Number> umiditaChart;
    private LineChart<String, Number> pressioneChart;
    private LineChart<String, Number> temperaturaChart;
    private LineChart<String, Number> precipitazioniChart;
    private LineChart<String, Number> altitudineChart;
    private LineChart<String, Number> massaChart;

    private LineChart<String, Number> dailyTemperatureChart;
    private LineChart<String, Number> dailyUmiditaChart;
    private LineChart<String, Number> dailyPressioneChart;
    private LineChart<String, Number> dailyPrecipitazioniChart;
    private LineChart<String, Number> dailyVentoChart;
    private LineChart<String, Number> dailyAltitudineChart;
    private LineChart<String, Number> dailyMassaChart;

    private List<ParametroClimatico> params;
    private String areaId;
    public GraphDialog(QueryHandler queryHandler, String areaId,  List<ParametroClimatico> params){
        this.queryHandler = queryHandler;
        this.params = params;
        this.areaId = areaId;
    }


    /**
     * Note to self:
     * Si assume che venga passata una lista che rappresenta l'interita
     * dei parametri climatici registrati per quell'area e verranno
     * rappresentati sul grafico.
     * **/
    @FXML
    public void initialize(){
        //init the graphs
        ventoChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.wind);
        ventoChart.setUserData(monthlyChart.monthlyWindChart);
        umiditaChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.umidity);
        umiditaChart.setUserData(monthlyChart.monthlyUmidityChart);
        temperaturaChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.temperature);
        temperaturaChart.setUserData(monthlyChart.monthlyTemperatureChart);
        pressioneChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.atmPressure);
        pressioneChart.setUserData(monthlyChart.monthlyPressureChart);
        precipitazioniChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.rainfall);
        precipitazioniChart.setUserData(monthlyChart.monthlyRainfallChart);
        altitudineChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.glacierAlt);
        altitudineChart.setUserData(monthlyChart.monthlyAltChart);
        massaChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.glacierMass);
        massaChart.setUserData(monthlyChart.monthlyMassChart);

        dailyTemperatureChart.setUserData(dailyChart.dailyTemperatureChart);
        dailyVentoChart.setUserData(dailyChart.dailyWindChart);
        dailyUmiditaChart.setUserData(dailyChart.dailyUmidityChart);
        dailyPressioneChart.setUserData(dailyChart.dailyPressureChart);
        dailyPrecipitazioniChart.setUserData(dailyChart.dailyRainfallChart);
        dailyAltitudineChart.setUserData(dailyChart.dailyAltChart);
        dailyMassaChart.setUserData(dailyChart.dailyMassChart);

        tfMonthFilter.setOnMouseClicked(e -> tfMonthFilter.setText(""));
        tfYearFilter.setOnMouseClicked(e -> tfYearFilter.setText(""));

        //default charts - temperature charts
        String denomArea = queryHandler.selectObjectWithCond(
                "denominazione",
                QueryHandler.tables.AREA_INTERESSE,
                "areaid",
                params.get(0).getAreaInteresseId()).get(0);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(denomArea);
        if(switchMode.isSelected()){
            contentBox.getChildren().add(dailyTemperatureChart);
            //the default month is January
            List<ParametroClimatico> filteredParams = params.stream().filter(param -> param.getPubDate().getMonth().equals(Month.JANUARY)).toList();
            List<Pair<LocalDate, Number>> data = calcData(filteredParams, ParameterType.temperatura);
            data.forEach(pair -> series
                    .getData()
                    .add(new XYChart.Data<>(String.valueOf(pair.getKey().getDayOfMonth()), pair.getValue())));
        }else{
            contentBox.getChildren().add(temperaturaChart);
            List<Pair<Month, List<ParametroClimatico>>> filteredParamsMonth = new LinkedList<Pair<Month, List<ParametroClimatico>>>();
            for(Month m : Month.values()){
                System.out.println("filtering month: " + m);
                List<ParametroClimatico> monthParameters = params
                        .stream()
                        .filter(pc -> pc.getPubDate().getMonth().equals(m))
                        .toList();
                filteredParamsMonth.add(new Pair<Month, List<ParametroClimatico>>(m, monthParameters));
                params.removeAll(monthParameters);
            }

            List<Pair<Number, String>> data = new LinkedList<Pair<Number, String>>();
            for(Pair<Month, List<ParametroClimatico>> pair : filteredParamsMonth){
                //System.out.println("printing list that corresponds to: " + pair.getKey());
                List<ParametroClimatico> values = pair.getValue();
                //values.forEach(System.out::println);
                data.add(new Pair<Number, String>(
                    getAverageValue(values, ParameterType.temperatura), GraphBuilder.getLocaleMonth(pair.getKey().getValue())));
            }

            //add the values to the graph
            data.forEach(pair -> {
                series.getData().add(new XYChart.Data<>(pair.getValue(), pair.getKey()));
            });

            temperaturaChart.getData().add(series);
        }

    }

    private Number getAverageValue(List<ParametroClimatico> params, ParameterType parameterType){
        int averageValue = 0;
        switch(parameterType){
            case vento -> {
                for(ParametroClimatico p : params)
                    averageValue += p.getVentoValue();
            }
            case umidita -> {
                for(ParametroClimatico p : params)
                    averageValue += p.getUmiditaValue();
            }
            case temperatura -> {
                for(ParametroClimatico p : params)
                    averageValue += p.getTemperaturaValue();
            }
            case pressione -> {
                for(ParametroClimatico p : params)
                    averageValue += p.getPressioneValue();
            }
            case precipitazioni -> {
                for(ParametroClimatico p : params)
                    averageValue += p.getPrecipitazioniValue();
            }
            case alt_ghiacciai -> {
                for(ParametroClimatico p : params)
                    averageValue += p.getAltitudineValue();
            }
            case massa_ghiacciai -> {
                for(ParametroClimatico p : params)
                    averageValue += p.getMassaValue();
            }
        }
        return averageValue/params.size();
    }

    private int getNumOccurrences(List<ParametroClimatico> params, int day){
        return (int)params.stream().filter(p -> p.getPubDate().getDayOfMonth() == day).count();
    }

    @FXML
    public void filterYear(){

    }

    @FXML
    public void filterMonth(){
        tfMonthFilter.clear();

        //if there are monthly charts, remove them
        for(Node child : contentBox.getChildren()){
            Object childUserData = child.getUserData();
            if(childUserData.equals(monthlyChart.monthlyWindChart)
                    || childUserData.equals(monthlyChart.monthlyUmidityChart)
                    || childUserData.equals(monthlyChart.monthlyTemperatureChart)
                    || childUserData.equals(monthlyChart.monthlyPressureChart)
                    || childUserData.equals(monthlyChart.monthlyRainfallChart)
                    || childUserData.equals(monthlyChart.monthlyAltChart)
                    || childUserData.equals(monthlyChart.monthlyMassChart)){
                contentBox.getChildren().remove(child);
                System.out.println("Removed child");
            }
        }

        int month = Integer.parseInt(tfMonthFilter.getText());
        List<ParametroClimatico> params = queryHandler.selectAllWithCond(QueryHandler.tables.PARAM_CLIMATICO, "areaid", areaId);
        int year = params.get(0).getPubDate().getYear();
        Month m = Month.of(month);
        List<ParametroClimatico> filteredParams = params
                .stream()
                .filter(pc -> pc.getPubDate().getMonth().equals(m))
                .toList();


        List<Pair<LocalDate, Number>> data = calcData(filteredParams, );

        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();


        data.forEach(pair -> series
                .getData()
                .add(new XYChart.Data<>(String.valueOf(pair.getKey().getDayOfMonth()), pair.getValue())));


        dailyTemperatureChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.temperature, year, month);
        dailyTemperatureChart.getData().add(series);
        contentBox.getChildren().add(dailyTemperatureChart);
        tfMonthFilter.setText("Filtra Mese");
    }


    private List<Pair<LocalDate, Number>> calcData(List<ParametroClimatico> filteredParams, ParameterType pType){
        List<Pair<LocalDate, Number>> data = new LinkedList<Pair<LocalDate, Number>>();
        for(ParametroClimatico param : filteredParams){
            LocalDate pubDate = param.getPubDate();
            if(getNumOccurrences(filteredParams, pubDate.getDayOfMonth()) > 1) {
                List<ParametroClimatico> paramsSameDate = filteredParams.stream().filter(pc -> pc.getPubDate().equals(param.getPubDate())).toList();
                switch(pType){
                    case vento ->
                        data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(paramsSameDate, ParameterType.vento)));
                    case temperatura ->
                        data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(paramsSameDate, ParameterType.temperatura)));
                    case umidita ->
                        data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(paramsSameDate, ParameterType.umidita)));
                    case pressione ->
                        data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(paramsSameDate, ParameterType.pressione)));
                    case precipitazioni ->
                        data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(paramsSameDate, ParameterType.precipitazioni)));
                    case alt_ghiacciai ->
                        data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(paramsSameDate, ParameterType.alt_ghiacciai)));
                    case massa_ghiacciai ->
                        data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(paramsSameDate, ParameterType.massa_ghiacciai)));
                }
            } else {
                switch(pType){
                    case vento ->
                            data.add(new Pair<LocalDate, Number>(param.getPubDate(), param.getVentoValue()));
                    case temperatura ->
                            data.add(new Pair<LocalDate, Number>(param.getPubDate(), param.getTemperaturaValue()));
                    case umidita ->
                            data.add(new Pair<LocalDate, Number>(param.getPubDate(), param.getUmiditaValue()));
                    case pressione ->
                            data.add(new Pair<LocalDate, Number>(param.getPubDate(), param.getPressioneValue()));
                    case precipitazioni ->
                            data.add(new Pair<LocalDate, Number>(param.getPubDate(), param.getPrecipitazioniValue()));
                    case alt_ghiacciai ->
                            data.add(new Pair<LocalDate, Number>(param.getPubDate(), param.getAltitudineValue()));
                    case massa_ghiacciai ->
                            data.add(new Pair<LocalDate, Number>(param.getPubDate(), param.getMassaValue()));
                }
            }
        }
        return data;
    }

    @FXML
    public void addVento(){
        boolean mode = switchMode.isSelected();
        boolean isMonthlyWindChartPresent = contentBox.getChildren().contains(ventoChart);
        boolean isDailyWindChartPresent = contentBox.getChildren().contains(dailyVentoChart);
        if(mode && isMonthlyWindChartPresent && !isDailyWindChartPresent){
            //vista giornaliera , grafico mensile presente, grafico giornaliero non presente
            contentBox.getChildren().remove(ventoChart);
            contentBox.getChildren().add(dailyVentoChart);
        }else if(!mode && isDailyWindChartPresent && !isMonthlyWindChartPresent){
            //vista mensile, grafico giornaliero presente, grafico mensile non presente
            contentBox.getChildren().remove(dailyVentoChart);
            contentBox.getChildren().add(ventoChart);
        }
    }

    @FXML
    public void addTemperatura(){
        boolean mode = switchMode.isSelected();
        boolean isMonthlyTempChartPresent = contentBox.getChildren().contains(temperaturaChart);
        boolean isDailyTempChartPresent = contentBox.getChildren().contains(dailyTemperatureChart);
        if(mode && isMonthlyTempChartPresent && !isDailyTempChartPresent){
            //vista giornaliera, grafico mensile presente, grafico gioraliero non presente
            contentBox.getChildren().remove(temperaturaChart);
            contentBox.getChildren().add(dailyTemperatureChart);
        }else if(!mode && isDailyTempChartPresent && isMonthlyTempChartPresent){
            contentBox.getChildren().remove(dailyTemperatureChart);
            contentBox.getChildren().add(temperaturaChart);
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
