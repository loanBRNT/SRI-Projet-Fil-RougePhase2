package dev.bong.view;

import dev.bong.control.ControlEnvoieResultat;
import dev.bong.control.ControlRechercheCouleur;
import dev.bong.control.ControlRechercheFichier;
import dev.bong.control.ControlRechercheMotCle;
import dev.bong.entity.Config;
import dev.bong.entity.GestionAlerte;
import dev.bong.entity.Historique;
import fr.dgac.ivy.IvyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    protected void onClickSearch() throws IOException {
        affichageLancerRecherche();

        try {
            String motcle=textFieldSearch.getText();
            String banWord;
            if (banWordsButtonActivate){
                banWord =textFieldBanWords.getText();
            }else{
                banWord ="";
            }

            if(verifSaisie(motcle,banWord)){
                GestionAlerte.genererAlertChangeFenetre("Saisie incorrecte",Alert.AlertType.ERROR,"La saisie contient des caracteres non autorisés","hello-view.fxml");
            }else {
                loadingScreenMc = new ControlRechercheMotCle(progressIndicator,progressBar,List.of(motcle.split("/")),List.of(banWord.split("/")),config.getTypeMoteur(),this);
                Thread thread = new Thread(loadingScreenMc);
                thread.setDaemon(true);
                thread.start();
            }

        } catch (IvyException e) {
            e.printStackTrace();
           GestionAlerte.genererAlertChangeFenetre("Ivy Erreur",Alert.AlertType.ERROR,"La connexion au bus a échouée","hello-view.fxml");
        } catch (Exception e){
            e.printStackTrace();
           GestionAlerte.genererAlertChangeFenetre("Ivy Erreur",Alert.AlertType.ERROR,"Communication avec le(s) moteur(s) impossible","hello-view.fxml");
        }

    }

    @FXML
    protected void onClickFicSearch() throws IOException {
        affichageLancerRecherche();

        try {
            String motcle=textFieldSearch.getText();
            String banWord;
            if (banWordsButtonActivate){
                banWord =textFieldBanWords.getText();
            }else{
                banWord ="";
            }

            loadingScreenFic = new ControlRechercheFichier(progressIndicator, progressBar, List.of(motcle.split("/")), List.of(banWord.split("/")), (String) choiceType.getValue(), Config.getInstance().getMode(), config.getTypeMoteur(), this);
            Thread thread = new Thread(loadingScreenFic);
            thread.setDaemon(true);
            thread.start();


        } catch (IvyException e) {
            e.printStackTrace();
           GestionAlerte.genererAlertChangeFenetre("Ivy Erreur",Alert.AlertType.ERROR,"La connexion au bus a échouée","rechercheFichier.fxml");
        } catch (Exception e){
            e.printStackTrace();
           GestionAlerte.genererAlertChangeFenetre("Ivy Erreur",Alert.AlertType.ERROR,"Communication avec le(s) moteur(s) impossible","rechercheFichier.fxml");
        }
    }

    @FXML
    protected void onClickColorSearch() throws IOException  {
        progressBar.setVisible(true);
        progressIndicator.setVisible(true);
        labelInProgress.setVisible(true);

        buttonSearch.setVisible(false);
        listeCouleur.setVisible(false);


        try {
            loadingScreenColor = new ControlRechercheCouleur(progressIndicator,progressBar,config.getTypeMoteur(),listeCouleur.getValue(),this);
            Thread thread = new Thread(loadingScreenColor);
            thread.setDaemon(true);
            thread.start();
        } catch (IvyException e) {
            e.printStackTrace();
            GestionAlerte.genererAlertChangeFenetre("Ivy Erreur",Alert.AlertType.ERROR,"La connexion au bus a échouée","paletteCouleur.fxml");
        } catch (Exception e){
            e.printStackTrace();
            GestionAlerte.genererAlertChangeFenetre("Ivy Erreur",Alert.AlertType.ERROR,"Communication avec le(s) moteur(s) impossible","paletteCouleur.fxml");
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
            this.banWordsButton.setText("- Mots ban");
        }else{
            textFieldBanWords.setVisible(false);
            this.banWordsButtonActivate=false;
            this.banWordsButton.setText("+ Mots ban");
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
        ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();
        ArrayList<String> listeResultat = (ArrayList<String>) controlEnvoieResultat.getResultat();
        if (listeResultat.isEmpty() || listeResultat.toString().equals("[ ]")) {
            try {
                GestionAlerte.genererAlertChangeFenetre("Resultats", Alert.AlertType.ERROR, "La recherche n'a aboutit à aucun résultat", "hello-view.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            RechercheApplication.changerScene("resultats.fxml");
        }
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


    public void afficherResultat(){
        progressBar.setVisible(false);
        progressIndicator.setVisible(false);
        labelInProgress.setVisible(false);

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

    @FXML
    public void onEnterMotCle(ActionEvent ae) throws IOException {
        onClickSearch();
    }
    @FXML
    public void onEnterFic(ActionEvent ae) throws IOException {
        onClickFicSearch();
    }

    public boolean verifSaisie(String motcle ,String banword){
        CharSequence[] banChar={"$","£","@","#","&","?","§","*","¤","+","=","{","}","(",")","~","°", "\"","'"};
        CharSequence[] seqVoyelle={"a","e","i","o","u","y","A","E","Y","O","U","I"};
        boolean erreur =false;

        for(CharSequence caractere : banChar){
            if(motcle.contains(caractere) || banword.contains(caractere))
                erreur=true;
        }

        if (!erreur){
            erreur = true;
            for (CharSequence voyelle : seqVoyelle){
                if(motcle.contains(voyelle) || banword.contains(voyelle))
                    erreur=false;
            }
        }

        return erreur;

    }

}

