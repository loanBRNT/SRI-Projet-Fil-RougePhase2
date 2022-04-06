package dev.bong.control;

import dev.bong.entity.TypeFichier;

import java.util.List;

public class ControlEnvoieResultat {

    private List<String> resultat;
    private TypeFichier type;

    private ControlEnvoieResultat(){}

    private static class ControlEnvoieResultatHolder{
        private static final ControlEnvoieResultat instance = new ControlEnvoieResultat();
    }

    public static ControlEnvoieResultat getInstance(){
        return ControlEnvoieResultatHolder.instance;
    }

    public void receptionResultat(List<String> resultat){
        this.resultat = resultat;
    }

    public List<String> getResultat() {
        return resultat;
    }

    public TypeFichier getType() {
        return type;
    }

    public void receptionType(TypeFichier typeFichier){
        type = typeFichier;
    }
}
