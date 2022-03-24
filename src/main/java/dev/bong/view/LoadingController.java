package dev.bong.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoadingController {


    @FXML
    private Label bongLoading;

    @FXML
    protected void onLoadingAchieve() throws IOException {
        Parent param = FXMLLoader.load(LoadingController.class.getResource("/layout/resultats.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) bongLoading.getScene().getWindow();
        thisStage.setTitle("Résultats");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Recherche terminée");
    }

    @FXML
    protected void onLoadingFailled() throws IOException {
        ButtonType okBtn = ButtonType.YES;
        ButtonType cancelBtn = ButtonType.NO;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",okBtn,cancelBtn);
        alert.setTitle("Echec");
        alert.setHeaderText("Echec de la recherche");
        alert.setContentText("Voulez-vous relancer la recherche ?");
        alert.setResizable(false);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            System.out.println("OUI");
            //Mettre l'appel a nouveau de la fonction recherche
        }
        else if(result.isPresent() && result.get() == ButtonType.NO){
            Parent param = FXMLLoader.load(AdminController.class.getResource("/layout/hello-view.fxml"));
            Scene scene = new Scene(param);
            Stage thisStage = (Stage) bongLoading.getScene().getWindow();
            thisStage.setTitle("Rechercher");
            thisStage.setScene(scene);
            thisStage.show();
            System.out.println("NON");
        }
    }
}
