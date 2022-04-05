package dev.bong.control;

import dev.bong.entity.CommunicationIvy;
import fr.dgac.ivy.IvyException;

public class ControlDeconnexionMoteur {
    private static CommunicationIvy communicationIvy = CommunicationIvy.getInstance();

    public static void deconnexionMoteurs() throws IvyException {

        try {
            communicationIvy.lancerCommunication();
            Thread.sleep(2000);
        } catch (InterruptedException | IvyException e ) {
            e.printStackTrace();
        }

        communicationIvy.envoieMessage("Stop");

        communicationIvy.fermerCommunication();
    }
}
