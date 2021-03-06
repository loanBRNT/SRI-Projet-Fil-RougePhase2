package dev.bong.control;


import dev.bong.entity.*;
import dev.bong.view.RechercheController;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlRechercheFichier extends ControlRecherche implements Runnable {

    //liste de mot clé
    private List<String> nomFicRecherche = new ArrayList<>();
    private List<String> nomFicBan = new ArrayList<>();

    //Mode pour la recherche
    private boolean modeOuvert;

    //Permet d'initialiser la com + les listes de mots clés
    public ControlRechercheFichier(ProgressIndicator progressIndicator, ProgressBar progressBar,
                                   List<String> nomFicRecherche, List<String> nomFicBan, String extension,
                                   boolean modeOuvert, TypeMoteur typeMoteur, RechercheController rechercheController) throws Exception {
        super(new ControlRequete(TypeRequete.RECHERCHE_FICHIER),progressIndicator,progressBar, typeMoteur,rechercheController);

        this.typeFichier = TypeFichier.stringToEnum(extension);
        this.modeOuvert = modeOuvert;

        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        //Envoie au control resultat des donnees
        controlEnvoieResultat.receptionType(typeFichier);

        //Laisse le temps à la communication de s'établir entre tous les agents
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //On test la connexion au bus et aux moteurs
        testCommunication.testerCommunication();

        //On initialise les listes pour la recherche
        this.ajoutExtension(extension,nomFicRecherche,nomFicBan);
    }

    //se lance avec .start() (Tread)
    public void run(){

        // creation des set servant a recuperer les resultats des recherches
        List<Set<String>> resMotCle = new ArrayList<>();
        List<Set<String>> resMotBan = new ArrayList<>();
        List<String> resTotal = new ArrayList<>();

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);


        if (modeOuvert){
            System.out.println("Lancement du Protocole d'indexation du Mode ouvert");
            ControlIndexation.IndexationDuModeOuvert();
        }

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // appel des fonctions de recherches
        if (!nomFicRecherche.toString().equals("[]")) {
            resMotCle=recherche(nomFicRecherche,TypeRequete.RECHERCHE_FICHIER,true);
        }
        else {
            progressBar.setProgress(progressBar.getProgress() + 0.3);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
        }
        if (!nomFicBan.toString().equals("[]")) resMotBan=recherche(nomFicBan,TypeRequete.RECHERCHE_FICHIER,false);
        else {
            progressBar.setProgress(progressBar.getProgress() + 0.3);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.3);
        }

        // ajout des recherches a polarité positives et suppression des polarité négatives
        if (nomFicRecherche.toString().equals("[]")){
            try {
                resTotal.addAll(Database.getListeFichier(TypeFichier.enumToString(typeFichier)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            resTotal.removeAll(resMotBan);

        } else {
            if (!nomFicBan.toString().equals("[]")){
                resTotal=sommeCleEtBan(resMotCle,resMotBan);
            }
            else{
                resTotal=sommeCleSansBan(resMotCle);
            }

        }

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        //envoie resultats
        controlEnvoieResultat.receptionResultat(resTotal);

        //appel de l'historique
        Historique.ecrire(TypeRequete.RECHERCHE_FICHIER,"motCle : " + nomFicRecherche + ",motBan : " + nomFicBan + ";" + resTotal);


        //delier l'OBSERVER
        controlRequete.removePropertyChangeListener();
        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        rechercheController.afficherResultat();
    }

    public void ajoutExtension(String extension, List<String> listeMc, List<String> listeBan){

        if (!listeMc.toString().equals("[]")) {
            for (String mot : listeMc){
                this.nomFicRecherche.add(mot + extension);
            }
        }

        if (!listeBan.toString().equals("[]")){
            for (String mot : listeBan){
                this.nomFicBan.add(mot+extension);
            }
        }


    }

    protected List<String> sommeCleEtBan(List<Set<String>> resMotCle,List<Set<String>> resMotBan){
        Set<String> moteur1 = new HashSet<>();
        Set<String> moteur2 = new HashSet<>();
        Set<String> resTotal = new HashSet<>();
        List<String> resTotalList = new ArrayList<>();
        if(this.typeMoteur==TypeMoteur.BINGBONG || this.typeMoteur==TypeMoteur.BONGALA){
            if (typeFichier == TypeFichier.WAV){
                resTotal = compareAudio(resTotal,resMotCle.get(0),resMotBan.get(0));
            } else {
                resTotal.addAll(resMotCle.get(0));
                resTotal.removeAll(resMotBan.get(0));
            }
        }
        else {
            if (typeFichier == TypeFichier.WAV){
                resTotal = compareAudio(resTotal,resMotCle.get(0),resMotBan.get(0));
            } else {
                moteur1.addAll(resMotCle.get(0));
                moteur1.removeAll(resMotBan.get(0));
            }
            if (typeFichier == TypeFichier.WAV){
                moteur2 = compareAudio(moteur2,resMotCle.get(1),resMotBan.get(1));
            } else {
                moteur2.addAll(resMotCle.get(1));
                moteur2.removeAll(resMotBan.get(1));
            }
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

    protected Set<String> compareAudio(Set<String> resTotal,Set<String> resRec, Set<String> resBan){

        for (String fichierRecherche : resRec){
            for (String fichierBan : resBan){
                System.out.println(List.of(fichierRecherche.split(" : ")).get(0) + " | " + List.of(fichierBan.split(" : ")).get(0));
                if (!List.of(fichierRecherche.split(" : ")).get(0).equals(List.of(fichierBan.split(" : ")).get(0))){
                    resTotal.add(fichierRecherche);
                }
            }
        }
        return resTotal;
    }




}
