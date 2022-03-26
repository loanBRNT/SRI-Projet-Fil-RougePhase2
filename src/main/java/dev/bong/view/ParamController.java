package dev.bong.view;

import dev.bong.entity.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class ParamController {



    private Config config=Config.getInstance();
    public ToggleGroup Multimoteur;
    public ToggleGroup Mode;
    public ToggleButton ButtonOuvert;
    public ToggleButton ButtonFerme;
    public ToggleButton ButtonMultiModeOn;
    public ToggleButton ButtonMultiModeOff;
    @FXML
    private Label paramText;


    public void initialize(){
        ButtonOuvert.setSelected(config.getMode());
        ButtonFerme.setSelected(!config.getMode());
        ButtonMultiModeOff.setSelected(!config.getMultiMoteur());
        ButtonMultiModeOn.setSelected((config.getMultiMoteur()));
    }





    @FXML
    protected void onBack() throws IOException {
        Parent param = FXMLLoader.load(ParamController.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) paramText.getScene().getWindow();
        thisStage.setTitle("Rechercher");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton retour appuyé");
    }
    @FXML
    protected void onConnexionAdmin() throws IOException {
        Parent param = FXMLLoader.load(ParamAdminController.class.getResource("/layout/admin.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) paramText.getScene().getWindow();
        thisStage.setTitle("Connexion administrateur");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton admin appuyé");
    }


    public void ChoixmodeFerme() {
        ButtonFerme.setSelected(true);
        ButtonOuvert.setSelected(false);
        config.setModeFerme();

    }

    public void ChoixmodeOuvert() {
        ButtonOuvert.setSelected(true);
        ButtonFerme.setSelected(false);
        config.setModeOuvert();
    }

    public void ChoixMoteurOff() {
        ButtonMultiModeOff.setSelected(true);
        ButtonMultiModeOn.setSelected(false);
        config.setMultiMoteurOff();
    }

    public void ChoixMoteurOn() {
        ButtonMultiModeOn.setSelected(true);
        ButtonMultiModeOff.setSelected(false);
        config.setMultiMoteurOn();
    }
}
