package it.uninsubria.graphbuilder;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import javafx.collections.FXCollections;
import javafx.scene.chart.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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

    public static String[] months = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio",
            "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};

    public static String getLocaleMonth(int month){
        return months[month - 1];
    }


    public static LineChart<String, Number> getBasicMonthLineChart(Resource r) {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableList(Arrays.asList(months)));
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(1);
        yAxis.setUpperBound(5);
        yAxis.setTickUnit(1);
        xAxis.setLabel("Mese");
        yAxis.setLabel("Valore");
        return prepareLineChart(r, xAxis, yAxis);
    }

    public static LineChart<String, Number> getBasicDailyLineChart(Resource r, int year, int month){
        final CategoryAxis xAxis = new CategoryAxis();
        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        final int[] numbers = IntStream.rangeClosed(1, monthLength).toArray();
        String[] days = new String[numbers.length];
        for(int i = 0; i < numbers.length; i++) days[i] = String.valueOf(numbers[i]);
        xAxis.setCategories(FXCollections.observableList(List.of(days)));
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(1);
        yAxis.setUpperBound(5);
        yAxis.setTickUnit(1);

        xAxis.setLabel("Giorno");
        yAxis.setLabel("valore");
        return prepareLineChart(r, xAxis, yAxis);
    }

    public static CategoryAxis getDailyMonth(int year, int month){
        final CategoryAxis xAxis = new CategoryAxis();
        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        final int[] numbers = IntStream.rangeClosed(1, monthLength).toArray();
        String[] days = new String[numbers.length];
        for(int i = 0; i < numbers.length; i++) days[i] = String.valueOf(numbers[i]);
        xAxis.setCategories(FXCollections.observableList(List.of(days)));
        xAxis.setLabel("Giorno");
        return xAxis;
    }

    public static NumberAxis getValuesAxis(){
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(1);
        yAxis.setUpperBound(5);
        yAxis.setTickUnit(1);
        return yAxis;
    }

    private static LineChart<String, Number> prepareLineChart(Resource r, Axis<String> xAxis, Axis<Number> yAxis){
        LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
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
        }
        return null;
    }
}