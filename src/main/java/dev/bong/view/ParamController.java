package dev.bong.view;

import dev.bong.entity.Config;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;


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
        RechercheApplication.changerScene("hello-view.fxml");
    }
    @FXML
    protected void onConnexionAdmin() throws IOException {
        RechercheApplication.changerScene("admin.fxml");
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
