package dev.bong.view;

import dev.bong.control.ControlEnvoieResultat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ResultatsController implements Initializable {
    public Label textResultat;
    @FXML
    private Label bongResultats;

    private ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();

    @FXML
    protected void onRelancerRecherche() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
        System.out.println("Bouton relancé appuyé");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textResultat.setText(controlEnvoieResultat.getResultat().toString());
    }
}
