package dev.bong.control;

import dev.bong.entity.Historique;
import dev.bong.entity.TypeMoteur;
import dev.bong.entity.TypeRequete;
import dev.bong.view.RechercheController;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ControlRechercheCouleur extends ControlRecherche implements Runnable{
    private Color couleur;

    public ControlRechercheCouleur(ProgressIndicator progressIndicator, ProgressBar progressBar, TypeMoteur typeMoteur, Color couleur, RechercheController rechercheController) throws Exception {

        super(new ControlRequete(TypeRequete.RECHERCHE_COULEUR), progressIndicator, progressBar, typeMoteur, rechercheController);

        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        //Laisse le temps à la communication de s'établir entre tous les agents
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testCommunication.testerCommunication();

        this.couleur = couleur;

    }

    @Override
    public void run(){
        List<String> listeCouleur = new ArrayList<>();
        listeCouleur.add(couleur.toString());

        System.out.println(couleur.toString());

        progressBar.setProgress(progressBar.getProgress() + 0.3);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);

        System.out.println(listeCouleur);

        Set<String> resTotal  = recherche(listeCouleur,TypeRequete.RECHERCHE_COULEUR,true);

        progressBar.setProgress(progressBar.getProgress() + 0.2);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.2);

        //envoie resultats
        controlEnvoieResultat.receptionResultat(resTotal);

        //appel de l'historique
        Historique.ecrire(TypeRequete.RECHERCHE_MOT_CLE,"motCle : " + couleur.toString() + ",motBan : " + "[]" + ";" + resTotal);

        //delier l'OBSERVER
        controlRequete.removePropertyChangeListener();
        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        progressBar.setProgress(progressBar.getProgress() + 0.2);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.2);

        rechercheController.afficherResultat();


    }
}
