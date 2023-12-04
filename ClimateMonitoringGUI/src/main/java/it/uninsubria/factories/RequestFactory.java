package it.uninsubria.factories;

import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.servercm.ServerInterface;

import java.util.HashMap;
import java.util.Map;

public class RequestFactory {

    static final String paramLengthError = "Lunghezza parametri non corretta";
    static final String undefinedRequestType = "Tipo richiesta non definito";
    public static final String condKey = "cond";
    public static final String fieldKey = "field";
    public static final String objectKey = "object";
    public static final String joinKey = "joinTable";
    public static final String userKey = "user";
    public static final String passwordKey = "password";
    public static final String nomeOpKey = "nomeOp";
    public static final String cognomeOpKey = "cognomeOp";
    public static final String codFiscOpKey = "codFiscOp";
    public static final String emailOpKey = "emailOp";
    public static final String centroAfferenzaKey = "centroAfferenzaOp";
    public static final String parameterIdKey = "parameterId";
    public static final String centroIdKey = "centroId";
    public static final String areaIdKey = "areaId";
    public static final String pubDateKey = "pubdate";
    public static final String notaIdKey = "notaid";
    public static final String valoreVentoKey = "valore_vento";
    public static final String valoreUmiditaKey = "valore_umidita";
    public static final String valorePressioneKey = "valore_pressione";
    public static final String valoreTemperaturaKey = "valore_temperatura";
    public static final String valorePrecipitazioniKey = "valore_precipitazioni";
    public static final String valoreAltGhiacciaiKey = "valore_alt_ghiacciai";
    public static final String valoreMassaGhiacciaiKey = "valore_massa_ghiacciai";

    public static final String nomeCentroKey = "nomeCentro";
    public static final String comuneCentroKey = "comuneCentro";
    public static final String countryCentroKey = "countryCentro";
    public static final String listAiKey = "listAi";
    public static final String denominazioneAreaKey = "denominazioneArea";
    public static final String statoAreaKey = "statoArea";
    public static final String latitudineKey = "latitudine";
    public static final String longitudineKey = "longitudine";
    public static final String notaVentoKey = "notaVento";
    public static final String notaUmidita = "notaUmidita";
    public static final String notaPressione = "notaPressione";
    public static final String notaTemperatura = "notaTemperatura";
    public static final String notaPrecipitazioni = "notaPrecipitazioni";
    public static final String notaAltGhiacciai = "notaAltGhiacciai";
    public static final String notaMassaGhiacciai = "notaMassaGhiacciai";

    public static Request buildRequest(String clientId, ServerInterface.RequestType requestType, ServerInterface.Tables table, Map<String, String> params) throws MalformedRequestException{
        switch(requestType){
            case selectAll -> {
                return new Request(clientId, requestType, table, null);
            }
            case selectAllWithCond -> {
                if(params.keySet().size() < ServerInterface.selectAllWithCondParamsLength){
                    throw new MalformedRequestException(paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case selectObjWithCond -> {
                if(params.keySet().size() < ServerInterface.selectObjWithCondParamsLength){
                    throw new MalformedRequestException(paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case selectObjJoinWithCond -> {
                if(params.keySet().size() < ServerInterface.selectObjJoinWithCondParamsLength){
                    throw new MalformedRequestException(paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case executeLogin -> {
                if(params.keySet().size() < ServerInterface.executeLoginParamsLength){
                    throw new MalformedRequestException(paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case executeSignUp -> {
                if(params.keySet().size() < ServerInterface.executeSignUpParamsLength){
                    throw new MalformedRequestException(paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case insert -> {
                switch (table){
                    case AREA_INTERESSE -> {
                        if(params.keySet().size() < ServerInterface.insertAiParamsLength){
                            throw new MalformedRequestException(paramLengthError);
                        }else{
                            return new Request(clientId, requestType, table, params);
                        }
                    }
                    case CENTRO_MONITORAGGIO -> {
                        if(params.keySet().size() < ServerInterface.insertCmParamsLength){
                            throw new MalformedRequestException(paramLengthError);
                        }else{
                            return new Request(clientId, requestType, table, params);
                        }
                    }
                    case OPERATORE -> {
                        if(params.keySet().size() < ServerInterface.insertOpParamsLength){
                            throw new MalformedRequestException(paramLengthError);
                        }else{
                            return new Request(clientId, requestType, table, params);
                        }
                    }
                    case PARAM_CLIMATICO -> {
                        if(params.keySet().size() < ServerInterface.insertPcParamsLength){
                            throw new MalformedRequestException(paramLengthError);
                        }else{
                            return new Request(clientId, requestType, table, params);
                        }
                    }
                }
            }
        }
        throw new MalformedRequestException(undefinedRequestType);
    }

    public static Map<String, String> buildInsertParams(ServerInterface.Tables table){
        Map<String, String> params = new HashMap<String, String>();
        switch(table){
            case AREA_INTERESSE -> {
                params.put(areaIdKey, "");
                params.put(denominazioneAreaKey, "");
                params.put(statoAreaKey, "");
                params.put(latitudineKey, "");
                params.put(longitudineKey, "");
            }
            case PARAM_CLIMATICO -> {
                params.put(parameterIdKey, "");
                params.put(centroIdKey, "");
                params.put(areaIdKey, "");
                params.put(pubDateKey, "");
                params.put(notaIdKey, "");
                params.put(valoreVentoKey, "");
                params.put(valoreUmiditaKey, "");
                params.put(valorePressioneKey, "");
                params.put(valoreTemperaturaKey, "");
                params.put(valorePrecipitazioniKey, "");
                params.put(valoreAltGhiacciaiKey, "");
                params.put(valoreMassaGhiacciaiKey, "");
            }
            case CENTRO_MONITORAGGIO -> {
                params.put(centroIdKey, "");
                params.put(nomeCentroKey, "");
                params.put(comuneCentroKey, "");
                params.put(countryCentroKey, "");
                params.put(listAiKey, "");
            }
            case NOTA_PARAM_CLIMATICO -> {
                params.put(notaIdKey, "");
                params.put(notaVentoKey, "");
                params.put(notaUmidita, "");
                params.put(notaPressione, "");
                params.put(notaPrecipitazioni, "");
                params.put(notaAltGhiacciai, "");
                params.put(notaMassaGhiacciai, "");
            }
            case OPERATORE -> {
                params.put(nomeOpKey, "");
                params.put(cognomeOpKey, "");
                params.put(codFiscOpKey, "");
                params.put(emailOpKey, "");
                params.put(userKey, "");
                params.put(passwordKey, "");
                params.put(centroAfferenzaKey, "");
            }
            default -> {return null;}
        }
        return params;
    }

    public static Map<String, String> buildParams(ServerInterface.RequestType reqType){
        Map<String, String> params = new HashMap<String, String>();
        switch(reqType){
            case selectAll -> {
                return params;
            }
            case selectAllWithCond -> {
                params.put("cond", "");
                params.put("field", "");
            }
            case selectObjWithCond -> {
                params.put("object", "");
                params.put("cond", "");
                params.put("field", "");
            }
            case selectObjJoinWithCond -> {
                params.put("object", "");
                params.put("joinTable", "");
                params.put("cond", "");
                params.put("field", "");
            }
            case executeLogin -> {
                params.put("user", "");
                params.put("password", "");
            }
            case executeSignUp -> {
                params.put(codFiscOpKey, "");
                params.put(nomeOpKey, "");
                params.put(cognomeOpKey, "");
                params.put(userKey, "");
                params.put(emailOpKey, "");
                params.put(passwordKey, "");
                params.put(centroAfferenzaKey, "");
            }
            default -> {
                return null;
            }
        }
        return params;
    }

}
