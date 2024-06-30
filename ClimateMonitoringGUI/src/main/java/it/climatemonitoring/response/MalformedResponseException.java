package it.climatemonitoring.response;

import it.climatemonitoring.servercm.ServerInterface;

public class MalformedResponseException extends Exception{
    private final String message;
    public MalformedResponseException(String clientId, String requestId, ServerInterface.ResponseType respType, ServerInterface.Tables table, Object result, String message) {
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
