package it.uninsubria.queryhandler;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ClimateParameter;
import it.uninsubria.util.Item;

import javax.sound.sampled.Line;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class QueryHandler{

    public enum tables{
        AREA_INTERESSE,
        CENTRO_MONITORAGGIO,
        CITY,
        NOTA_PARAM_CLIMATICO,
        OPERATORE,
        OP_AUTORIZZATO,
        PARAM_CLIMATICO
    };

    private String dbUrl;
    private Properties props;


    public QueryHandler(String url, Properties props){
        this.dbUrl = url;
        this.props = props;
    }

    public void selectObjectWithCond(String oggetto, tables table, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                LinkedList<String> res = w.selectObjFromCityWithCond(oggetto, fieldCond, cond);
                res.forEach(System.out::println);
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM");
                LinkedList<String> res = w.selectObjFromCMWithCond(oggetto, fieldCond, cond);
                res.forEach(System.out::println);
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOP");
                LinkedList<String> res = w.selectObjFromOPWithCond(oggetto, fieldCond, cond);
                res.forEach(System.out::println);
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOP");
                LinkedList<String> res = w.selectObjFromAuthOPWithCond(oggetto, fieldCond, cond);
                res.forEach(System.out::println);
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI");
                LinkedList<String> res = w.selectObjFromAIWithCond(oggetto, fieldCond, cond);
                res.forEach(System.out::println);
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota");
                //TODO
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPM");
                LinkedList<String> res = w.selectObjFromPCWithCond(oggetto, fieldCond, cond);
                res.forEach(System.out::println);
            }
            //default -> {return null;}
        }

    }

    public <T> List<T> selectAll(tables table){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                List<T> cities = w.selectAllFromTable(tables.CITY);
                return cities;
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM");
                List<T> cms =  w.selectAllFromTable(tables.CENTRO_MONITORAGGIO);
                return cms;
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOP");
                List<T> ops = w.selectAllFromTable(tables.OPERATORE);
                return ops;
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOp");
                List<T> authOps = w.selectAllFromTable(tables.OP_AUTORIZZATO);
                return authOps;
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI");
                List<T> ais = w.selectAllFromTable(tables.AREA_INTERESSE);
                return ais;
            }
            case NOTA_PARAM_CLIMATICO -> {
                //TODO
                return null;
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPC") ;
                List<T> pcs = w.selectAllFromTable(tables.PARAM_CLIMATICO);
                return pcs;
            }
            default -> {return null;}
        }
    }

    public <T> LinkedList<T> selectAllWithCond(tables table, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                LinkedList<City> cities = w.selectAllFromCityWithCond(fieldCond, cond);
                //cities.forEach(System.out::println);
                return (LinkedList<T>) cities;
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM");
                LinkedList<CentroMonitoraggio> cms = w.selectAllFromCMWithCond(fieldCond, cond);
                //cms.forEach(System.out::println);
                return (LinkedList<T>) cms;
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOperatore");
                LinkedList<Operatore> operatori = w.selectAllFromOpWithCond(fieldCond, cond);
                //operatori.forEach(System.out::println);
                return (LinkedList<T>) operatori;
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOP");
                LinkedList<OperatoreAutorizzato> operatoriAutorizzati = w.selectAllFromAuthOpWithCond(fieldCond, cond);
                //operatoriAutorizzati.forEach(System.out::println);
                return (LinkedList<T>) operatoriAutorizzati;
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI");
                LinkedList<AreaInteresse> areeInteresse = w.selectAllFromAIWithCond(fieldCond, cond);
                //areeInteresse.forEach(System.out::println);
                return (LinkedList<T>) areeInteresse;
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota");
                //TODO
                return null;
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPM");
                LinkedList<ClimateParameter> cps = w.selectAllFromCPWithCond(fieldCond, cond);
                //cps.forEach(System.out::println);
                return (LinkedList<T>) cps;
            }
            default -> {return null;}
        }
    }

    public Operatore executeLogin(String userID, String password){
        Worker w = new Worker(dbUrl, props, "workerLogin");
        return w.executeLogin(userID, password);
    }


}
