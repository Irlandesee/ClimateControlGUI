package it.uninsubria.util;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
import org.postgresql.core.Query;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class FakeDataGenerator{

    private final QueryHandler queryHandler;
    private final LocalDate canonicalStartDate = LocalDate.of(1900, 1, 1);
    private final LocalDate canonicalEndDate = LocalDate.of(2100, 1, 1);
    private final LocalDate endDate = LocalDate.of(2022, 12, 31);
    public FakeDataGenerator(QueryHandler queryHandler){
        this.queryHandler = queryHandler;
    }

    public ConcurrentHashMap<String, Item> getNewTestMap(int numOfItems){
        Random rand = new Random();
        int bound = rand.nextInt(Integer.MAX_VALUE);
        ConcurrentHashMap<String, Item> testMap = new ConcurrentHashMap<String, Item>();
        for(int i = 0; i < numOfItems; i++){
            String id = IDGenerator.generateID();
            Item item = new Item(id);
            item.setVal(rand.nextInt(bound));
            testMap.put(id, item);
        }
        return testMap;
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

}