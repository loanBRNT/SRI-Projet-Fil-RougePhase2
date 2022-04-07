package dev.bong.control;

import dev.bong.entity.Historique;
import dev.bong.entity.TypeFichier;
import dev.bong.entity.TypeMoteur;
import dev.bong.entity.TypeRequete;
import dev.bong.view.RechercheController;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Loading screen runnable class
public class ControlRechercheMotCle extends ControlRecherche implements Runnable {

    //liste de mot clé
    private List<String> motcle;
    private List<String> motBan;

    public ControlRechercheMotCle(ProgressIndicator progressIndicator, ProgressBar progressBar,List<String> motcle, List<String> motBan,TypeMoteur typeMoteur, RechercheController rc) throws Exception {
        super(new ControlRequete(TypeRequete.RECHERCHE_MOT_CLE),progressIndicator,progressBar,typeMoteur,rc);

        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        controlEnvoieResultat.receptionType(TypeFichier.XML);

        //Laisse le temps à la communication de s'établir entre tous les agents
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testCommunication.testerCommunication();

        this.motBan = motBan;
        this.motcle = motcle;
    }

    //se lance avec .start() (Tread)
    @Override
    public void run(){
        // creation des set servant a recuperer les resultats des recherches
        List<Set<String>> resMotCle = new ArrayList<>();
        List<Set<String>> resMotBan = new ArrayList<>();
        List<String> resTotal = new ArrayList<>();

        progressBar.setProgress(progressBar.getProgress() + 0.2);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.2);

        // appel des fonctions de recherches
        if (!motcle.toString().equals("[]")) resMotCle=recherche(motcle,TypeRequete.RECHERCHE_MOT_CLE,true);
        else {
            progressBar.setProgress(progressBar.getProgress() + 0.3);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
        }
        if (!motBan.toString().equals("[]")) resMotBan=recherche(motBan,TypeRequete.RECHERCHE_MOT_CLE,false);
        else {
            progressBar.setProgress(progressBar.getProgress() + 0.3);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
        }


        // creation des resultats a partir des listes de resultats de chaque moteur et de la polarité
        if (!motBan.toString().equals("[]")) {
            resTotal = sommeCleEtBan(resMotCle, resMotBan);
        }
        else {
            resTotal = sommeCleSansBan(resMotCle);
        }


        // affichage du resultat total
        System.out.println("resultat final : \n"+resTotal);

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        //envoie resultats
        controlEnvoieResultat.receptionResultat(resTotal);

        //appel de l'historique
        Historique.ecrire(TypeRequete.RECHERCHE_MOT_CLE,"motCle : " + motcle + ",motBan : " + motBan + ";" + resTotal);

        //delier l'OBSERVER
        controlRequete.removePropertyChangeListener();
        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        rechercheController.afficherResultat();

    }

    protected List<String> sommeCleEtBan(List<Set<String>> resMotCle,List<Set<String>> resMotBan){
        List<String> moteur1 = new ArrayList<>();
        List<String> moteur2 = new ArrayList<>();
        Set<String> resTotal = new HashSet<>();
        List<String> resTotalList = new ArrayList<>();
        if(this.typeMoteur==TypeMoteur.BINGBONG || this.typeMoteur==TypeMoteur.BONGALA){
            resTotal.addAll(resMotCle.get(0));
            resTotal.removeAll(resMotBan.get(0));
        }
        else {
            moteur1.addAll(resMotCle.get(0));
            moteur1.removeAll(resMotBan.get(0));
            moteur2.addAll(resMotCle.get(1));
            moteur2.removeAll(resMotBan.get(1));
            if (this.typeMoteur==TypeMoteur.UNION){
                resTotal.addAll(moteur1);
                resTotal.addAll(moteur2);

            }
            else {
                resTotal.addAll(moteur1);
                resTotal.retainAll(moteur2);
            }

            }
        resTotalList.addAll(resTotal);
        return  resTotalList;

    }
    protected List<String> sommeCleSansBan(List<Set<String>> resMotCle){
        List<String> moteur1 = new ArrayList<>();
        List<String> moteur2 = new ArrayList<>();
        Set<String> resTotal = new HashSet<>();
        List<String> resTotalList = new ArrayList<>();
        if(this.typeMoteur==TypeMoteur.BINGBONG || this.typeMoteur==TypeMoteur.BONGALA){
            resTotal.addAll(resMotCle.get(0));
        }
        else {
            moteur1.addAll(resMotCle.get(0));
            moteur2.addAll(resMotCle.get(1));
            if (this.typeMoteur==TypeMoteur.UNION){
                resTotal.addAll(moteur1);
                resTotal.addAll(moteur2);
            }
            else {
                resTotal.addAll(moteur1);
                resTotal.retainAll(moteur2);
            }
        }
        resTotalList.addAll(resTotal);
        return  resTotalList;

    }

}
