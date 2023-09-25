package it.uninsubria.graphbuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.chart.*;

import java.util.Arrays;

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

}