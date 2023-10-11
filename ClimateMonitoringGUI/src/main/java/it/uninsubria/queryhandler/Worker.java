package it.uninsubria.queryhandler;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.util.IDGenerator;
import javafx.util.Pair;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Worker extends Thread{

    private String workerID;
    private final String dbUrl;
    private final Properties props;

    private Connection conn;
    public Worker(String url, Properties props, String workerID){
        this.dbUrl = url;
        this.props = props;
        this.workerID = workerID;
        try {
            this.conn = DriverManager.getConnection(url, props);
        }catch(SQLException sqle){sqle.printStackTrace(); }
    }

    public void run(){}


    public ResultSet prepAndExecuteStatement(String query, String arg) throws SQLException{
        PreparedStatement stat = conn.prepareStatement(query);
        System.out.println(stat);
        stat.setString(1, arg);
        return stat.executeQuery();
    }

    public <T> List<Pair<City, T>> selectAllFromCityJoin(QueryHandler.tables otherTable){
        String query = "select * from city c join";
        switch(otherTable){
            case AREA_INTERESSE -> {
                query += "area_interesse ai on c.ascii_name = ai.denominazione";
                List<Pair<City, T>> resultList = new LinkedList<Pair<City, T>>();
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        City c = new City(
                                rSet.getString("geoname_id"),
                                rSet.getString("ascii_name"),
                                rSet.getString("country"),
                                rSet.getString("country_code"),
                                rSet.getFloat("latitude"),
                                rSet.getFloat("longitude")
                        );
                        AreaInteresse ai = new AreaInteresse(
                                rSet.getString("areaid"),
                                rSet.getString("denominazione"),
                                rSet.getString("stato"),
                                rSet.getFloat("latitude"),
                                rSet.getFloat("longitude")
                        );
                        resultList.add((Pair<City, T>) new Pair<City, AreaInteresse>(c, ai));
                    }
                    return resultList;
                }catch(SQLException sqle){sqle.printStackTrace(); return null;}
            }
            case CENTRO_MONITORAGGIO -> {
                query += "centro_monitoraggio cm on c.ascii_name = cm.comune";
                List<Pair<City, T>> resultList = new LinkedList<Pair<City, T>>();
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        City c = new City(
                                rSet.getString("geoname_id"),
                                rSet.getString("ascii_name"),
                                rSet.getString("country"),
                                rSet.getString("country_code"),
                                rSet.getFloat("latitude"),
                                rSet.getFloat("longitude")
                        );
                        CentroMonitoraggio cm = new CentroMonitoraggio(
                                rSet.getString("centroid"),
                                rSet.getString("nomecentro"),
                                rSet.getString("comune"),
                                rSet.getString("country")
                        );
                        Array areeIds = rSet.getArray("aree_interesse_ids");
                        String[] ids = (String[]) areeIds.getArray();
                        for(String s : ids) cm.putAreaId(s);
                        resultList.add((Pair<City, T>) new Pair<City, CentroMonitoraggio>(c, cm));
                    }
                }catch(SQLException sqle){sqle.printStackTrace(); return null;}
            }
            default -> {return null;}
        }
        return null;
    }

    public <T> List<T> selectAllFromCmJoin(QueryHandler.tables otherTable){

    }

    public <T> List<T> selectAllFromAiJoin(QueryHandler.tables otherTable){

    }

    public <T> List<T> selectAllFromNotaJoin(QueryHandler.tables otherTable){
        //only parametro_climatico
    }

    public <T> List<T> selectAllFromPcJoin(QueryHandler.tables otherTable){
        String query = "select * from parametro_climatico pc join";
        switch(otherTable){
            case AREA_INTERESSE -> {
                query += "area_interesse ai on pc.areaid = ai.areaid";
                System.out.println(query);
                List<AreaInteresse> result = new LinkedList<AreaInteresse>();
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        result.add(new AreaInteresse(
                                rSet.getString("areaid"),
                                rSet.getString("denominazione"),
                                rSet.getString("stato"),
                                rSet.getFloat("latitudine"),
                                rSet.getFloat("longitudine")
                                ));
                    }
                    return (List<T>) result;
                }catch(SQLException sqle){sqle.printStackTrace(); return null;}
            }
            case CENTRO_MONITORAGGIO -> {
                query += "centro_monitoraggio cm on pc.centroid = cm.centroid";
                System.out.println(query);
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    List<CentroMonitoraggio> resultList = new LinkedList<CentroMonitoraggio>();
                    while(rSet.next()){
                        CentroMonitoraggio cm = new CentroMonitoraggio(
                                rSet.getString("centroid"),
                                rSet.getString("nomecentro"),
                                rSet.getString("comune"),
                                rSet.getString("country")
                        );
                        Array a = rSet.getArray("aree_interesse_ids");
                        String[] areeIds = (String[])a.getArray();
                        for(String id : areeIds) cm.putAreaId(id);
                        resultList.add(cm);
                    }
                    return (List<T>) resultList;
                }catch(SQLException sqle){sqle.printStackTrace(); return null;}
            }
            case NOTA_PARAM_CLIMATICO -> {
                query += "nota_parametro_climatico npc on pc.notaid = npc.notaid";
                System.out.println(query);
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        //TODO
                        return null;
                    }
                }catch(SQLException sqle){sqle.printStackTrace(); return null;}
            }
            default -> {return null;}
        }
        return null;
    }

    public <T> List<T> selectAllFromCmJoinCond(QueryHandler.tables otherTable, String fieldCond, String cond){

    }

    public <T> List<T> selectAllFromAiJoinCond(QueryHandler.tables otherTable, String fieldCond, String cond){

    }

    public <T> List<T> selectAllFromCityJoinCond(QueryHandler.tables otherTable, String field, String cond){

    }

    public <T> List<T> selectAllFromPcJoinCond(QueryHandler.tables otherTable, String fieldCond, String cond){

    }

    public <T> List<T> selectAllFromNotaJoinCond(String fieldCond, String cond){

    }

    public String selectObjFromCityJoinCond(String oggetto, QueryHandler.tables otherTable, String fieldCond, String cond){

    }

    public String selectObjFromCmJoinCond(String oggetto, QueryHandler.tables otherTable, String fieldCond, String cond){

    }

    public String selectObjFromAiJoinCond(String oggetto, QueryHandler.tables otherTable, String fieldCond, String cond){

    }

    public String selectObjFromNotaJoinCond(String oggetto, QueryHandler.tables otherTable, String fieldCond, String cond){

    }

    public String selectObjFromPcJoinCond(String oggetto, QueryHandler.tables otherTable, String fieldCond, String cond){
        String query = "select %s" + oggetto + " from parametro_climatico pc join ";
        switch(otherTable){
            case AREA_INTERESSE -> {
                query = query.formatted("ai.");
                query += "area_interesse ai on pc.areaid = ai.areaid where pc." + fieldCond + " = '" + cond +"'";
                System.out.println(query);
            }
            case CENTRO_MONITORAGGIO -> {
                query = query.formatted("cm.");
                query += "centro_monitoraggio cm on pc.centroid = cm.centroid where pc." +fieldCond + " = '" + cond + "'";
                System.out.println(query);
            }
            case NOTA_PARAM_CLIMATICO -> {
                //TODO
                return null;
            }
            default -> {return null;}
        }
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet res = stat.executeQuery();
            res.next();
            return res.getString(oggetto);
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
    }

    public LinkedList<City> selectAllFromCityWithCond(String fieldCond, String cond){
        String query = "select * from city where " + fieldCond + " = ?";
        LinkedList<City> cities = new LinkedList<City>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                City temp = new City(
                        res.getString("geoname_id"),
                        res.getString("ascii_name"),
                        res.getString("country"),
                        res.getString("country_code"),
                        res.getFloat("latitude"),
                        res.getFloat("longitude"));
                cities.add(temp);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return cities;
    }

    public LinkedList<CentroMonitoraggio> selectAllFromCMWithCond(String fieldCond, String cond){
        String query = "select * from centro_monitoraggio where " + fieldCond + " = ?";
        LinkedList<CentroMonitoraggio> cms = new LinkedList<CentroMonitoraggio>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                CentroMonitoraggio cm = new CentroMonitoraggio(
                        res.getString("centroid"),
                        res.getString("nomecentro"),
                        res.getString("comune"),
                        res.getString("country")
                );
                cms.add(cm);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return cms;
    }

    public LinkedList<Operatore> selectAllFromOpWithCond(String fieldCond, String cond){
        String query = "select * from operatore where " + fieldCond + " = ?";
        LinkedList<Operatore> operatori = new LinkedList<Operatore>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                Operatore op = new Operatore(
                        res.getString("nome"),
                        res.getString("cognome"),
                        res.getString("codice_fiscale"),
                        res.getString("email"),
                        res.getString("userid"),
                        res.getString("password"),
                        res.getString("centroid")
                );
                operatori.add(op);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return operatori;
    }

    public LinkedList<OperatoreAutorizzato> selectAllFromAuthOpWithCond(String fieldCond, String cond){
        String query = "select * from operatore_autorizzati where " + fieldCond + " = ?";
        LinkedList<OperatoreAutorizzato> opAutorizzati = new LinkedList<OperatoreAutorizzato>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                OperatoreAutorizzato authOp = new OperatoreAutorizzato(
                        res.getString("codice_fiscale"),
                        res.getString("email")
                );
                opAutorizzati.add(authOp);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return opAutorizzati;
    }

    public LinkedList<AreaInteresse> selectAllFromAIWithCond(String fieldCond, String cond){
        String query = "select * from area_interesse where " + fieldCond + " = ?";
        LinkedList<AreaInteresse> areeInteresse = new LinkedList<AreaInteresse>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                AreaInteresse ai = new AreaInteresse(
                        res.getString("areaid"),
                        res.getString("denominazione"),
                        res.getString("stato"),
                        res.getFloat("latitudine"),
                        res.getFloat("longitudine")
                );
                areeInteresse.add(ai);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return areeInteresse;
    }

    public LinkedList<Object> selectAllFromNotaWithCond(String fieldCond, String cond){
        //TODO
        return null;
    }

    public LinkedList<ParametroClimatico> selectAllFromCPWithCond(String fieldCond, String cond){
        String query = "select * from parametro_climatico where " + fieldCond + " = ?";
        System.out.println(query);
        LinkedList<ParametroClimatico> parametriClimatici = new LinkedList<ParametroClimatico>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                ParametroClimatico cp = new ParametroClimatico(
                        res.getString("parameterid"),
                        res.getString("centroid"),
                        res.getString("areaid"),
                        res.getDate("pubdate").toLocalDate()
                );
                cp.setVentoValue(res.getShort("valore_vento"));
                cp.setUmiditaValue(res.getShort("valore_umidita"));
                cp.setPressioneValue(res.getShort("valore_pressione"));
                cp.setTemperaturaValue(res.getShort("valore_temperatura"));
                cp.setPrecipitazioniValue(res.getShort("valore_precipitazioni"));
                cp.setAltitudineValue(res.getShort("valore_alt_ghiacciai"));
                cp.setMassaValue(res.getShort("valore_massa_ghiacciai"));

                parametriClimatici.add(cp);
            }
        }catch(SQLException sqle){sqle.printStackTrace();}
        return parametriClimatici;
    }

    private List<String> getQueryResult(String query, String oggetto, String cond){
        List<String> objs = new LinkedList<String>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                objs.add(res.getString(oggetto));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return objs;
    }

    public String selectObjFromCityCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from city where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public String selectObjFromCmCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from centro_monitoraggio where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public String selectObjFromOperatoreCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public String selectObjFromAuthOpCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore_autorizzati where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public String selectObjFromAiCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from area_interesse where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public String selectObjFromNotaCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from nota_parametro_climatico where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public String selectObjFromPcCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from parametro_climatico where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public <T> List<T> selectAllFromTable(QueryHandler.tables table){
        switch (table){
            case CITY -> {
                List<City> res = new LinkedList<City>();
                String query = "select * from city";
                try{
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        City c = new City(
                                rSet.getString("geoname_id"),
                                rSet.getString("ascii_name"),
                                rSet.getString("country"),
                                rSet.getString("country_code"),
                                rSet.getFloat("latitude"),
                                rSet.getFloat("longitude")
                        );
                        res.add(c);
                    }
                }catch(SQLException sqle){sqle.printStackTrace();}
                return (List<T>) res;
            }
            case CENTRO_MONITORAGGIO -> {
                List<CentroMonitoraggio> res = new LinkedList<CentroMonitoraggio>();
                String query = "select * from centro_monitoraggio";
                try{
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        CentroMonitoraggio cm = new CentroMonitoraggio(
                                rSet.getString("centroid"),
                                rSet.getString("nomecentro"),
                                rSet.getString("comune"),
                                rSet.getString("country")
                        );
                        String[] areeInteresseAssociate = (String[]) rSet.getArray("aree_interesse_ids").getArray();
                        //String[] areeInteresseAssociate = (String[])tmp.getArray();
                        for(String id : areeInteresseAssociate)
                            cm.putAreaId(id);
                        res.add(cm);
                    }
                }catch(SQLException sqle){sqle.printStackTrace();}
                return (List<T>) res;
            }
            case OPERATORE -> {
                List<Operatore> res = new LinkedList<Operatore>();
                String query = "select * from operatore";
                try{
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        Operatore o = new Operatore(
                                rSet.getString("nome"),
                                rSet.getString("cognome"),
                                rSet.getString("codice_fiscale"),
                                rSet.getString("email"),
                                rSet.getString("userid"),
                                rSet.getString("password"),
                                rSet.getString("centroid")
                        );
                        res.add(o);
                    }

                }catch(SQLException sqle){sqle.printStackTrace();}
                return (List<T>) res;
            }
            case OP_AUTORIZZATO -> {
                List<OperatoreAutorizzato> operatoriAutorizzati = new LinkedList<OperatoreAutorizzato>();
                String query = "select * from operatore_autorizzati";
                try{
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        OperatoreAutorizzato oAutorizzato = new OperatoreAutorizzato(
                                rSet.getString("codice_fiscale"),
                                rSet.getString("email")
                        );
                        operatoriAutorizzati.add(oAutorizzato);
                    }

                }catch(SQLException sqle){sqle.printStackTrace();}
                return (List<T>) operatoriAutorizzati;
            }
            case PARAM_CLIMATICO -> {
                //TODO
                return null;
            }
            case NOTA_PARAM_CLIMATICO -> {
                //TODO
                return null;
            }
            case AREA_INTERESSE -> {
                List<AreaInteresse> res = new LinkedList<AreaInteresse>();
                String query = "select * from area_interesse";
                try {
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        AreaInteresse ai = new AreaInteresse(rSet.getString("areaid"),
                                rSet.getString("denominazione"),
                                rSet.getString("stato"),
                                rSet.getFloat("latitudine"),
                                rSet.getFloat("longitudine"));
                        res.add(ai);
                    }
                }catch(SQLException sqle){sqle.printStackTrace();}
                return (List<T>) res;
            }
            default -> {return null;}
        }
    }


    public Operatore executeLogin(String userID, String password){
        String query = "select * from operatore where userid = '%s' and password = '%s'".formatted(userID, password);
        System.out.println(query);
        try{
            PreparedStatement stat = conn.prepareStatement(query);
            ResultSet rSet = stat.executeQuery();
            rSet.next();
            //expect 1 row ?
            Operatore o = new Operatore(
                    rSet.getString("nome"),
                    rSet.getString("cognome"),
                    rSet.getString("codice_fiscale"),
                    rSet.getString("email"),
                    rSet.getString("userid"),
                    rSet.getString("password"),
                    rSet.getString("centroid")
            );
            return o;
        }catch(SQLException sqle){
            sqle.printStackTrace();
            return null;
        }
    }

    public boolean insertOperatore(String nomeOp, String cognomeOp, String codFisc, String userID, String email, String password, String centroAfferenza){
        String query = "insert into operatore(nome, cognome, codice_fiscale, email, userid, password, centroid) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')"
                .formatted(nomeOp, cognomeOp, codFisc, userID, email, password, centroAfferenza);
        System.out.println(query);
        try{
            PreparedStatement stat = conn.prepareStatement(query);
            int res = stat.executeUpdate();
            System.out.println(res);
            return res == 1;
        }catch(SQLException sqle){sqle.printStackTrace(); return false;}
    }

    public boolean insertCentroMonitoraggio(String nomeCentro, String comune, String stato, List<String> areeInteresseAssociateIDs){
        String centroid = IDGenerator.generateID();
        int idsSize = areeInteresseAssociateIDs.size();
        StringBuilder arrayIDS = new StringBuilder();
        for(int i = 0; i < idsSize; i++){
            arrayIDS.append(areeInteresseAssociateIDs.get(i));
            if(i < idsSize - 1)
                arrayIDS.append(",");
        }
        String query = "insert into centro_monitoraggio(centroid, nomecentro, comune, country, aree_interesse_ids) values ('%s', '%s', '%s', '%s', '{%s}')"
                .formatted(centroid, nomeCentro, comune, stato, arrayIDS.toString());
        System.out.println(query);
        try{
            PreparedStatement stat = conn.prepareStatement(query);
            int res = stat.executeUpdate();
            System.out.println(res);
            return res == 1;
        }catch(SQLException sqle){sqle.printStackTrace(); return false;}
    }

}
