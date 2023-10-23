package it.uninsubria.util;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.parametroClimatico.NotaParametro;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class FakeDataGenerator{

    private final QueryHandler queryHandler;
    private final LocalDate canonicalStartDate = LocalDate.of(1900, 1, 1);
    private final LocalDate canonicalEndDate = LocalDate.of(2100, 1, 1);
    private final LocalDate endDate = LocalDate.of(2022, 12, 31);
    private final String apiUrl = "https://api.random.org/json-rpc/4/invoke";

    public FakeDataGenerator(QueryHandler queryHandler){
        this.queryHandler = queryHandler;
    }

    public List<String> getRandomStrings(int numberOfItems, int itemLength, String apiKey){
        JSONObject jsonPacket = getJsonObject(numberOfItems, itemLength, apiKey);
        List<String> randomStrings = new LinkedList<String>();
        try{
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(apiUrl);
            StringEntity postingString = new StringEntity(jsonPacket.toString(), ContentType.APPLICATION_JSON);
            postRequest.setEntity(postingString);
            postRequest.setHeader("Content-type", "application/json");

            HttpResponse response = httpClient.execute(postRequest);

            //handle response
            if(response != null){
                System.out.println(response.getStatusLine().getStatusCode());
                if(response.getStatusLine().getStatusCode() == 200){
                    BufferedReader bReader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            response.getEntity().getContent()));
                    String responseObject = bReader.readLine();
                    JSONArray randomStringsJSONArray = new JSONObject(responseObject)
                            .getJSONObject("result")
                            .getJSONObject("random")
                            .getJSONArray("data");
                    for(Object o: randomStringsJSONArray)
                        randomStrings.add(o.toString());
                    return randomStrings;
                }else{
                    System.out.println("Request failed with Error: " + response.getStatusLine().getStatusCode());
                }
            }

        }catch(IOException ioe){ioe.printStackTrace();}
        return null;
    }

    private JSONObject getJsonObject(int numberOfItems, int itemLength, String apiKey) {
        String method = "generateStrings";
        JSONObject jsonPacket = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("apiKey", apiKey);
        params.put("n", numberOfItems);
        params.put("length", itemLength);
        params.put("characters", "abcdefghijklmnopqrstuvwxyz");
        params.put("replacement", true);


        jsonPacket.put("jsonrpc", "2.0");
        jsonPacket.put("method", method);
        jsonPacket.put("params", params);
        jsonPacket.put("id", 6934);
        return jsonPacket;
    }


    public static LocalDate getRandomPubDate(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay);
    }

    public Short getParamValue(){
        Short[] values = {1, 2, 3, 4, 5};
        return values[ThreadLocalRandom.current().nextInt(0, values.length)];
    }

    public List<ParametroClimatico> generateParamClimatici(int numberOfItems){
        List<ParametroClimatico> result = new LinkedList<ParametroClimatico>();
        List<AreaInteresse> areeInteresseInDb = queryHandler.selectAll(QueryHandler.tables.AREA_INTERESSE);
        List<CentroMonitoraggio> centroMonitoraggioInDb = queryHandler.selectAll(QueryHandler.tables.CENTRO_MONITORAGGIO);
        Random rand = new Random();
        for(int i = 0; i < numberOfItems; i++){
            String areaid = areeInteresseInDb
                    .get(rand.nextInt(areeInteresseInDb.size())).getAreaid();
            String centroid = centroMonitoraggioInDb
                    .get(rand.nextInt(centroMonitoraggioInDb.size())).getCentroID();
            String parameterId = IDGenerator.generateID();
            String notaId = IDGenerator.generateID();
            LocalDate pubDate = getRandomPubDate(canonicalStartDate, endDate);
            ParametroClimatico pc = new ParametroClimatico(parameterId, centroid, areaid, pubDate);
            pc.setVentoValue(getParamValue());
            pc.setUmiditaValue(getParamValue());
            pc.setPressioneValue(getParamValue());
            pc.setTemperaturaValue(getParamValue());
            pc.setPrecipitazioniValue(getParamValue());
            pc.setAltitudineValue(getParamValue());
            pc.setMassaValue(getParamValue());
            pc.setNotaId(notaId);
            result.add(pc);
        }
        return result;
    }

    //Genera parametri climatici relativi a un anno specifico
    public List<ParametroClimatico> generateParamClimaticiGivenYear(int year){

    }


    //Genera parametri climatici relativi a un mese specifico
    public List<ParametroClimatico> generateParamClimaticiGivenMonth(int month){

    }

    public List<AreaInteresse> generateAreeInteressse(int numberOfItems){
        List<AreaInteresse> result = new LinkedList<AreaInteresse>();
        List<City> cities = queryHandler.selectAll(QueryHandler.tables.CITY);
        for(int i = 0; i < numberOfItems; i++){
            City c = cities.get(ThreadLocalRandom.current().nextInt(cities.size()));
            result.add(new AreaInteresse(
                    IDGenerator.generateID(),
                    c.getAsciiName(),
                    c.getCountry(),
                    c.getLatitude(),
                    c.getLongitude()));
        }
        return result;
    }

    public List<CentroMonitoraggio> generateCentroMonitoraggio(int numberOfItems){
        List<CentroMonitoraggio> result = new LinkedList<CentroMonitoraggio>();
        List<City> cities = queryHandler.selectAll(QueryHandler.tables.CITY);
        for(int i = 0; i < numberOfItems; i++){
            City c = cities.get(ThreadLocalRandom.current().nextInt(cities.size()));
            List<AreaInteresse> randomAree =
                    generateAreeInteressse(
                            ThreadLocalRandom.current().nextInt(1, 5));
            CentroMonitoraggio cm = new CentroMonitoraggio(
                            IDGenerator.generateID(),
                            c.getAsciiName() + "Centro",
                            c.getAsciiName(),
                            c.getCountry());
            randomAree.forEach(area -> cm.putAreaId(area.getAreaid()));
            result.add(cm);
        }
        return result;
    }

    public List<NotaParametro> generateNotaParametro(int numberOfItems){
        List<NotaParametro> result = new LinkedList<NotaParametro>();
        String api_key = "b1579b15-ecb5-47c3-bcb8-9548ee05f230";

        for(int i = 0; i < numberOfItems; i++){
            List<String> note = getRandomStrings(7,5, api_key);
            result.add(
                    new NotaParametro(
                            IDGenerator.generateID(),
                            note.get(0),
                            note.get(1),
                            note.get(2),
                            note.get(3),
                            note.get(4),
                            note.get(5),
                            note.get(6)));
        }
        return result;
    }

}