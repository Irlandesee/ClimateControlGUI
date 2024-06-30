package it.climatemonitoring.request;
import it.climatemonitoring.servercm.ServerInterface.RequestType;
import it.climatemonitoring.servercm.ServerInterface.Tables;
import it.climatemonitoring.util.IDGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String requestId;
    private final String clientId;
    private final Tables table;
    private final RequestType requestType;
    private final Map<String, String> params;
    public Request(String clientId, RequestType requestType, Tables table, Map<String, String> params){
        this.requestId = IDGenerator.generateID();
        this.clientId = clientId;
        this.table = table;
        this.requestType = requestType;
        this.params = params;
    }

    public String getClientId(){
        return this.clientId;
    }

    public String getRequestId(){
        return this.requestId;
    }

    public Tables getTable(){
        return this.table;
    }

    public RequestType getRequestType(){
        return this.requestType;
    }

    public Map<String, String> getParams(){
        return this.params;
    }

    public String toString(){
        return this.requestId + " " + this.clientId + " " + this.requestType + " " + this.table;
    }
}
