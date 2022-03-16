package control;

import entity.CommunicationIvy;
import entity.Requete;
import entity.RequeteName;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ControlRequete implements PropertyChangeListener{
    private CommunicationIvy bus = CommunicationIvy.getInstance();

    public void lancerCommunicationBus(){
        bus.lancerCommunication();
    }

    public void fermerCommunicationBus(){
        bus.fermerCommunication();
    }

    public void envoyerRequete(Requete requete){
        requete.setListener(RequeteName.RECHERCHE.toString(),this);
        requete.start();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String message = (String) evt.getNewValue();
        System.out.println(message);
    }
}
