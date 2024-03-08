package it.uninsubria.controller.dialog;

import it.uninsubria.datamodel.parametroClimatico.ParametroClimatico;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class PcDialog {

    private final ParametroClimatico pc;
    private final String nomeCentro;
    private final String nomeArea;

    public Label paramIdLabel;
    public Label dataPubLabel;
    public Label nomeAreaLabel;
    public Label nomeCentroLabel;
    public ListView<Label> valoriList;
    public PcDialog(ParametroClimatico pc, String nomeCentro, String nomeArea){
        this.pc = pc;
        this.nomeCentro = nomeCentro;
        this.nomeArea = nomeArea;
    }

    @FXML
    public void initialize(){
        paramIdLabel.setText(pc.getParameterId());
        dataPubLabel.setText(pc.getPubDate().toString());
        nomeCentroLabel.setText(nomeCentro);
        nomeAreaLabel.setText(nomeArea);

        valoriList.getItems()
                .add(new Label("vento: " + pc.getVentoValue()));
        valoriList.getItems()
                .add(new Label("umidità: " + pc.getUmiditaValue()));
        valoriList.getItems()
                .add(new Label("pressione: " + pc.getPressioneValue()));
        valoriList.getItems()
                .add(new Label("temperatura: " + pc.getTemperaturaValue()));
        valoriList.getItems()
                .add(new Label("precipitazioni: " + pc.getPrecipitazioniValue()));
        valoriList.getItems()
                .add(new Label("Altitudine ghiacciai: " + pc.getAltitudineValue()));
        valoriList.getItems()
                .add(new Label("Massa Ghiacciai: " + pc.getMassaValue()));


    }

    @FXML
    public void close(ActionEvent actionEvent){
        Stage s = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(s != null)
            s.close();
    }

}
