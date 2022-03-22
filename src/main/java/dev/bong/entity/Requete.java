package dev.bong.entity;

import fr.dgac.ivy.IvyException;


public class Requete{
    private CommunicationIvy communicationIvy= CommunicationIvy.getInstance();
    private String mot;
    private EtatRequete etatRequete = EtatRequete.WAITING_FOR_INIT;
    private String resultat="";

    public Requete(String mot){
        this.mot = mot;
    }

    public Requete(){
        this.mot = "";
    }

    public void initRecherche(){
        try {

            communicationIvy.definirBind(mot);

            etatRequete = EtatRequete.READY_FOR_START;

        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public void initIndexation(){
        try {

            communicationIvy.definirBind();

            etatRequete = EtatRequete.READY_FOR_START;

        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public void start(dev.bong.entity.TypeRequete requete) {
        try {
            int i = communicationIvy.envoieMessage("Interface message=" + requete.toString() + " source=" + mot);
            System.out.println("Interface message=" + requete.toString() + " source=" + mot + " | " + i);
            etatRequete = EtatRequete.RUNNABLE;
        } catch (IvyException e) {
            e.printStackTrace();
        }
    }

    public EtatRequete getEtatRequete() {
        return etatRequete;
    }
}
