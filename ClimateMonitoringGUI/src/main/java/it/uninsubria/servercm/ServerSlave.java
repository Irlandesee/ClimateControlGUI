package it.uninsubria.servercm;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerCm;
import it.uninsubria.util.IDGenerator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class ServerSlave extends Thread{

    private int slaveId;
    private ServerCm serv;
    private Socket sock;
    private Worker worker;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private Properties props;

    public ServerSlave(Socket sock, ServerCm serv, int slaveId, Properties props){
        this.sock = sock;
        this.serv = serv;
        this.slaveId = slaveId;
        this.props = props;
        this.setName("Slave " +slaveId);
        this.start();
    }

    public void run(){
        String clientId = "";
        boolean runCondition = true;
        try{
            outStream = new ObjectOutputStream(sock.getOutputStream());
            inStream = new ObjectInputStream(sock.getInputStream());
            while(runCondition){
                String s = inStream.readObject().toString();
                switch(s){
                    case ServerInterface.ID -> {
                        clientId = inStream.readObject().toString();
                        System.out.printf("Slave %d connected to client %s\n", slaveId, clientId);
                    }
                    case ServerInterface.NEXT -> {
                        Request req = (Request) inStream.readObject();
                        serv.addRequest(req, this.getName());
                        System.err.println("Adding request, size: " + serv.getRequestsQueueSize());
                        worker = new Worker(IDGenerator.generateID(), serv.getDbUrl(), props, serv);
                        worker.start();
                        try {
                            System.out.printf("%s waiting for worker to join\n", this.getName());
                            worker.join();
                            System.out.println("worker joined");
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                        Response res = serv.getResponse(this.getName());
                        System.err.printf("Slave %d sending %s\n", slaveId, res);
                        outStream.writeObject(res);
                    }
                    case ServerInterface.TEST -> {
                        int number = (int) inStream.readObject();
                        System.out.printf("Slave %d received %d\n", slaveId, number);
                        number += 1;
                        System.out.printf("Slave %d sending: %d\n", slaveId, number);
                        outStream.writeObject(number);
                    }
                    case ServerInterface.QUIT -> {
                        System.out.printf("Client %s has disconnected, Slave %d terminating\n", clientId, slaveId);
                        runCondition = false;
                    }
                    default -> {
                        System.err.println("Received some undefined behaviour");
                        outStream.writeObject(ServerInterface.UNDEFINED_BEHAVIOUR);
                    }
                }
            }
        }catch(IOException ioe){
            System.out.printf("Client %s has disconnected, Slave %d terminating\n", clientId, slaveId);
            runCondition = false;
            ioe.printStackTrace();
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }finally{
            try{
                outStream.close();
                inStream.close();
                sock.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

    }

}
