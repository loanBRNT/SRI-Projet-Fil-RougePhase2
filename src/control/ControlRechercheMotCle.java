package control;

import entity.Requete;

import java.util.List;

public class ControlRechercheMotCle {
    private ControlCommunicationIvy controlCommunicationIvy;
    private ControlRequete controlRequete;

    public ControlRechercheMotCle(ControlCommunicationIvy controlCommunicationIvy, ControlRequete controlRequete){
        this.controlCommunicationIvy = controlCommunicationIvy;
        this.controlRequete = controlRequete;
    }

    public void rechercheMotCle(List<String> motCle, List<Integer> polarite) {
        controlCommunicationIvy.lancerCommunication();
        controlRequete.envoyerRequete(new Requete("monde"));
        controlCommunicationIvy.stopperCommunication();
    }
}
