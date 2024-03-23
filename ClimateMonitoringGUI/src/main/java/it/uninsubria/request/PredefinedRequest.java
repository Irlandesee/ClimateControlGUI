package it.uninsubria.request;

import it.uninsubria.clientCm.Client;
import it.uninsubria.factories.RequestFactory;
import it.uninsubria.servercm.ServerInterface;

import java.util.Map;

public class PredefinedRequest {
    public static Request getRequestAi(String hostName){
        try{
            return RequestFactory.buildRequest(
                    hostName,
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.AREA_INTERESSE, null);
        }catch(MalformedRequestException mre){
            mre.printStackTrace();
            return null;
        }
    }
    public static Request getRequestCm(String hostName){
        try{
            return RequestFactory.buildRequest(
                    hostName,
                    ServerInterface.RequestType.selectAll,
                    ServerInterface.Tables.CENTRO_MONITORAGGIO, null);
        }catch(MalformedRequestException mre){
            mre.printStackTrace();
            return null;
        }
    }
    public static Request getRequestPc(String hostName,String column, String cond){
        try{
            Map<String, String> params = RequestFactory
                    .buildParams(
                            ServerInterface.RequestType.selectAllWithCond,
                            column, cond);
            return RequestFactory.buildRequest(
                    hostName,
                    ServerInterface.RequestType.selectAllWithCond,
                    ServerInterface.Tables.PARAM_CLIMATICO, params);
        }catch(MalformedRequestException mre){
            mre.printStackTrace();
            return null;
        }
    }


    public static Request getRequestNpc(String hostName, String notaId){
        try{
            Map<String, String> params = RequestFactory.buildParams(
                    ServerInterface.RequestType.selectAllWithCond,
                    "notaid", notaId);
            return RequestFactory.buildRequest(
                    hostName,
                    ServerInterface.RequestType.selectAllWithCond,
                    ServerInterface.Tables.NOTA_PARAM_CLIMATICO,
                    params);
        }catch(MalformedRequestException mre){
            mre.printStackTrace();
            return null;
        }
    }

}
