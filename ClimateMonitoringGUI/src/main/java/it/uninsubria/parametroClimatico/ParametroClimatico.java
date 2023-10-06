package it.uninsubria.parametroClimatico;

import java.util.*;
import java.time.LocalDate;

public class ParametroClimatico {

    private String parameterId;
    private String idCentro;
    private String areaInteresse;
    private LocalDate pubDate;

    public static final String generalSeparator = ",";

    public static final String notaVento = "Vento note";
    public static final String notaUmidita = "Umidita note";
    public static final String notaPressione = "Pressione note";
    public static final String notaTemp = "Temp note";
    public static final String notePrecipitazioni = "Precip note";
    public static final String noteAltGhiacciai = "Alt ghiacciai note";
    public static final String noteMassaGhiacciai = "Massa ghiacciai note";

    private short ventoValue;
    private short umiditaValue;
    private short pressioneValue;
    private short precipitazioniValue;
    private short temperaturaValue;
    private short altitudineValue;
    private short massaValue;

    private String ventoNotes;
    private String umiditaNotes;
    private String pressioneNotes;
    private String precipitazioniNotes;
    private String tempNotes;
    private String altGhicciaiNotes;
    private String massaGhiacciaiNotes;

    public ParametroClimatico(String parameterID){
        this.parameterId = parameterID;
    }

    public ParametroClimatico(String parameterID, String idCentro, String areaInteresse, LocalDate pubDate){
        this.parameterId = parameterID;
        this.idCentro = idCentro;
        this.areaInteresse = areaInteresse;
        this.pubDate = pubDate;

    }

    public String getParameterId(){
        return this.parameterId;
    }

    public String getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(String idCentro) {
        this.idCentro = idCentro;
    }

    public String getAreaInteresse() {
        return areaInteresse;
    }

    public void setAreaInteresse(String areaInteresse) {
        this.areaInteresse = areaInteresse;
    }

    public LocalDate getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDate pubDate) {
        this.pubDate = pubDate;
    }

    public short getVentoValue() {
        return ventoValue;
    }

    public void setVentoValue(short ventoValue) {
        this.ventoValue = ventoValue;
    }

    public short getUmiditaValue() {
        return umiditaValue;
    }

    public void setUmiditaValue(short umiditaValue) {
        this.umiditaValue = umiditaValue;
    }

    public short getPressioneValue() {
        return pressioneValue;
    }

    public void setPressioneValue(short pressioneValue) {
        this.pressioneValue = pressioneValue;
    }

    public short getTemperaturaValue() {
        return temperaturaValue;
    }

    public short getPrecipitazioniValue() {
        return precipitazioniValue;
    }

    public void setPrecipitazioniValue(short precipitazioniValue) {
        this.precipitazioniValue = precipitazioniValue;
    }

    public void setTemperaturaValue(short temperaturaValue) {
        this.temperaturaValue = temperaturaValue;
    }

    public short getAltitudineValue() {
        return altitudineValue;
    }

    public void setAltitudineValue(short altitudineValue) {
        this.altitudineValue = altitudineValue;
    }

    public short getMassaValue() {
        return massaValue;
    }

    public void setMassaValue(short massaValue) {
        this.massaValue = massaValue;
    }


    public String getVentoNotes() {
        return ventoNotes;
    }

    private void setVentoNotes(String ventoNotes) {
        this.ventoNotes = ventoNotes;
    }

    public String getUmiditaNotes() {
        return umiditaNotes;
    }

    private void setUmiditaNotes(String umiditaNotes) {
        this.umiditaNotes = umiditaNotes;
    }

    public String getPrecipitazioniNotes(){return this.precipitazioniNotes;}
    private void setPrecipitazioniNotes(String notes){this.precipitazioniNotes = notes;}

    public String getPressioneNotes() {
        return pressioneNotes;
    }

    private void setPressioneNotes(String pressioneNotes) {
        this.pressioneNotes = pressioneNotes;
    }

    public String getTempNotes() {
        return tempNotes;
    }

    private void setTempNotes(String tempNotes) {
        this.tempNotes = tempNotes;
    }

    public String getAltGhicciaiNotes() {
        return altGhicciaiNotes;
    }

    private void setAltGhicciaiNotes(String altGhicciaiNotes) {
        this.altGhicciaiNotes = altGhicciaiNotes;
    }

    public String getMassaGhiacciaiNotes() {
        return massaGhiacciaiNotes;
    }

    private void setMassaGhiacciaiNotes(String massaGhiacciaiNotes) {
        this.massaGhiacciaiNotes = massaGhiacciaiNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParametroClimatico that = (ParametroClimatico) o;
        return ventoValue == that.ventoValue && umiditaValue == that.umiditaValue && pressioneValue == that.pressioneValue && precipitazioniValue == that.precipitazioniValue && temperaturaValue == that.temperaturaValue && altitudineValue == that.altitudineValue && massaValue == that.massaValue && Objects.equals(parameterId, that.parameterId) && Objects.equals(idCentro, that.idCentro) && Objects.equals(areaInteresse, that.areaInteresse) && Objects.equals(pubDate, that.pubDate) && Objects.equals(ventoNotes, that.ventoNotes) && Objects.equals(umiditaNotes, that.umiditaNotes) && Objects.equals(pressioneNotes, that.pressioneNotes) && Objects.equals(precipitazioniNotes, that.precipitazioniNotes) && Objects.equals(tempNotes, that.tempNotes) && Objects.equals(altGhicciaiNotes, that.altGhicciaiNotes) && Objects.equals(massaGhiacciaiNotes, that.massaGhiacciaiNotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterId, idCentro, areaInteresse, pubDate, ventoValue, umiditaValue, pressioneValue, precipitazioniValue, temperaturaValue, altitudineValue, massaValue, ventoNotes, umiditaNotes, pressioneNotes, precipitazioniNotes, tempNotes, altGhicciaiNotes, massaGhiacciaiNotes);
    }

    //centroID;areaInteresse;data;params1,paramN;note
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.parameterId).append(ParametroClimatico.generalSeparator)
                .append(this.idCentro).append(ParametroClimatico.generalSeparator)
                .append(this.areaInteresse).append(ParametroClimatico.generalSeparator)
                .append(this.pubDate).append(ParametroClimatico.generalSeparator)
                .append(this.ventoValue).append(ParametroClimatico.generalSeparator)
                .append(this.umiditaValue).append(ParametroClimatico.generalSeparator)
                .append(this.pressioneValue).append(ParametroClimatico.generalSeparator)
                .append(this.temperaturaValue).append(ParametroClimatico.generalSeparator)
                .append(this.precipitazioniValue).append(ParametroClimatico.generalSeparator)
                .append(this.altitudineValue).append(ParametroClimatico.generalSeparator)
                .append(this.massaValue).append(ParametroClimatico.generalSeparator)
                .append(ParametroClimatico.notaVento).append(": ").append(this.getVentoNotes()).append(ParametroClimatico.generalSeparator)
                .append(ParametroClimatico.notaUmidita).append(": ").append(this.getUmiditaNotes()).append(ParametroClimatico.generalSeparator)
                .append(ParametroClimatico.notaPressione).append(": ").append(this.getPressioneNotes()).append(ParametroClimatico.generalSeparator)
                .append(ParametroClimatico.notaTemp).append(": ").append(this.getTempNotes()).append(ParametroClimatico.generalSeparator)
                .append(ParametroClimatico.notePrecipitazioni).append(": ").append(this.getPrecipitazioniNotes()).append(ParametroClimatico.generalSeparator)
                .append(ParametroClimatico.noteAltGhiacciai).append(": ").append(this.getAltGhicciaiNotes()).append(ParametroClimatico.generalSeparator)
                .append(ParametroClimatico.noteMassaGhiacciai).append(": ").append(this.getMassaGhiacciaiNotes()).append(ParametroClimatico.generalSeparator);
        return builder.toString();
    }
}
