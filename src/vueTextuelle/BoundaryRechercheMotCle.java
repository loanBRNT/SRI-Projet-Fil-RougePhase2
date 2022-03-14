package vueTextuelle;

import controleur.ControlRechercheMotCle;

import java.util.ArrayList;
import java.util.List;

public class BoundaryRechercheMotCle {
    private ControlRechercheMotCle controlRechercheMotCle;

    public BoundaryRechercheMotCle(ControlRechercheMotCle controlRechercheMotCle) {
        this.controlRechercheMotCle = controlRechercheMotCle;
    }


    public void Recherche() {

        List<String> motCle = new ArrayList<>();
        List<Integer> polarite = new ArrayList<>();
        boolean continuer = true;
        boolean saisie = false;
        boolean ok = false;
        int nbMot = 0;
        int choix = 0;
        String entree = "";
        int choixPolarite;

        while (continuer) {
            entree = this.SaisirMotCle();
            motCle.add(entree);

            choixPolarite = this.choixPolarité();
            polarite.add(choixPolarite);
            nbMot++;
            if(nbMot!=4){
                continuer = this.Continuer();
            }else{
                continuer=false;
            }
        }

        this.controlRechercheMotCle.rechercheMotCle(motCle,polarite);

    }



    private int choixPolarité() {
        boolean ok = false;
        int choix = 0;
        System.out.println("Saisir polarité\n" +
                "1 rajouter le mot\n" +
                "2 exclure le mot\n");
        do {
            do {
                try {
                    choix = Clavier.entrerClavierInt();
                    ok = true;
                } catch (java.util.InputMismatchException e) {
                    System.out.println("saisie incorrecte");
                    Clavier.nextLine();
                }
            } while (!ok);

            ok = false;
            if (choix == 1 || choix == 2)
                ok = true;
        } while (!ok);

        return choix;


    }

    private String SaisirMotCle(){
        String entree = "";
        boolean saisie = false;
        System.out.println("Veuillez saisir un mot clé");
        do {
            try {
                entree = Clavier.entrerClavierString();
                saisie = true;
            } catch (java.util.InputMismatchException e) {
                System.out.println("saisie incorrecte");
                Clavier.nextLine();
            }
        } while (!saisie);
        return entree;
    }

    private boolean Continuer() {

        boolean ok = false;
        boolean saisie = false;
        boolean continuer = false;
        int choix = 0;

        System.out.println("Continuez ?" +
                "\n 1 oui" +
                "\n 2 non \n");

        while (!ok) {
            do {
                try {
                    choix = Clavier.entrerClavierInt();
                    saisie = true;
                } catch (java.util.InputMismatchException e) {
                    System.out.println("saisie incorrecte");
                    Clavier.nextLine();
                }
            } while (!saisie);

            saisie = false;
            switch (choix) {
                case 1:
                    ok = true;
                    continuer = true;
                    break;
                case 2:
                    continuer = false;
                    ok = true;
                    break;
                default:
                    System.out.println("Saisi incorrecte");
            }
        }
        return continuer;
    }
}
