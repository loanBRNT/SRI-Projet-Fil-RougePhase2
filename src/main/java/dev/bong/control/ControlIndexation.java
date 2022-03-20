package dev.bong.control;

import dev.bong.entity.Requete;
import dev.bong.entity.TypeRequete;

public class ControlIndexation {

    public void indexation() {
        ControlRequete controlRequete = new ControlRequete(TypeRequete.INDEXATION);

        int pourcentgeFinit;

        //Lancer la com
        controlRequete.lancerCommunicationBus();

        //Utilisation particulière de controlRequete, car on ne soumet ici qu'une seule requête spécifique à l'indexation

        controlRequete.creerRequeteIndexation();

        controlRequete.test();

        for (Requete requete : controlRequete.getListeRequete()){
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
