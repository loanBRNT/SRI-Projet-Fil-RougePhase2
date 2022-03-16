import boundary.Clavier;
import control.ControlRechercheMotCle;
import control.ControlRequete;
import entity.CommunicationIvy;
import entity.Requete;

import java.util.ArrayList;
import java.util.List;

public class Main{
    //Attributs

    //Constructeur

    //Méthodes
    public static void main(String[] args) {


        ArrayList<String> liste = new ArrayList<>();
        liste.add("momo");
        liste.add("christophe");
        liste.add("jeanjean");

        ArrayList<Integer> listePolarite = new ArrayList<>();
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);

        ControlRechercheMotCle controlRechercheMotCle = new ControlRechercheMotCle(new ControlRequete());

        controlRechercheMotCle.rechercheMotCle(liste, listePolarite);

        /* MAIN IVY
        String motTape = "";
        Requete requete;
        List<Requete> listeRequeteEnCours = new ArrayList<>();

        CommunicationIvy bus = CommunicationIvy.getInstance();

        bus.lancerCommunication();

        while(!motTape.equals("Stop")){
            motTape = Clavier.entrerClavierString();
            requete = new Requete(motTape);
            requete.start();

            listeRequeteEnCours.add(requete);


        }
        bus.fermerCommunication();
        //*/
    }
}
