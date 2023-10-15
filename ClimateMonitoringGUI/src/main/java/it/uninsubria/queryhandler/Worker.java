package it.uninsubria.queryhandler;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.NotaParametro;
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

    private List<String> getQueryResult(String query, String oggetto, String cond){
        List<String> objs = new LinkedList<String>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                objs.add(res.getString(oggetto));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return objs;
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

    private AreaInteresse extractAreaInteresse(ResultSet rSet) throws SQLException{
        AreaInteresse ai = new AreaInteresse(
                rSet.getString("areaid"),
                rSet.getString("denominazione"),
                rSet.getString("stato"),
                rSet.getFloat("latitudine"),
                rSet.getFloat("longitudine"));
        return ai;
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

    public List<Pair<City, AreaInteresse>> selectAllCityJoinAi(){
        String query = "select * from city c join area_interesse ai on c.ascii_name = ai.denominazione";
        List<Pair<City, AreaInteresse>> resultList = new LinkedList<Pair<City, AreaInteresse>>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                City c = extractCity(rSet);
                AreaInteresse ai = extractAreaInteresse(rSet);
                resultList.add(new Pair<City, AreaInteresse>(c, ai));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<City, CentroMonitoraggio>> selectAllCityJoinCm(){
        String query = "select * from city c join centro_monitoraggio cm on c.ascii_name = cm.comune";
        List<Pair<City, CentroMonitoraggio>> resultList = new LinkedList<Pair<City, CentroMonitoraggio>>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                City c = extractCity(rSet);
                CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
                resultList.add(new Pair<City, CentroMonitoraggio>(c, cm));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<NotaParametro, ParametroClimatico>> selectAllFromNotaJoin(){
        String query = "select * from nota_parametro_climatico npc join public.parametro_climatico pc using(notaid)";
        try(PreparedStatement stat = conn.prepareStatement(query)){
            List<Pair<NotaParametro, ParametroClimatico>> resultList = new LinkedList<Pair<NotaParametro, ParametroClimatico>>();
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                NotaParametro np = extractNota(rSet);
                ParametroClimatico pc = extractParametroClimatico(rSet);
                resultList.add(new Pair<NotaParametro, ParametroClimatico>(np, pc));
            }
            return resultList;
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
    }

    public List<Pair<ParametroClimatico, CentroMonitoraggio>> selectAllPcJoinCm(){
        String query = "select * from parametro_climatico pc join centro_monitoraggio cm using(centroid)";
        List<Pair<ParametroClimatico, CentroMonitoraggio>> resultList
                = new LinkedList<Pair<ParametroClimatico, CentroMonitoraggio>>();
        System.out.println(query);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                ParametroClimatico pc = extractParametroClimatico(rSet);
                CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
                resultList.add(new Pair<ParametroClimatico, CentroMonitoraggio>(pc, cm));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<ParametroClimatico, AreaInteresse>> selectAllPcJoinAi(){
        String query = "select * from parametro_climatico pc join area_interesse ai using(areaid)";
        System.out.println(query);
        List<Pair<ParametroClimatico, AreaInteresse>> resultList =
                new LinkedList<Pair<ParametroClimatico, AreaInteresse>>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                ParametroClimatico pc = extractParametroClimatico(rSet);

                AreaInteresse ai = extractAreaInteresse(rSet);
                resultList.add(new Pair<ParametroClimatico, AreaInteresse>(pc, ai));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<ParametroClimatico, NotaParametro>> selectAllPcJoinNpc(){
        String query = "select * from parametro_climatico pc join nota_parametro_climatico npc using(notaid)";
        System.out.println(query);
        List<Pair<ParametroClimatico, NotaParametro>> resultList =
                new LinkedList<Pair<ParametroClimatico, NotaParametro>>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                ParametroClimatico pc = extractParametroClimatico(rSet);
                NotaParametro nota = extractNota(rSet);
                resultList.add(new Pair<ParametroClimatico, NotaParametro>(pc, nota));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    //CentroMonitoraggio
    public List<String> selectObjectsCmJoinAiCond(String oggetto, String fieldCond, String cond){
        String query = "select %s from centro_monitoraggio cm join area_interesse ai on ai.areaid = any(cm.aree_interesse_ids) where %s = '%s'";
        return getResultList(oggetto, fieldCond, cond, query);
    }

    public List<String> selectObjectsCmJoinPcCond(String oggetto, String fieldCond, String cond){
        String query = "select %s from centro_monitoraggio cm join parametro_climatico pc using(centroid) where %s = '%s'";
        return getResultList(oggetto, fieldCond, cond, query);
    }

    //AreaInteresse
    public List<String> selectObjectsAiJoinPcCond(String oggetto, String fieldCond, String cond){
        String query = "select %s from area_interesse ai join parametro_climatico pc using(areaid) where %s = '%s'";
        return getResultList(oggetto, fieldCond, cond, query);
    }

    public List<String> selectObjectsAiJoinCmCond(String oggetto, String fieldCond, String cond){
        String query = "select %s from area_interesse ai join centro_monitoraggio cm on ai.areaid = any(aree_interesse_ids) where %s = '%s'";
        return getResultList(oggetto, fieldCond, cond, query);
    }

    public List<String> selectObjectsAiJoinCityCond(String oggetto, String fieldCond, String cond){
        String query = "select %s from area_interesse ai join city c on ai.denominazione = c.ascii_name where %s = '%s'" ;
        return getResultList(oggetto, fieldCond, cond, query);
    }

    //npc
    public List<String> selectObjectsNotaJoinPcCond(String oggetto, String fieldCond, String cond){
        String query = "select %s from nota_parametro_climatico npc join parametro_climatico pc using(notaid) where %s = '%s'";
        return getResultList(oggetto, fieldCond, cond, query);
    }

    private List<String> getResultList(String oggetto, String fieldCond, String cond, String query) {
        query = query.formatted(oggetto, fieldCond, cond);
        List<String> resultList = new LinkedList<String>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                resultList.add(rSet.getString(oggetto));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public LinkedList<City> selectAllCityCond(String fieldCond, String cond){
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

    public LinkedList<CentroMonitoraggio> selectAllCmCond(String fieldCond, String cond){
        String query = "select * from centro_monitoraggio where " + fieldCond + " = ?";
        LinkedList<CentroMonitoraggio> cms = new LinkedList<CentroMonitoraggio>();
        try(ResultSet rSet = prepAndExecuteStatement(query, cond)){
            while(rSet.next()){
                CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
                cms.add(cm);
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return cms;
    }

    public List<Operatore> selectAllOpCond(String fieldCond, String cond){
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

    public List<AreaInteresse> selectAllAiCond(String fieldCond, String cond){
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

    public List<NotaParametro> selectAllFromNotaWithCond(String fieldCond, String cond){
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

    public List<ParametroClimatico> selectAllPcCond(String fieldCond, String cond){
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

    public List<String> selectObjFromCityCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from city where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public List<String> selectObjFromCmCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from centro_monitoraggio where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public List<String> selectObjFromOperatoreCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public List<String> selectObjFromAuthOpCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore_autorizzati where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public List<String> selectObjFromAiCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from area_interesse where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public List<String> selectObjFromNotaCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from nota_parametro_climatico where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public List<String> selectObjFromPcCond(String oggetto, String fieldCond, String cond){
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
                        City c = extractCity(rSet);
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
                        CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
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
                        Operatore o = extractOperatore(rSet);
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
                        OperatoreAutorizzato oAutorizzato = extractAuthOp(rSet);
                        operatoriAutorizzati.add(oAutorizzato);
                    }

                }catch(SQLException sqle){sqle.printStackTrace();}
                return (List<T>) operatoriAutorizzati;
            }
            case PARAM_CLIMATICO -> {
                List<ParametroClimatico> res = new LinkedList<ParametroClimatico>();
                String query = "select * from parametro_climatico";
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        /**
                        String nomeArea =
                                selectObjFromAiCond("denominazione", "areaid", rSet.getString("areaid"))
                                .get(0);
                        String nomeCentro =
                                selectObjFromCmCond("nomecentro", "centroid", rSet.getString("centroid"))
                                .get(0);
                         **/
                        ParametroClimatico pc =
                                extractParametroClimatico(rSet);
                        res.add(pc);
                    }
                }catch(SQLException sqle){sqle.printStackTrace(); }
                return (List<T>)res;
            }
            case NOTA_PARAM_CLIMATICO -> {
                List<NotaParametro> res = new LinkedList<NotaParametro>();
                String query = "select * from nota_parametro_climatico";
                try(PreparedStatement stat = conn.prepareStatement(query)){
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        NotaParametro nota = extractNota(rSet);
                        res.add(nota);
                    }
                }catch(SQLException sqle){sqle.printStackTrace();}
                return (List<T>)res;
            }
            case AREA_INTERESSE -> {
                List<AreaInteresse> res = new LinkedList<AreaInteresse>();
                String query = "select * from area_interesse";
                try {
                    PreparedStatement stat = conn.prepareStatement(query);
                    ResultSet rSet = stat.executeQuery();
                    while(rSet.next()){
                        AreaInteresse ai = extractAreaInteresse(rSet);
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
            //expect 1 row
            Operatore o = extractOperatore(rSet);
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
