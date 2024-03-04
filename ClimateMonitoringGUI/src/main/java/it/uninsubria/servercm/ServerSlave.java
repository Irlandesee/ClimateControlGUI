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
                switch (cmd) {
                    case ServerInterface.ID -> {
                        clientId = inStream.readObject().toString();
                        System.err.printf("Slave %d connected to client %s\n", slaveId, clientId);
                    }
                    case ServerInterface.NEXT -> {
                        Request req = (Request) inStream.readObject();
                        serv.addRequest(req, this.getName());
                        System.err.println("Adding request, size: " + serv.getRequestsQueueSize());
                        worker = new Worker(
                                IDGenerator.generateID(),
                                serv.getDbUrl(),
                                props,
                                serv);
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
                        int number = inStream.readInt();
                        System.err.printf("Slave %d received %d\n", slaveId, number);
                        number += 1;
                        System.out.printf("Slave %d sending: %d\n", slaveId, number);
                        outStream.writeInt(number);
                    }
                    default -> {
                        System.err.println("Received some undefined behaviour");
                        outStream.writeObject(ServerInterface.UNDEFINED_BEHAVIOUR);
                    }
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
