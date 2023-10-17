package it.uninsubria.util;
import it.uninsubria.areaInteresse.AreaInteresse;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
public class FakeDataGenerator{

    private int numOfItems;
    public FakeDataGenerator(int numOfItems) {
        this.numOfItems = numOfItems;
    }

    public ConcurrentHashMap<String, Item> getNewTestMap(){
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

    //public ConcurrentHashMap<String, AreaInteresse>

}