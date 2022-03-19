package dev.bong.entity;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CommunicationIvy {
    private Ivy bus = new Ivy("interface", " interface is ready", null);
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String resultat = "";

    private CommunicationIvy() {}

    private class CommunicationIvyHolder {
        private static final CommunicationIvy instance = new CommunicationIvy();
    }

    public static CommunicationIvy getInstance() {
        return CommunicationIvyHolder.instance;
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
        support.addPropertyChangeListener(propertyName,listener);
    }

    public void lancerCommunication(){
        try
        {
            bus.start("127.255.255.255:2010");
        }
        catch (IvyException ie)
        {
            ie.printStackTrace();
            fermerCommunication();
        }
    }

    public void definirBind(String mot) throws IvyException {
        bus.bindMsg("^Moteur mot=" + mot + " liste=(.*)",(client, args) -> {
            String message = mot + "," + args[0];
            support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,message);
            resultat = "";
        });
    }

    public int envoieMessage(String mess) throws IvyException{
        return bus.sendMsg(mess);
    }

    public void fermerCommunication(){
        bus.stop();
    }

}