package it.uninsubria.queryhandler;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ClimateParameter;

import java.awt.geom.Area;
import java.sql.*;
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
        String query = "selct * from operatore where " + fieldCond + " = ?";
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

    public LinkedList<ClimateParameter> selectAllFromCPWithCond(String fieldCond, String cond){
        String query = "select * from parametro_climatico where " + fieldCond + " = ?";
        System.out.println(query);
        LinkedList<ClimateParameter> parametriClimatici = new LinkedList<ClimateParameter>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                ClimateParameter cp = new ClimateParameter(
                        res.getString("parameterid"),
                        res.getString("idcentro"),
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

    private LinkedList<String> getQueryResult(String query, String oggetto, String cond){
        LinkedList<String> objs = new LinkedList<String>();
        try(ResultSet res = prepAndExecuteStatement(query, cond)){
            while(res.next()){
                objs.add(res.getString(oggetto));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return objs;
    }

    public LinkedList<String> selectObjFromCityWithCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from city where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public LinkedList<String> selectObjFromCMWithCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from centro_monitoraggio where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public LinkedList<String> selectObjFromOPWithCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public LinkedList<String> selectObjFromAuthOPWithCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from operatore_autorizzati where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public LinkedList<String> selectObjFromAIWithCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from area_interesse where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public LinkedList<String> selectObjFromNotaWithCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from nota_parametro_climatico where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
    }

    public LinkedList<String> selectObjFromPCWithCond(String oggetto, String fieldCond, String cond){
        String query = "select " +oggetto+ " from parametro_climatico where "+ fieldCond + " = ?";
        return getQueryResult(query, oggetto, cond);
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
                //TODO
                return null;
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
}
