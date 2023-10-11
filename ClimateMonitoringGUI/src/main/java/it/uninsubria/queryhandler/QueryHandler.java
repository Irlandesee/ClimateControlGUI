package it.uninsubria.queryhandler;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ParametroClimatico;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class QueryHandler{

    public enum tables{
        AREA_INTERESSE("area_interesse"),
        CENTRO_MONITORAGGIO("centro_monitoraggio"),
        CITY("city"),
        NOTA_PARAM_CLIMATICO("nota_parametro_climatico"),
        OPERATORE("operatore"),
        OP_AUTORIZZATO("operatore_autorizzati"),
        PARAM_CLIMATICO("parametro_climatico");

        public final String label;

        private tables(String label){
            this.label = label;
        }
    };

    private String dbUrl;
    private Properties props;

    public QueryHandler(String url, Properties props){
        this.dbUrl = url;
        this.props = props;
    }

    public <T> List<T> selectAllJoin(tables table, tables otherTable){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                return w.selectAllFromCityJoin();
            }
            case CENTRO_MONITORAGGIO -> {
                //TODO
                Worker w = new Worker(dbUrl, props, "workerAuthOp");
                return w.selectAllFromCmJoin(otherTable);
            }
            case AREA_INTERESSE -> {
                //TODO
                Worker w = new Worker(dbUrl, props, "workerAi");
                return w.selectAllFromAiJoin(otherTable);
            }
            case NOTA_PARAM_CLIMATICO -> {
                //TODo
                Worker w = new Worker(dbUrl, props, "workerNota");
                return w.selectAllFromNotaJoin(otherTable);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPc");
                return w.selectAllFromPcJoin(otherTable);
            }
            default -> {return null;}
        }
    }

    public <T> List<T> selectAllJoinCond(tables table, tables otherTable, String fieldCond, String cond){
        switch(table){
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM");
                return w.selectAllFromCmJoinCond(otherTable, fieldCond, cond);
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAi");
                return w.selectAllFromAiJoinCond(otherTable, fieldCond, cond);
            }
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                return w.selectAllFromCityJoinCond(otherTable, fieldCond, cond);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPC");
                return w.selectAllFromPcJoinCond(otherTable, fieldCond, cond);
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota");
                return w.selectAllFromNotaJoinCond(fieldCond, cond);
            }
            default -> {return null;}
        }
    }

    public String selectObjectJoinWithCond(String oggetto, tables table, tables otherTable, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                //TODO
                Worker w = new Worker(dbUrl, props, "workerCity");
                return w.selectObjFromCityJoinCond(oggetto, otherTable, fieldCond, cond);
            }
            case CENTRO_MONITORAGGIO -> {
                //TODO
                Worker w = new Worker(dbUrl, props, "workerCM");
                return w.selectObjFromCmJoinCond(oggetto, otherTable, fieldCond, cond);
            }
            case AREA_INTERESSE -> {
                //TODO
                Worker w = new Worker(dbUrl, props, "workerAI");
                return w.selectObjFromAiJoinCond(oggetto, otherTable, fieldCond, cond);
            }
            case NOTA_PARAM_CLIMATICO -> {
                //TODO
                Worker w = new Worker(dbUrl, props, "workerNota");
                return w.selectObjFromNotaJoinCond(oggetto, otherTable, fieldCond, cond);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPC");
                return w.selectObjFromPcJoinCond(oggetto, otherTable, fieldCond, cond);
            }
            default -> {return null;}
        }
    }

    public String selectObjectWithCond(String oggetto, tables table, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                return w.selectObjFromCityCond(oggetto, fieldCond, cond);
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM");
                return w.selectObjFromCmCond(oggetto, fieldCond, cond);
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOP");
                return w.selectObjFromOperatoreCond(oggetto, fieldCond, cond);
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOP");
                return w.selectObjFromAuthOpCond(oggetto, fieldCond, cond);
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI");
                return w.selectObjFromAiCond(oggetto, fieldCond, cond);
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota");
                return w.selectObjFromNotaCond(oggetto, fieldCond, cond);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPM");
                return w.selectObjFromPcCond(oggetto, fieldCond, cond);
            }
            default -> {return null;}
        }

    }

    public <T> List<T> selectAll(tables table){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                return w.selectAllFromTable(tables.CITY);
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM");
                return w.selectAllFromTable(tables.CENTRO_MONITORAGGIO);
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOP");
                return w.selectAllFromTable(tables.OPERATORE);
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOp");
                return w.selectAllFromTable(tables.OP_AUTORIZZATO);
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI");
                return w.selectAllFromTable(tables.AREA_INTERESSE);
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota");
                return w.selectAllFromTable(tables.NOTA_PARAM_CLIMATICO);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPC") ;
                return w.selectAllFromTable(tables.PARAM_CLIMATICO);
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
                LinkedList<ParametroClimatico> cps = w.selectAllFromCPWithCond(fieldCond, cond);
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

    public boolean requestSignUp(String codFisc, String email){
        Worker w = new Worker(dbUrl, props, "workerSignUp");
        List<OperatoreAutorizzato> opAutorizzati = w.selectAllFromTable(tables.OP_AUTORIZZATO);
        //should make this faster
        for(OperatoreAutorizzato op : opAutorizzati)
            if(op.getEmail().equals(email) && op.getCodFiscale().equals(codFisc))
                return true;
        return false;
    }

    public boolean executeSignUp(String nomeOp, String cognomeOp, String codFisc, String userID, String email, String password, String centroAfferenza){
        Worker w = new Worker(dbUrl, props, "workerSignUp");
        return w.insertOperatore(nomeOp, cognomeOp, codFisc, userID, email, password, centroAfferenza);
    }

    public boolean executeInsertCentroMonitoraggio(String nomeCentro, String comune, String stato, List<String> areeInteresseAssociate){
        Worker workerIDs = new Worker(dbUrl, props, "workerAreeIDs");
        List<String> areeIds = new LinkedList<String>();
        for(String area : areeInteresseAssociate)
            areeIds.add(workerIDs.selectObjFromAiCond("areaid", "denominazione", area));
        try{
            workerIDs.join();
        }catch(InterruptedException ie){ie.printStackTrace();}
        Worker w = new Worker(dbUrl, props, "workerInsertCM");
        return w.insertCentroMonitoraggio(nomeCentro, comune, stato, areeIds);
    }


}
