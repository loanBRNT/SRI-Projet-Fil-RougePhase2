package entity;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Requete extends Thread{
    private CommunicationIvy communicationIvy= CommunicationIvy.getInstance();
    private String mot;
    private EtatRequete etatRequete = EtatRequete.RUNNABLE;
    private String resultat="";

    public Requete(String mot){
        super("Recherche de " + mot);
        this.mot = mot;
    }

    public void run(){

        try {
            communicationIvy.bus.bindMsgOnce("^Moteur liste=(.*)", new EcouteIvy(this));
            communicationIvy.bus.bindMsgOnce("^Moteur erreur=(.*)", (client, args) -> {
                communicationIvy.support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,args[0]);
                resultat = "";
                setEtatRequete(EtatRequete.TERMINATED);
            });
            communicationIvy.envoieMessage("Interface message=rechercheMotCle source=" + mot);
        } catch (IvyException e) {
            e.printStackTrace();
            setEtatRequete(EtatRequete.ERROR);
        }
        while (toujoursEnCours());
        System.out.println("ok");
    }

    public boolean toujoursEnCours(){
        return etatRequete == EtatRequete.RUNNABLE;
    }

    public EtatRequete getEtatRequete() {
        return etatRequete;
    }

    public void setEtatRequete(EtatRequete etatRequete) {
        this.etatRequete = etatRequete;
    }

    public void setListener(String nomDeLaPropriete, PropertyChangeListener listener){
        communicationIvy.addPropertyChangeListener(nomDeLaPropriete,listener);
    }
}
