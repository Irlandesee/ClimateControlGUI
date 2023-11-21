package it.uninsubria.servercm;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface.RequestType;
import it.uninsubria.servercm.ServerInterface.Tables;
import it.uninsubria.servercm.ServerInterface.ResponseType;


import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Worker extends Thread{

    private static int workerCount = 0;
    private String clientId;
    private String requestId;
    private final String workerId;
    private final ServerCm server;
    private final String dbUrl;
    private final Properties props;
    private Connection conn;

    public Worker(String workerId, String dbUrl, Properties props, ServerCm server){
        this.workerId = workerId;
        this.setName(workerId);
        this.server = server;
        this.dbUrl = dbUrl;
        this.props = props;
        try{
            this.conn = DriverManager.getConnection(dbUrl, props);
        }catch(SQLException sqle){sqle.printStackTrace();}
    }

    public void run(){
        System.out.printf("Worker %s started\n", workerId);
        System.out.printf("Worker %s getting request\n", workerId);
        Request request = server.getRequest(this.workerId);
        this.clientId = request.getClientId();
        this.requestId = request.getRequestId();
        System.out.printf("Worker %s serving client{%s} request: %s\n", workerId, clientId, requestId);
        //System.out.println("Request: " + request.toString());
        //Read the request
        Response res = null;
        switch(request.getRequestType()){
            //query the database
            case selectAll -> {
                res = selectAll(request.getTable());
                System.out.println("Response: " + res);
            }
            case selectAllWithCond -> {
                res = selectAllWithCond(request.getTable(), request.getParams());
            }
        }
        //save the result in the server's queue
        System.out.printf("Worker %s saving request %s in server's queue\n", this.workerId, request.getRequestId());
        server.addResponse(res, this.workerId);
    }

    private Response selectAll(Tables table){
        switch(table){
            case AREA_INTERESSE -> {
                List<AreaInteresse> res = new LinkedList<AreaInteresse>();
                String query = "select * from area_interesse";
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet resultSet = stat.executeQuery();
                    while(resultSet.next()){
                        res.add(extractAreaInteresse(resultSet));
                    }
                }catch(SQLException sqle){sqle.printStackTrace();}
                return new Response(
                        clientId,
                        ResponseType.List,
                        Tables.AREA_INTERESSE,
                        res);
            }
        }
        return new Response(clientId, ResponseType.Error, table, null);
    }

    private AreaInteresse extractAreaInteresse(ResultSet rSet) throws SQLException{
        AreaInteresse ai = new AreaInteresse(
                rSet.getString("areaid"),
                rSet.getString("denominazione"),
                rSet.getString("stato"),
                rSet.getFloat("latitudine"),
                rSet.getFloat("longitudine"));
        return ai;
    }


    private Response selectAllWithCond(Tables table, String[] params){
        //TODO
        return null;
    }


}
