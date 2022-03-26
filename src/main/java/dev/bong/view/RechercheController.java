package dev.bong.view;

import dev.bong.control.ControlEnvoieResultat;
import dev.bong.control.ControlRechercheMotCle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RechercheController {

    private boolean banWordsButtonActivate = false;


    @FXML
    private Label welcomeText;

    @FXML
    private ToggleButton banButton;

    @FXML
    private TextField textFieldBanWords;

    @FXML
    private TextField barreRecherche;

    @FXML
    protected void onClickSearch() throws IOException {
        Parent param = FXMLLoader.load(Objects.requireNonNull(RechercheController.class.getResource("/layout/loading.fxml")));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Chargement des résultats");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton recherche appuyé");

        String motcle=barreRecherche.getText();
        String banWord=textFieldBanWords.getText();

        ControlRechercheMotCle controlRechercheMotCle = new ControlRechercheMotCle(List.of(motcle.split("/")),List.of(banWord.split("/")));
        controlRechercheMotCle.start();
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
    protected void onBanWords() throws IOException {
        if (this.banWordsButtonActivate ==  false) {
            textFieldBanWords.setVisible(true);
            this.banWordsButtonActivate=true;
            this.banButton.setText("- Ban words");
        }else{
            textFieldBanWords.setVisible(false);
            this.banWordsButtonActivate=false;
            this.banButton.setText("+ Ban words");
        }
    }

    @FXML
    protected void onTypeText() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
    }

    @FXML
    protected void onTypeImageRef() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
    }

    @FXML
    protected void onTypeImagePalette() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/paletteCouleur.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
    }

    @FXML
    protected void onTypeAudio() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
    }
}
