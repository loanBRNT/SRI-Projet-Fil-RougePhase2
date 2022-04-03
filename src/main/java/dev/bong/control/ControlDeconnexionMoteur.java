package dev.bong.control;

import dev.bong.entity.CommunicationIvy;
import fr.dgac.ivy.IvyException;

public class ControlDeconnexionMoteur {
    private static CommunicationIvy communicationIvy = CommunicationIvy.getInstance();

    public static void deconnexionMoteurs() throws IvyException {
        communicationIvy.lancerCommunication();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        communicationIvy.envoieMessage("Stop");

        communicationIvy.fermerCommunication();
    }
}
