package it.uninsubria.servercm;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.NotaParametro;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.queryhandler.QueryHandler;
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
                if(request.getParams().length < ServerInterface.selectAllWithCondParamsLength){
                    res = new Response(clientId, ResponseType.Error, request.getTable(), null);
                }else{
                    res = selectAllWithCond(request);
                }
            }
            case selectObjWithCond -> {
                if(request.getParams().length < ServerInterface.selectObjWithCondParamsLength){
                    res = new Response(clientId, ResponseType.Error, request.getTable(), null);
                }
                else{
                    res = selectObjWithCond(request);
                }
            }
            case selectObjJoinWithCond -> {
                //TODO
            }
        }
        //save the result in the server's queue
        System.out.printf("Worker %s saving request %s in server's queue\n", this.workerId, request.getRequestId());
        server.addResponse(res, this.workerId);
    }


    public ResultSet prepAndExecuteStatement(String query, String arg) throws SQLException{
        PreparedStatement stat = conn.prepareStatement(query);
        System.out.println(workerId + ":"+ stat);
        stat.setString(1, arg);
        return stat.executeQuery();
    }

    private String getQueryResult(String query, String oggetto, String cond){
        String obj = "";
        System.out.println(workerId + ":" + query);
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                obj = res.getString(oggetto);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return obj;
    }



    private ParametroClimatico extractParametroClimatico(ResultSet rSet) throws SQLException{
        ParametroClimatico pc = new ParametroClimatico(
                rSet.getString("parameterid"),
                rSet.getString("centroid"),
                rSet.getString("areaid"),
                rSet.getDate("pubdate").toLocalDate());
        pc.setVentoValue(rSet.getShort("valore_vento"));
        pc.setUmiditaValue(rSet.getShort("valore_umidita"));
        pc.setPressioneValue(rSet.getShort("valore_pressione"));
        pc.setTemperaturaValue(rSet.getShort("valore_temperatura"));
        pc.setPrecipitazioniValue(rSet.getShort("valore_precipitazioni"));
        pc.setAltitudineValue(rSet.getShort("valore_alt_ghiacciai"));
        pc.setMassaValue(rSet.getShort("valore_massa_ghiacciai"));
        return pc;
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

    private NotaParametro extractNota(ResultSet rSet) throws SQLException{
        NotaParametro nota =
                new NotaParametro(
                        rSet.getString("notaid"),
                        rSet.getString("nota_vento"),
                        rSet.getString("nota_umidita"),
                        rSet.getString("nota_pressione"),
                        rSet.getString("nota_temperatura"),
                        rSet.getString("nota_precipitazioni"),
                        rSet.getString("nota_alt_ghiacciai"),
                        rSet.getString("nota_massa_ghiacciai"));
        return nota;
    }

    private CentroMonitoraggio extractCentroMonitoraggio(ResultSet rSet) throws SQLException{
        CentroMonitoraggio cm = new CentroMonitoraggio(
                rSet.getString("centroid"),
                rSet.getString("nomecentro"),
                rSet.getString("comune"),
                rSet.getString("country")
        );
        Array a = rSet.getArray("aree_interesse_ids");
        for(String s : (String[])a.getArray()){
            cm.putAreaId(s);
        }
        return cm;
    }

    private City extractCity(ResultSet rSet) throws SQLException{
        City c = new City(
                rSet.getString("geoname_id"),
                rSet.getString("ascii_name"),
                rSet.getString("country"),
                rSet.getString("country_code"),
                rSet.getFloat("latitude"),
                rSet.getFloat("longitude")
        );
        return c;
    }

    private Operatore extractOperatore(ResultSet rSet) throws SQLException{
        Operatore op = new Operatore(
                rSet.getString("nome"),
                rSet.getString("cognome"),
                rSet.getString("codice_fiscale"),
                rSet.getString("email"),
                rSet.getString("userid"),
                rSet.getString("password"),
                rSet.getString("centroid")
        );
        return op;
    }

    private OperatoreAutorizzato extractAuthOp(ResultSet rSet) throws SQLException{
        OperatoreAutorizzato authOp = new OperatoreAutorizzato(
                rSet.getString("codice_fiscale"),
                rSet.getString("email")
        );
        return authOp;
    }

    private Response selectAll(Tables table){
        switch(table){
            case CITY -> {
                List<City> res = new LinkedList<City>();
                String query = "select * from city";
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        City c = extractCity(rSet);
                        res.add(c);
                    }
                }catch(SQLException sqle){sqle.printStackTrace();}
                return new Response(clientId,
                        ResponseType.List,
                        Tables.CITY,
                        res);
            }
            case CENTRO_MONITORAGGIO -> {
                List<CentroMonitoraggio> res = new LinkedList<CentroMonitoraggio>();
                String query = "select * from centro_monitoraggio";
                try{
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
                        res.add(cm);
                    }
                }catch(SQLException sqle){sqle.printStackTrace();}
                return new Response(
                        clientId,
                        ResponseType.List,
                        Tables.CENTRO_MONITORAGGIO,
                        res);
            }
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
            case OPERATORE -> {
                List<Operatore> res = new LinkedList<Operatore>();
                String query = "select * from operatore";
                try{
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        Operatore o = extractOperatore(rSet);
                        res.add(o);
                    }

                }catch(SQLException sqle){sqle.printStackTrace();}
                return new Response(clientId,
                        ResponseType.List,
                        Tables.OPERATORE,
                        res);
            }
            case OP_AUTORIZZATO -> {
                List<OperatoreAutorizzato> res = new LinkedList<OperatoreAutorizzato>();
                String query = "select * from operatore_autorizzati";
                try{
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        OperatoreAutorizzato oAutorizzato = extractAuthOp(rSet);
                        res.add(oAutorizzato);
                    }

                }catch(SQLException sqle){sqle.printStackTrace();}
                return new Response(clientId,
                        ResponseType.List,
                        Tables.OP_AUTORIZZATO,
                        res);
            }
            case PARAM_CLIMATICO -> {
                List<ParametroClimatico> res = new LinkedList<ParametroClimatico>();
                String query = "select * from parametro_climatico";
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        ParametroClimatico pc =
                                extractParametroClimatico(rSet);
                        res.add(pc);
                    }
                }catch(SQLException sqle){sqle.printStackTrace(); }
                return new Response(clientId,
                        ResponseType.List,
                        Tables.PARAM_CLIMATICO,
                        res);
            }
        }
        return new Response(clientId, ResponseType.Error, table, null);
    }

    private List<City> selectAllCityCond(String fieldCond, String cond){
        String query = "select * from city where " + fieldCond + " = ?";
        LinkedList<City> cities = new LinkedList<City>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                City c = extractCity(rSet);
                cities.add(c);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return cities;
    }

     private List<CentroMonitoraggio> selectAllCmCond(String fieldCond, String cond){
        String query = "select * from centro_monitoraggio where " + fieldCond + " = ?";
        List<CentroMonitoraggio> cms = new LinkedList<CentroMonitoraggio>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
                cms.add(cm);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return cms;
    }

    private List<AreaInteresse> selectAllAiCond(String fieldCond, String cond){
        String query = "select * from area_interesse where " + fieldCond + " = ?";
        LinkedList<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                AreaInteresse ai = extractAreaInteresse(rSet);
                areeInteresse.add(ai);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return areeInteresse;
    }

    private List<ParametroClimatico> selectAllPcCond(String fieldCond, String cond){
        String query = "select * from parametro_climatico where " + fieldCond + " = ?";
        System.out.println(query);
        LinkedList<ParametroClimatico> parametriClimatici = new LinkedList<ParametroClimatico>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                ParametroClimatico cp = extractParametroClimatico(rSet);
                parametriClimatici.add(cp);
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return parametriClimatici;
    }

    private List<NotaParametro> selectAllNotaCond(String fieldCond, String cond){
        String query = "select * from nota_parametro_climatico where %s = '%s'";
        query = query.formatted(fieldCond, cond);
        List<NotaParametro> resultList = new LinkedList<NotaParametro>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                NotaParametro np = extractNota(rSet);
                resultList.add(np);
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return resultList;
    }

    private List<Operatore> selectAllOpCond(String fieldCond, String cond){
        String query = "select * from operatore where " + fieldCond + " = ?";
        LinkedList<Operatore> operatori = new LinkedList<Operatore>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                Operatore op = extractOperatore(rSet);
                operatori.add(op);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return operatori;
    }

    public List<OperatoreAutorizzato> selectAllAuthOpCond(String fieldCond, String cond){
        String query = "select * from operatore_autorizzati where " + fieldCond + " = ?";
        LinkedList<OperatoreAutorizzato> opAutorizzati = new LinkedList<OperatoreAutorizzato>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                OperatoreAutorizzato authOp = extractAuthOp(rSet);
                opAutorizzati.add(authOp);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return opAutorizzati;
    }

    private Response selectAllWithCond(Request r){
        switch(r.getTable()){
            case CITY -> {
                List<City> res = selectAllCityCond(r.getParams()[0], r.getParams()[1]);
                return new Response(r.getClientId(), ResponseType.List, r.getTable(), res);
            }
            case CENTRO_MONITORAGGIO -> {
                List<CentroMonitoraggio> res = selectAllCmCond(r.getParams()[0], r.getParams()[1]);
                return new Response(r.getClientId(), ResponseType.List, r.getTable(), res);
            }
            case AREA_INTERESSE -> {
                List<AreaInteresse> res = selectAllAiCond(r.getParams()[0], r.getParams()[1]);
                return new Response(r.getClientId(), ResponseType.List, r.getTable(), res);
            }
            case PARAM_CLIMATICO -> {
                List<ParametroClimatico> res = selectAllPcCond(r.getParams()[0], r.getParams()[1]);
                return new Response(r.getClientId(), ResponseType.List, r.getTable(), res);
            }
            case NOTA_PARAM_CLIMATICO -> {
                List<NotaParametro> res = selectAllNotaCond(r.getParams()[0], r.getParams()[1]);
                return new Response(r.getClientId(), ResponseType.List, r.getTable(), res);
            }
            case OPERATORE -> {
                List<Operatore> res = selectAllOpCond(r.getParams()[0], r.getParams()[1]);
                return new Response(r.getClientId(), ResponseType.List, r.getTable(), res);
            }
            case OP_AUTORIZZATO -> {
                List<OperatoreAutorizzato> res = selectAllAuthOpCond(r.getParams()[0], r.getParams()[1]);
                return new Response(r.getClientId(), ResponseType.List, r.getTable(), res);
            }
            default -> {
                return new Response(
                        r.getClientId(),
                        ResponseType.List,
                        r.getTable(),
                        null);
            }
        }
    }

    private String selectObjCityCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from city where "+ fieldCond + " = ?";
        return getQueryResult(query, fieldCond, cond) ;
    }

    private String selectObjCmCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from centro_monitoraggio where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    private String selectObjAiCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from area_interesse where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    private String selectObjPcCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from parametro_climatico where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    private String selectObjNpcCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from nota_parametro_climatico where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    private String selectObjOpCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    private String selectObjAuthOpCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore_autorizzati where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    private Response selectObjWithCond(Request r){
        switch(r.getTable()){
            case CITY -> {
                String res = selectObjCityCond(r.getParams()[0], r.getParams()[1], r.getParams()[3]);
                return new Response(r.getClientId(), ResponseType.Object, r.getTable(), res);
            }
            case CENTRO_MONITORAGGIO -> {
                String res = selectObjCmCond(r.getParams()[0], r.getParams()[1], r.getParams()[3]);
                return new Response(r.getClientId(), ResponseType.Object, r.getTable(), res);
            }
            case AREA_INTERESSE -> {
                String res = selectObjAiCond(r.getParams()[0], r.getParams()[1], r.getParams()[3]);
                return new Response(r.getClientId(), ResponseType.Object, r.getTable(), res);
            }
            case PARAM_CLIMATICO -> {
                String res = selectObjPcCond(r.getParams()[0], r.getParams()[1], r.getParams()[3]);
                return new Response(r.getClientId(), ResponseType.Object, r.getTable(), res);
            }
            case NOTA_PARAM_CLIMATICO -> {
                String res = selectObjNpcCond(r.getParams()[0], r.getParams()[1], r.getParams()[3]);
                return new Response(r.getClientId(), ResponseType.Object, r.getTable(), res);
            }
            case OPERATORE -> {
                String res = selectObjOpCond(r.getParams()[0], r.getParams()[1], r.getParams()[3]);
                return new Response(r.getClientId(), ResponseType.Object, r.getTable(), res);
            }
            case OP_AUTORIZZATO -> {
                String res = selectObjAuthOpCond(r.getParams()[0], r.getParams()[1], r.getParams()[3]);
                return new Response(r.getClientId(), ResponseType.Object, r.getTable(), res);
            }
            default -> {
                return new Response(
                        r.getClientId(),
                        ResponseType.Object,
                        r.getTable(),
                        null);
            }
        }
    }

    private Response getQueryResultList(Tables table, String oggetto, String fieldCond, String cond, String query) {
        query = query.formatted(oggetto, fieldCond, cond);
        List<String> resultList = new LinkedList<String>();
        Response res = null;
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                resultList.add(rSet.getString(oggetto));
            }
            res = new Response(clientId, ResponseType.Object, table, resultList);
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return res;
    }

    private Response selectObjectsCityJoinAiCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from city c join area_interesse ai on c.ascii_name = ai.denominazione where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    private Response selectObjectsCityJoinCmCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from city c join centro_monitoraggio cm on c.ascii_name = cm.nomecentro where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    private Response selectObjectsCmJoinAiCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from centro_monitoraggio cm join area_interesse ai on ai.areaid = any(cm.aree_interesse_ids) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    private Response selectObjectsCmJoinPcCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from centro_monitoraggio cm join parametro_climatico pc using(centroid) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    private Response selectObjectsAiJoinPcCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from area_interesse ai join parametro_climatico pc using(areaid) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    private Response selectObjectsAiJoinCmCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from area_interesse ai join centro_monitoraggio cm on ai.areaid = any(aree_interesse_ids) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }


    private Response selectObjectsAiJoinCityCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from area_interesse ai join city c on ai.denominazione = c.ascii_name where %s = '%s'" ;
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }


    private Response selectObjectsNotaJoinPcCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from nota_parametro_climatico npc join parametro_climatico pc using(notaid) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }


    private Response selectObjectsPcJoinAiCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from parametro_climatico pc join area_interesse ai using(areaid) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    private Response selectObjectsPcJoinCmCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from parametro_climatico pc join centro_monitoraggio using(centroid) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    private Response selectObjectsPcJoinNpcCond(Tables table, String oggetto, String fieldCond, String cond){
        String query = "select %s from parametro_climatico pc join nota_parametro_climatico using(notaid) where %s = '%s'";
        return getQueryResultList(table, oggetto, fieldCond, cond, query);
    }

    public Response selectObjectJoinWithCond(String oggetto, Tables table, Tables otherTable, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return selectObjectsCityJoinAiCond(table, oggetto, fieldCond, cond);
                    }
                    case CENTRO_MONITORAGGIO -> {
                        return selectObjectsCityJoinCmCond(table, oggetto, fieldCond, cond);
                    }
                }
            }
            case CENTRO_MONITORAGGIO -> {
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return selectObjectsCmJoinAiCond(table, oggetto, fieldCond, cond);
                    }
                    case PARAM_CLIMATICO -> {
                        return selectObjectsCmJoinPcCond(table, oggetto, fieldCond, cond);
                    }
                }
            }
            case AREA_INTERESSE -> {
                switch(otherTable){
                    case CENTRO_MONITORAGGIO -> {
                        return selectObjectsAiJoinCmCond(table, oggetto, fieldCond, cond);
                    }
                    case PARAM_CLIMATICO -> {
                        return selectObjectsAiJoinPcCond(table, oggetto, fieldCond, cond);
                    }
                    case CITY -> {
                        return selectObjectsAiJoinCityCond(table, oggetto, fieldCond, cond);
                    }
                }
            }
            case NOTA_PARAM_CLIMATICO -> {
                return selectObjectsNotaJoinPcCond(table, oggetto, fieldCond, cond);
            }
            case PARAM_CLIMATICO -> {
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return selectObjectsPcJoinAiCond(table, oggetto, fieldCond, cond);
                    }
                    case CENTRO_MONITORAGGIO -> {
                        return selectObjectsPcJoinCmCond(table, oggetto, fieldCond, cond);
                    }
                    case NOTA_PARAM_CLIMATICO -> {
                        return selectObjectsPcJoinNpcCond(table, oggetto, fieldCond, cond);
                    }
                }
            }
            default -> {
                return new Response(clientId, ResponseType.Error, table, null);
            }
        }
        return null;
    }


}
