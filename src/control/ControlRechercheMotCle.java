package control;

import boundary.Clavier;
import entity.CommunicationIvy;
import entity.Requete;
import entity.TypeRequete;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ControlRechercheMotCle{
    private int pourcentgeFinit = 0;

    public void rechercheMotCle(List<String> motCle, List<Integer> polarite) {
        ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_MOT_CLE);

        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        for (String s : motCle) {
            controlRequete.creerRequete(s);
        }

        controlRequete.test();

        for (Requete requete : controlRequete.getListeRequete()) {
            controlRequete.envoyerRequete(requete);
        }

        while (!controlRequete.touteRequeteFinit()){
            pourcentgeFinit = controlRequete.nombreRequeteFinit();
            System.out.println(pourcentgeFinit); //ne marche pas si je l'affiche pas
        }

        System.out.println(controlRequete.getListeResultat());


        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();
    }

}
