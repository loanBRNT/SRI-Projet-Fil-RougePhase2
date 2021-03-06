package dev.bong.control;

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

public abstract class ControlRecherche {
    protected ProgressIndicator progressIndicator;
    protected ProgressBar progressBar;
    protected RechercheController rechercheController;

    //Création d'un controlleur associé à la recherche.
    protected ControlRequete controlRequete;
    protected ControlEnvoieResultat controlEnvoieResultat = ControlEnvoieResultat.getInstance();
    protected ControlTestCommunication testCommunication = ControlTestCommunication.getInstance();

    protected TypeMoteur typeMoteur;

    protected TypeFichier typeFichier;

    public ControlRecherche(ControlRequete controlRequete,ProgressIndicator progressIndicator, ProgressBar progressBar,TypeMoteur typeMoteur, RechercheController rechercheController) {

        this.typeMoteur = typeMoteur;

        this.controlRequete = controlRequete;
        this.progressIndicator = progressIndicator;
        this.progressBar = progressBar;
        this.rechercheController = rechercheController;
    }

    //A partir de la liste de mot clé, Créé une liste de requête (une requête par mot) et l'envoie au moteur
    //Récupère une String des résultats que l'on split en une liste de String (un élément pour un fichier)
    protected List<Set<String>> recherche(List<String> listeMot,TypeRequete typeRequete,boolean polarite){
        List<String> resBongala, resBingBong;
        Set<String> resFinalBingBong;
        Set<String> resFinalBongala;
        List<Set<String>> listeResultat = new ArrayList<>();

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

        if (typeMoteur == TypeMoteur.INTERSECTION || typeMoteur == TypeMoteur.UNION){
            resBongala=controlRequete.getListeResultat(TypeMoteur.BONGALA.name());
            resBingBong=controlRequete.getListeResultat(TypeMoteur.BINGBONG.name());
            if (typeFichier == TypeFichier.WAV){
                resFinalBongala=triAudio(resBongala);
                resFinalBingBong=triAudio(resBingBong);
            } else {
                resFinalBingBong=calculListeResultats(resBingBong,polarite,typeRequete);
                resFinalBongala=calculListeResultats(resBongala,polarite,typeRequete);;
            }
            listeResultat.add(resFinalBingBong);
            listeResultat.add(resFinalBongala);
        } else if (typeMoteur == TypeMoteur.BONGALA){
            resBongala=controlRequete.getListeResultat(TypeMoteur.BONGALA.name());
            if (typeFichier == TypeFichier.WAV){
                resFinalBongala=triAudio(resBongala);
            } else {
                resFinalBongala=calculListeResultats(resBongala,polarite,typeRequete);
            }
            listeResultat.add(resFinalBongala);
        } else {
            resBingBong=controlRequete.getListeResultat(TypeMoteur.BINGBONG.name());
            if (typeFichier == TypeFichier.WAV){
                resFinalBingBong=triAudio(resBingBong);
            } else {
                resFinalBingBong=calculListeResultats(resBingBong,polarite,typeRequete);
            }
            listeResultat.add(resFinalBingBong);
        }

        progressBar.setProgress(progressBar.getProgress() + 0.1);
        progressIndicator.setProgress(progressIndicator.getProgress() + 0.1);

        return listeResultat;
    }

    protected Set<String> calculListeResultats(List<String> res,boolean polarite,TypeRequete typeRequete){
        Set<String> resTotal = new HashSet<>();
        switch (typeRequete){
            case RECHERCHE_MOT_CLE :
                if(polarite)
                    resTotal=triResPlus(res);
                else
                    resTotal=triResMotCleMoins(res);
                break;
            case RECHERCHE_FICHIER:
                if(polarite)
                    resTotal=triResPlus(res);
                else
                    resTotal=triResFichierMoins(res);
                break;
            case RECHERCHE_COULEUR:
                resTotal=triResPlus(res);
            default:
                break;
        }
        return resTotal;

    }


    protected  Set<String> triResFichierMoins(List<String> res){
        Set<String> resTotal = new HashSet<>();
        for (String listFichierTrouve : res) {
            ArrayList<String> fichiersTrouvee = new ArrayList<>(List.of(listFichierTrouve.split(",")));
            resTotal.addAll(fichiersTrouvee);
        }
        return resTotal;

    }

    protected  Set<String> triResMotCleMoins(List<String> res){
        Set<String> resTotal = new HashSet<>();
        for (String listFichierTrouve : res) {
            ArrayList<String> fichiersTrouvee = new ArrayList<>(List.of(listFichierTrouve.split(",")));
            fichiersTrouvee.remove(0);
            resTotal.addAll(fichiersTrouvee);
        }
        return resTotal;
    }

    protected Set<String> triResPlus(List<String> res) {
        Set<String> resTotal = new HashSet<>();
        for (int i = 0; i < res.size(); i++) {
            ArrayList<String> fichiersTrouvee = new ArrayList<>(List.of(res.get(i).split(",")));
            fichiersTrouvee.remove(0);
            if (i == 0) {
                resTotal.addAll(fichiersTrouvee);
            } else {
                resTotal.retainAll(fichiersTrouvee);
            }
        }
        return resTotal;
    }

    protected Set<String> triAudio(List<String> res){
        Set<String> resTotal = new HashSet<>();
        for (String listFichierTrouve : res) {
            ArrayList<String> fichiersTrouvee = new ArrayList<>(List.of(listFichierTrouve.split(",")));
            fichiersTrouvee.remove(0);
            for (String listeRepSon : fichiersTrouvee){
                ArrayList<String> repSon = new ArrayList<>(List.of(listeRepSon.split("/")));
                resTotal.add(repSon.get(1) + " : " + repSon.get(0));
            }
        }
        return resTotal;
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
