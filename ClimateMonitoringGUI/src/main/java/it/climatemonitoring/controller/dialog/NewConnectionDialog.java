package it.climatemonitoring.controller.dialog;

import it.climatemonitoring.clientCm.Client;
import it.climatemonitoring.clientCm.ClientProxy;
import it.climatemonitoring.controller.mainscene.MainWindowController;
import it.climatemonitoring.controller.operatore.OperatoreViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private Stage connectionStage;
    public NewConnectionDialog(MainWindowController mainWindowController, Stage connectionStage){
        this.mainWindowController = mainWindowController;
        this.connectionStage = connectionStage;
        whichController = true;
    }

    public NewConnectionDialog(OperatoreViewController operatoreViewController, Stage connectionStage){
        this.operatoreViewController = operatoreViewController;
        whichController = false;
        this.connectionStage = connectionStage;
    }

    /**
     * Prova a creare una nuova connessione al server, associandola al controller corretto
     */
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
                connectionStage.close();
            }else{
                new Alert(Alert.AlertType.ERROR, "Connessione al server fallita!").showAndWait();
            }
        }catch(IOException ioe){new Alert(Alert.AlertType.ERROR, "Indirizzo ip o hostname non validi!").showAndWait();}
    }



}
