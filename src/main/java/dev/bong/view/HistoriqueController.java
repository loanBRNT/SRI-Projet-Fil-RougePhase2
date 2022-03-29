package dev.bong.view;

import dev.bong.entity.GestionAlerte;
import dev.bong.entity.Historique;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HistoriqueController implements Initializable {

    @FXML
    protected void onBack() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }
    @FXML
    protected void onClean() throws IOException {
        Historique.effacer();
        GestionAlerte.genererInfos("Historique","L'historique a bien été nettoyé");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Historique.lire();
            ArrayList<ArrayList<String>> listeDeliste = Historique.getListeDeListe();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
