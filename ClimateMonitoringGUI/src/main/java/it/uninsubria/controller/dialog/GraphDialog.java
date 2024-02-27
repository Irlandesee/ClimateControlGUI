package it.uninsubria.controller.dialog;

import it.uninsubria.clientCm.Client;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.graphbuilder.GraphBuilder;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.time.LocalDate;
import java.time.Month;
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

    @FXML
    public ListView listViewDati;

    private final Client client;

    private List<ParametroClimatico> params;
    private final String areaId;
    private final String denomArea;

    private final int defaultYear = 1900;
    private final int defaultMonth = 1;

    public GraphDialog(Client client, String areaId, List<ParametroClimatico> params){
        this.client = client;
        this.params = params;
        this.areaId = areaId;
        denomArea = getDenomArea();
    }

    public GraphDialog(Client client, String areaId){
        this.client = client;
        this.areaId = areaId;
        this.denomArea = getDenomArea();
        this.params = new LinkedList<ParametroClimatico>();
    }

    private String getDenomArea(){


        Request request = null;
        try{
            Map<String, String> params = RequestFactory
                    .buildParams(ServerInterface.RequestType.selectObjWithCond, "denominazione", "areaid", areaId);
            request = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectObjWithCond,
                    ServerInterface.Tables.AREA_INTERESSE,
                    params
            );

        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
        }
        client.addRequest(request);
        Response response = client.getResponse(request.getClientId());
        String denominazione = "";
        if(response.getRespType() == ServerInterface.ResponseType.Object
                && response.getTable() == ServerInterface.Tables.AREA_INTERESSE){
            denominazione = response.getResult().toString();
        }

        return denominazione;
    }

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

        if(!params.isEmpty()){
            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(denomArea);
            List<ParametroClimatico> filteredParams = params.stream().filter(param -> param.getPubDate().getMonth().equals(Month.JANUARY)).toList();
            List<Pair<LocalDate, Number>> data = calcMonthlyData(filteredParams, ParameterType.temperatura);
            addMonthlyDataToSeries(data, series);

            monthlyTempLineChart.getData().add(series);
        }else{
            Request request = null;
            try{
                Map<String, String> requestParameters = RequestFactory
                        .buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", areaId);
                request = RequestFactory.buildRequest(
                        client.getClientId(),
                        ServerInterface.RequestType.selectAllWithCond,
                        ServerInterface.Tables.PARAM_CLIMATICO,
                        requestParameters
                );
            }catch(MalformedRequestException mre){
                new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
                mre.printStackTrace();
                return;
            }
            client.addRequest(request);
            Response response = client.getResponse(request.getRequestId());
            params = (List<ParametroClimatico>) response.getResult();

            listViewDati.setEditable(false);
            for(ParametroClimatico p : params){
                listViewDati.getItems().add(p.getPubDate());
            }
            listViewDati.setOnMouseClicked(mouseClicked -> {
                LocalDate clickedPubDate =  (LocalDate) listViewDati.getSelectionModel().getSelectedItem();
                tfMonthFilter.setText(String.valueOf(clickedPubDate.getMonth().getValue()));
                tfYearFilter.setText(String.valueOf(clickedPubDate.getYear()));
            });

        }


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

    private int getNumOccurrencesOfMonth(List<ParametroClimatico> params, int month){
        return (int) params.stream().filter(p -> p.getPubDate().getMonth().getValue() == month).count();
    }


    private XYChart.Series<String, Number> addMonthlyDataToSeries(List<Pair<LocalDate, Number>> data, XYChart.Series<String, Number> series){
        data.forEach(param -> series
                .getData()
                .add(new XYChart.Data<String, Number>(GraphBuilder.getLocaleMonth(param.getKey().getMonthValue()), param.getValue())));
        return series;
    }

    private XYChart.Series<String, Number> addDailyDataToSeries(List<Pair<LocalDate, Number>> data, XYChart.Series<String, Number> series){
        data.forEach(param -> series
                .getData()
                .add(new XYChart.Data<String, Number>(String.valueOf(param.getKey().getDayOfMonth()), param.getValue())));
        return series;
    }

    @FXML
    public void filterYear(){
        String yearFilterText = tfYearFilter.getText();
        if(yearFilterText.isEmpty() || yearFilterText.equals("Inserisci anno")){
            new Alert(Alert.AlertType.ERROR, "campo non valido").showAndWait();
            return;
        }
        int year = Integer.parseInt(yearFilterText);
        Request request = null;
        try{
            Map<String, String> requestParams = RequestFactory.buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", areaId);
            request = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectAllWithCond,
                    ServerInterface.Tables.PARAM_CLIMATICO,
                    requestParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        client.addRequest(request);
        Response response = client.getResponse(request.getRequestId());
        params = (List<ParametroClimatico>) response.getResult();
        List<ParametroClimatico> filteredParams = filterListByYear(params, year);

        System.out.println("printing params");
        System.out.println(filteredParams.size());
        filteredParams.forEach(System.out::println);

        //if not working?
        if(filteredParams.isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Dati non presenti per questa area e anno").showAndWait();
            return;
        }

        List<Pair<LocalDate, Number>> data = new LinkedList<Pair<LocalDate, Number>>();
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series.setName(denomArea);
        for(Node n: contentBox.getChildren()){
            if(n.equals(monthlyTempLineChart) && monthlyTempLineChart.isVisible()){
                monthlyTempLineChart.getData().clear();
                System.out.println("Adding data to monthly temp graph");
                monthlyTempLineChart
                        .getData()
                        .add(addMonthlyDataToSeries(calcMonthlyData(filteredParams, ParameterType.temperatura), series));
            }
            else if(n.equals(monthlyWindLineChart) && monthlyWindLineChart.isVisible()){
                monthlyWindLineChart.getData().clear();
                System.out.println("Adding data to monthly wind graph");
                monthlyWindLineChart
                        .getData()
                        .add(addMonthlyDataToSeries(calcMonthlyData(filteredParams, ParameterType.vento), series));
            }
            else if(n.equals(monthlyUmidityLineChart) && monthlyUmidityLineChart.isVisible()) {
                monthlyUmidityLineChart.getData().clear();
                System.out.println("Adding data to monthly umidity graph");
                monthlyUmidityLineChart
                        .getData()
                        .add(addMonthlyDataToSeries(calcMonthlyData(filteredParams, ParameterType.umidita), series));
            }
            else if(n.equals(monthlyPressureChart) && monthlyPressureChart.isVisible()) {
                monthlyPressureChart.getData().clear();
                System.out.println("Adding data to monthly pressure graph");
                monthlyPressureChart
                        .getData()
                        .add(addMonthlyDataToSeries(calcMonthlyData(filteredParams, ParameterType.pressione), series));
            }
            else if(n.equals(monthlyRainfallChart) && monthlyRainfallChart.isVisible()){
                monthlyRainfallChart.getData().clear();
                System.out.println("Adding data to monthly rainfall graph");
                monthlyRainfallChart
                        .getData()
                        .add(addMonthlyDataToSeries(calcMonthlyData(filteredParams, ParameterType.precipitazioni), series));
            }
            else if(n.equals(monthlyAltLineChart) && monthlyAltLineChart.isVisible()){
                monthlyAltLineChart.getData().clear();
                System.out.println("Adding data to monthly altitude graph");
                monthlyAltLineChart.getData().add(addMonthlyDataToSeries(calcMonthlyData(filteredParams, ParameterType.alt_ghiacciai), series));
            }
            else if(n.equals(monthlyMassChart) && monthlyMassChart.isVisible()){
                monthlyMassChart.getData().clear();
                System.out.println("Adding data to monthly mass graph");
                monthlyMassChart.getData().add(addMonthlyDataToSeries(calcMonthlyData(filteredParams, ParameterType.massa_ghiacciai), series));
            }
        }
        tfYearFilter.setText("Inserisci anno");
    }
    @FXML
    public void filterMonth(){
        String monthFilterText = tfMonthFilter.getText();
        String yearFilterText = tfYearFilter.getText();

        if(monthFilterText.isEmpty() || monthFilterText.equals("Inserisci mese")) {
            new Alert(Alert.AlertType.ERROR, "Campo non valido").showAndWait();
            return;
        }
        if(yearFilterText.isEmpty() || yearFilterText.equals("Inserisci anno")){
            new Alert(Alert.AlertType.ERROR, "Campo non valido").showAndWait();
            return;
        }

        int month = Integer.parseInt(monthFilterText);
        int year = Integer.parseInt(yearFilterText);


        Request request = null;
        try{
            Map<String, String> requestParams = RequestFactory
                    .buildParams(ServerInterface.RequestType.selectAllWithCond, "areaid", areaId);
            request = RequestFactory.buildRequest(
                    client.getClientId(),
                    ServerInterface.RequestType.selectAllWithCond,
                    ServerInterface.Tables.PARAM_CLIMATICO,
                    requestParams
            );
        }catch(MalformedRequestException mre){
            new Alert(Alert.AlertType.ERROR, mre.getMessage()).showAndWait();
            mre.printStackTrace();
            return;
        }
        client.addRequest(request);
        Response response = client.getResponse(request.getRequestId());
        List<ParametroClimatico> params = (List<ParametroClimatico>)response.getResult();
        List<ParametroClimatico> filteredParams = filterListByMonth(filterListByYear(params, year), Month.of(month));

        if(filteredParams.isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Dati non presenti per questo mese, anno e area").showAndWait();
            return;
        }

        filteredParams.forEach(System.out::println);

        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series.setName(denomArea);
        System.out.println("Number of children in contentBox: " + contentBox.getChildren().size());
        for(Node n : contentBox.getChildren()){
            if(n.equals(dailyTempLineChart) && dailyTempLineChart.isVisible()){
                dailyTempLineChart.getData().clear();
                System.out.println("Adding data to daily temp graph");
                dailyTempLineChart
                        .getData()
                        .add(addDailyDataToSeries(calcDailyData(filteredParams, ParameterType.temperatura), series));
            }
            else if(n.equals(dailyWindLineChart) && dailyWindLineChart.isVisible()){
                dailyWindLineChart.getData().clear();
                dailyWindLineChart
                        .getData()
                        .add(addDailyDataToSeries(calcDailyData(filteredParams, ParameterType.vento), series));
                System.out.println("Adding data to daily wind linechart");
            }
            else if(n.equals(dailyUmidityLineChart) && dailyUmidityLineChart.isVisible()){
                dailyUmidityLineChart.getData().clear();
                System.out.println("Adding data to daily umidity chart");
                dailyUmidityLineChart
                        .getData()
                        .add(addDailyDataToSeries(calcDailyData(filteredParams, ParameterType.umidita), series));
            }
            else if(n.equals(dailyPressureChart) && dailyPressureChart.isVisible()){
                dailyPressureChart.getData().clear();
                System.out.println("Adding data to daily pressure chart");
                dailyPressureChart
                        .getData()
                        .add(addDailyDataToSeries(calcDailyData(filteredParams, ParameterType.pressione), series));
            }
            else if(n.equals(dailyRainfallLineChart) && dailyRainfallLineChart.isVisible()){
                dailyRainfallLineChart.getData().clear();
                System.out.println("Adding data to rainfall chart");
                dailyRainfallLineChart
                        .getData()
                        .add(addDailyDataToSeries(calcDailyData(filteredParams, ParameterType.precipitazioni), series));
            }
            else if(n.equals(dailyAltLineChart) && dailyAltLineChart.isVisible()){
                dailyAltLineChart.getData().clear();
                System.out.println("adding data to altitude chart");
                dailyAltLineChart
                        .getData()
                        .add(addDailyDataToSeries(calcDailyData(filteredParams, ParameterType.alt_ghiacciai), series));
            }
            else if(n.equals(dailyMassChart) && dailyMassChart.isVisible()){
                dailyMassChart.getData().clear();
                System.out.println("Adding data to daily mass chart");
                dailyMassChart
                        .getData()
                        .add(addDailyDataToSeries(calcDailyData(filteredParams, ParameterType.massa_ghiacciai), series));
            }
        }

        tfMonthFilter.setText("Inserisci mese");
        tfYearFilter.setText("Inserisci anno");
    }


    private List<ParametroClimatico> filterListByMonth(List<ParametroClimatico> params, Month m){
        return params.stream().filter(pc -> pc.getPubDate().getMonth().equals(m)).toList();
    }

    private List<ParametroClimatico> filterListByYear(List<ParametroClimatico> params, int year){
        return params.stream().filter(pc -> pc.getPubDate().getYear() == year).toList();
    }


    private void addParamToList(ParameterType pType, List<Pair<LocalDate, Number>> data, ParametroClimatico param) {
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

    private void addParamAverageToList(List<ParametroClimatico> filteredParams, ParameterType pType, List<Pair<LocalDate, Number>> data, ParametroClimatico param) {
        switch(pType){
            case vento ->
                    data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(filteredParams, ParameterType.vento)));
            case temperatura ->
                    data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(filteredParams, ParameterType.temperatura)));
            case umidita ->
                    data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(filteredParams, ParameterType.umidita)));
            case pressione ->
                    data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(filteredParams, ParameterType.pressione)));
            case precipitazioni ->
                    data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(filteredParams, ParameterType.precipitazioni)));
            case alt_ghiacciai ->
                    data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(filteredParams, ParameterType.alt_ghiacciai)));
            case massa_ghiacciai ->
                    data.add(new Pair<LocalDate, Number>(param.getPubDate(), getAverageValue(filteredParams, ParameterType.massa_ghiacciai)));
        }
    }

    private List<Pair<LocalDate, Number>> calcMonthlyData(List<ParametroClimatico> filteredParams, ParameterType pType){
        List<Pair<LocalDate, Number>> data = new LinkedList<Pair<LocalDate, Number>>();
        for(ParametroClimatico param : filteredParams){
            LocalDate pubDate = param.getPubDate();
            if(getNumOccurrencesOfMonth(filteredParams, pubDate.getMonth().getValue()) > 1){
                addParamAverageToList(filteredParams, pType, data, param);
            }else{
                addParamToList(pType, data, param);
            }

        }
        return data;
    }

    private List<Pair<LocalDate, Number>> calcDailyData(List<ParametroClimatico> filteredParams, ParameterType pType){
        List<Pair<LocalDate, Number>> data = new LinkedList<Pair<LocalDate, Number>>();
        for(ParametroClimatico param : filteredParams){
            LocalDate pubDate = param.getPubDate();
            if(getNumOccurrences(filteredParams, pubDate.getDayOfMonth()) > 1) {
                List<ParametroClimatico> paramsSameDate = filteredParams.stream().filter(pc -> pc.getPubDate().equals(param.getPubDate())).toList();
                addParamAverageToList(paramsSameDate, pType, data, param);
            } else {
                addParamToList(pType, data, param);
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

