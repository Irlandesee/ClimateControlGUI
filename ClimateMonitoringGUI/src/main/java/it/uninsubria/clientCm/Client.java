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
    private Inet4Address serverIp;
    private int portNumber;
    private Socket sock;
    private boolean runCondition;

    private LinkedBlockingQueue<Request> requests;
    private LinkedBlockingQueue<Response> responses;
    private Logger logger;

    public Client(){
        logger = Logger.getLogger("Client");
        this.requests = new LinkedBlockingQueue<Request>();
        this.responses = new LinkedBlockingQueue<Response>();
    }

    public String getHostName(){return this.hostName;}

    public void setHostName(String hostName){this.hostName = hostName;}

    public Inet4Address getServerIp(){return this.serverIp;}
    public void setServerIp(Inet4Address serverIp){this.serverIp = serverIp;}

    public int getPortNumber(){return this.portNumber;}

    public void setPortNumber(int portNumber){this.portNumber = portNumber;}

    public synchronized void setRunCondition(boolean runCondition) {
        try{
            wait();
            this.runCondition = runCondition;
        }catch(InterruptedException ie){
            logger.info(ie.getMessage());
        }
        notify();
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

    public boolean testConnection(){
        ObjectInputStream inStream;
        ObjectOutputStream outStream;
        try{
            sock = new Socket(serverIp, portNumber);
            inStream = new ObjectInputStream(sock.getInputStream());
            outStream = new ObjectOutputStream(sock.getOutputStream());

            System.out.println("Testing connection to server...");
            //send id
            outStream.writeObject(ServerInterface.ID);
            int number = ThreadLocalRandom.current().nextInt(10, 100);
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(25, 50));
            }catch(InterruptedException ie){ie.printStackTrace();}
            outStream.writeObject(hostName);
            //send number
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(25, 50));
            }catch(InterruptedException ie){ie.printStackTrace();}
            outStream.writeObject(ServerInterface.TEST);
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(25, 50));
            }catch(InterruptedException ie){ie.printStackTrace();}
            System.out.printf("Sending %d...\n", number);

            outStream.writeObject(number);

            try{
                int numberReceived = (int) inStream.readObject();
                System.out.println(numberReceived);
                if(numberReceived == number+1) return true;
            }catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}

            outStream.close();
            inStream.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        return false;
    }

    public void run(){
        logger.info("Client started");
        try{
            sock = new Socket(serverIp, portNumber);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        this.clientProxy = new ClientProxy(this, sock, hostName);
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
