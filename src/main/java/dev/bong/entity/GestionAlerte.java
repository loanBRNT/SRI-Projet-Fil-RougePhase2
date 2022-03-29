package dev.bong.entity;

import javafx.scene.control.Alert;

public class GestionAlerte {
    private static TestCommunication testCommunication = TestCommunication.getInstance();

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

}
