package dev.bong.view;

import dev.bong.control.ControlEnvoieResultat;
import dev.bong.control.ControlRequete;
import dev.bong.entity.Historique;
import dev.bong.entity.TypeRequete;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RechercheController implements Initializable {

    private boolean ResultatTrouvé = false;
    private boolean banWordsButtonActivate = false;
    LoadingScreen loadingScreen;

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
    private Label welcomeText;

    @FXML
    private ToggleButton banButton;

    @FXML
    private TextField textFieldBanWords;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button afficheResultat;



    @FXML
    protected void onClickSearch(){
        progressBar.setVisible(true);
        progressIndicator.setVisible(true);
        labelInProgress.setVisible(true);
        buttonAgain.setVisible(true);
        textFieldBanWords.setVisible(false);
        banButton.setVisible(false);
        textFieldSearch.setVisible(false);
        menuBar.setVisible(false);
        param.setVisible(false);
        buttonSearch.setVisible(false);
        afficheResultat.setVisible(false);

        String motcle=textFieldSearch.getText();
        String banWord=textFieldBanWords.getText();
        loadingScreen.initialisation(List.of(motcle.split("/")),List.of(banWord.split("/")));

        Thread thread = new Thread(loadingScreen);
        thread.setDaemon(true);
        thread.start();
    }
    @FXML
    protected void onClickParam() throws IOException {
        RechercheApplication.changerScene("parametre.fxml");
        System.out.println("Boutton paramètre appuyé");
    }
    @FXML
    protected void onBanWords() throws IOException {
        if (this.banWordsButtonActivate ==  false) {
            textFieldBanWords.setVisible(true);
            this.banWordsButtonActivate=true;
            this.banButton.setText("- Ban words");
        }else{
            textFieldBanWords.setVisible(false);
            this.banWordsButtonActivate=false;
            this.banButton.setText("+ Ban words");
        }
    }

    @FXML
    protected void onTypeText() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }

    @FXML
    protected void onTypeImageRef() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }

    @FXML
    protected void onTypeImagePalette() throws IOException {
        RechercheApplication.changerScene("paletteCouleur.fxml");
    }

    @FXML
    protected void onTypeAudio() throws IOException {
        RechercheApplication.changerScene("hello-view.fxml");
    }

    @FXML
    protected void onLoadingAchieve() throws IOException {
        RechercheApplication.changerScene("resultats.fxml");
        System.out.println("Recherche terminée");
    }

    @FXML
    protected void onLoadingFailled() throws IOException {
        ButtonType okBtn = ButtonType.YES;
        ButtonType cancelBtn = ButtonType.NO;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",okBtn,cancelBtn);
        alert.setTitle("Echec");
        alert.setHeaderText("Echec de la recherche");
        alert.setContentText("Voulez-vous relancer la recherche ?");
        alert.setResizable(false);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            System.out.println("OUI");
            //Mettre l'appel a nouveau de la fonction recherche
            restart();
            Thread thread = new Thread(loadingScreen);
            thread.setDaemon(true);
            thread.start();
        }
        else if(result.isPresent() && result.get() == ButtonType.NO){
            RechercheApplication.changerScene("hello-view.fxml");
            System.out.println("NON");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadingScreen = new LoadingScreen(progressIndicator,progressBar, labelInProgress);
    }

    @FXML
    void restart(/*ActionEvent event*/) {
        progressIndicator.setProgress(0);
        progressBar.setProgress(0);
    }

    void afficherResultat(){
        progressBar.setVisible(false);
        progressIndicator.setVisible(false);
        labelInProgress.setVisible(false);
        buttonAgain.setVisible(false);

        afficheResultat.setVisible(true);
    }

    //Loading screen runnable class
    public class LoadingScreen implements Runnable {

        ProgressIndicator progressIndicator;
        Label loadingText;
        ProgressBar progressBar;

        //Création d'un controlleur associé à la recherche.
        private ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_MOT_CLE);

        private ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();

        //liste de mot clé
        private List<String> motcle;
        private List<String> motBan;


        public LoadingScreen(ProgressIndicator progressIndicator,ProgressBar progressBar, Label loadingText) {
            this.progressIndicator = progressIndicator;
            this.progressBar = progressBar;
            this.loadingText = loadingText;
        }

        public void initialisation(List<String> motcle, List<String> motBan){
            //LANCER LA COM
            controlRequete.lancerCommunicationBus();

            this.motBan = motBan;
            this.motcle = motcle;
        }

        //se lance avec .start() (Tread)
        @Override
        public void run(){

            // creation des set servant a recuperer les resultats des recherches
            Set<String> resMotCle = new HashSet<>();
            Set<String> resMotBan = new HashSet<>();
            Set<String> resTotal = new HashSet<>();

            //Laisse le temps à la communication de s'établir entre tous les agents
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            progressBar.setProgress(progressBar.getProgress() + 0.2);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.2);

            // appel des fonctions de recherches
            if (!motcle.toString().equals("[]")) resMotCle=rechercheMotCle(motcle);
            else {
                progressBar.setProgress(progressBar.getProgress() + 0.3);
                progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
            }
            if (!motBan.toString().equals("[]")) resMotBan=rechercheMotCle(motBan);
            else {
                progressBar.setProgress(progressBar.getProgress() + 0.3);
                progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
            }

            //System.out.println("mot cle : " + resMotCle + "\nmot ban : " + resMotBan);

            // ajout des recherches a polarité positives et suppression des polarité négatives
            resTotal.addAll(resMotCle);
            resTotal.removeAll(resMotBan);

            // affichage du resultat total
            System.out.println("resultat final : \n"+resTotal);

            progressBar.setProgress(progressBar.getProgress() + 0.1);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

            //envoie resultats
            //controlEnvoieResultat.receptionResultat(resTotal);
            //RechercheApplication.stage.show();

            //appel de l'historique
            Historique.ecrire(TypeRequete.RECHERCHE_MOT_CLE,"motCle:" + motcle + ",motBan:" + motBan + "resultats:" + resTotal);

            //delier l'OBSERVER
            controlRequete.removePropertyChangeListener();
            //STOPPER LA COM
            controlRequete.fermerCommunicationBus();

            progressBar.setProgress(progressBar.getProgress() + 0.1);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

            afficherResultat();

        }

        //A partir de la liste de mot clé, Créé une liste de requête (une requête par mot) et l'envoie au moteur
        //Récupère une String des résultats que l'on split en une liste de String (un élément pour un fichier)
        public Set<String> rechercheMotCle(List<String> motCle) {
            List<String> res;
            Set<String> resTotal = new HashSet<>();
            boolean requeteFinit = false;

            //Envoie de la liste au controlleur, qui envoie ensuite au moteur
            controlRequete.creerEtenvoyerListeRequete(motCle);

            progressBar.setProgress(progressBar.getProgress() + 0.1);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

            while (!requeteFinit){
                try {
                    requeteFinit = controlRequete.touteRequeteFinit();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            progressBar.setProgress(progressBar.getProgress() + 0.1);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

            res=controlRequete.getListeResultat();

            for (String resultatRecherche : res) {
                ArrayList<String> fichiersTrouvee = new ArrayList<>(List.of(resultatRecherche.split(",")));
                fichiersTrouvee.remove(0);
                resTotal.addAll(fichiersTrouvee);
            }

            progressBar.setProgress(progressBar.getProgress() + 0.1);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

            return resTotal;
        }
    }
}
