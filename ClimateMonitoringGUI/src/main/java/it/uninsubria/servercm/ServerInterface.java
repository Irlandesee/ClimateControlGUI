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
        insert("insert"),
        requestSignUp("requestSignUp"),
        executeSignUp("executeSignUp");
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
        loginKo("loginKo"),
        requestSignUpOk("requestSignUpOk"),
        requestSignUpKo("requestSignUpKo"),
        executeSignUpOk("executeSignUpOk"),
        executeSignUpKo("executeSignUpKo");
        public final String label;
        private ResponseType(String label){this.label = label;}
    }



    int PORT = 9999;
    int selectAllWithCondParamsLength = 2;
    int selectObjWithCondParamsLength = 3;
    int selectObjJoinWithCondParamsLength = 4;
    int executeLoginParamsLength = 2;
    int insertPcParamsLength = 12;
    int insertCmParamsLength = 4;
    int insertOpParamsLength = 7;
    //int insertAiParamsLength ;

    String QUIT = "QUIT";
    String NEXT = "NEXT";
    String ID = "ID";
    String UNDEFINED_BEHAVIOUR = "UNDEFINED";

    //public void quit() throws IOException;
    void sendRequest(Request r);


}
