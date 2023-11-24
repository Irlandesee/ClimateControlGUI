package it.uninsubria.request;

import it.uninsubria.servercm.ServerInterface;

public class MalformedRequestException extends Request{

    private final String message;
    public MalformedRequestException(String clientId, ServerInterface.RequestType requestType, ServerInterface.Tables table, String[] params, String message) {
        super(clientId, requestType, table, params);
        this.message = message;
    }
    public String getMessage(){ return this.message;}

}
