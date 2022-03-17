package control;

import entity.CommunicationIvy;
import entity.Requete;
import entity.RequeteName;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ControlRequete implements PropertyChangeListener{
    private CommunicationIvy bus = CommunicationIvy.getInstance();
    private List<Requete> listeRequete = new ArrayList<>();
    private List<String> listeResultat = new ArrayList<>();
    private int nbRequeteFinit = 0;

    public ControlRequete(){
        bus.addPropertyChangeListener(RequeteName.RECHERCHE.toString(),this);
    }

    public void lancerCommunicationBus(){
        bus.lancerCommunication();
    }

    public void fermerCommunicationBus(){
        bus.fermerCommunication();
    }

    public void creerETenvoyerRequete(String mot){
        Requete requete = new Requete(mot);
        requete.start();
        listeRequete.add(requete);
    }

    public boolean touteRequeteFinit(){
        return nbRequeteFinit == listeRequete.size();
    }

    public void touteRequeteTermine(){
        nbRequeteFinit++;
    }

    public int nombreRequeteFinit(){
        return nbRequeteFinit;
    }

    public List<Requete> getListeRequete() {
        return listeRequete;
    }

    public List<String> getListeResultat() {
        return listeResultat;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String message = (String) evt.getNewValue();
        System.out.println(message);
        /*
        if (!listeResultat.contains(message)){
            listeResultat.add(message);
            this.nbRequeteFinit++;
        }

         */
        listeResultat.add(message);
        this.nbRequeteFinit++;
        System.out.println(nbRequeteFinit);
    }
}
