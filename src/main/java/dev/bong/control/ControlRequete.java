package dev.bong.control;

import dev.bong.entity.*;
import fr.dgac.ivy.IvyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ControlRequete implements PropertyChangeListener{
    //Lien vers le singleton de communication Ivy
    private CommunicationIvy communicationIvy = CommunicationIvy.getInstance();

    //Type de requête associé à ce controlleur
    private TypeRequete typeRequete;

    //Liste de données
    private List<Requete> listeRequete = new ArrayList<>();

    private List<String> listeResultatBongala = new ArrayList<>();
    private List<String> listeResultatBingBong = new ArrayList<>();

    //Compteur de retour
    private int nbRequeteFinit = 0;


    public ControlRequete(TypeRequete typeRequete){
        this.typeRequete = typeRequete;

        //Abonnement du controlleur à l'écoute de l'arrivée de message sur le bus Ivy {PATTERN OBSERVER}
        communicationIvy.addPropertyChangeListener(ListenerPropriete.RESULTAT.toString(),this);
    }

    /* ------------------- FONCTIONS GENERALES ------------------ */

    //Connexion de notre Agent (Communication ivy)
    public void lancerCommunicationBus() throws IvyException {
        communicationIvy.lancerCommunication();
    }

    //Déconnexion de l'agent
    public void fermerCommunicationBus(){
        communicationIvy.fermerCommunication();
    }

    //A partir de la liste en param,
    public void creerEtenvoyerListeRequete(List<String> motCle,String destinataire){

        for (String s : motCle) {
            creerRequeteRecherche(s,destinataire);
        }

        envoyerTouteRequete();
    }

    public void envoyerRequete(Requete requete){
        requete.start(typeRequete);
    }

    public boolean touteRequeteFinit(){
        System.out.println("Nombre de requête reçu : " + nbRequeteFinit + " / " + listeRequete.size());
        return nbRequeteFinit >= listeRequete.size();
    }

    /* ------------------- FONCTIONS SPECIFIQUES ------------------ */

    public void creerRequeteRecherche(String mot,String destinataire){
        Requete requete = new Requete(mot,destinataire);
        requete.initRecherche();
        listeRequete.add(requete);
    }

    public void creerRequeteIndexation(boolean forcee){
        initRequete();
        Requete requete;
        if (forcee){
            requete = new Requete("reset",TypeMoteur.BONGALA.name());
        } else {
            requete = new Requete("",TypeMoteur.BONGALA.name());
        }
        requete.initIndexation();
        listeRequete.add(requete);
    }

    public void initRequete(){
        listeRequete.clear();
        listeResultatBongala.clear();
        listeResultatBingBong.clear();
        nbRequeteFinit=0;
    }

    public void envoyerTouteRequete(){
        for (Requete requete : listeRequete) {
            if (requete.getEtatRequete() != EtatRequete.RUNNABLE) envoyerRequete(requete);
        }
    }

    private Requete trouverRequeteViaMot(String mot){
        Requete requeteCorrespondante = null;
        for (Requete requete : listeRequete){
            if (requete.getMot().equals(mot) && (requete.getEtatRequete() != EtatRequete.TERMINATED)) requeteCorrespondante = requete;
        }
        return requeteCorrespondante;
    }

    /* ------------------- FONCTIONS accesseurs ------------------ */

    public List<Requete> getListeRequete() {
        return listeRequete;
    }

    public List<String> getListeResultat(String nomDuMoteur){
        if (nomDuMoteur.equals(TypeMoteur.BONGALA.name())){
            return getListeResultatBongala();
        }
        return getListeResultatBingBong();
    }

    private List<String> getListeResultatBongala() {
        return listeResultatBongala;
    }

    private List<String> getListeResultatBingBong() {
        return listeResultatBingBong;
    }

    public int getNbRequeteFinit(){return nbRequeteFinit;}

    /* ------------------- FONCTIONS du listener ------------------ */

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String message = (String) evt.getNewValue();
        if (typeRequete == TypeRequete.INDEXATION){
            System.out.println(message);
        } else {
            String mot = List.of(message.split(",")).get(0);
            Requete requete = trouverRequeteViaMot(mot);
            if (requete != null){
                requete.setEtatRequete(EtatRequete.TERMINATED);
                if (requete.getDestinataire().equals(TypeMoteur.BONGALA.name())) listeResultatBongala.add(message);
                else listeResultatBingBong.add(message);
            }
        }
        this.nbRequeteFinit++;
    }

    public void removePropertyChangeListener(){
        communicationIvy.removePropertyChangeListener(ListenerPropriete.RESULTAT.toString(),this);
    }

}
