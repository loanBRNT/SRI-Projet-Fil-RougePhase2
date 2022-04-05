package dev.bong.entity;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CommunicationIvy {
    private Ivy bus = new Ivy("interface", " interface is ready", null);
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String resultat = "";

    //SINGLETON
    private CommunicationIvy() {}

    private class CommunicationIvyHolder {
        private static final CommunicationIvy instance = new CommunicationIvy();
    }

    public static CommunicationIvy getInstance() {
        return CommunicationIvyHolder.instance;
    }

    //LISTENER

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
        support.addPropertyChangeListener(propertyName,listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener){
        support.removePropertyChangeListener(propertyName,listener);
    }


    //IVY
    public void lancerCommunication() throws IvyException {
        bus.start("127.255.255.255:2010");
    }

    public void definirBind(String mot, String destinataire) throws IvyException {
        bus.bindMsgOnce("^" + destinataire + " mot=" + mot + " liste=(.*)",(client, args) -> {
            String message = mot + "," + args[0];
            support.firePropertyChange(ListenerPropriete.RESULTAT.toString(),resultat,message);
            resultat = "";
        });
    }

    public void definirBind(ListenerPropriete listenerPropriete, String destinataire) throws IvyException {
        bus.bindMsg("^" + destinataire + " message=(.*)", ((ivyClient, args) -> {
            support.firePropertyChange(listenerPropriete.toString(),resultat,args[0]);
            resultat = "";
        }));
    }

    public int envoieMessage(String mess) throws IvyException{
        return bus.sendMsg(mess);
    }

    public void fermerCommunication(){
        bus.stop();
    }

}
