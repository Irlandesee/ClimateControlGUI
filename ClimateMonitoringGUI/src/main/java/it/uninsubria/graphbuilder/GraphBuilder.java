package it.uninsubria.graphbuilder;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.chart.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GraphBuilder {

    public enum Resource {
        wind,
        umidity,
        atmPressure,
        rainfall,
        temperature,
        glacierAlt,
        glacierMass
    }

    private static String[] months = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio",
            "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};

    public GraphBuilder() {

    }

    public static LineChart<String, Number> getBasicExample() {
        //Defining thet axes
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableList(Arrays.asList(months)));
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Mese");
        //creating chart
        final LineChart<String, Number> lineChart =
                new LineChart<String, Number>(xAxis, yAxis);
        lineChart.setTitle("Basic Example Chart");
        return lineChart;
    }

    public static LineChart<String, Number> getBasicLineChart(Resource r) {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableList(Arrays.asList(months)));
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(1);
        yAxis.setUpperBound(5);
        yAxis.setTickUnit(1);
        xAxis.setLabel("Mese");
        yAxis.setLabel("Valore");
        final LineChart<String, Number> lineChart =
                new LineChart<String, Number>(xAxis, yAxis);

        XYChart.Series series = new XYChart.Series();
        switch (r) {
            case wind -> {
                series.setName("Vento");
                lineChart.getData().add(series);
                return lineChart;
            }
            case umidity -> {
                series.setName("Umidita");
                lineChart.getData().add(series);
                return lineChart;
            }
            case atmPressure -> {
                series.setName("Pressione");
                lineChart.getData().add(series);
                return lineChart;
            }
            case rainfall -> {
                series.setName("Precipitazioni");
                lineChart.getData().add(series);
                return lineChart;
            }
            case temperature -> {
                series.setName("Temperatura");
                lineChart.getData().add(series);
                return lineChart;
            }
            case glacierAlt -> {
                series.setName("Altitudine Ghiacciai");
                lineChart.getData().add(series);
                return lineChart;
            }
            case glacierMass -> {
                series.setName("Massa Ghiacciai");
                lineChart.getData().add(series);
                return lineChart;
            }
            default -> {
                //TODO: throw exception maybe?
                return null;
            }
        }
    }

    public XYChart.Series<LocalDate, Short> getSeries(Resource r, List<ParametroClimatico> params){
        XYChart.Series<LocalDate, Short> resultSeries = new XYChart.Series<LocalDate, Short>();
        switch(r){
            case wind -> {
                resultSeries.setName("Vento");
                params.forEach(param -> resultSeries
                            .getData()
                            .add(new XYChart.Data<LocalDate, Short>(param.getPubDate(), param.getVentoValue())));
            }
            case umidity -> {
                resultSeries.setName("Umidita");
                params.forEach(param -> resultSeries
                        .getData()
                        .add(new XYChart.Data<LocalDate, Short>(param.getPubDate(), param.getUmiditaValue())));
            }
            case atmPressure -> {
                resultSeries.setName("Pressione");
                params.forEach(param -> resultSeries
                        .getData()
                        .add(new XYChart.Data<LocalDate, Short>(param.getPubDate(), param.getPressioneValue())));
            }
            case rainfall -> {
                resultSeries.setName("Precipitazioni");
                params.forEach(param -> resultSeries
                        .getData()
                        .add(new XYChart.Data<LocalDate, Short>(param.getPubDate(), param.getPrecipitazioniValue())));
            }
            case temperature -> {
                resultSeries.setName("Temperatura");
                params.forEach(param -> resultSeries
                        .getData()
                        .add(new XYChart.Data<LocalDate, Short>(param.getPubDate(), param.getTemperaturaValue())));
            }
            case glacierAlt -> {
                resultSeries.setName("Altitudine ghiacciai");
                params.forEach(param -> resultSeries
                        .getData()
                        .add(new XYChart.Data<LocalDate, Short>(param.getPubDate(), param.getAltitudineValue())));
            }
            case glacierMass -> {
                resultSeries.setName("Massa ghiacciai");
                params.forEach(param -> resultSeries
                        .getData()
                        .add(new XYChart.Data<LocalDate, Short>(param.getPubDate(), param.getMassaValue())));
            }
        }
        return resultSeries;
    }
}