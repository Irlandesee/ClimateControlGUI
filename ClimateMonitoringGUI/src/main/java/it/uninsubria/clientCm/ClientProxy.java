package it.uninsubria.clientCm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ClientProxy implements ServerInterface {
    private final String proxyId;
    private final Socket sock;
    private final Client client;
    private final Logger logger;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;


    public ClientProxy(Client client, Socket sock, String proxyId){
        this.logger = Logger.getLogger("ClientProxy: "+proxyId);
        this.client = client;
        this.proxyId = proxyId;
        this.sock = sock;
        try{
            this.inStream = new ObjectInputStream(sock.getInputStream());
            this.outStream = new ObjectOutputStream(sock.getOutputStream());
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public void sendRequest(Request req){
        int result = -1;
        try{

            //System.out.printf("Proxy %s sending id to server\n", this.proxyId);
            outStream.writeObject(ServerInterface.ID);
            outStream.writeObject(proxyId);
            try{
                //System.out.printf("Proxy %s sleeping\n", this.proxyId);
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            }catch(InterruptedException ie){ie.printStackTrace();}

            //System.out.printf("Proxy %s sending request to server\n", this.proxyId);
            outStream.writeObject(ServerInterface.NEXT);
            outStream.writeObject(req);
            try{
                //System.out.printf("Proxy %s sleeping\n", this.proxyId);
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            }catch(InterruptedException ie){ie.printStackTrace();}

            //System.out.printf("Proxy %s waiting for response from server\n", this.proxyId);
            Response res =  (Response) inStream.readObject();
            client.addResponse(res);
            logger.info("Adding response to queue");
            //System.out.printf("Proxy %s quitting\n", proxyId);
            //outStream.writeObject(ServerInterface.QUIT);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}
    }

    public void quit(){
        try{
            outStream.writeObject(ServerInterface.QUIT);
            outStream.close();
            inStream.close();
        }catch(IOException ioe){ioe.printStackTrace();}
    }



}
