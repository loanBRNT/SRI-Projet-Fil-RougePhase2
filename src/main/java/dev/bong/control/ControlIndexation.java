package dev.bong.control;

import dev.bong.entity.Requete;
import dev.bong.entity.TestCommunication;
import dev.bong.entity.TypeRequete;

public class ControlIndexation {
    private static ControlRequete controlRequete = new ControlRequete(TypeRequete.INDEXATION);
    private static TestCommunication testCommunication = TestCommunication.getInstance();

    public static void indexationForcee() throws Exception {
        boolean requeteFinit = false;

        //Lancer la com
        controlRequete.lancerCommunicationBus();

        Thread.sleep(2000);

        testCommunication.testerCommunication();

        //Utilisation particulière de controlRequete, car on ne soumet ici qu'une seule requête spécifique à l'indexation

        controlRequete.initRequete();

        controlRequete.creerRequeteIndexation(true);

        for (Requete requete : controlRequete.getListeRequete()){
            controlRequete.envoyerRequete(requete);
        }

        while (!requeteFinit){
            try {
                requeteFinit = controlRequete.touteRequeteFinit();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        //delier
        controlRequete.removePropertyChangeListener();
    }

    public static void IndexationDuModeOuvert(){
        controlRequete.initRequete();
        controlRequete.creerRequeteIndexation(true);

        for (Requete requete : controlRequete.getListeRequete()){
            controlRequete.envoyerRequete(requete);
        }

        boolean requeteFinit = false;

        while (!requeteFinit){
            try {
                requeteFinit = controlRequete.touteRequeteFinit();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("indexation ok");
    }
}
