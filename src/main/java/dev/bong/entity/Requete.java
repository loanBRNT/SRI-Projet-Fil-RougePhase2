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
            setEtatRequete(EtatRequete.ERROR);
        }
    }

    public void initIndexation(){
        try {

            communicationIvy.definirBind();

            etatRequete = EtatRequete.READY_FOR_START;

        } catch (IvyException e) {
            e.printStackTrace();
            setEtatRequete(EtatRequete.ERROR);
        }
    }

    public void start(dev.bong.entity.TypeRequete requete) {
        int nbAgentReceveur;
        try {
            nbAgentReceveur = communicationIvy.envoieMessage("Interface message=" + requete.toString() + " source=" + mot);
            if (nbAgentReceveur > 0){
                System.out.println(mot + " envoyé à " + nbAgentReceveur + " agents");
                setEtatRequete(EtatRequete.RUNNABLE);
            }
            else {
                System.out.println("Impossible d'envoyer le mot : " + mot);
                setEtatRequete(EtatRequete.ERROR);
            }
        } catch (IvyException e) {
            e.printStackTrace();
            setEtatRequete(EtatRequete.ERROR);
       }
    }

    public void setEtatRequete(EtatRequete etatRequete) {
        this.etatRequete = etatRequete;
    }

    public EtatRequete getEtatRequete() {
        return etatRequete;
    }
}
