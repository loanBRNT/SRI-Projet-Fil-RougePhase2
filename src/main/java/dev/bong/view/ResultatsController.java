package dev.bong.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ResultatsController {
    @FXML
    private Label bongResultats;

    @FXML
    protected void onRelancerRecherche() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
        System.out.println("Bouton relancé appuyé");
    }
}
