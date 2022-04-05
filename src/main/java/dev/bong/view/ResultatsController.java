package dev.bong.view;

import dev.bong.control.ControlEnvoieResultat;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


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
        //String path = "/images/jingle_fi.wav";
        //me = new Media(new File(path).toURI().toString());
        //mediaPlayer =new MediaPlayer(me);
        //mediaView.setMediaPlayer(mediaPlayer);
        //Initialisation de la listeView
        listViewRes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Remplissage
        listViewRes.getItems().addAll(controlEnvoieResultat.getResultat());
        listViewRes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(listViewRes.getSelectionModel().getSelectedItem()!=null) {
                    int index = listViewRes.getSelectionModel().getSelectedIndex();
                    try  {
                        File file = new File("./moteurs/Database/Image/RGB/18.jpg");
                        if (!Desktop.isDesktopSupported()){
                            System.out.println("not supportred");
                        } else {
                            Desktop desktop = Desktop.getDesktop();
                            desktop.open(file);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}



