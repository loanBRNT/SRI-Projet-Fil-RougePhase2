package dev.bong.control;

import dev.bong.entity.TestCommunication;
import dev.bong.entity.TypeMoteur;
import dev.bong.entity.TypeRequete;
import dev.bong.view.RechercheController;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ControlRecherche {
    protected ProgressIndicator progressIndicator;
    protected ProgressBar progressBar;
    protected RechercheController rechercheController;

    //Création d'un controlleur associé à la recherche.
    protected ControlRequete controlRequete;
    protected ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();
    protected TestCommunication testCommunication = TestCommunication.getInstance();

    protected TypeMoteur typeMoteur;

    public ControlRecherche(ControlRequete controlRequete,ProgressIndicator progressIndicator, ProgressBar progressBar,TypeMoteur typeMoteur, RechercheController rechercheController) {

        this.typeMoteur = typeMoteur;

        this.controlRequete = controlRequete;
        this.progressIndicator = progressIndicator;
        this.progressBar = progressBar;
        this.rechercheController = rechercheController;
    }

    //A partir de la liste de mot clé, Créé une liste de requête (une requête par mot) et l'envoie au moteur
    //Récupère une String des résultats que l'on split en une liste de String (un élément pour un fichier)
    protected Set<String> recherche(List<String> listeMot){
        List<String> res;
        Set<String> resTotal = new HashSet<>();

        boolean requeteFinit = false;
        int nbRequete = listeMot.size(), nbRequeteFinit=0;
        double pourcentage;

        controlRequete.initRequete(); //reset à 0

        //Envoie de la liste au controlleur, qui envoie ensuite au moteur
        if (typeMoteur == TypeMoteur.INTERSECTION || typeMoteur == TypeMoteur.UNION){
            controlRequete.creerEtenvoyerListeRequete(listeMot,TypeMoteur.BONGALA.name());
            controlRequete.creerEtenvoyerListeRequete(listeMot,TypeMoteur.BINGBONG.name());
        } else {
            controlRequete.creerEtenvoyerListeRequete(listeMot,typeMoteur.name());
        }

        while (!requeteFinit){
            try {
                pourcentage = nbRequeteFinit * 0.2 / nbRequete;
                progressBar.setProgress(progressBar.getProgress() + pourcentage);
                progressIndicator.setProgress(progressIndicator.getProgress() + pourcentage);
                nbRequeteFinit = controlRequete.getNbRequeteFinit();
                requeteFinit = controlRequete.touteRequeteFinit();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
