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
        Parent param = FXMLLoader.load(RechercheController.class.getResource("/layout/loading.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Chargement des résultats");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton recherche appuyé");
    }
    @FXML
    protected void onClickParam() throws IOException {
        Parent param = FXMLLoader.load(RechercheController.class.getResource("/layout/parametre.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Paramètres");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton paramètre appuyé");
    }

    @FXML
    protected void onTypeText() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton retour appuyé");
    }

    @FXML
    protected void onTypeImageRef() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton retour appuyé");
    }

    @FXML
    protected void onTypeImagePalette() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/paletteCouleur.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton retour appuyé");
    }

    @FXML
    protected void onTypeAudio() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/parametre.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton retour appuyé");
    }
}
