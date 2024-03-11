package it.uninsubria.controller.dialog;

import it.uninsubria.clientCm.Client;
import it.uninsubria.clientCm.ClientProxy;
import it.uninsubria.controller.mainscene.MainWindowController;
import it.uninsubria.controller.operatore.OperatoreViewController;
import it.uninsubria.servercm.ServerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class NewConnectionDialog {

    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Button buttonConnect;
    private MainWindowController mainWindowController;
    private OperatoreViewController operatoreViewController;
    private boolean whichController;
    public NewConnectionDialog(MainWindowController mainWindowController){
        this.mainWindowController = mainWindowController;
        whichController = true;
    }

    public NewConnectionDialog(OperatoreViewController operatoreViewController){
        this.operatoreViewController = operatoreViewController;
        whichController = false;
    }

    @FXML
    public void handleConnect(){
        String ipAddr = ipField.getText();
        String portNumber = portField.getText();
        try{
            String hostName = InetAddress.getLocalHost().getHostName();
            Inet4Address ipv4Addr = (Inet4Address) InetAddress.getByName(ipAddr);
            int port = -1;
            try{
                port = Integer.parseInt(portNumber);
            }catch(NumberFormatException nfe){
                new Alert(Alert.AlertType.ERROR, "Numero di porta non valido!").showAndWait();
                return;
            }
            Client client = new Client();
            client.setHostName(hostName);
            ClientProxy clientProxy = new ClientProxy(client, hostName);
            client.setClientProxy(clientProxy);
            clientProxy.setIpAddr(ipv4Addr);
            clientProxy.setPortNumber(port);
            clientProxy.init();

            if(clientProxy.testConnection()){
                new Alert(Alert.AlertType.CONFIRMATION, "Connessione al server stabilita!").showAndWait();
                if(whichController){
                    mainWindowController.setClient(client);
                    mainWindowController.setClientProxy(clientProxy);
                }else{
                    operatoreViewController.setClient(client);
                    operatoreViewController.setClientProxy(clientProxy);
                }
                client.setRunCondition(true);
                client.start();
            }else{
                new Alert(Alert.AlertType.ERROR, "Connessione al server fallita!").showAndWait();
            }
        }catch(IOException ioe){new Alert(Alert.AlertType.ERROR, "Indirizzo ip o hostname non validi!").showAndWait();}
    }



}
