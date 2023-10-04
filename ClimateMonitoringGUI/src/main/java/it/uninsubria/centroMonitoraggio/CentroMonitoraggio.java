package it.uninsubria.centroMonitoraggio;

import it.uninsubria.areaInteresse.AreaInteresse;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class CentroMonitoraggio {


    //key: String => areaID

    //contiene solo gli id delle aree interesse associate
    private LinkedList<String> areeInteresseIdAssociate;

    public static final String emptyAreeInteresse = "empty";
    public static final String generalSeparator = ";";
    public static final String areeSeparator = ",";

    private final StringProperty centroID;
    private final StringProperty denominazione;
    private final StringProperty comune;
    private final StringProperty country;

    public CentroMonitoraggio(String centroID, String denominazioneCentro,
                              String comune, String country){
        this.centroID = new SimpleStringProperty();
        this.denominazione = new SimpleStringProperty();
        this.comune = new SimpleStringProperty();
        this.country = new SimpleStringProperty();
        this.centroID.set(centroID);
        this.denominazione.set(denominazioneCentro);
        this.comune.set(comune);
        this.country.set(country);
    }

    public String getCentroID() {
        return centroID.get();
    }

    public void setCentroID(String centroID) {
        this.centroID.set(centroID);
    }

    public String getDenominazione() {
        return this.denominazione.get();
    }

    public void setDenominazione(String denominazioneCentro) {
        this.denominazione.set(denominazioneCentro);
    }

    public String getComune() {
        return this.comune.get();
    }

    public void setComune(String comune) {
        this.comune.set(comune);
    }

    public String getCountry(){
        return this.country.get();
    }

    public void setCountry(String country){
        this.country.set(country);
    }

    public void putAreaId(String areaID){
        if(!areeInteresseIdAssociate.contains(areaID))
            areeInteresseIdAssociate.add(areaID);
        else{
            System.out.println("CM contenente già id");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CentroMonitoraggio that = (CentroMonitoraggio) o;
        return Objects.equals(areeInteresseIdAssociate, that.areeInteresseIdAssociate) && Objects.equals(centroID, that.centroID) && Objects.equals(denominazione, that.denominazione) && Objects.equals(comune, that.comune) && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(areeInteresseIdAssociate, centroID, denominazione, comune, country);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(centroID.get()).append(generalSeparator)
                .append(denominazione.get()).append(generalSeparator)
                .append(comune.get()).append(generalSeparator)
                .append(country.get()).append(generalSeparator);

        if(areeInteresseIdAssociate.isEmpty()) builder.append(emptyAreeInteresse);
        else{
            for (String tmp : areeInteresseIdAssociate) //append the keys
                builder.append(tmp).append(areeSeparator);
        }
        return builder.toString();
    }

}
