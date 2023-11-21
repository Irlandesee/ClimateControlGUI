package it.uninsubria.clientCm;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import it.uninsubria.servercm.ServerInterface.Tables;
import it.uninsubria.servercm.ServerInterface.RequestType;
import it.uninsubria.servercm.ServerInterface.ResponseType;
import it.uninsubria.util.IDGenerator;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Client {

    private ClientProxy cp;
    private String clientId;
    private Socket sock;
    public Client(String clientId){
        this.clientId = clientId;
        try{
            sock = new Socket(InetAddress.getLocalHost(), ServerInterface.PORT);
        }catch(IOException ioe){ioe.printStackTrace();}
        this.cp = new ClientProxy(sock, clientId);
    }

    public static void main(String[] args){
        Client c = new Client(IDGenerator.generateID());
        System.err.printf("Client %s started\n", c.clientId);

        String[] params = new String[1];
        Request request = new Request(
                c.clientId,
                RequestType.selectAll,
                Tables.AREA_INTERESSE,
                params);
        Response res = c.cp.addRequest(request);
        if(res != null){
            System.out.printf("Client %s, received: %s\n", c.clientId, res);
            switch(res.getRespType()){
                case List -> {
                    List<Object> result = (List<Object>) res.getResult();
                    //Cast the object to the needed class
                }
                case Object -> {
                    Object o = res.getResult();
                }
                case Error -> {
                    System.out.printf("Response %s containing error\n", res.getResponseId());
                }
                case NoSuchElement -> {
                    System.out.println("No such element in db");
                }

            }
        }

    }

}
