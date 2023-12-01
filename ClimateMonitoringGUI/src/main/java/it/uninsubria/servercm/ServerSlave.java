package it.uninsubria.servercm;
import it.uninsubria.request.Request;
import it.uninsubria.response.Response;
import it.uninsubria.servercm.ServerCm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class ServerSlave extends Thread{

    private int slaveId;
    private ServerCm serv;
    private Socket sock;
    private Worker worker;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;


    public ServerSlave(Socket sock, ServerCm serv, int slaveId, Worker w){
        this.sock = sock;
        this.serv = serv;
        this.slaveId = slaveId;
        this.worker = w;
        this.setName("Slave " +slaveId);
        try{
            outStream = new ObjectOutputStream(sock.getOutputStream());
            inStream = new ObjectInputStream(sock.getInputStream());
        }catch(IOException ioe){ioe.printStackTrace();}
        this.start();
    }

    public void run(){
        String clientId = "";
        try{
            String cmd = "";
            while(!(cmd = inStream.readObject().toString()).equals(ServerInterface.QUIT)){
                if(cmd.equals(ServerInterface.ID)){
                    clientId =  inStream.readObject().toString();
                    System.err.printf("Slave %d connected to client %s\n", slaveId, clientId);
                }
                else if(cmd.equals(ServerInterface.NEXT)) {
                    Request req = (Request) inStream.readObject();
                    serv.addRequest(req, this.getName());
                    System.err.println("Adding request, size: " + serv.getRequestsQueueSize());
                    try{
                        System.out.printf("%s waiting for worker to join\n", this.getName());
                        worker.join();
                        System.out.println("worker joined");
                    }catch(InterruptedException ie){ie.printStackTrace();}
                    Response res = serv.getResponse(this.getName());
                    System.err.printf("Slave %d sending %s\n", slaveId, res);
                    outStream.writeObject(res);
                }
                else{
                    System.err.println("Received some undefined behaviour");
                    outStream.writeObject(ServerInterface.UNDEFINED_BEHAVIOUR);
                }
            }
            System.out.printf("Client %d has disconnected\n", slaveId);
        }catch (IOException ioe){
            ioe.printStackTrace();
        } catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}
        finally {
            System.out.printf("Slave %d closing\n", slaveId);
            try{
                sock.close();
            }catch(IOException i){i.printStackTrace();}
        }

    }

}
