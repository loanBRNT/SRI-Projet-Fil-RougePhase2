package control;

import boundary.Clavier;
import entity.CommunicationIvy;
import entity.Requete;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ControlRechercheMotCle{
    private int pourcentgeFinit = 0;

    public void rechercheMotCle(List<String> motCle, List<Integer> polarite) {
        String mot = "";
        Requete requete;
        ControlRequete controlRequete = new ControlRequete();

        //LANCER LA COM
       controlRequete.lancerCommunicationBus();


       System.out.println("start");

        for (String s : motCle) {
            controlRequete.creerETenvoyerRequete(s);
        }

        while (!controlRequete.touteRequeteFinit()){
            pourcentgeFinit = controlRequete.nombreRequeteFinit();
            //System.out.println(pourcentgeFinit);
        }

        System.out.println(controlRequete.getListeResultat());

        /*
        List<Requete> listeRequete = new ArrayList<>();
        int i = 0;
        while(i != motCle.size()) {
            Clavier.entrerClavierString();
            controlRequete.creerETenvoyerRequete(motCle.get(i));
            i++;
        }

        while (listeRequete.size() != pourcentgeFinit){
            pourcentgeFinit = controlRequete.nombreRequeteFinit();
        }

         */


        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();
    }

}
