package dev.bong.control;


import dev.bong.entity.TypeRequete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlRechercheFichier extends Thread {

    //Création d'un controlleur associé à la recherche.
    private static ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_FICHIER);

    //liste de mot clé
    private List<String> nomFicRecherche;
    private List<String> nomFicBan;

    //Mode pour la recherche
    private boolean modeOuvert;

    //Permet d'initialiser la com + les listes de mots clés
    public ControlRechercheFichier(List<String> nomFicRecherche, List<String> nomFicBan, boolean modeOuvert){
        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        this.nomFicBan = nomFicBan;
        this.nomFicRecherche = nomFicRecherche;

        this.modeOuvert = modeOuvert;
    }

    //se lance avec .start() (Tread)
    public void run(){

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

        if (modeOuvert){
            ControlIndexation.IndexationDuModeOuvert();
        }

        // appel des fonctions de recherches
        if (!nomFicRecherche.toString().equals("[]")) resMotCle=rechercheMotCle(nomFicRecherche);
        if (!nomFicBan.toString().equals("[]")) resMotBan=rechercheMotCle(nomFicBan);

        // ajout des recherches a polarité positives et suppression des polarité négatives
        if (nomFicRecherche.toString().equals("[]")){

            //inverser les résultats

        } else {
            resTotal.addAll(resMotCle);
            resTotal.removeAll(resMotBan);
        }



        // affichage du resultat total
        System.out.println("resultat final : \n"+resTotal);

        //envoie resultats


        //delier l'OBSERVER
        controlRequete.removePropertyChangeListener();
        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

    }

    //A partir de la liste de mot clé, Créé une liste de requête (une requête par mot) et l'envoie au moteur
    //Récupère une String des résultats que l'on split en une liste de String (un élément pour un fichier)
    public Set<String> rechercheMotCle(List<String> motCle) {
        List<String> res;
        Set<String> resTotal = new HashSet<>();
        boolean requeteFinit = false;

        //Envoie de la liste au controlleur, qui envoie ensuite au moteur
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
