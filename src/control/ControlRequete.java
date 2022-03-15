package control;

import entity.Requete;

public class ControlRequete {


    public void envoyerRequete(Requete requete){
        requete.start();
    }
}
