package dev.bong.control;

import dev.bong.view.RechercheApplication;

import java.io.IOException;
import java.util.Set;

public class ControlEnvoieResultat {

    private Set<String> resultat;

    private ControlEnvoieResultat(){}

    private static class ControlEnvoieResultatHolder{
        private static final ControlEnvoieResultat instance = new ControlEnvoieResultat();
    }

    public static ControlEnvoieResultat getInstance(){
        return ControlEnvoieResultatHolder.instance;
    }

    public void receptionResultat(Set<String> resultat){
        this.resultat = resultat;
    }

    public Set<String> getResultat() {
        return resultat;
    }
}
