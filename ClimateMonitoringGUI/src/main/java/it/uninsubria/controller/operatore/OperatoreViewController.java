package it.uninsubria.controller.operatore;

import it.uninsubria.MainWindow;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.controller.scene.SceneController;
import it.uninsubria.graphbuilder.GraphBuilder;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class OperatoreViewController {

    public Button buttonRicercaAreaInteresse;
    public Button buttonVisualizzaParametri;
    public Button buttonInserisciParametri;
    public Button buttonInserisciCentroMonitoraggio;
    public Button buttonRegistraOp;

    public TableView tableView;
    public BorderPane borderPane;
    public VBox contentBox;
    public VBox paramBox;

    //per area interesse
    private TextField tDenominazione;
    private TextField tStato;
    private TextField tLatitudine;
    private TextField tLongitudine;
    private Button btnRicercaAreaPerDenom;
    private Button btnRicercaAreaPerStato;
    private Button btnRicercaAreaCoord;

    //Visualizzazione parametri climatici
    private TextField tAreaInteresse;
    private TextField tCentroMonitoraggio;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button btnRicercaPC;

    //Per centro monitoraggio
    private TextField nomeCentroField;
    private TextField comuneField;
    private TextField statoCMField;
    private TextField areaInteresseCMField;
    private TextArea areeInteresseBox;
    private Button inserisciArea;
    private Button inserisciCM;
    private Button clearCM;

    //alerts
    private Alert coordAlert;
    private Alert denomAlert;
    private Alert statoAlert;
    private Alert pcAlert;
    private Alert areaInteresseAlert;
    private Alert invalidDateAlert;
    private Alert cmAlert;

    private QueryHandler queryHandler;
    private final String url = "jdbc:postgresql://localhost/postgres";
    private Properties props;

    private final SceneController sceneController;
    public OperatoreViewController(SceneController sceneController){
        this.sceneController = sceneController;
    }


    @FXML
    public void initialize(){

        //Init the query handler
        props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "qwerty");

        queryHandler = new QueryHandler(url, props);

        //show aree interesse presenti
        showAreeInserite();
        //line chart
        contentBox.getChildren().add(
                GraphBuilder.getBasicLineChart(
                        GraphBuilder.Resource.wind
                )
        );
        initAlerts();
    }

    private void initAlerts(){

    }

    @FXML
    public void exit(ActionEvent actionEvent){

    }

    private void addNodesToParamBox(LinkedList<Node> nodes){
        nodes.forEach((node) -> paramBox.getChildren().add(node));
    }

    public void handleRicercaAreaInteresse(ActionEvent actionEvent){

    }

    private void prepTableAreaInteresse(){

    }

    private void showAreeInserite(){

    }

    private void ricercaAreaPerDenom(){

    }

    private void ricercaAreaPerStato(){

    }

    private void ricercaPerCoord(){

    }

    public void handleVisualizzaParametriClimatici(ActionEvent actionEvent){

    }

    private void prepTablePC(){

    }

    private void visualizzaPC(){

    }

    public void handleInserisciParametriClimatici(ActionEvent actionEvent){
        try{
            Parent root = FXMLLoader.load(MainWindow.class.getResource("fxml/parametro_climatico-scene.fxml")); //watch out for this line of code
            //Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Stage pcStage = new Stage();
            Scene scene = new Scene(root);
            pcStage.setScene(scene);
            pcStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}

    }

    public void executeInsertPCQuery(String nomeArea, String centroMon, LocalDate pubdate, short[] paramValues, String[] notes){
        //TODO
    }


    public void executeRegistraOpQuery(String nomeOp, String cognomeOp, String codFisc, String email, String password, String centroAfferenza){
        //TODO
    }

    public void handleButtonInserisciCentroMonitoraggio(ActionEvent actionEvent){

        //show centri di monitoraggio already present in the database
        TableColumn nomeColumn = new TableColumn("denominazione");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("denominazione"));
        TableColumn comuneColumn = new TableColumn("comune");
        comuneColumn.setCellValueFactory(new PropertyValueFactory<>("comune"));
        TableColumn countryColumn = new TableColumn("stato");
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        tableView.getColumns().addAll(nomeColumn, comuneColumn, countryColumn);

        //populate the tableView
        List< CentroMonitoraggio> centriPresenti = queryHandler.selectAll(QueryHandler.tables.CENTRO_MONITORAGGIO);
        centriPresenti.forEach(centro -> {
            tableView.getItems().add(centro);
        });

        //aree interesse?
        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    CentroMonitoraggio c = (CentroMonitoraggio) row.getItem();
                    System.out.println("Item double clicked: "+c);
                    //show cm details
                    LinkedList<String> cmAree = c.getAreeInteresseIdAssociate();
                    LinkedList<String> nomiAree = new LinkedList<String>();
                    for(String id: cmAree){
                        //query the db to get the areas names
                        String denominazione = queryHandler.selectObjectWithCond("denominazione", QueryHandler.tables.AREA_INTERESSE, "areaid", id)
                                .get(0);
                        nomiAree.add(denominazione);
                    }
                    //Create a new window containing the cms details
                    try {
                        Stage cmDialogStage = new Stage();
                        CMDialog cmDialogController = new CMDialog(sceneController, nomiAree);
                        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("fxml/cm-dialog.fxml"));
                        fxmlLoader.setController(cmDialogController);
                        Scene dialogScene = new Scene(fxmlLoader.load(), 400, 300);
                        cmDialogStage.setScene(dialogScene);
                        cmDialogStage.show();
                    }catch(IOException ioe){ioe.printStackTrace();}

                }
            });
            return row;
        });

        this.paramBox = new VBox(10);
        nomeCentroField = new TextField("Nome centro");
        nomeCentroField.setOnMouseClicked((event) -> nomeCentroField.clear());
        comuneField = new TextField("Comune centro");
        comuneField.setOnMouseClicked((event) -> comuneField.clear());
        statoCMField = new TextField("Stato centro");
        statoCMField.setOnMouseClicked((event) -> statoCMField.clear());
        areaInteresseCMField = new TextField("Area interesse");
        areaInteresseCMField.setOnMouseClicked((event) -> areaInteresseCMField.clear());
        areeInteresseBox = new TextArea();
        areeInteresseBox.setEditable(false);
        inserisciArea = new Button("Inserisci Area");
        inserisciArea.setOnAction((event) -> addAreaToBox());
        inserisciCM = new Button("Inserisci CM");
        inserisciCM.setOnAction((event) -> executeInsertCMQuery());
        clearCM = new Button("Pulisci");
        clearCM.setOnAction((event) -> clearCMFields());

        LinkedList<Node> nodesToAdd = new LinkedList<Node>();
        nodesToAdd.add(nomeCentroField);
        nodesToAdd.add(comuneField);
        nodesToAdd.add(statoCMField);
        nodesToAdd.add(areaInteresseCMField);
        nodesToAdd.add(areeInteresseBox);
        nodesToAdd.add(inserisciArea);
        nodesToAdd.add(inserisciCM);
        nodesToAdd.add(clearCM);

        addNodesToParamBox(nodesToAdd);

        this.borderPane.setRight(paramBox);

    }

    private void addAreaToBox(){
        String nomeArea = areaInteresseCMField.getText();
        if(!nomeArea.isEmpty()){
            String text = areeInteresseBox.getText();
            text += nomeArea + "\n";
            areeInteresseBox.setText(text);
        }
    }

    private void clearCMFields(){
        nomeCentroField.clear();
        comuneField.clear();
        statoCMField.clear();
        areaInteresseCMField.clear();
    }

    private void executeInsertCMQuery(){
        String nomeCentro = nomeCentroField.getText();
        String comuneCentro = comuneField.getText();
        String statoCentro = statoCMField.getText();
        //Area interesse è campo particolare, si possono inserire una quantita
        //indefinita di aree di interesse -> si cancella in automatico
        //solo areaInteresseCentro, per pulire tutto si usa clearCMFields()

        if(nomeCentro.isEmpty() || comuneCentro.isEmpty() || statoCentro.isEmpty()){cmAlert.showAndWait();}

        LinkedList<String> l = new LinkedList<String>();
        for(String nome: areeInteresseBox.getText().split("\n"))
            l.add(nome.trim());

        areaInteresseCMField.clear();
        boolean res = queryHandler.executeInsertCentroMonitoraggio(nomeCentro, comuneCentro, statoCentro, l);
        if(res){
            System.out.println("CM inserito");
            new Alert(Alert.AlertType.CONFIRMATION).showAndWait();
            clearCMFields();
        }
    }

    public void handleRegistraOperatore(ActionEvent actionEvent){
        try{
            Parent root = FXMLLoader.load(MainWindow.class.getResource("fxml/registrazione-scene.fxml")); //watch out for this line of code
            //Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Stage registrazioneStage = new Stage();
            Scene scene = new Scene(root);
            registrazioneStage.setScene(scene);
            registrazioneStage.show();
        }catch(IOException ioe){ioe.printStackTrace();}
    }



}
