package it.uninsubria.controller.dialog;

import it.uninsubria.graphbuilder.GraphBuilder;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.control.ToggleSwitch;

import java.nio.channels.AlreadyBoundException;
import java.security.PrivilegedAction;
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

    @FXML
    public VBox contentBox;

    private LineChart<String, Number> monthlyTempLineChart;
    private LineChart<String, Number> dailyTempLineChart;
    private LineChart<String, Number> monthlyWindLineChart;
    private LineChart<String, Number> dailyWindLineChart;
    private LineChart<String, Number> monthlyUmidityLineChart;
    private LineChart<String, Number> dailyUmidityLineChart;
    private LineChart<String, Number> monthlyPressureChart;
    private LineChart<String, Number> dailyPressureChart;
    private LineChart<String, Number> monthlyRainfallChart;
    private LineChart<String, Number> dailyRainfallLineChart;
    private LineChart<String, Number> monthlyAltLineChart;
    private LineChart<String, Number> dailyAltLineChart;
    private LineChart<String, Number> monthlyMassChart;
    private LineChart<String, Number> dailyMassChart;

    @FXML
    public Button btnFilterYear;
    @FXML
    public Button btnFilterMonth;

    @FXML
    public TextField tfYearFilter;
    @FXML
    public TextField tfMonthFilter;

    private final QueryHandler queryHandler;

    private final List<ParametroClimatico> params;
    private final String areaId;

    private final int defaultYear = 1900;
    private final int defaultMonth = 1;

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

        monthlyTempLineChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.temperature);
        dailyTempLineChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.temperature, defaultYear, defaultMonth);

        monthlyWindLineChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.wind);
        dailyWindLineChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.wind, defaultYear, defaultMonth);

        monthlyUmidityLineChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.umidity);
        dailyUmidityLineChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.umidity, defaultYear, defaultMonth);

        monthlyPressureChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.atmPressure);
        dailyPressureChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.atmPressure, defaultYear, defaultMonth);

        monthlyRainfallChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.rainfall);
        dailyRainfallLineChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.rainfall, defaultYear, defaultMonth);

        monthlyAltLineChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.rainfall);
        dailyAltLineChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.rainfall, defaultYear, defaultMonth);

        monthlyMassChart = GraphBuilder.getBasicMonthLineChart(GraphBuilder.Resource.rainfall);
        dailyMassChart = GraphBuilder.getBasicDailyLineChart(GraphBuilder.Resource.rainfall, defaultYear, defaultMonth);


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
        List<ParametroClimatico> filteredParams = params.stream().filter(param -> param.getPubDate().getMonth().equals(Month.JANUARY)).toList();
        List<Pair<LocalDate, Number>> data = calcData(filteredParams, ParameterType.temperatura);
        data.forEach(pair -> series
                .getData()
                .add(new XYChart.Data<>(String.valueOf(pair.getKey().getDayOfMonth()), pair.getValue())));

        monthlyTempLineChart.getData().add(series);
        contentBox.getChildren().add(monthlyTempLineChart);
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

    private XYChart.Series<String, Number> addMonthlyDataToSeries(List<Pair<LocalDate, Number>> data, XYChart.Series<String, Number> series){
        data.forEach(param -> series
                .getData()
                .add(new XYChart.Data<String, Number>(String.valueOf(param.getKey().getMonth()), param.getValue())));
        return series;
    }

    private XYChart.Series<String, Number> addDailyDataToSeries(List<Pair<LocalDate, Number>> data, XYChart.Series<String, Number> series){
        data.forEach(param -> series
                .getData()
                .add(new XYChart.Data<String, Number>(String.valueOf(param.getKey().getDayOfMonth()), param.getValue())));
        return series;
    }

    @FXML
    public void filterMonth(){
        String monthFilterText = tfMonthFilter.getText();
        if(monthFilterText.isEmpty())
            new Alert(Alert.AlertType.ERROR, "Campo non valido").showAndWait();

        int month = Integer.parseInt(monthFilterText);

        List<ParametroClimatico> params = queryHandler.selectAllWithCond(QueryHandler.tables.PARAM_CLIMATICO, "areaid", areaId);
        int year = params.get(0).getPubDate().getYear();
        Month m = Month.of(month);
        List<ParametroClimatico> filteredParams = params
                .stream()
                .filter(pc -> pc.getPubDate().getMonth().equals(m))
                .toList();

        List<Pair<LocalDate, Number>> data = new LinkedList<Pair<LocalDate, Number>>();
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        for(Node n : contentBox.getChildren()){
            if(n.equals(monthlyTempLineChart) && monthlyTempLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.temperatura);
                monthlyTempLineChart.getData().add(addMonthlyDataToSeries(data, series));
            }
            else if(n.equals(dailyTempLineChart) && dailyTempLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.temperatura);
                dailyTempLineChart.getData().add(addDailyDataToSeries(data, series));
            }
            else if(n.equals(monthlyWindLineChart) && monthlyWindLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.vento);
                monthlyWindLineChart.getData().add(addMonthlyDataToSeries(data, series));
            }
            else if(n.equals(dailyWindLineChart) && dailyWindLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.vento);
                dailyWindLineChart.getData().add(addDailyDataToSeries(data, series));
            }
            else if(n.equals(monthlyUmidityLineChart) && monthlyUmidityLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.umidita);
                monthlyUmidityLineChart.getData().add(addMonthlyDataToSeries(data, series));
            }
            else if(n.equals(dailyUmidityLineChart) && dailyUmidityLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.umidita);
                dailyUmidityLineChart.getData().add(addDailyDataToSeries(data, series));
            }
            else if(n.equals(monthlyPressureChart) && monthlyPressureChart.isVisible()){
                data = calcData(filteredParams, ParameterType.pressione);
                monthlyPressureChart.getData().add(addMonthlyDataToSeries(data, series));
            }
            else if(n.equals(dailyPressureChart) && dailyPressureChart.isVisible()){
                data = calcData(filteredParams, ParameterType.pressione);
                dailyPressureChart.getData().add(addDailyDataToSeries(data, series));
            }
            else if(n.equals(monthlyRainfallChart) && monthlyRainfallChart.isVisible()){
                data = calcData(filteredParams, ParameterType.precipitazioni);
                monthlyRainfallChart.getData().add(addMonthlyDataToSeries(data, series));
            }
            else if(n.equals(dailyRainfallLineChart) && dailyRainfallLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.precipitazioni);
                dailyRainfallLineChart.getData().add(addDailyDataToSeries(data, series));
            }
            else if(n.equals(monthlyAltLineChart) && monthlyAltLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.alt_ghiacciai);
                monthlyAltLineChart.getData().add(addMonthlyDataToSeries(data, series));
            }
            else if(n.equals(dailyAltLineChart) && dailyAltLineChart.isVisible()){
                data = calcData(filteredParams, ParameterType.alt_ghiacciai);
                dailyAltLineChart.getData().add(addDailyDataToSeries(data, series));
            }
            else if(n.equals(monthlyMassChart) && monthlyMassChart.isVisible()){
                data = calcData(filteredParams, ParameterType.massa_ghiacciai);
                monthlyMassChart.getData().add(addMonthlyDataToSeries(data, series));
            }
            else if(n.equals(dailyMassChart) && dailyMassChart.isVisible()){
                data = calcData(filteredParams, ParameterType.massa_ghiacciai);
                dailyMassChart.getData().add(addDailyDataToSeries(data, series));
            }
        }

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
    public void monthlyTemp(){
        if(contentBox.getChildren().contains(monthlyTempLineChart)){
            System.out.println("Rimuovendo grafico mensile temperatura");
            contentBox.getChildren().remove(monthlyTempLineChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico mensile temperatura");
                contentBox.getChildren().add(monthlyTempLineChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }
    }

    @FXML
    public void dailyTemp(){
        if(contentBox.getChildren().contains(dailyTempLineChart)){
            System.out.println("Rimuovendo grafico giornaliero temperatura");
            contentBox.getChildren().remove(dailyTempLineChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico giornaliero temperatura");
                contentBox.getChildren().add(dailyTempLineChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }
    }

    @FXML
    public void monthlyWind(){
        if(contentBox.getChildren().contains(monthlyWindLineChart)){
            System.out.println("Rimuovendo grafico mensile vento");
            contentBox.getChildren().remove(monthlyWindLineChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico mensile vento");
                contentBox.getChildren().add(monthlyWindLineChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }

    }

    @FXML
    public void dailyWind(){
        if(contentBox.getChildren().contains(dailyWindLineChart)){
            System.out.println("Rimuovendo grafico giornaliero vento");
            contentBox.getChildren().remove(dailyWindLineChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico giornaliero vento");
                contentBox.getChildren().add(dailyWindLineChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }

    }

    @FXML
    public void monthlyUmidity(){
        if(contentBox.getChildren().contains(monthlyUmidityLineChart)){
            System.out.println("Rimuovendo grafico mensile umidita");
            contentBox.getChildren().remove(monthlyUmidityLineChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico mensile umidita");
                contentBox.getChildren().add(monthlyUmidityLineChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }
    }

    @FXML
    public void dailyUmidity(){
        if(contentBox.getChildren().contains(dailyUmidityLineChart)){
            System.out.println("Rimuovendo grafico giornaliero umidita");
            contentBox.getChildren().remove(dailyUmidityLineChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico giornaliero umidita");
                contentBox.getChildren().add(dailyUmidityLineChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }

    }

    @FXML
    public void monthlyPressure(){
        if(contentBox.getChildren().contains(monthlyPressureChart)){
            System.out.println("Rimuovendo grafico mensile pressione");
            contentBox.getChildren().remove(monthlyPressureChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico mensile pressione");
                contentBox.getChildren().add(monthlyPressureChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }
    }

    @FXML
    public void dailyPressure(){
        if(contentBox.getChildren().contains(dailyPressureChart)){
            System.out.println("Rimuovendo grafico giornaliero pressione");
            contentBox.getChildren().remove(dailyPressureChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico giornaliero pressione");
                contentBox.getChildren().add(dailyPressureChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }
    }

    @FXML
    public void monthlyRainfall(){
        if(contentBox.getChildren().contains(monthlyRainfallChart)){
            System.out.println("Rimuovendo grafico mensile precipitazioni");
            contentBox.getChildren().remove(monthlyRainfallChart);
        }
        else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico mensile precipitazioni");
                contentBox.getChildren().add(monthlyRainfallChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }

    }

    @FXML
    public void dailyRainfall() {
        if (contentBox.getChildren().contains(dailyRainfallLineChart)) {
            System.out.println("Rimuovendo grafico giornaliero precipitazioni");
            contentBox.getChildren().remove(dailyRainfallLineChart);
        } else {
            if (contentBox.getChildren().size() < 2) {
                System.out.println("Aggiungendo grafico giornaliero precipitazioni");
                contentBox.getChildren().add(dailyRainfallLineChart);
            } else {
                System.out.println("Grafico non aggiunto: troppi grafici in content box");
            }
        }
    }

        @FXML
        public void monthlyAlt () {
            if (contentBox.getChildren().contains(monthlyAltLineChart)) {
                System.out.println("Rimuovendo grafico mensile altitudine");
                contentBox.getChildren().remove(monthlyAltLineChart);
            } else {
                if (contentBox.getChildren().size() < 2) {
                    System.out.println("Aggiungendo grafico mensile altitudine");
                    contentBox.getChildren().add(monthlyAltLineChart);
                } else {
                    System.out.println("Grafico non aggiunto: troppi grafici in content box");
                }
            }

        }

        @FXML
        public void dailyAlt () {
            if (contentBox.getChildren().contains(dailyAltLineChart)) {
                System.out.println("Rimuovendo grafico giornaliero altitudine");
                contentBox.getChildren().remove(dailyAltLineChart);
            } else {
                if (contentBox.getChildren().size() < 2) {
                    System.out.println("Aggiungendo grafico giornaliero altitudine");
                    contentBox.getChildren().add(dailyAltLineChart);
                } else {
                    System.out.println("Grafico non aggiunto: troppi grafici in content box");
                }
            }

        }

        @FXML
        public void monthlyMass () {
            if (contentBox.getChildren().contains(monthlyMassChart)) {
                System.out.println("Rimuovendo grafico mensile massa");
                contentBox.getChildren().remove(monthlyMassChart);
            } else {
                if (contentBox.getChildren().size() < 2) {
                    System.out.println("Aggiungendo grafico mensile massa");
                    contentBox.getChildren().add(monthlyMassChart);
                } else {
                    System.out.println("Grafico non aggiunto: troppi grafici in content box");
                }
            }
        }

        @FXML
        public void dailyMass () {
            if (contentBox.getChildren().contains(dailyMassChart)) {
                System.out.println("Rimuovendo grafico giornaliero massa");
                contentBox.getChildren().remove(dailyMassChart);
            } else {
                if (contentBox.getChildren().size() < 2) {
                    System.out.println("Aggiungendo grafico giornaliero massa");
                    contentBox.getChildren().add(dailyMassChart);
                } else {
                    System.out.println("Grafico non aggiunto: troppi grafici in content box");
                }
            }

        }

        @FXML
        public void close (ActionEvent actionEvent){
            Stage s = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            if (s != null)
                s.close();
        }

}

