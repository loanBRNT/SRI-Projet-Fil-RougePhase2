package dev.bong.control;

import dev.bong.entity.Requete;
import dev.bong.entity.TypeRequete;

public class ControlIndexation extends Thread {
    private ControlRequete controlRequete = new ControlRequete(TypeRequete.INDEXATION);

    public void run() {
        boolean requeteFinit = false;

        //Lancer la com
        controlRequete.lancerCommunicationBus();

        //Utilisation particulière de controlRequete, car on ne soumet ici qu'une seule requête spécifique à l'indexation

        controlRequete.creerRequeteIndexation();

        for (Requete requete : controlRequete.getListeRequete()){
            controlRequete.envoyerRequete(requete);
        }

        while (!requeteFinit){
            try {
                requeteFinit = controlRequete.touteRequeteFinit();
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(controlRequete.getListeResultat());


        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();
    }
}
