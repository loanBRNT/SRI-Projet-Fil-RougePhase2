package dev.bong.entity;


import dev.bong.control.ControlEnvoieResultat;
import fr.dgac.ivy.IvyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestCommunication implements PropertyChangeListener {
    private static final TypeRequete typeRequete = TypeRequete.TEST_COMMUNICATION;
    private static CommunicationIvy communicationIvy = CommunicationIvy.getInstance();

    private boolean recu = false;
    private int tick=0;

    private TestCommunication(){}

    private static class TestCommunicationHolder{
        private static final TestCommunication instance = new TestCommunication();
    }

    public static TestCommunication getInstance(){
        return TestCommunicationHolder.instance;
    }

    public static void fermerCommunication(){
        communicationIvy.fermerCommunication();
    }


    public void testerCommunication() throws Exception {
        tick = 0;
        communicationIvy.addPropertyChangeListener(ListenerPropriete.TEST.toString(),this);

        communicationIvy.definirBind(ListenerPropriete.TEST);

        communicationIvy.envoieMessage("Interface message=test");

        while (!recu && tick < 2000){
            tick++;
            System.out.println(recu + " : " + tick);
        }

        communicationIvy.removePropertyChangeListener(ListenerPropriete.TEST.toString(),this);

        if (tick >= 2000){
            throw new Exception();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String mess = (String) evt.getNewValue();
        System.out.println(mess);
        if (mess.equals("testOk")){
            recu = true;
        }
    }
}
