package entity;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

public class CommunicationIvy {
    private Ivy bus = new Ivy("interface", " interface_processing is ready", null);

    private CommunicationIvy() {}

    private class CommunicationIvyHolder {
        private static final CommunicationIvy instance = new CommunicationIvy();
    }

    public static CommunicationIvy getInstance() {
        return CommunicationIvyHolder.instance;
    }

    public boolean lancerCommunication(){
        try
        {
            bus.start("127.255.255.255:2010");

            bus.bindMsg("^Moteur message=(.*)", new IvyMessageListener()
            {
                public void receive(IvyClient client, String[] args)
                {
                    String message = args[0];
                    System.out.println(message);
                }
            });

        }
        catch (IvyException ie)
        {
            ie.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean envoieMessage(String mess){
        try {
            bus.sendMsg(mess);
        } catch (IvyException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void fermerCommunication(){
        bus.stop();
    }

}
