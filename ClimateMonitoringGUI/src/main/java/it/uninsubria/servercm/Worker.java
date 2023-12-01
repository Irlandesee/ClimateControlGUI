package it.uninsubria.servercm;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.NotaParametro;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface.Tables;
import it.uninsubria.servercm.ServerInterface.ResponseType;
import it.uninsubria.util.IDGenerator;


import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Worker extends Thread{

    private static int workerCount = 0;
    private String clientId;
    private String requestId;
    private String responseId;
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
        this.responseId = IDGenerator.generateID();
        System.out.printf("Worker %s serving client{%s} request: %s\n", workerId, clientId, requestId);
        //System.out.println("Request: " + request.toString());
        //Read the request
        Response res = null;
        switch(request.getRequestType()){
            //query the database
            case selectAll -> {
                res = selectAll(request);
                System.out.println("Response: " + res);
            }
            case selectAllWithCond -> {
                if(request.getParams().size() < ServerInterface.selectAllWithCondParamsLength){
                    res = new Response(clientId, requestId, responseId,  ResponseType.Error, request.getTable(), null);
                }else{
                    res = selectAllWithCond(request);
                }
            }
            case selectObjWithCond -> {
                if(request.getParams().size() < ServerInterface.selectObjWithCondParamsLength){
                    res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                }
                else{
                    res = selectObjWithCond(request);
                }
            }
            case selectObjJoinWithCond -> {
                if(request.getParams().size() < ServerInterface.selectObjJoinWithCondParamsLength){
                    res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                }else{
                    res = selectObjectJoinWithCond(request);
                }
            }
            case executeLogin -> {
                if(request.getParams().size() < ServerInterface.executeLoginParamsLength){
                    res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                }else{
                    res = executeLogin(request);
                }
            }
            case insert -> {
                switch(request.getTable()){
                    case AREA_INTERESSE -> {
                        if(request.getParams().size() < ServerInterface.insertAiParamsLength){
                            res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                        }else{
                            res = insertAreaInteresse(request);
                        }
                    }
                    case PARAM_CLIMATICO -> {
                        if(request.getParams().size() < ServerInterface.insertPcParamsLength){
                            res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                        }else{
                            res = insertParametroClimatico(request);
                        }
                    }
                    case OPERATORE -> {
                        if(request.getParams().size() < ServerInterface.insertOpParamsLength){
                            res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                        }else{
                            res = insertOperatore(request);
                        }
                    }
                    case NOTA_PARAM_CLIMATICO -> {
                        if(request.getParams().size() < ServerInterface.insertNpcParamsLength){
                            res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                        }else{
                            res = insertNotaParametroClimatico(request);
                        }
                    }
                    case CENTRO_MONITORAGGIO -> {
                        if(request.getParams().size() < ServerInterface.insertCmParamsLength){
                            res = new Response(clientId, requestId, responseId, ResponseType.Error, request.getTable(), null);
                        }else{
                            res = insertCentroMonitoraggio(request);
                        }
                    }
                }

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

    private Response selectAll(Request req){
        switch(req.getTable()){
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
                return new Response(
                        clientId,
                        req.getRequestId(),
                        IDGenerator.generateID(),
                        ResponseType.List,
                        req.getTable(),
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
                        req.getRequestId(),
                        IDGenerator.generateID(),
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
                        req.getRequestId(),
                        IDGenerator.generateID(),
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
                return new Response(
                        clientId,
                        req.getRequestId(),
                        IDGenerator.generateID(),
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
                return new Response(
                        clientId,
                        req.getRequestId(),
                        IDGenerator.generateID(),
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
                return new Response(
                        clientId,
                        req.getRequestId(),
                        IDGenerator.generateID(),
                        ResponseType.List,
                        Tables.PARAM_CLIMATICO,
                        res);
            }
        }
        return new Response(
                clientId,
                req.getRequestId(),
                IDGenerator.generateID(),
                ResponseType.Error,
                req.getTable(),
                null);
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
        String query = "select * from area_interesse where %s = '%s'".formatted(fieldCond, cond);
        System.out.println(query);
        List<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                AreaInteresse ai = extractAreaInteresse(rSet);
                areeInteresse.add(ai);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        areeInteresse.forEach(System.out::println);
        return areeInteresse;
    }

    private List<ParametroClimatico> selectAllPcCond(String fieldCond, String cond){
        String query = "select * from parametro_climatico where %s = '%s'".formatted(fieldCond, cond);
        System.out.println(query);
        LinkedList<ParametroClimatico> parametriClimatici = new LinkedList<ParametroClimatico>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                ParametroClimatico cp = extractParametroClimatico(rSet);
                parametriClimatici.add(cp);
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return parametriClimatici;
    }

    private List<NotaParametro> selectAllNotaCond(String fieldCond, String cond){
        String query = "select * from nota_parametro_climatico where %s = '%s'".formatted(fieldCond, cond);
        List<NotaParametro> resultList = new LinkedList<NotaParametro>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                NotaParametro np = extractNota(rSet);
                resultList.add(np);
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return resultList;
    }

    private List<Operatore> selectAllOpCond(String fieldCond, String cond){
        String query = "select * from operatore where %s = '%s'".formatted(fieldCond, cond);
        LinkedList<Operatore> operatori = new LinkedList<Operatore>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                Operatore op = extractOperatore(rSet);
                operatori.add(op);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return operatori;
    }

    public List<OperatoreAutorizzato> selectAllAuthOpCond(String fieldCond, String cond){
        String query = "select * from operatore_autorizzati where %s = '%s'".formatted(fieldCond, cond);
        LinkedList<OperatoreAutorizzato> opAutorizzati = new LinkedList<OperatoreAutorizzato>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                OperatoreAutorizzato authOp = extractAuthOp(rSet);
                opAutorizzati.add(authOp);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return opAutorizzati;
    }

    private Response selectAllWithCond(Request r){
        Map<String, String> params = r.getParams();
        System.out.println("executing request" + r);
        switch(r.getTable()){
            case CITY -> {
                List<City> res = selectAllCityCond(params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.List, r.getTable(), res);
            }
            case CENTRO_MONITORAGGIO -> {
                List<CentroMonitoraggio> res = selectAllCmCond(params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.List, r.getTable(), res);
            }
            case AREA_INTERESSE -> {
                List<AreaInteresse> res = selectAllAiCond(params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.List, r.getTable(), res);
            }
            case PARAM_CLIMATICO -> {
                List<ParametroClimatico> res = selectAllPcCond(params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.List, r.getTable(), res);
            }
            case NOTA_PARAM_CLIMATICO -> {
                List<NotaParametro> res = selectAllNotaCond(params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.List, r.getTable(), res);
            }
            case OPERATORE -> {
                List<Operatore> res = selectAllOpCond(params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.List, r.getTable(), res);
            }
            case OP_AUTORIZZATO -> {
                List<OperatoreAutorizzato> res = selectAllAuthOpCond(params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.List, r.getTable(), res);
            }
            default -> {
                return new Response(
                        clientId,
                        requestId,
                        IDGenerator.generateID(),
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
        Map<String, String> params = r.getParams();
        switch(r.getTable()){
            case CITY -> {
                String res = selectObjCityCond(params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.Object, Tables.CITY, res);
            }
            case CENTRO_MONITORAGGIO -> {
                String res = selectObjCmCond(params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.Object, Tables.CENTRO_MONITORAGGIO, res);
            }
            case AREA_INTERESSE -> {
                String res = selectObjAiCond(params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.Object, Tables.AREA_INTERESSE, res);
            }
            case PARAM_CLIMATICO -> {
                String res = selectObjPcCond(params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.Object, Tables.PARAM_CLIMATICO, res);
            }
            case NOTA_PARAM_CLIMATICO -> {
                String res = selectObjNpcCond(params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.Object, Tables.NOTA_PARAM_CLIMATICO, res);
            }
            case OPERATORE -> {
                String res = selectObjOpCond(params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.Object, Tables.OPERATORE, res);
            }
            case OP_AUTORIZZATO -> {
                String res = selectObjAuthOpCond(params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                return new Response(clientId, requestId, responseId, ResponseType.Object, Tables.OP_AUTORIZZATO, res);
            }
            default -> {
                return new Response(
                        clientId,
                        requestId,
                        responseId,
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
            res = new Response(clientId, requestId, responseId, ResponseType.Object, table, resultList);
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

    public Response selectObjectJoinWithCond(Request req){
        Map<String, String> params = req.getParams();
        Tables otherTable = Tables.valueOf(params.get(RequestFactory.joinKey));
        switch(req.getTable()){
            case CITY -> {
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return selectObjectsCityJoinAiCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                    case CENTRO_MONITORAGGIO -> {
                        return selectObjectsCityJoinCmCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                }
            }
            case CENTRO_MONITORAGGIO -> {
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return selectObjectsCmJoinAiCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                    case PARAM_CLIMATICO -> {
                        return selectObjectsCmJoinPcCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                }
            }
            case AREA_INTERESSE -> {
                switch(otherTable){
                    case CENTRO_MONITORAGGIO -> {
                        return selectObjectsAiJoinCmCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                    case PARAM_CLIMATICO -> {
                        return selectObjectsAiJoinPcCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                    case CITY -> {
                        return selectObjectsAiJoinCityCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                }
            }
            case NOTA_PARAM_CLIMATICO -> {
                return selectObjectsNotaJoinPcCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
            }
            case PARAM_CLIMATICO -> {
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return selectObjectsPcJoinAiCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                    case CENTRO_MONITORAGGIO -> {
                        return selectObjectsPcJoinCmCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                    case NOTA_PARAM_CLIMATICO -> {
                        return selectObjectsPcJoinNpcCond(otherTable, params.get(RequestFactory.objectKey), params.get(RequestFactory.condKey), params.get(RequestFactory.fieldKey));
                    }
                }
            }
            default -> {
                return new Response(clientId, requestId, responseId, ResponseType.Error, req.getTable(), null);
            }
        }
        return null;
    }

    public Response executeLogin(Request request){
        String userId = request.getParams().get(RequestFactory.userKey);
        String password = request.getParams().get(RequestFactory.passwordKey);
        String query = "select * from operatore where userid = '%s' and password = '%s'".formatted(userId, password);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            rSet.next();//expects 1 row
            Operatore operatore = extractOperatore(rSet);
            return new Response(
                    clientId,
                    requestId,
                    responseId,
                    ResponseType.loginOk,
                    Tables.OPERATORE,
                    operatore);

        }catch(SQLException sqle){sqle.printStackTrace();
            return new Response(
                    clientId,
                    requestId,
                    responseId,
                    ResponseType.loginKo,
                    Tables.OPERATORE,
                    null);
        }
    }

    public Response insertOperatore(Request request){
        Map<String, String> params = request.getParams();

        String nomeOp = params.get(RequestFactory.nomeOpKey);
        String cognomeOp = params.get(RequestFactory.cognomeOpKey);
        String codFisc = params.get(RequestFactory.codFiscOpKey);
        String userId = params.get(RequestFactory.userKey);
        String password = params.get(RequestFactory.passwordKey);
        String email = params.get(RequestFactory.emailOpKey);
        String centroAfferenza = params.get(RequestFactory.centroAfferenzaKey);
        String query = "insert into operatore(nome, cognome, codice_fiscale, email, userid, password, centroid) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')"
                .formatted(nomeOp, cognomeOp, codFisc, userId, email, password, centroAfferenza);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            int res = stat.executeUpdate();
            if(res == 1){
                return new Response(
                        clientId,
                        requestId,
                        responseId,
                        ResponseType.insertOk,
                        Tables.OPERATORE,
                        true);
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return new Response(
                clientId,
                requestId,
                responseId,
                ResponseType.insertKo,
                Tables.OPERATORE,
                false);
    }

    public Response insertCentroMonitoraggio(Request request){
        Map<String, String> params = request.getParams();
        String centroId = IDGenerator.generateID();
        //String,String,String,String
        String tmp =  params.get(RequestFactory.listAiKey);
        String[] idAreeInteresseAssociate = tmp.split(",");
        int idListSize = idAreeInteresseAssociate.length;
        StringBuilder ids = new StringBuilder();
        for(int i = 0; i < idListSize; i++){
            ids.append(idAreeInteresseAssociate[i]);
            if(i < idListSize - 1)
                ids.append(",");
        }
        String query = "insert into centro_monitoraggio(centroid, nomecentro, comune, country, aree_interesse_ids) values ('%s', '%s', '%s', '%s', '{%s}')"
                .formatted(centroId,
                        params.get(RequestFactory.nomeCentroKey),
                        params.get(RequestFactory.comuneCentroKey),
                        params.get(RequestFactory.countryCentroKey),
                        ids.toString());
        try(PreparedStatement stat = conn.prepareStatement(query)){
            int res = stat.executeUpdate();
            if(res == 1){
                return new Response(
                        clientId,
                        requestId,
                        responseId,
                        ResponseType.insertOk,
                        Tables.CENTRO_MONITORAGGIO,
                        true);
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return new Response(
                clientId,
                requestId,
                responseId,
                ResponseType.insertKo,
                Tables.CENTRO_MONITORAGGIO,
                false);
    }

    //Non inserisci notaid
    public Response insertParametroClimatico(Request request){
        Map<String, String> params = request.getParams();
        String parameterId = params.get(RequestFactory.parameterIdKey);
        String centroId = params.get(RequestFactory.centroIdKey);
        String areaId = params.get(RequestFactory.pubDateKey);
        String notaId = params.get(RequestFactory.notaIdKey);
        String valoreVento = params.get(RequestFactory.valoreVentoKey);
        String valoreUmidita = params.get(RequestFactory.valoreUmiditaKey);
        String valorePressione = params.get(RequestFactory.valorePressioneKey);
        String valoreTemperatura= params.get(RequestFactory.valoreTemperaturaKey);
        String valorePrecipitazioni = params.get(RequestFactory.valorePrecipitazioniKey);
        String valoreAltGhiacciai = params.get(RequestFactory.valoreAltGhiacciaiKey);
        String valoreMassaGhiacciai = params.get(RequestFactory.valoreMassaGhiacciaiKey);
        String query =
                "insert into parametro_climatico(parameterid, centroid, areaid, pubdate, valore_vento, valore_umidita, valore_pressione, valore_temperatura, valore_precipitazioni, valore_alt_ghiacciai, valore_massa_ghiacciai) " +
                        "values ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')";
        query = query.formatted(
                parameterId,
                centroId,
                areaId,
                notaId,
                valoreVento,
                valoreUmidita,
                valorePressione,
                valoreTemperatura,
                valorePrecipitazioni,
                valoreAltGhiacciai,
                valoreMassaGhiacciai);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            int res = stat.executeUpdate();
            if(res == 1)
                return new Response(
                        clientId,
                        requestId,
                        responseId,
                        ResponseType.insertOk,
                        Tables.PARAM_CLIMATICO,
                        true);
        }catch(SQLException sqle){sqle.printStackTrace();}
        return new Response(
                clientId,
                requestId,
                responseId,
                ResponseType.insertKo,
                Tables.PARAM_CLIMATICO,
                false);
    }


    private Response insertAreaInteresse(Request request) {
        Map<String, String> params = request.getParams();
        String areaId = params.get(RequestFactory.areaIdKey);
        String denominazione = params.get(RequestFactory.denominazioneAreaKey);
        String stato = params.get(RequestFactory.statoAreaKey);
        String latitudine = params.get(RequestFactory.latitudineKey);
        String longitudine = params.get(RequestFactory.longitudineKey);
        String query = "insert into area_interesse(areaid, denominazione, stato, latitudine, longitudine) values ('%s', '%s', '%s', '%s', '%s')";
        query = query.formatted(areaId, denominazione, stato, latitudine, longitudine);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            int res = stat.executeUpdate();
            if(res == 1){
                return new Response(
                        clientId,
                        requestId,
                        responseId,
                        ResponseType.insertOk,
                        Tables.AREA_INTERESSE,
                        true
                );
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return new Response(
                clientId,
                requestId,
                responseId,
                ResponseType.insertKo,
                Tables.AREA_INTERESSE,
                false
        );
    }

    private Response insertNotaParametroClimatico(Request request){
        Map<String, String> params = request.getParams();
        String notaId = params.get(RequestFactory.notaIdKey);
        String notaVento = params.get(RequestFactory.notaVentoKey);
        String notaUmidita = params.get(RequestFactory.notaUmidita);
        String notaPressione = params.get(RequestFactory.notaPressione);
        String notaTemperatura = params.get(RequestFactory.notaTemperatura);
        String notaPrecipitazioni = params.get(RequestFactory.notaPrecipitazioni);
        String notaAltGhiacciai = params.get(RequestFactory.notaAltGhiacciai);
        String notaMassaGhiacciai = params.get(RequestFactory.notaMassaGhiacciai);

        String query = "insert into nota_parametro_climatico(notaid, nota_vento, nota_umidita, nota_pressione, nota_temperatura, nota_precipitazioni, nota_alt_ghiacciai, nota_massa_ghiacciai)" +
                "values ('%s','%s', '%s', '%s', '%s', '%s', '%s', '%s')";
        query = query.formatted(notaId, notaVento, notaUmidita, notaPressione, notaTemperatura, notaPrecipitazioni, notaAltGhiacciai, notaMassaGhiacciai);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            int res = stat.executeUpdate();
            if(res == 1){
                return new Response(
                        clientId,
                        requestId,
                        responseId,
                        ResponseType.insertOk,
                        Tables.NOTA_PARAM_CLIMATICO,
                        true
                );
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return new Response(
                clientId,
                requestId,
                responseId,
                ResponseType.insertKo,
                Tables.NOTA_PARAM_CLIMATICO,
                false
        );
    }


}
