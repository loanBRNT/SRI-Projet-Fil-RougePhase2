package dev.bong.control;

import dev.bong.entity.Historique;
import dev.bong.entity.TypeRequete;
import dev.bong.view.RechercheApplication;
import dev.bong.view.RechercheController;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Loading screen runnable class
public class ControlRechercheMotCle implements Runnable {

    ProgressIndicator progressIndicator;
    Label loadingText;
    ProgressBar progressBar;
    RechercheController rechercheController;

    //Création d'un controlleur associé à la recherche.
    private ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_MOT_CLE);
    private ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();

    //liste de mot clé
    private List<String> motcle;
    private List<String> motBan;


    public ControlRechercheMotCle(ProgressIndicator progressIndicator, ProgressBar progressBar, Label loadingText, List<String> motcle, List<String> motBan, RechercheController rc) {
        this.progressIndicator = progressIndicator;
        this.progressBar = progressBar;
        this.loadingText = loadingText;
        this.rechercheController = rc;

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
        controlEnvoieResultat.receptionResultat(resTotal);

        //appel de l'historique
        Historique.ecrire(TypeRequete.RECHERCHE_MOT_CLE,"motCle:" + motcle + ",motBan:" + motBan + "resultats:" + resTotal);

        //delier l'OBSERVER
        controlRequete.removePropertyChangeListener();
        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        rechercheController.afficherResultat();

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
