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
    private List<String> listeResultat = new ArrayList<>();

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
    public void creerEtenvoyerListeRequete(List<String> motCle){
        initRequete();

        for (String s : motCle) {
            creerRequeteRecherche(s);
        }

        for (Requete requete : listeRequete) {
            envoyerRequete(requete);
        }
    }

    public void envoyerRequete(Requete requete){
        requete.start(typeRequete);
    }

    public boolean touteRequeteFinit(){
        return nbRequeteFinit == listeRequete.size();
    }

    /* ------------------- FONCTIONS SPECIFIQUES ------------------ */

    public void creerRequeteRecherche(String mot){
        Requete requete = new Requete(mot);
        requete.initRecherche();
        listeRequete.add(requete);
    }

    public void creerRequeteIndexation(boolean forcee){
        initRequete();
        Requete requete;
        if (forcee){
            requete = new Requete("reset");
        } else {
            requete = new Requete("");
        }
        requete.initIndexation();
        listeRequete.add(requete);
    }

    public void initRequete(){
        listeRequete.clear();
        listeResultat.clear();
        nbRequeteFinit=0;
    }

    /* ------------------- FONCTIONS accesseurs ------------------ */

    public List<Requete> getListeRequete() {
        return listeRequete;
    }

    public List<String> getListeResultat() {
        return listeResultat;
    }


    public int getNbRequeteFinit(){return nbRequeteFinit;}

    /* ------------------- FONCTIONS du listener ------------------ */

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String message = (String) evt.getNewValue();
        listeResultat.add(message);
        this.nbRequeteFinit++;
        System.out.println(this + " -> " + nbRequeteFinit + " / " + listeRequete.size());
    }

    public void removePropertyChangeListener(){
        communicationIvy.removePropertyChangeListener(ListenerPropriete.RESULTAT.toString(),this);
    }

}
