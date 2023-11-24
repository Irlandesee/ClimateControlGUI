package it.uninsubria.clientCm;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;
import it.uninsubria.servercm.ServerInterface.Tables;
import it.uninsubria.servercm.ServerInterface.RequestType;
import it.uninsubria.servercm.ServerInterface.ResponseType;
import it.uninsubria.util.IDGenerator;
import java.util.logging.Logger;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Client extends Thread{

    private final ClientProxy clientProxy;
    private final String clientId;
    private Socket sock;
    private boolean runCondition;

    private LinkedBlockingQueue<Request> requests;
    private LinkedBlockingQueue<Response> responses;
    private Logger logger;

    public Client(String clientId){
        this.clientId = clientId;
        this.setName(clientId);
        logger = Logger.getLogger("Client");
        try{
            sock = new Socket(InetAddress.getLocalHost(), ServerInterface.PORT);
        }catch(IOException ioe){ioe.printStackTrace();}
        this.requests = new LinkedBlockingQueue<Request>();
        this.responses = new LinkedBlockingQueue<Response>();
        this.clientProxy = new ClientProxy(this, sock, clientId);
        this.runCondition = true;
    }

    public synchronized void setRunCondition(boolean runCondition) {
        try{
            wait();
            this.runCondition = runCondition;
        }catch(InterruptedException ie){
            logger.info(ie.getMessage());
        }
        notify();
    }

    public String getClientId(){
        return this.clientId;
    }

    public synchronized boolean getRunCondition(){
        //does this method release the lock on the object?
        try{
            wait();
        }catch(InterruptedException ie){logger.info(ie.getMessage());}
        return this.runCondition;
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
        logger.info("Client quitting, closing the socket");
        try{
            sock.close();
        }catch(IOException ioe){logger.info(ioe.getMessage());}

    }

}
