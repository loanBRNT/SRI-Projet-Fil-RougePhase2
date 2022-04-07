package dev.bong.view;

import dev.bong.control.ControlEnvoieResultat;
import dev.bong.entity.GestionAlerte;
import dev.bong.entity.TypeFichier;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class ResultatsController implements Initializable {

    @FXML
    public ListView listViewRes;

    private ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();

    @FXML
    protected void onRelancerRecherche() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }

    @FXML
    protected void onRealeaseSelection() {
        listViewRes.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ArrayList<String> listeResultat = (ArrayList<String>) controlEnvoieResultat.getResultat();
        System.out.println(listeResultat);
        listViewRes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Remplissage
        listViewRes.getItems().addAll(listeResultat);
        listViewRes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(listViewRes.getSelectionModel().getSelectedItem()!=null) {
                    int index = listViewRes.getSelectionModel().getSelectedIndex();
                    String name = listeResultat.get(index);
                    TypeFichier typeFichier = controlEnvoieResultat.getType();
                    String path;
                    switch (typeFichier){
                        case JPG :
                            path = "Image/RGB/";
                            break;
                        case BMP :
                            path = "Image/NB/";
                            break;
                        case WAV :
                            path = "Audio/";
                            name = List.of(name.split(" :")).get(0);
                            break;
                        default :
                            path = "Texte/";
                            break;
                    }
                    try  {
                        File file = new File("./moteurs/Database/"+ path + name);
                        if (!Desktop.isDesktopSupported()){
                            GestionAlerte.genererErreur("Erreur", "Desktop not supported");
                        } else {
                            Desktop desktop = Desktop.getDesktop();
                            desktop.open(file);
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                        GestionAlerte.genererWarning("Attention", "Fichier introuvable");
                    }
                }
            }
        });
    }
}



