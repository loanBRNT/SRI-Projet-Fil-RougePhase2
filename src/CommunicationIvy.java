import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

import java.util.Scanner;

public class CommunicationIvy {
    private Ivy bus = new Ivy("interface", " interface_processing is ready", null);;

    public boolean lancerCommunication(){
        try
        {
            bus.start("127.255.255.255:2010");

            bus.bindMsg("^Interface=ok", new IvyMessageListener()
            {
                public void receive(IvyClient client, String[] args)
                {
                    System.out.println("message sent successfully!");
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
