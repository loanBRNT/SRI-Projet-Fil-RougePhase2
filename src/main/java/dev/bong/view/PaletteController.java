package dev.bong.view;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;

public class PaletteController {

    private boolean banWordsButtonActivate = false;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onClickSearch() throws IOException {
        RechercheApplication.changerScene("loading.fxml");
        System.out.println("Boutton recherche appuyé");
    }
    @FXML
    protected void onClickParam() throws IOException {
        RechercheApplication.changerScene("parametre.fxml");
        System.out.println("Boutton paramètre appuyé");
    }

    @FXML
    protected void onTypeText() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
        System.out.println("Boutton retour appuyé");
    }

    @FXML
    protected void onTypeImageRef() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
        System.out.println("Boutton retour appuyé");
    }

    @FXML
    protected void onTypeImagePalette() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
        System.out.println("Boutton retour appuyé");
    }

    @FXML
    protected void onTypeAudio() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
        System.out.println("Boutton retour appuyé");
    }
}
