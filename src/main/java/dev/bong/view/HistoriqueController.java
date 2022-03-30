package dev.bong.view;

import dev.bong.entity.GestionAlerte;
import dev.bong.entity.Historique;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HistoriqueController implements Initializable {

    @FXML
    public ListView<String> listViewHisto;

    @FXML
    protected void onBack() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }

    @FXML
    protected void onRealeaseSelection() {
        listViewHisto.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onClean() throws IOException {
        if (Historique.getRecherches().isEmpty() && Historique.getResultats().isEmpty()){
            GestionAlerte.genererInfos("Historique", "L'historique est déja vide");
        }else{
            try {
                listViewHisto.getSelectionModel().clearSelection();
                Historique.effacer();
                Historique.lire();
                listViewHisto.getSelectionModel().selectAll();
                ObservableList<Integer> indices = listViewHisto.getSelectionModel().getSelectedIndices();
                for (int index : indices) {
                    listViewHisto.getSelectionModel().getSelectedItems().remove(index);
                }
                listViewHisto.getItems().addAll(Historique.listeDeListe.get(0));
                RechercheApplication.changerScene("historique.fxml");
                GestionAlerte.genererInfos("Historique", "L'historique a bien été nettoyé");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Mise a jour de la liste hitorique
            Historique.lire();
            //Initialisation de la listeView
            listViewHisto.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            //Ajoute le premier champ de notre listeDeListe qui correpond a la recherche
            listViewHisto.getItems().addAll(Historique.listeDeListe.get(0));
            listViewHisto.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    int index = listViewHisto.getSelectionModel().getSelectedIndex();
                    GestionAlerte.genererInfos("Résultats de la recherche", Historique.getResultats().get(index));
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}



