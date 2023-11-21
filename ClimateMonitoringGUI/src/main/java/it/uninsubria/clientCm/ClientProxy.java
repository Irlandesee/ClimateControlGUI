package it.uninsubria.clientCm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class ClientProxy implements ServerInterface {
    private final String proxyId;
    private final Socket sock;

    public ClientProxy(Socket sock, String proxyId){
        this.proxyId = proxyId;
        this.sock = sock;
    }

    public Response addRequest(Request req){
        int result = -1;
        try{
            ObjectOutputStream outStream = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream inStream = new ObjectInputStream(sock.getInputStream());

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
            //System.out.printf("Proxy %s quitting\n", proxyId);
            outStream.writeObject(ServerInterface.QUIT);
            return  res;
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}
        return null;
    }

}
