package dev.bong.control;

import dev.bong.entity.Requete;
import dev.bong.entity.TypeRequete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlRechercheMotCle extends Thread {
    private ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_MOT_CLE);
    private List<String> motcle;
    private List<String> motBan;

    public void initRecherche(List<String> motcle, List<String> motBan){
        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        this.motBan = motBan;
        this.motcle = motcle;
    }

    public void run(){
        if (motcle == null || motBan == null){
            this.interrupt();
        }

        // creation des set servant a recuperer les resultats des recherches
        Set<String> resMotCle = new HashSet<>();
        Set<String> resMotBan = new HashSet<>();
        Set<String> resTotal = new HashSet<>();

        //Laisse le temps à la communication de s'établir entre tous les agents
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // appel des fonctions de recherches
        if (!motcle.toString().equals("[]")) resMotCle=rechercheMotCle(motcle);
        if (!motBan.toString().equals("[]")) resMotBan=rechercheMotCle(motBan);

        //System.out.println("mot cle : " + resMotCle + "\nmot ban : " + resMotBan);

        // ajout des recherches a polarité positives et suppression des polarité négatives
        resTotal.addAll(resMotCle);
        resTotal.removeAll(resMotBan);

        // affichage du resultat total
        System.out.println("resultat final : \n"+resTotal);

        //envoie resultats

        //delier l'OBSERVER
        controlRequete.removePropertyChangeListener();
        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();


    }

    public Set<String> rechercheMotCle(List<String> motCle) {
        List<String> res;
        Set<String> resTotal = new HashSet<>();
        boolean requeteFinit = false;

        controlRequete.creerEtenvoyerListeRequete(motCle);

        while (!requeteFinit){
            try {
                requeteFinit = controlRequete.touteRequeteFinit();
                sleep(1000);
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

        return resTotal;
    }


}
