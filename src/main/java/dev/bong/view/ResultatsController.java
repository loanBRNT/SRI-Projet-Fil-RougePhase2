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
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ResultatsController implements Initializable {

    @FXML
    public ListView listViewRes;
    public ImageView affImage;
    public MediaView mediaView;
    public MediaPlayer mediaPlayer;
    public Media me;

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

        if (listeResultat.isEmpty() || listeResultat.toString().equals("[ ]")) {
            GestionAlerte.genererWarning("Resultats","La recherche n'a aboutit à aucun résultat");
        } else {
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
                                break;
                            default :
                                path = "Texte/";
                                break;
                        }
                        try  {
                            File file = new File("./moteurs/Database/"+ path +name);
                            if (!Desktop.isDesktopSupported()){
                                GestionAlerte.genererErreur("Erreur", "Desktop not supported");
                            } else {
                                Desktop desktop = Desktop.getDesktop();
                                desktop.open(file);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            GestionAlerte.genererWarning("Attention", "Fichier introuvable");

                        }
                    }
                }
            });
        }



    }
}



