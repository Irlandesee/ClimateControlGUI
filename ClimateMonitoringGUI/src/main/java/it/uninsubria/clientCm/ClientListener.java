package it.uninsubria.clientCm;

import it.uninsubria.servercm.ServerInterface;
import it.uninsubria.update.Update;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ClientListener extends Thread{

    private Logger listenerLogger;
    private LinkedBlockingQueue<Update> updates;
    private boolean runCondition;
    private Client client;
    private String hostname;
    private Inet4Address ipAddr;
    private int portNumber;
    private Socket sock;
    private ObjectInputStream inStream;
    public void ClientListener(Client client, String hostName){
        this.client = client;
        this.hostname = hostname;
        listenerLogger = Logger.getLogger("ClientListener");
        updates = new LinkedBlockingQueue<Update>();
    }
    public void init(){
        String ipAddr = getIpAddr().getHostAddress();
        try{
            sock = new Socket(ipAddr, getPortNumber());
            inStream = new ObjectInputStream(sock.getInputStream());
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public Inet4Address getIpAddr(){
        return this.ipAddr;
    }

    public void setIpAddr(Inet4Address ipAddr){
        this.ipAddr = ipAddr;
    }

    public int getPortNumber(){
        return this.portNumber;
    }

    public void setPortNumber(int portNumber){
        this.portNumber = portNumber;
    }

    public void pushUpdate(Update update){
        updates.offer(update);
    }
    
    public Update takeUpdate(){
        try{
            Update update = updates.take();
            return update;
        }catch(InterruptedException ie){
            ie.printStackTrace();
            return null;
        }
    }

    public boolean getRuncondition(){return this.runCondition;}
    public void setRunCondition(boolean runCondition){this.runCondition = runCondition;}
    public void run(){
        
        while(getRuncondition()){
            listenerLogger.info("Listening: ");
            try{
                String s = inStream.readObject().toString();
                if(s.equals(ServerInterface.UPDATE)){
                    Update update = (Update) inStream.readObject();
                    listenerLogger.info("update received: " + update.toString());
                }
            }catch(ClassNotFoundException | IOException e){
                e.printStackTrace();

            }

        }
    }

}
