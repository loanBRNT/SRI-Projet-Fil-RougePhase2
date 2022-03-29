package dev.bong.entity;

import javafx.scene.control.Alert;

public class GestionErreurs {
    private static TestCommunication testCommunication = TestCommunication.getInstance();

    public static void genererWarning(String titre, String message){
        testCommunication.fermerCommunication();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}
