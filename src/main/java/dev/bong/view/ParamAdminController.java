package dev.bong.view;

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
import java.util.ResourceBundle;

public class ParamAdminController implements Initializable {

    @FXML
    private Label panneauAdminText;

    @FXML
    protected void onApplyParamAdmin() throws IOException {
        Parent param = FXMLLoader.load(ParamAdminController.class.getResource("/layout/parametre.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) panneauAdminText.getScene().getWindow();
        thisStage.setTitle("Paramètres");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton appliqué appuyé");
    }
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
    private Spinner<Integer> spNbPointFen;
    @FXML
    private Spinner<Integer> spNbBitQuant;

    //Spinner Value Factory
    final int initialValue = 1; //permet d'initialisé la valeur dans la case par ex: ancienne valeur
    SpinnerValueFactory<Integer> svf1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100,initialValue);
    SpinnerValueFactory<Integer> svf2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,200,initialValue);
    SpinnerValueFactory<Integer> svf3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,200,initialValue);
    SpinnerValueFactory<Integer> svf4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,200,initialValue);
    SpinnerValueFactory<Integer> svf5 = new SpinnerValueFactory.IntegerSpinnerValueFactory(2,1024,2,2);
    SpinnerValueFactory<Integer> bite = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,8);
    @FXML
    private TextArea taSummary;



    @FXML
    public void HandleBtnAfficheAction(ActionEvent actionEvent) {
        taSummary.clear();
        taSummary.appendText(String.valueOf("Taux de similarité : " + spTauxSimi.getValue()) + "\n");
        taSummary.appendText(String.valueOf("Nombre d'Intervalle : " + spIntervalle.getValue()) + "\n");
        taSummary.appendText(String.valueOf("Seuil Occurence : " + spOccu.getValue()) + "\n");
        taSummary.appendText(String.valueOf("Nombre de mot par texte : " + spNbMotTexte.getValue()) + "\n");
        taSummary.appendText(String.valueOf("Nombre de Point par fenêtre : " + spNbPointFen.getValue()) + "\n");
        taSummary.appendText(String.valueOf("Nombre bit Quantification : " + spNbBitQuant.getValue()) + "\n");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spTauxSimi.setValueFactory(svf1);
        spIntervalle.setValueFactory(svf2);
        spOccu.setValueFactory(svf3);
        spNbMotTexte.setValueFactory(svf4);
        spNbPointFen.setValueFactory(svf5);
        spNbBitQuant.setValueFactory(bite);

    }
}
