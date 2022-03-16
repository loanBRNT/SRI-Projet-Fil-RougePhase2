package control;

import boundary.Clavier;
import entity.CommunicationIvy;
import entity.Requete;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ControlRechercheMotCle{
    private ControlRequete controlRequete;

    public ControlRechercheMotCle(ControlRequete controlRequete){
        this.controlRequete = controlRequete;
    }

    public void rechercheMotCle(List<String> motCle, List<Integer> polarite) {
        String mot = "";

        //LANCER LA COM
       controlRequete.lancerCommunicationBus();

        while(!mot.equals("Stop")) {
            mot = Clavier.entrerClavierString();
            controlRequete.envoyerRequete(new Requete(mot));
        }
        /*
        for (int i = 0 ; i < motCle.size() ; i++){
            mot = motCle.get(i);
            requete = new Requete(mot);
            requete.start();
        }
*/


        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();
    }

}
