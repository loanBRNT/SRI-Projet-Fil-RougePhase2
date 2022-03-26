package dev.bong.control;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

public class ControlEnvoieResultat {

    private ControlEnvoieResultat(){}

    private class ControlEnvoieResultatHolder{
        private static final ControlEnvoieResultat instance = new ControlEnvoieResultat();
    }

    public static ControlEnvoieResultat getInstance(){
        return ControlEnvoieResultatHolder.instance;
    }

    public void receptionResultat(Set<String> resultat){

    }


}
