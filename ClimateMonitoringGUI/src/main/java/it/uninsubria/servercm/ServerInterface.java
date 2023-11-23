package it.uninsubria.servercm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;

import java.io.IOException;

public interface ServerInterface {

     public enum Tables{
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

    public enum RequestType{
        selectAll("selectAll"),
        selectAllWithCond("selectAllWithCond"),
        selectObjWithCond("selectObjectWithCond"),
        selectObjJoinWithCond("selectObjectJoinWithCond");
        public final String label;
        private RequestType(String label){
            this.label = label;
        }
    }

    public enum ResponseType {
        List("List"),
        Object("Object"),
        NoSuchElement("NoSuchElement"),
        Error("Error");
        public final String label;
        private ResponseType(String label){this.label = label;}
    }



    public static final int PORT = 9999;
    public static final int selectAllParamsLength = 1;
    public static final int selectAllWithCondParamsLength = 2;
    public static final int selectObjWithCondParamsLength = 3;
    public static final int selectObjJoinWithCondParamsLength = 5;

    public static final String QUIT = "QUIT";
    public static final String NEXT = "NEXT";
    public static final String ID = "ID";
    public static final String UNDEFINED_BEHAVIOUR = "UNDEFINED";

    //public void quit() throws IOException;
    public Response addRequest(Request r);


}
