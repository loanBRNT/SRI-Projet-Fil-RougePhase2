package dev.bong.boundary;

import dev.bong.control.ControlRechercheMotCle;

import java.util.ArrayList;

public class Main {
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

        //controlRechercheMotCle.rechercheMotCle(liste);
        controlRechercheMotCle.rechercheMultiple(liste,null);
    }
}
