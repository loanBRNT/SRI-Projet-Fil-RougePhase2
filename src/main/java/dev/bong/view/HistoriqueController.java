package dev.bong.view;

import javafx.fxml.FXML;

import java.io.IOException;

public class HistoriqueController {

    @FXML
    protected void onBack() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }
    @FXML
    protected void onClean() throws IOException {

    }

}
