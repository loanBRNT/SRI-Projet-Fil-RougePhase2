package dev.bong.view;

import dev.bong.control.ControlModifierConfig;
import dev.bong.entity.Config;
import dev.bong.entity.GestionAlerte;
import fr.dgac.ivy.IvyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ParamAdminController implements Initializable {
    private ControlModifierConfig controlModifierConfig= new ControlModifierConfig();
    Config config = Config.getInstance();

    @FXML
    private Label panneauAdminText;


    @FXML
    private Button btnAffiche;

    @FXML
    private Label lbTauxSimi;
    @FXML
    private Label lbIntervalle;
    @FXML
    private Label lbOccu;
    @FXML
    private Label lbNbMotTexte;
    @FXML
    private Label lbNbPointFen;
    @FXML
    private Label lbNbBitQuant;

    @FXML
    private Spinner<Integer> spTauxSimi;
    @FXML
    private Spinner<Integer> spIntervalle; //1,2,3 pour intervalle
    @FXML
    private Spinner<Integer> spOccu;
    @FXML
    private Spinner<Integer> spNbMotTexte;
    @FXML
    private Spinner<String> spNbPointFen;
    //private Spinner<Integer> spNbPointFen;
    @FXML
    private Spinner<Integer> spNbBitQuant;

     public ObservableList<String> puissanceDeux = FXCollections.observableArrayList("2","4","8","16","32","64","128","256","512","1024","2048");
    //Spinner Value Factory

    final int initialValue = 1; //permet d'initialis√© la valeur dans la case par ex: ancienne valeur
    SpinnerValueFactory<Integer> svf1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100,config.getTauxSim());
    SpinnerValueFactory<Integer> svf2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,200,config.getNbrIntervalleAudio());
    SpinnerValueFactory<Integer> svf3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,200,config.getSeuilOccMot());
    SpinnerValueFactory<Integer> svf4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,200,config.getNbMaxMotParTexte());
    SpinnerValueFactory<String> svf5 = new SpinnerValueFactory.ListSpinnerValueFactory<String>(puissanceDeux);
    SpinnerValueFactory<Integer> svf6 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,4,config.getBitQuantification());
    @FXML
    private TextArea taSummary;


    @FXML
    protected void onApplyParamAdmin() throws IOException {
        ArrayList<Integer> listeValeur = recupererValeur() ;
        try {
            ControlModifierConfig.modifConfig(listeValeur);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation changement");
            alert.setHeaderText("modification du .config effectu√©e avec succ√®s");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            GestionAlerte.genererErreur("Ivy ERREUR","Echec de l'indexation automatique, connexion au moteur impossible");
        }
    }
    @FXML
    protected void onBack() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }
    @FXML
    public void HandleBtnAfficheAction(ActionEvent actionEvent) {
        taSummary.clear();
        taSummary.appendText(String.valueOf("Taux de similarit√© : " + config.getTauxSim()) + "\n");
        taSummary.appendText(String.valueOf("Nombre de mot par texte : " + config.getNbMaxMotParTexte()) + "\n");
        taSummary.appendText(String.valueOf("Seuil Occurence : " + config.getSeuilOccMot()) + "\n");
        taSummary.appendText(String.valueOf("Nombre d'Intervalle audio : " + config.getNbrIntervalleAudio()) + "\n");
        taSummary.appendText(String.valueOf("Nombre de Point par fen√™tre : " + config.getNbrPointsAudio()) + "\n");
        taSummary.appendText(String.valueOf("Nombre bit Quantification : " + config.getBitQuantification() ) + "\n");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        svf5.setValue(String.valueOf(config.getNbrPointsAudio()));
        spTauxSimi.setEditable(true);
        spTauxSimi.setValueFactory(svf1);
        spIntervalle.setEditable(true);
        spIntervalle.setValueFactory(svf2);
        spOccu.setEditable(true);
        spOccu.setValueFactory(svf3);
        spNbMotTexte.setEditable(true);
        spNbMotTexte.setValueFactory(svf4);
        spNbPointFen.setEditable(false);
        spNbPointFen.setValueFactory(svf5);
        spNbBitQuant.setEditable(true);
        spNbBitQuant.setValueFactory(svf6);

        try {
            config.chargementConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> recupererValeur(){
        ArrayList<Integer> listeValeur = new ArrayList<>();
        listeValeur.add(spTauxSimi.getValue());
        listeValeur.add(spNbMotTexte.getValue());
        listeValeur.add( spOccu.getValue());
        listeValeur.add(spIntervalle.getValue());
        listeValeur.add(Integer.parseInt(spNbPointFen.getValue()));
        listeValeur.add(spNbBitQuant.getValue());
        return listeValeur;
    }

/*
    public ObservableList<Integer> getPuissanceDeux() {
        return puissanceDeux;
    }

 */
}
