package dev.bong.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class LoadingController {
    
    @FXML
    private Label bongLoading;

    @FXML
    protected void onLoadingAchieve() throws IOException {
        changementVersResultat();
        System.out.println("Recherche terminée");
    }

    public void changementVersResultat() throws IOException {
        Parent param = FXMLLoader.load(LoadingController.class.getResource("/layout/resultats.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) bongLoading.getScene().getWindow();
        thisStage.setTitle("Résultats");
        thisStage.setScene(scene);
        thisStage.show();
    }
}
