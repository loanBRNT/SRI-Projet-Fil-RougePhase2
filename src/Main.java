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
        liste.add("jean");
        liste.add("chris");
        liste.add("mon");
        liste.add("moti");
        liste.add("lala");
        liste.add("jeanjean");


        ArrayList<Integer> listePolarite = new ArrayList<>();
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);
        listePolarite.add(1);

        ControlRechercheMotCle controlRechercheMotCle = new ControlRechercheMotCle();

        controlRechercheMotCle.rechercheMotCle(liste, listePolarite);
    }
}
