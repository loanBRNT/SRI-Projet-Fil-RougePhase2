package dev.bong.view;

import dev.bong.entity.Config;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.io.IOException;

public class ParamController {

    public ChoiceBox choiceMoteur;
    private Config config=Config.getInstance();
    public ToggleGroup Mode;
    public ToggleButton ButtonOuvert;
    public ToggleButton ButtonFerme;

    @FXML
    private Label paramText;


    public void initialize(){
        ButtonOuvert.setSelected(config.getMode());
        ButtonFerme.setSelected(!config.getMode());
        choiceMoteur.getItems().add("BONGALA");
        choiceMoteur.getItems().add("BINGBONG");
        choiceMoteur.getItems().add("INTERSECTION");
        choiceMoteur.getItems().add("UNION");
        choiceMoteur.setValue(config.getTypeMoteur().name());
    }

    @FXML
    protected void onBack() throws IOException {
        config.setTypeMoteur((String)choiceMoteur.getValue());
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

}
