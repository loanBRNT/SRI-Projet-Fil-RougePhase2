package entity;

import control.ControlRequete;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

import java.util.PrimitiveIterator;

public class EcouteIvy implements IvyMessageListener {
    private CommunicationIvy communicationIvy = CommunicationIvy.getInstance();
    private Requete threadOriginel;
    private String resultat = "";

    public EcouteIvy(Requete requete){
        super();
        threadOriginel = requete;
    }
    @Override
    public void receive(IvyClient ivyClient, String[] strings) {
        communicationIvy.support.firePropertyChange(RequeteName.RECHERCHE.toString(),resultat,strings[0]);
        threadOriginel.setEtatRequete(EtatRequete.TERMINATED);
    }
}
