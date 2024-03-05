package it.uninsubria.controller.dialog;

import it.uninsubria.clientCm.Client;
import it.uninsubria.controller.mainscene.MainWindowController;
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

    private Client client;
    public NewConnectionDialog(Client client){
        this.client = client;
    }

    @FXML
    public void initialize(){
        /**
        ipField.setText("192.168.26");
        portField.setText("9999");
         **/
    }

    @FXML
    public void handleConnect(){
        String ipAddr = ipField.getText();
        String portNumber = portField.getText();
        try{
            String hostName = InetAddress.getLocalHost().getHostName();
            Inet4Address ipv4Addr = (Inet4Address) InetAddress.getByName(ipAddr);
            client.setHostName(hostName);
            client.setServerIp(ipv4Addr);
            try{
                int port = Integer.parseInt(portNumber);
                client.setPortNumber(port);
            }catch(NumberFormatException nfe){
                new Alert(Alert.AlertType.ERROR, "Numero di porta non valido!").showAndWait();
                return;
            }
        }catch(IOException ioe){new Alert(Alert.AlertType.ERROR, "Indirizzo ip o hostname non validi!").showAndWait(); return;}
        if(client.testConnection()){
            new Alert(Alert.AlertType.CONFIRMATION, "Connessione al server stabilita!").showAndWait();
            client.start();
        }else{
            new Alert(Alert.AlertType.ERROR, "Connessione al server fallita!").showAndWait();
        }
    }



}
