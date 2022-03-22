package dev.bong.view;

import dev.bong.control.ControlRechercheMotCle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RechercheController {

    public TextField barreRecherche;
    public TextField barreBanWord;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onClickSearch() throws IOException {
        Parent param = FXMLLoader.load(Objects.requireNonNull(RechercheController.class.getResource("/layout/loading.fxml")));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) welcomeText.getScene().getWindow();
        thisStage.setTitle("Chargement des résultats");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton recherche appuyé");

        ControlRechercheMotCle controlRechercheMotCle = new ControlRechercheMotCle();
        String motcle=barreRecherche.getText();
        String banWord=barreBanWord.getText();
        controlRechercheMotCle.initRecherche(List.of(motcle.split("/")),List.of(banWord.split("/")));
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
}