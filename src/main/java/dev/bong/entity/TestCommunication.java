package dev.bong.entity;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestCommunication implements PropertyChangeListener {
    private static final CommunicationIvy communicationIvy = CommunicationIvy.getInstance();

    private boolean recu = false;

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
        int tick = 0;
        communicationIvy.addPropertyChangeListener(ListenerPropriete.TEST.toString(),this);

        communicationIvy.definirBind(ListenerPropriete.TEST,TypeMoteur.BONGALA.name());
        communicationIvy.definirBind(ListenerPropriete.TEST,TypeMoteur.BINGBONG.name());

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
        if (mess.equals("testOk")){
            recu = true;
        }
    }
}
