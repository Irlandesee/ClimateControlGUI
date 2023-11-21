package it.uninsubria.response;

import it.uninsubria.servercm.ServerInterface.RequestType;
import it.uninsubria.servercm.ServerInterface.ResponseType;
import it.uninsubria.servercm.ServerInterface.Tables;
import it.uninsubria.util.IDGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;


public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String clientId;
    private final String responseId;
    private final ResponseType respType;
    private final Tables table;
    private Object result;

    public Response(String clientId, ResponseType respType, Tables table, Object result){
        this.clientId = clientId;
        this.responseId = IDGenerator.generateID();
        this.respType = respType;
        this.table = table;
        this.result = result;
    }

    public String getClientId(){
        return this.clientId;
    }

    public String getResponseId(){
        return this.responseId;
    }

    public ResponseType getRespType(){
        return this.respType;
    }

    public Tables getTable(){
        return this.table;
    }

    public Object getResult(){
        return this.result;
    }

    public String toString(){
        return "{%s, %s := %s, %s}".formatted(this.clientId, this.responseId, this.respType, this.table);
    }

}
