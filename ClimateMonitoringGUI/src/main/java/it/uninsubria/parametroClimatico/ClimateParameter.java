package it.uninsubria.parametroClimatico;

import java.util.*;
import java.time.LocalDate;

public class ClimateParameter {

    private String parameterId;
    private String idCentro;
    private String areaInteresse;
    private LocalDate pubDate;

    private String notes; //max 256 chars

    private static final String ventoExp = "Velocità del vento (km/h), suddivisa in fasce";
    private static final String umiditaExp = "% di Umidità, suddivisa in fasce";
    private static final String pressioneExp = "In hPa, suddivisa in fasce";
    private static final String tempExp = "In C°, suddivisa in fasce";
    private static final String precipationiExp = "In mm di pioggia, suddivisa in fasce";
    private static final String altiGhiacciaiExp = "In m, suddivisa in piogge";
    private static final String massaGhiacciaiExp = "In kg, suddivisa in fasce";

    public static final String notaVento = "Vento note";
    public static final String notaUmidita = "Umidita note";
    public static final String notaPressione = "Pressione note";
    public static final String notaTemp = "Temp note";
    public static final String notePrecipitazioni = "Precip note";
    public static final String noteAltGhiacciai = "Alt ghiacciai note";
    public static final String noteMassaGhiacciai = "Massa ghiacciai note";

    private static final short minVal = 1;
    private static final short maxVal = 5;
    private static final short maxNoteLength = 256;
    public static final String generalSeparator = ";";
    public static final String generalParamSeparator = ",";
    public static final String paramKeySeparator = ":";
    public static final String ERROR_STR_NOT_VALID = "param str must be a valid string!\n";
    public static final String ERROR_PARAM_KEY= "param key must be valid a valid string!\n";
    public static final String ERROR_TOO_MANY_CHARS = "note length must be under 256 chars!\n";
    public static final String ERROR_INVALID_MIN_VALUE = "min value must be >= 1\n";
    public static final String ERROR_INVALID_MAX_VALUE = "max value must be <= 5\n";
    public static final String ERROR_INVALID_KEY = "invalid parameter key!\n";

    public static final String paramVento = "vento";
    public static final String paramUmidita = "umidita";
    public static final String paramPressione = "pressione";
    public static final String paramTemp = "temperature";
    public static final String paramAltGhiacciai = "altGhiacciai";
    public static final String paramMassaGhiacciai = "massaGhiacciai";

    public static final short defaultValue = -1;

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

    private HashMap<String, Short> paramValues;
    private HashMap<String, String> paramNotes;

    public ClimateParameter(String parameterID){
        this.parameterId = parameterID;
        this.paramValues = new HashMap<String, Short>();
        this.initParamValues();
    }

    public ClimateParameter(String parameterID, String idCentro
            , String areaInteresse
            , LocalDate pubDate){
        this.parameterId = parameterID;
        this.idCentro = idCentro;
        this.areaInteresse = areaInteresse;
        this.pubDate = pubDate;

        this.paramValues = new HashMap<String, Short>();
        this.initParamValues();
    }

    private void initParamValues(){
        this.paramValues.put(ClimateParameter.paramVento, ClimateParameter.defaultValue);
        this.paramValues.put(ClimateParameter.paramUmidita, ClimateParameter.defaultValue);
        this.paramValues.put(ClimateParameter.paramPressione, ClimateParameter.defaultValue);
        this.paramValues.put(ClimateParameter.paramTemp, ClimateParameter.defaultValue);
        this.paramValues.put(ClimateParameter.paramAltGhiacciai, ClimateParameter.defaultValue);
        this.paramValues.put(ClimateParameter.paramMassaGhiacciai, ClimateParameter.defaultValue);
    }

    public boolean addParameter(String param, short value) {
        if(param == null || param.isBlank())
            throw new IllegalArgumentException(ClimateParameter.ERROR_PARAM_KEY);
        else if(value < minVal)
            throw new IllegalArgumentException(ClimateParameter.ERROR_INVALID_MIN_VALUE);
        else if(value > maxVal)
            throw new IllegalArgumentException(ClimateParameter.ERROR_INVALID_MAX_VALUE);
        if(paramValues.containsKey(param)){
            paramValues.replace(param, value);
            return true;
        }
        return false;
    }


    public boolean rmParameter(String param){
        if(param == null || param.isBlank())
            throw new IllegalArgumentException(ClimateParameter.ERROR_PARAM_KEY);
        if(!paramValues.isEmpty()){
            if(paramValues.containsKey(param)){
                paramValues.replace(param, ClimateParameter.defaultValue);
                return true;
            }
            else return false;
        }
        return false;
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

    public String getNotes() {
        return notes;
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

    //params1,...,paramN;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClimateParameter that = (ClimateParameter) o;
        return Objects.equals(parameterId, that.parameterId)
                && Objects.equals(pubDate, that.pubDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterId, pubDate);
    }

    //centroID;areaInteresse;data;params1,paramN;note
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.parameterId).append(ClimateParameter.generalSeparator)
                .append(this.idCentro).append(ClimateParameter.generalSeparator)
                .append(this.areaInteresse).append(ClimateParameter.generalSeparator)
                .append(this.pubDate).append(ClimateParameter.generalSeparator)
                .append(this.ventoValue).append(ClimateParameter.generalSeparator)
                .append(this.umiditaValue).append(ClimateParameter.generalSeparator)
                .append(this.pressioneValue).append(ClimateParameter.generalSeparator)
                .append(this.temperaturaValue).append(ClimateParameter.generalSeparator)
                .append(this.precipitazioniValue).append(ClimateParameter.generalSeparator)
                .append(this.altitudineValue).append(ClimateParameter.generalSeparator)
                .append(this.massaValue).append(ClimateParameter.generalSeparator)
                .append(ClimateParameter.notaVento).append(": ").append(this.getVentoNotes()).append(ClimateParameter.generalSeparator)
                .append(ClimateParameter.notaUmidita).append(": ").append(this.getUmiditaNotes()).append(ClimateParameter.generalSeparator)
                .append(ClimateParameter.notaPressione).append(": ").append(this.getPressioneNotes()).append(ClimateParameter.generalSeparator)
                .append(ClimateParameter.notaTemp).append(": ").append(this.getTempNotes()).append(ClimateParameter.generalSeparator)
                .append(ClimateParameter.notePrecipitazioni).append(": ").append(this.getPrecipitazioniNotes()).append(ClimateParameter.generalSeparator)
                .append(ClimateParameter.noteAltGhiacciai).append(": ").append(this.getAltGhicciaiNotes()).append(ClimateParameter.generalSeparator)
                .append(ClimateParameter.noteMassaGhiacciai).append(": ").append(this.getMassaGhiacciaiNotes()).append(ClimateParameter.generalSeparator);
        return builder.toString();
    }
}
