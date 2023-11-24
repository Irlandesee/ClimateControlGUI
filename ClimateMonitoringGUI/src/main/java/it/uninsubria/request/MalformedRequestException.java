package it.uninsubria.request;

import it.uninsubria.servercm.ServerInterface;

public class MalformedRequestException extends Exception{

    private final String message;
    public MalformedRequestException(String clientId, ServerInterface.RequestType requestType, ServerInterface.Tables table, String[] params, String message) {
        this.message = message;
    }
    public String getMessage(){ return this.message;}

}
