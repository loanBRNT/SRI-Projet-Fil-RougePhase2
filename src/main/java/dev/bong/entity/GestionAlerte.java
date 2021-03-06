package dev.bong.entity;

import dev.bong.control.ControlTestCommunication;
import dev.bong.view.RechercheApplication;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.Optional;

public class GestionAlerte {
    private static ControlTestCommunication testCommunication = ControlTestCommunication.getInstance();

    public static void genererWarning(String titre, String message){
        testCommunication.fermerCommunication();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static void genererErreur(String titre, String message){
        testCommunication.fermerCommunication();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static void genererInfos(String titre, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static void genererAlertChangeFenetre(String titre, Alert.AlertType type, String message, String destination) throws IOException {
        testCommunication.fermerCommunication();
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if(button == ButtonType.OK){
            RechercheApplication.changerScene(destination);
        }
    }

}
