package it.uninsubria.servercm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;

import java.io.IOException;

public interface ServerInterface {

     enum Tables{
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
    }

    enum RequestType{
        selectAll("selectAll"),
        selectAllWithCond("selectAllWithCond"),
        selectObjWithCond("selectObjectWithCond"),
        selectObjJoinWithCond("selectObjectJoinWithCond"),
        executeLogin("executeLogin"),
        insert("insert");
        public final String label;
        private RequestType(String label){
            this.label = label;
        }
    }

    enum ResponseType {
        List("List"),
        Object("Object"),
        NoSuchElement("NoSuchElement"),
        Error("Error"),
        insertOk("insertOk"),
        insertKo("insertKo"),
        loginOk("loginOk"),
        loginKo("loginKo");
        public final String label;
        private ResponseType(String label){this.label = label;}
    }



    static final int PORT = 9999;
    static final int selectAllParamsLength = 1;
    static final int selectAllWithCondParamsLength = 2;
    static final int selectObjWithCondParamsLength = 3;
    static final int selectObjJoinWithCondParamsLength = 5;

    static final String QUIT = "QUIT";
    static final String NEXT = "NEXT";
    static final String ID = "ID";
    static final String UNDEFINED_BEHAVIOUR = "UNDEFINED";

    //public void quit() throws IOException;
    void sendRequest(Request r);


}
