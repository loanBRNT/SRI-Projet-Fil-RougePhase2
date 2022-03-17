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
        int securite = 0;
        try {

            communicationIvy.bus.bindMsgOnce("^Moteur mot=" + mot + " liste=(.*)",(client, args) -> {
                String message = mot + "," + args[0];
                System.out.println(message);
                communicationIvy.support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,message);
                resultat = "";
                setMessageRecu(true);
            });

            communicationIvy.bus.bindMsgOnce("^Moteur mot=" + mot + " erreur=(.*)", (client, args) -> {
                String message = mot + "," + args[0];
                System.out.println(message);
                communicationIvy.support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,message);
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
                sleep(3000);
                securite++;
                if (securite > 1){ //si securite depasse les 1 c'est qu'il n'a pas trouvé donc on s'arrette
                    communicationIvy.support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,"erreur recherche");
                    break;
                }
                System.out.println(this.getName() + " :" + securite);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        communicationIvy.bus.unBindMsg("^Moteur mot=" + mot + " liste=(.*)");
        communicationIvy.bus.unBindMsg("^Moteur mot=" + mot + " erreur=(.*)");
    }

    public void setMessageRecu(boolean messageRecu) {
        this.messageRecu = messageRecu;
    }
}
