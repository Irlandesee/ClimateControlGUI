package it.uninsubria.queryhandler;
import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import javafx.util.Pair;

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

    //TODO
    /**
    public <T, V> List<Pair<T, V>> selectAllJoin(tables table, tables otherTable){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity");
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return (List<Pair<T, V>>) w.selectAllCityJoinAi();
                    }
                    case CENTRO_MONITORAGGIO -> {

                    }
                }
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
                Worker w = new Worker(dbUrl, props, "workerNota");
                return w.selectAllFromNotaJoin();
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPc");
                return w.selectAllFromPcJoin(otherTable);
            }
            default -> {return null;}
        }
    }
     **/
    public List<String> selectObjectJoinWithCond(String oggetto, tables table, tables otherTable, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity_");
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return w.selectObjectsCityJoinAiCond(oggetto, fieldCond, cond);
                    }
                    case CENTRO_MONITORAGGIO -> {
                        return w.selectObjectsCityJoinCmCond(oggetto, fieldCond, cond);
                    }
                }
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM_");
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return w.selectObjectsCmJoinAiCond(oggetto, fieldCond, cond);
                    }
                    case PARAM_CLIMATICO -> {
                        return w.selectObjectsCmJoinPcCond(oggetto, fieldCond, cond);
                    }
                }
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI_");
                switch(otherTable){
                    case CENTRO_MONITORAGGIO -> {
                        return w.selectObjectsAiJoinCmCond(oggetto, fieldCond, cond);
                    }
                    case PARAM_CLIMATICO -> {
                        return w.selectObjectsAiJoinPcCond(oggetto, fieldCond, cond);
                    }
                    case CITY -> {
                        return w.selectObjectsAiJoinCityCond(oggetto, fieldCond, cond);
                    }
                }
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota_");
                return w.selectObjectsNotaJoinPcCond(oggetto, fieldCond, cond);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPC_");
                switch(otherTable){
                    case AREA_INTERESSE -> {
                        return w.selectObjectsPcJoinAiCond(oggetto, fieldCond, cond);
                    }
                    case CENTRO_MONITORAGGIO -> {
                        return w.selectObjectsPcJoinCmCond(oggetto, fieldCond, cond);
                    }
                    case NOTA_PARAM_CLIMATICO -> {
                        return w.selectObjectsPcJoinNpcCond(oggetto, fieldCond, cond);
                    }
                }
            }
        }
        return null;
    }

    public List<String> selectObjectWithCond(String oggetto, tables table, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity_");
                return w.selectObjFromCityCond(oggetto, fieldCond, cond);
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM_");
                return w.selectObjFromCmCond(oggetto, fieldCond, cond);
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOP_");
                return w.selectObjFromOperatoreCond(oggetto, fieldCond, cond);
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOP_");
                return w.selectObjFromAuthOpCond(oggetto, fieldCond, cond);
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI_");
                return w.selectObjFromAiCond(oggetto, fieldCond, cond);
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota_");
                return w.selectObjFromNotaCond(oggetto, fieldCond, cond);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPC_");
                return w.selectObjFromPcCond(oggetto, fieldCond, cond);
            }
            default -> {return null;}
        }

    }

    public <T> List<T> selectAll(tables table){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity_");
                return w.selectAllFromTable(tables.CITY);
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM_");
                return w.selectAllFromTable(tables.CENTRO_MONITORAGGIO);
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOP_");
                return w.selectAllFromTable(tables.OPERATORE);
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOp_");
                return w.selectAllFromTable(tables.OP_AUTORIZZATO);
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI_");
                return w.selectAllFromTable(tables.AREA_INTERESSE);
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota_");
                return w.selectAllFromTable(tables.NOTA_PARAM_CLIMATICO);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPC_") ;
                return w.selectAllFromTable(tables.PARAM_CLIMATICO);
            }
            default -> {return null;}
        }
    }

    public <T> List<T> selectAllWithCond(tables table, String fieldCond, String cond){
        switch(table){
            case CITY -> {
                Worker w = new Worker(dbUrl, props, "workerCity_");
                return (List<T>) w.selectAllCityCond(fieldCond, cond);
            }
            case CENTRO_MONITORAGGIO -> {
                Worker w = new Worker(dbUrl, props, "workerCM_");
                return (List<T>) w.selectAllCmCond(fieldCond, cond);
            }
            case OPERATORE -> {
                Worker w = new Worker(dbUrl, props, "workerOperatore_");
                return (List<T>) w.selectAllOpCond(fieldCond, cond);
            }
            case OP_AUTORIZZATO -> {
                Worker w = new Worker(dbUrl, props, "workerAuthOP_");
                return (List<T>) w.selectAllAuthOpCond(fieldCond, cond);
            }
            case AREA_INTERESSE -> {
                Worker w = new Worker(dbUrl, props, "workerAI_");
                return (List<T>) w.selectAllAiCond(fieldCond, cond);
            }
            case NOTA_PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerNota_");
                return (List<T>) w.selectAllNotaCond(fieldCond, cond);
            }
            case PARAM_CLIMATICO -> {
                Worker w = new Worker(dbUrl, props, "workerPM_");
                return (List<T>) w.selectAllPcCond(fieldCond, cond);
            }
            default -> {return null;}
        }
    }

    public Operatore executeLogin(String userID, String password){
        Worker w = new Worker(dbUrl, props, "workerLogin_");
        return w.executeLogin(userID, password);
    }

    public boolean requestSignUp(String codFisc, String email){
        Worker w = new Worker(dbUrl, props, "workerSignUp_");
        List<OperatoreAutorizzato> opAutorizzati = w.selectAllFromTable(tables.OP_AUTORIZZATO);
        //should make this faster
        for(OperatoreAutorizzato op : opAutorizzati)
            if(op.getEmail().equals(email) && op.getCodFiscale().equals(codFisc))
                return true;
        return false;
    }

    public boolean executeSignUp(String nomeOp, String cognomeOp, String codFisc, String userID, String email, String password, String centroAfferenza){
        Worker w = new Worker(dbUrl, props, "workerSignUp_");
        return w.insertOperatore(nomeOp, cognomeOp, codFisc, userID, email, password, centroAfferenza);
    }

    public boolean executeInsertCentroMonitoraggio(String nomeCentro, String comune, String stato, List<String> areeInteresseAssociate){
        Worker workerIDs = new Worker(dbUrl, props, "workerAreeIDs_");
        List<String> areeIds = new LinkedList<String>();
        for(String area : areeInteresseAssociate)
            areeIds.add(workerIDs.selectObjFromAiCond("areaid", "denominazione", area).get(0));
        try{
            workerIDs.join();
        }catch(InterruptedException ie){ie.printStackTrace();}
        Worker w = new Worker(dbUrl, props, "workerInsertCM_");
        return w.insertCentroMonitoraggio(nomeCentro, comune, stato, areeIds);
    }


}
