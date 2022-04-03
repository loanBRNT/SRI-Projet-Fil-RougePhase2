package dev.bong.view;


import dev.bong.entity.GestionAlerte;
import dev.bong.entity.Historique;
import dev.bong.entity.Recherche;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HistoriqueController implements Initializable {

    @FXML
    public TableView<Recherche> tableviewHisto;
    public TableColumn<Recherche,String> dateCol;
    public TableColumn typeCol;
    public TableColumn searchCol;
    public TableColumn bansCol;

    public ObservableList<Recherche> temp = FXCollections.observableArrayList();

    @FXML
    protected void onBack() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }

    @FXML
    protected void onRealeaseSelection() {
        tableviewHisto.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onClean() throws IOException {
        Historique.lire();
        if (Historique.getListeDeListe().isEmpty()){
            GestionAlerte.genererInfos("Historique", "L'historique est déja vide");
        }else{
            try {
                tableviewHisto.getSelectionModel().clearSelection();
                Historique.effacer();
                Historique.lire();
                temp.clear();
                tableviewHisto.refresh();
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
            tableviewHisto.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            dateCol.setCellValueFactory(new PropertyValueFactory<Recherche, String>("date"));
            typeCol.setCellValueFactory(new PropertyValueFactory<Recherche, String>("type"));
            searchCol.setCellValueFactory(new PropertyValueFactory<Recherche, String>("search"));
            bansCol.setCellValueFactory(new PropertyValueFactory<Recherche, String>("bans"));
            //Ajoute de l'historique actuel dans la liste observable
            for (int i = Historique.getListeDeListe().size()-1; i >=0; i--) {
                temp.add(Historique.getListeDeListe().get(i));
            }
            tableviewHisto.setItems(temp);
            tableviewHisto.setOnMouseClicked(e -> {
               events();
           });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void events(){
        if(tableviewHisto.getSelectionModel().getSelectedItem()!=null) {
            int index = tableviewHisto.getSelectionModel().getSelectedIndex();
            GestionAlerte.genererInfos("Résultats de la recherche", temp.get(index).getResultats());
            tableviewHisto.getSelectionModel().clearSelection();
        }
    }
}



