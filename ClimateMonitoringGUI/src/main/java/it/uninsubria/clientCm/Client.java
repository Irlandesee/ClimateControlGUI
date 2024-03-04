package it.uninsubria.clientCm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;

import java.net.UnknownHostException;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread{

    private final ClientProxy clientProxy;
    private final String clientId;
    private final String serverIp;
    private Socket sock;
    private boolean runCondition;

    private LinkedBlockingQueue<Request> requests;
    private LinkedBlockingQueue<Response> responses;
    private Logger logger;

    public Client(String clientId, String serverIp) {
        this.clientId = clientId;
        this.setName(clientId);
        logger = Logger.getLogger("Client");
        this.serverIp = serverIp;
        /**
        try{
            sock = new Socket(InetAddress.getLocalHost(), ServerInterface.PORT);
        }catch(IOException ioe){ioe.printStackTrace();}
         **/
        try{
            InetAddress inetAddress = InetAddress.getByName(serverIp);
            sock = new Socket(inetAddress.getHostAddress(), ServerInterface.PORT);
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
        while (true) {
            //wait for requests from the gui
            logger.info("waiting for a request");
            Request request = getRequest();
            //send request
            logger.info("Sending Request");
            clientProxy.sendRequest(request);
        }
        //Close the socket?
    }

}
