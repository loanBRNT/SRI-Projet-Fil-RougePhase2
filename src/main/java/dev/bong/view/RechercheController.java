package dev.bong.view;

import dev.bong.control.ControlRechercheMotCle;
import dev.bong.entity.GestionErreurs;
import fr.dgac.ivy.IvyException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.*;

public class RechercheController {

    private boolean ResultatTrouvé = false;
    private boolean banWordsButtonActivate = false;
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

        try {
            loadingScreen = new ControlRechercheMotCle(progressIndicator,progressBar,List.of(motcle.split("/")),List.of(banWord.split("/")),this);
            Thread thread = new Thread(loadingScreen);
            thread.setDaemon(true);
            thread.start();
        } catch (IvyException e) {
            e.printStackTrace();
            GestionErreurs.genererWarning("Ivy Erreur","La connexion au bus a échouée");
        } catch (Exception e){
            e.printStackTrace();
            GestionErreurs.genererWarning("Ivy Erreur","Communication avec le(s) moteur(s) impossible");
        }
    }


    @FXML
    protected void onClickParam() throws IOException {
        RechercheApplication.changerScene("parametre.fxml");
        System.out.println("Boutton paramètre appuyé");
    }


    @FXML
    protected void onBanWords(){
        if (!this.banWordsButtonActivate) {
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
}
