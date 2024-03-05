package it.uninsubria.clientCm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ClientProxy implements ServerInterface {
    private final String proxyId;
    private Socket sock;
    private final Client client;
    private final Logger logger;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private Inet4Address ipAddr;
    private int portNumber;
    private String hostName;

    public ClientProxy(Client client, String proxyId){
        this.logger = Logger.getLogger("ClientProxy: "+proxyId);
        this.client = client;
        this.proxyId = proxyId;
    }

    public Inet4Address getIpAddr(){return this.ipAddr;}
    public void setIpAddr(Inet4Address ipAddr){this.ipAddr = ipAddr;}
    public int getPortNumber(){return this.portNumber;}
    public void setPortNumber(int portNumber){this.portNumber = portNumber;}

    public String getHostName(){return this.hostName;}
    public void setHostName(String hostName){this.hostName = hostName;}

    public void init(){
        try{
            String ipAddr = getIpAddr().getHostAddress();
            System.out.println(ipAddr);
            sock = new Socket(ipAddr, getPortNumber());
            outStream = new ObjectOutputStream(sock.getOutputStream());
            inStream = new ObjectInputStream(sock.getInputStream());
        }catch(IOException ioe){ioe.printStackTrace();}
    }

    public boolean testConnection(){
        boolean res = false;
        try{
            System.out.println("Testing connection to server...");
            //send id
            outStream.writeObject(ServerInterface.ID);
            outStream.writeObject(getHostName());
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(25, 50));
            }catch(InterruptedException ie){ie.printStackTrace();}
            //send number
            int number = ThreadLocalRandom.current().nextInt(10, 100);
            outStream.writeObject(ServerInterface.TEST);
            outStream.writeObject(number);
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(25, 50));
            }catch(InterruptedException ie){ie.printStackTrace();}
            System.out.printf("Sending %d...\n", number);

            try{
                int numberReceived = (int) inStream.readObject();
                System.out.println(numberReceived);
                if(numberReceived == number+1) res = true;
            }catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}
            outStream.flush();

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        if(!res) quit();
        return res;
    }

    public void sendRequest(Request req){
        try{

            System.out.printf("Proxy %s sending id to server\n", this.proxyId);
            outStream.writeObject(ServerInterface.ID);
            outStream.writeObject(proxyId);
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            }
            catch(InterruptedException ie){ie.printStackTrace();}

            System.out.printf("Proxy %s sending request to server\n", this.proxyId);
            outStream.writeObject(ServerInterface.NEXT);
            outStream.writeObject(req);
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            }catch(InterruptedException ie){ie.printStackTrace();}

            System.out.printf("Proxy %s waiting for response from server\n", this.proxyId);
            Response res =  (Response) inStream.readObject();
            client.addResponse(res);
            logger.info("Adding response to queue");
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}
    }

    public void sendQuitRequest(){
        try{
            if(outStream != null)
                outStream.writeObject(ServerInterface.QUIT);
        }catch(IOException ioe){ioe.printStackTrace();}
        this.quit();
    }

    public void quit(){
        try{
            outStream.close();
            inStream.close();
            sock.close();
            client.setRunCondition(false);
        }catch(IOException ioe){ioe.printStackTrace();}
    }



}
