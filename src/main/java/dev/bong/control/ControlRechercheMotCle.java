package dev.bong.control;

import dev.bong.entity.Requete;
import dev.bong.entity.TypeRequete;

import java.util.List;

public class ControlRechercheMotCle {
    private int pourcentgeFinit = 0;

    public void rechercheMotCle(List<String> motCle, List<Integer> polarite) {
        ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_MOT_CLE);

        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        controlRequete.creerEtenvoyerListeRequete(motCle);

        while (!controlRequete.touteRequeteFinit()){
            pourcentgeFinit = controlRequete.nombreRequeteFinit();
            System.out.println(pourcentgeFinit); //ne marche pas si je l'affiche pas
        }

        System.out.println(controlRequete.getListeResultat());


        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();
    }

}
