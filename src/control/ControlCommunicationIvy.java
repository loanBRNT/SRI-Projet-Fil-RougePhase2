package control;

import entity.CommunicationIvy;

public class ControlCommunicationIvy {
    private CommunicationIvy bus = CommunicationIvy.getInstance();

    public boolean lancerCommunication(){
        return bus.lancerCommunication();
    }

    public void stopperCommunication() {
        bus.fermerCommunication();
    }

    public boolean envoyerMessage(String message){
        return bus.envoieMessage(message);
    }
}
