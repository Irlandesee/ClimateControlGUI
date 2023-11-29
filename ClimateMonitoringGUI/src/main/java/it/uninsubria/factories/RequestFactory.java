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
            case insert -> {
                switch (table){
                    case AREA_INTERESSE -> {
                        //TODO
                        return null;
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
            case insert -> {
                params.put("parameterid", "");
                params.put("centroid", "");
                params.put("areaid", "");
                params.put("pubdate", "");
                params.put("notaid", "");
                params.put("valore_vento", "");
                params.put("valore_umidita", "");
                params.put("valore_pressione", "");
                params.put("valore_temperatura", "");
                params.put("valore_precipitazioni", "");
                params.put("valore_alt_ghiacciai", "");
                params.put("valore_massa_ghiacciai", "");
            }
            default -> {
                return null;
            }
        }
        return params;
    }

}
