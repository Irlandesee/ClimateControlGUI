package it.uninsubria.factories;

import it.uninsubria.request.MalformedRequestException;
import it.uninsubria.request.Request;
import it.uninsubria.servercm.ServerInterface;

public class RequestFactory {

    static final String paramLengthError = "Lunghezza parametri non corretta";
    static final String undefinedRequestType = "Tipo richiesta non definito";
    public static Request buildRequest(String clientId, ServerInterface.RequestType requestType, ServerInterface.Tables table, String[] params) throws MalformedRequestException{
        switch(requestType){
            case selectAll -> {
                return new Request(clientId, requestType, table, null);
            }
            case selectAllWithCond -> {
                if(params.length < ServerInterface.selectAllWithCondParamsLength){
                    throw new MalformedRequestException(clientId, requestType, table, params, paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case selectObjWithCond -> {
                if(params.length < ServerInterface.selectObjWithCondParamsLength){
                    throw new MalformedRequestException(clientId, requestType, table, params, paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case selectObjJoinWithCond -> {
                if(params.length < ServerInterface.selectObjJoinWithCondParamsLength){
                    throw new MalformedRequestException(clientId, requestType, table, params, paramLengthError);
                }else{
                    return new Request(clientId, requestType, table, params);
                }
            }
            case executeLogin -> {
                if(params.length < ServerInterface.executeLoginParamsLength){
                    throw new MalformedRequestException(clientId, requestType, table, params, paramLengthError);
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
                        if(params.length < ServerInterface.insertCmParamsLength){
                            throw new MalformedRequestException(clientId, requestType, table, params, paramLengthError);
                        }else{
                            return new Request(clientId, requestType, table, params);
                        }
                    }
                    case OPERATORE -> {
                        if(params.length < ServerInterface.insertOpParamsLength){
                            throw new MalformedRequestException(clientId, requestType, table, params, paramLengthError);
                        }else{
                            return new Request(clientId, requestType, table, params);
                        }
                    }
                    case PARAM_CLIMATICO -> {
                        if(params.length < ServerInterface.insertPcParamsLength){
                            throw new MalformedRequestException(clientId, requestType, table, params, paramLengthError);
                        }else{
                            return new Request(clientId, requestType, table, params);
                        }
                    }
                }
            }
        }
        throw new MalformedRequestException(clientId, requestType, table, params, undefinedRequestType);
    }

}