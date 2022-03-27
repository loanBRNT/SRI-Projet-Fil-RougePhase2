package dev.bong.view;

import dev.bong.control.ControlEnvoieResultat;
import dev.bong.control.ControlRechercheMotCle;
import javafx.application.Preloader;
import javafx.event.EventType;
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
    private ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();

    private Scene sceneRes;

    public LoadingController(){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(RechercheApplication.class.getResource("/layout/resultats.fxml"));
        try {
            this.sceneRes = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLoadingAchieve() throws IOException {
        RechercheApplication.changerScene("resultats.fxml");
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
            RechercheApplication.changerScene("hello-view.fxml");
            System.out.println("NON");
        }
    }
}
