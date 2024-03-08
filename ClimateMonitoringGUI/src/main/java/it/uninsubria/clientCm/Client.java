package it.uninsubria.clientCm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread{

    private ClientProxy clientProxy;
    private String hostName;
    private boolean runCondition;

    private LinkedBlockingQueue<Request> requests;
    private LinkedBlockingQueue<Response> responses;
    private Logger logger;

    public Client(){
        logger = Logger.getLogger("Client");
        this.requests = new LinkedBlockingQueue<Request>();
        this.responses = new LinkedBlockingQueue<Response>();
        //this.clientProxy = new ClientProxy(this, hostName);
    }

    public String getHostName(){return this.hostName;}
    public void setHostName(String hostName){
        this.hostName = hostName;
    }

    public boolean getRunCondition(){
        return this.runCondition;
    }

    public void setRunCondition(boolean runCondition){
        this.runCondition = runCondition;
    }

    public void addRequest(Request req){
        try{
            requests.put(req);
        }catch(InterruptedException ie){logger.info(ie.getMessage());}
    }

    public Request getRequest(){
        try{
            return requests.take();
        }catch(InterruptedException ie){logger.info(ie.getMessage());return null;}
    }

    public void addResponse(Response res){
        try{
            responses.put(res);
        }catch(InterruptedException ie){logger.info(ie.getMessage());}
    }

    public Response getResponse(String responseId){
        try{
            return responses.take();
        }catch(InterruptedException ie){logger.info(ie.getMessage());return null;}
    }

    public ClientProxy getClientProxy(){
        return this.clientProxy;
    }

    public void setClientProxy(ClientProxy clientProxy){
        this.clientProxy = clientProxy;
    }

    public void run(){
        logger.info("Client started");
        while (getRunCondition()) {
            //wait for requests from the gui
            logger.info("waiting for a request");
            Request request = getRequest();
            //send request
            logger.info("Sending Request");
            clientProxy.sendRequest(request);
        }
    }

}
