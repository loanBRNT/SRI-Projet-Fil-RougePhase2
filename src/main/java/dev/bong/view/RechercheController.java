package dev.bong.view;

import dev.bong.control.ControlRechercheCouleur;
import dev.bong.control.ControlRechercheFichier;
import dev.bong.control.ControlRechercheMotCle;
import dev.bong.entity.Config;
import dev.bong.entity.GestionAlerte;
import dev.bong.entity.Historique;
import fr.dgac.ivy.IvyException;
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

    public ColorPicker listeCouleur;
    private boolean ResultatTrouvé = false;
    private boolean banWordsButtonActivate = false;
    private boolean banNomsButtonActivate = false;

    private Config config = Config.getInstance();

    public ControlRechercheMotCle loadingScreenMc;
    public ControlRechercheFichier loadingScreenFic;
    public ControlRechercheCouleur loadingScreenColor;

    @FXML
    public Button buttonSearch;
    @FXML
    public TextField textFieldSearch;
    @FXML
    public MenuBar menuBar;
    @FXML
    public ImageView param;
    @FXML
    public Label labelInProgress;
    @FXML
    public Button buttonAgain;
    @FXML
    public ProgressBar progressBar;
    @FXML
    private ToggleButton banWordsButton;
    @FXML
    private TextField textFieldBanWords;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button afficheResultat;
    @FXML
    public ChoiceBox choiceType = new ChoiceBox();


    private void affichageLancerRecherche(){
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

        try {
            choiceType.setVisible(false);
        } catch (Exception e){}

    }

    @FXML
    protected void onClickSearch(){
        affichageLancerRecherche();

        try {
            String motcle=textFieldSearch.getText();
            String banWord=textFieldBanWords.getText();

            loadingScreenMc = new ControlRechercheMotCle(progressIndicator,progressBar,List.of(motcle.split("/")),List.of(banWord.split("/")),config.getTypeMoteur(),this);
            Thread thread = new Thread(loadingScreenMc);
            thread.setDaemon(true);
            thread.start();
        } catch (IvyException e) {
            e.printStackTrace();
           GestionAlerte.genererErreur("Ivy Erreur","La connexion au bus a échouée");
        } catch (Exception e){
            e.printStackTrace();
           GestionAlerte.genererErreur("Ivy Erreur","Communication avec le(s) moteur(s) impossible");
        }
    }

    @FXML
    protected void onClickFicSearch(){
        affichageLancerRecherche();

        String motcle=textFieldSearch.getText();
        String banWord=textFieldBanWords.getText();

        try {
            loadingScreenFic = new ControlRechercheFichier(progressIndicator,progressBar,List.of(motcle.split("/")),List.of(banWord.split("/")),(String) choiceType.getValue(), Config.getInstance().getMode(),config.getTypeMoteur(),this);
            Thread thread = new Thread(loadingScreenFic);
            thread.setDaemon(true);
            thread.start();
        } catch (IvyException e) {
            e.printStackTrace();
           GestionAlerte.genererErreur("Ivy Erreur","La connexion au bus a échouée");
        } catch (Exception e){
            e.printStackTrace();
           GestionAlerte.genererErreur("Ivy Erreur","Communication avec le(s) moteur(s) impossible");
        }
    }

    @FXML
    protected void onClickColorSearch() throws IOException  {
        affichageLancerRecherche();

        try {
            loadingScreenColor = new ControlRechercheCouleur(progressIndicator,progressBar,config.getTypeMoteur(),listeCouleur.getValue(),this);
            Thread thread = new Thread(loadingScreenFic);
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
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
        if (Historique.getListeDeListe().isEmpty()){
            GestionAlerte.genererInfos("Historique", "L'historique est déja vide");
        }else {
            Historique.effacer();
            Historique.lire();
            GestionAlerte.genererInfos("Historique", "L'historique a bien été nettoyé");
        }
    }


    @FXML
    public void onLoadingFailled() throws Exception {
        String motcle=textFieldSearch.getText();
        String banWord=textFieldBanWords.getText();

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
            /*
            ControlRechercheMotCle loadingScreen = new ControlRechercheMotCle(progressIndicator,progressBar,List.of(motcle.split("/")),List.of(banWord.split("/")),this);
            Thread thread = new Thread(loadingScreen);
            thread.setDaemon(true);
            thread.start();
             */
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
        choiceType.getItems().add(".jpg");
        choiceType.getItems().add(".bmp");
        choiceType.getItems().add(".wav");
        choiceType.setValue(".xml");
    }

}

