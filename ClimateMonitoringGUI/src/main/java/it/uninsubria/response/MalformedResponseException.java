package it.uninsubria.response;

import it.uninsubria.servercm.ServerInterface;

public class MalformedResponseException extends Response{
    private final String message;
    public MalformedResponseException(String clientId, String requestId, ServerInterface.ResponseType respType, ServerInterface.Tables table, Object result, String message) {
        super(clientId, requestId, respType, table, result);
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
