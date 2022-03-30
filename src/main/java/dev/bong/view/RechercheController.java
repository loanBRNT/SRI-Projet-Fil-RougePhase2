package dev.bong.view;

import dev.bong.control.ControlRechercheMotCle;
import dev.bong.entity.GestionAlerte;
import dev.bong.entity.Historique;
import fr.dgac.ivy.IvyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class RechercheController implements Initializable {

    private boolean ResultatTrouvé = false;
    private boolean banWordsButtonActivate = false;
    private boolean banNomsButtonActivate = false;
    ControlRechercheMotCle loadingScreen;

    @FXML
    public Button buttonSearch;
    public TextField textFieldSearch;
    public MenuBar menuBar;
    public ImageView param;
    public Label labelInProgress;
    public Button buttonAgain;
    public ProgressBar progressBar;
    @FXML
    private ToggleButton banWordsButton;
    @FXML
    private ToggleButton banNomsButton;
    @FXML
    private TextField textFieldBanWords;
    @FXML
    private TextField textFieldBanNoms;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button afficheResultat;
    @FXML
    public ChoiceBox choiceType = new ChoiceBox();



    @FXML
    protected void onClickSearch(){
        progressBar.setVisible(true);
        progressIndicator.setVisible(true);
        labelInProgress.setVisible(true);
        buttonAgain.setVisible(true);

        textFieldBanWords.setVisible(false);
        banWordsButton.setVisible(false);
        textFieldSearch.setVisible(false);
        menuBar.setVisible(false);
        param.setVisible(false);
        buttonSearch.setVisible(false);
        afficheResultat.setVisible(false);

        String motcle=textFieldSearch.getText();
        String banWord=textFieldBanWords.getText();

        try {
            loadingScreen = new ControlRechercheMotCle(progressIndicator,progressBar,List.of(motcle.split("/")),List.of(banWord.split("/")),this);
            Thread thread = new Thread(loadingScreen);
            thread.setDaemon(true);
            thread.start();
        } catch (IvyException e) {
            e.printStackTrace();
           // GestionAlerte.genererErreur("Ivy Erreur","La connexion au bus a échouée");
        } catch (Exception e){
            e.printStackTrace();
           // GestionAlerte.genererErreur("Ivy Erreur","Communication avec le(s) moteur(s) impossible");
        }
    }


    @FXML
    protected void onClickParam() throws IOException {
        RechercheApplication.changerScene("parametre.fxml");
    }

    @FXML
    protected void onBanWords(){
        if (!this.banWordsButtonActivate) {
            textFieldBanWords.setVisible(true);
            this.banWordsButtonActivate=true;
            this.banWordsButton.setText("- Ban words");
        }else{
            textFieldBanWords.setVisible(false);
            this.banWordsButtonActivate=false;
            this.banWordsButton.setText("+ Ban words");
        }
    }

    @FXML
    protected void onBanNoms(){
        if (!this.banNomsButtonActivate) {
            textFieldBanNoms.setVisible(true);
            this.banNomsButtonActivate=true;
            this.banNomsButton.setText("- Ban noms");
        }else{
            textFieldBanNoms.setVisible(false);
            this.banNomsButtonActivate=false;
            this.banNomsButton.setText("+ Ban noms");
        }
    }

    @FXML
    protected void onTypeMotCle() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }

    @FXML
    protected void onTypeFichier() throws IOException {
        RechercheApplication.changerScene("rechercheFichier.fxml");
    }

    @FXML
    protected void onTypeImagePalette() throws IOException {
        RechercheApplication.changerScene("paletteCouleur.fxml");
    }

    @FXML
    protected void onLoadingAchieve() throws IOException {
        RechercheApplication.changerScene("resultats.fxml");
    }

    @FXML
    protected void onClean() throws IOException {
        Historique.lire();
        if (Historique.getRecherches().isEmpty() && Historique.getResultats().isEmpty()){
            GestionAlerte.genererInfos("Historique", "L'historique est déja vide");
        }else {
            Historique.effacer();
            Historique.lire();
            GestionAlerte.genererInfos("Historique", "L'historique a bien été nettoyé");
        }
    }


    @FXML
    public void onLoadingFailled() throws IOException {
        ButtonType okBtn = ButtonType.YES;
        ButtonType cancelBtn = ButtonType.NO;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",okBtn,cancelBtn);
        alert.setTitle("Echec");
        alert.setHeaderText("Echec de la recherche");
        alert.setContentText("Voulez-vous relancer la recherche ?");
        alert.setResizable(false);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            restart();
            Thread thread = new Thread(loadingScreen);
            thread.setDaemon(true);
            thread.start();
        }
        else if(result.isPresent() && result.get() == ButtonType.NO){
            RechercheApplication.changerScene("hello-view.fxml");
        }
    }

    @FXML
    void restart() {
        progressIndicator.setProgress(0);
        progressBar.setProgress(0);
    }

    public void afficherResultat(){
        progressBar.setVisible(false);
        progressIndicator.setVisible(false);
        labelInProgress.setVisible(false);
        buttonAgain.setVisible(false);

        afficheResultat.setVisible(true);
    }

    @FXML
    protected void onAffichHistorique() throws IOException {
        RechercheApplication.changerScene("historique.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceType.getItems().add(".xml");
        choiceType.getItems().add(".jpeg");
        choiceType.getItems().add(".wav");
        choiceType.setValue(".xml");
    }
}

