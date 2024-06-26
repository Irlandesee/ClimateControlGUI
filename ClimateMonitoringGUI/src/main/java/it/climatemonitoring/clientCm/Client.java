package it.climatemonitoring.clientCm;

import it.climatemonitoring.request.Request;
import it.climatemonitoring.response.Response;

import java.io.IOException;
import java.util.logging.Logger;
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
        this.setDaemon(true);
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

    public Response getResponse(){
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

    /**
     * Attende richieste da parte dell'utente per inviarle al server
     */
    public void run(){
        logger.info("Client started");
        while (getRunCondition()) {
            //wait for requests from the gui
            logger.info("waiting for a request");
            Request request = getRequest();
            //send request
            logger.info("Sending Request");
            try{
                clientProxy.sendRequest(request);
            } catch(IOException ioe){
                System.out.println("Server has disconnected, closing the connection...");
                clientProxy.quit();
                logger.info(ioe.getMessage());
            }
        }
    }

}
