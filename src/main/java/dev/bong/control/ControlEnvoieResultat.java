package dev.bong.control;

import java.util.HashSet;
import java.util.Set;

public class ControlEnvoieResultat {
    private Set<String> resTotal;

    private ControlEnvoieResultat(){}

    private class ControlEnvoieResultatHolder{
        private static final ControlEnvoieResultat instance = new ControlEnvoieResultat();
    }

    public static ControlEnvoieResultat getInstance(){
        return ControlEnvoieResultatHolder.instance;
    }

    public void receptionResultat(Set<String> resultat){
        this.resTotal = resultat;
    }



}
