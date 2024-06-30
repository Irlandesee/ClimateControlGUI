package it.climatemonitoring.clientCm;

import it.climatemonitoring.factories.RequestFactory;
import it.climatemonitoring.request.MalformedRequestException;
import it.climatemonitoring.request.Request;
import it.climatemonitoring.response.Response;
import it.climatemonitoring.servercm.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ClientProxy implements ServerInterface{
    private Socket sock;
    private final Client client;
    private final Logger logger;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private Inet4Address ipAddr;
    private int portNumber;
    private String hostName;

    public ClientProxy(Client client, String hostName){
        this.logger = Logger.getLogger("ClientProxy: "+hostName);
        this.client = client;
        this.hostName = hostName;
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

    /**
     * Testa la connessione al server
     * @return
     */
    public boolean testConnection(){
        boolean res = false;
        try{
            System.out.println("Testing connection to server...");
            //send id
            outStream.writeObject(ServerInterface.ID);
            String hostName = getHostName();
            System.out.println(hostName);
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
            System.out.println("Server has disconnected, closing the connection...");
            ioe.printStackTrace();
        }
        if(!res) quit();
        return res;
    }

    /**
     * Invia la richiesta al server
     * @param request
     * @throws IOException
     */
    public void sendRequest(Request request) throws IOException{
        try{

            System.out.printf("Proxy %s sending id to server\n", this.hostName);
            outStream.writeObject(ServerInterface.ID);
            outStream.writeObject(hostName);
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            }
            catch(InterruptedException ie){ie.printStackTrace();}
            if(request.getRequestType() == ServerInterface.RequestType.executeLogin){
                System.out.printf("Proxy %s sending login request to server\n", this.hostName);
                outStream.writeObject(ServerInterface.LOGIN);
                outStream.writeObject(request);
            }else{
                System.out.printf("Proxy %s sending request to server\n", this.hostName);
                outStream.writeObject(ServerInterface.NEXT);
                outStream.writeObject(request);
            }
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            }catch(InterruptedException ie){ie.printStackTrace();}
            System.out.printf("Proxy %s waiting for response from server\n", this.hostName);
            Response res =  (Response) inStream.readObject();
            client.addResponse(res);
            logger.info("Adding response to queue");
        }catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}
    }

    /**
     * Invia una notifica chiusura della connessione al server
     */
    public void sendQuitRequest(){
        try{
            if(outStream != null)
                outStream.writeObject(ServerInterface.QUIT);
        }catch(IOException ioe){ioe.printStackTrace();}
        this.quit();
    }

    /**
     * Invia una richiesta di logout al server
     */
    public void sendLogoutRequest(){
        try{
            if(outStream != null) {
                try{
                    Request logoutRequest = RequestFactory.buildRequest(
                            client.getHostName(),
                            RequestType.executeLogout,
                            null,
                            null);
                    outStream.writeObject(ServerInterface.LOGOUT);
                    outStream.writeObject(logoutRequest);
                    Response logoutResponse = (Response) inStream.readObject();
                    if(logoutResponse.getResponseType() == ServerInterface.ResponseType.logoutOk){
                        logger.info("Proxy has logged out successfully");
                    }else{
                        logger.info("Logger %s has failed to log out");
                    }
                }catch(MalformedRequestException mre){
                    mre.printStackTrace();
                }
            }
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void quit(){
        try{
            if(sock != null)
                sock.close();
            if(outStream != null)
                outStream.close();
            if(inStream != null)
                inStream.close();
            client.setRunCondition(false);
        }catch(IOException ioe){ioe.printStackTrace();}
    }

}
