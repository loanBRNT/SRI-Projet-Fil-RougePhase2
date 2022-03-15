package entity;

import java.util.List;

public class Requete extends Thread{
    private CommunicationIvy bus = CommunicationIvy.getInstance();
    private String mot;
    private EtatRequete etatRequete = EtatRequete.RUNNABLE;

    public Requete(String mot){
        super(mot);
        this.mot = mot;
    }

    public void run(){
        bus.envoieMessage("Interface message=RechercheMotCle source=" + mot);
        while (etatRequete == EtatRequete.RUNNABLE);
    }

    public EtatRequete getEtatRequete() {
        return etatRequete;
    }

    public void setEtatRequete(EtatRequete etatRequete) {
        this.etatRequete = etatRequete;
    }
}
