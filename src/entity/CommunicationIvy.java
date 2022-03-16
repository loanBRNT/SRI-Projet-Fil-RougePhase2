package entity;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CommunicationIvy {
    public Ivy bus = new Ivy("interface", " interface_processing is ready", null);
    public PropertyChangeSupport support = new PropertyChangeSupport(this);
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

    public void envoieMessage(String mess) throws IvyException{
        bus.sendMsg(mess);
    }

    public void fermerCommunication(){
        bus.stop();
    }

}
