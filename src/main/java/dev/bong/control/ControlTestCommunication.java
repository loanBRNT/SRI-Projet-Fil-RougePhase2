package dev.bong.control;


import dev.bong.entity.CommunicationIvy;
import dev.bong.entity.ListenerPropriete;
import dev.bong.entity.TypeMoteur;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ControlTestCommunication implements PropertyChangeListener {
    private static final CommunicationIvy communicationIvy = CommunicationIvy.getInstance();

    private boolean recu = false;

    private ControlTestCommunication(){}

    private static class TestCommunicationHolder{
        private static final ControlTestCommunication instance = new ControlTestCommunication();
    }

    public static ControlTestCommunication getInstance(){
        return TestCommunicationHolder.instance;
    }

    public static void fermerCommunication(){
        communicationIvy.fermerCommunication();
    }


    public void testerCommunication() throws Exception {
        int tick = 0;
        communicationIvy.addPropertyChangeListener(ListenerPropriete.TEST.toString(),this);

        communicationIvy.definirBind(ListenerPropriete.TEST, TypeMoteur.BONGALA.name());
        communicationIvy.definirBind(ListenerPropriete.TEST,TypeMoteur.BINGBONG.name());

        communicationIvy.envoieMessage("Interface message=test");

        while (!recu && tick < 2000){
            tick++;
            System.out.println(recu + " : " + tick);
        }

        communicationIvy.removePropertyChangeListener(ListenerPropriete.TEST.toString(),this);

        if (tick >= 2000){
            System.out.println("Protocole : Test de connexion au moteur --> Echoue");
            throw new Exception();
        }

        System.out.println("Protocole : Test de connexion au moteur --> réussi");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String mess = (String) evt.getNewValue();
        if (mess.equals("testOk")){
            recu = true;
        }
    }
}
