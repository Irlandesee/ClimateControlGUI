package it.uninsubria.queryhandler;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.ParametroClimatico;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class QueryHandler{

    public enum Tables {
        AREA_INTERESSE("area_interesse"),
        CENTRO_MONITORAGGIO("centro_monitoraggio"),
        CITY("city"),
        NOTA_PARAM_CLIMATICO("nota_parametro_climatico"),
        OPERATORE("operatore"),
        OP_AUTORIZZATO("operatore_autorizzati"),
        PARAM_CLIMATICO("parametro_climatico");

        public final String label;

        private Tables(String label){
            this.label = label;
        }
    };

    private String dbUrl;
    private Properties props;

    public QueryHandler(String url, Properties props){
        this.dbUrl = url;
        this.props = props;
    }


    public List<String> selectObjectWithCond(String oggetto, Tables table, String fieldCond, String cond){
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



    public Operatore executeLogin(String userID, String password){
        Worker w = new Worker(dbUrl, props, "workerLogin_");
        return w.executeLogin(userID, password);
    }

    public boolean requestSignUp(String codFisc, String email){
        Worker w = new Worker(dbUrl, props, "workerSignUp_");
        List<OperatoreAutorizzato> opAutorizzati = w.selectAllFromTable(Tables.OP_AUTORIZZATO);
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

    public void executeInsertParametroClimatico(List<ParametroClimatico> params){
        Worker workerParametroClimatico = new Worker(dbUrl, props, "workerPC_");
        params.forEach(workerParametroClimatico::insertParametroClimatico);
    }


}
