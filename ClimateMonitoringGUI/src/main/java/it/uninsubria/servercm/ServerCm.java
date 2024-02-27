package it.uninsubria.servercm;

import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.util.IDGenerator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ServerCm {

    private ServerSocket ss;
    private final int PORT = ServerInterface.PORT;
    private final String name = "ServerCm";
    private LinkedBlockingQueue<Request> requests;
    private LinkedBlockingQueue<Response> responses;
    private LinkedBlockingQueue<ServerSlave> slaves;
    private LinkedBlockingQueue<Worker> workers;


    //private final String dbUrl = "jdbc:postgresql://192.168.1.26/postgres";
    //private final String dbUrl = "jdbc:postgresql://192.168.1.7/postgres";
    private final String dbUrl = "jdbc:postgresql://localhost/postgres";

    private final Properties props;

    private final Logger logger;
    private final String user = "postgres";
    private final String password = "qwerty";


    public ServerCm(){
        try{
            ss = new ServerSocket(PORT);
            System.err.printf("%s started on port: %d\n", this.name, this.PORT);
        }catch(IOException ioe){ioe.printStackTrace();}
        slaves = new LinkedBlockingQueue<ServerSlave>();
        requests = new LinkedBlockingQueue<Request>();
        responses = new LinkedBlockingQueue<Response>();
        workers = new LinkedBlockingQueue<Worker>();
        props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        this.logger = Logger.getLogger(this.name);
    }

    public void addResponse(Response res, String threadId){
        //System.out.printf("%s puts response in queue\n", threadId);
        try{
            responses.put(res);
            //System.out.println("Queue size: " + responses.size());
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    public Response getResponse(String threadId){
        //System.out.printf("%s gets response\n", threadId);
        try{
            return responses.take();
        }catch(InterruptedException ie){ie.printStackTrace(); return null;}
    }

    public void addRequest(Request r, String threadId){
        //System.out.printf("%s adds request\n", threadId);
        try{
            requests.put(r);
            //System.out.println("Added request to queue: " + requests.size());
        }catch(InterruptedException ie){ie.printStackTrace();}
    }

    public Request getRequest(String threadId){
        //System.out.printf("%s gets request\n", threadId);
        try{
            return requests.take();
        }catch(InterruptedException ie){ie.printStackTrace(); return null;}
    }

    public void addWorker(Worker w){
        try{
            workers.put(w);
        }catch(InterruptedException ie){ie.printStackTrace();}
    }

    public void addSlave(ServerSlave ss){
        try{
            slaves.put(ss);
        }catch(InterruptedException ie){ie.printStackTrace();}
    }

    public boolean removeWorker(Worker w){
        return workers.remove(w);
    }

    public boolean removeSlave(ServerSlave s){
        return slaves.remove(s);
    }

    public int getResponsesQueueSize(){
        return this.responses.size();
    }

    public int getRequestsQueueSize(){
        return this.requests.size();
    }

    public int getSlavesSize(){
        return this.slaves.size();
    }

    public int getWorkersSize(){
        return this.workers.size();
    }

    public String getDbUrl(){
        return this.dbUrl;
    }

    public static void main(String[] args){
        int i = 0;
        ServerCm serv = new ServerCm();

        try{
            while(true){
                Socket sock = serv.ss.accept();
                serv.logger.info("New connection accepted");
                ServerSlave slave = new ServerSlave(sock, serv, i, serv.props);
                i++;
                try{
                    serv.slaves.put(slave);
                    System.out.printf("%s starting new worker\n", serv.name);
                }catch(InterruptedException ie){ie.printStackTrace();}
            }
        }catch(IOException ioe){ioe.printStackTrace();}
        finally{
            try{
                System.out.println("Master server closing server socket" + i);
                serv.ss.close();
        }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

    }


}
