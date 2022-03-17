package entity;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Requete extends Thread{
    private CommunicationIvy communicationIvy= CommunicationIvy.getInstance();
    private String mot;
    private boolean messageRecu = false;
    private String resultat="";

    public Requete(String mot){
        super("Recherche de " + mot);
        this.mot = mot;
    }

    public void run(){
        try {

            communicationIvy.bus.bindMsgOnce("^Moteur liste=(.*)",(client, args) -> {
                communicationIvy.support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,args[0]);
                resultat = "";
                setMessageRecu(true);
            });

            communicationIvy.bus.bindMsgOnce("^Moteur erreur=(.*)", (client, args) -> {
                communicationIvy.support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,args[0]);
                resultat = "";
                setMessageRecu(true);
            });

            communicationIvy.envoieMessage("Interface message=rechercheMotCle source=" + mot);
        } catch (IvyException e) {
            e.printStackTrace();
            setMessageRecu(true);
        }
        while (!messageRecu){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        communicationIvy.bus.unBindMsg("^Moteur liste=(.*)");
        communicationIvy.bus.unBindMsg("^Moteur erreur=(.*)");
    }

    public void setMessageRecu(boolean messageRecu) {
        this.messageRecu = messageRecu;
    }
}
