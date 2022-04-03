package dev.bong.entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Historique {

    public static ArrayList<Recherche> listeDeListe = new ArrayList<>();

    public static void ecrire(TypeRequete type, String texte) {
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            File f = new File("./Historique.txt");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            String recherche = format.format(cal.getTime())+" - "+type.toString()+" : "+texte+"\n";
            fw.write(recherche);
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void effacer() {
        try {
            File f = new File("./Historique.txt");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, false);
            fw.write("");
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void lire() throws FileNotFoundException {
        String recherches = "";
        String resultats = "";
        String date = "";
        String type = "";
        String bans = "";
        File f = new File("./Historique.txt");
        Scanner sc = new Scanner(f);
        listeDeListe.clear();
        while (sc.hasNext()){
            String ligne = sc.nextLine();
            ArrayList<String> decoupeRes = new ArrayList<>(List.of(ligne.split(";")));
            ArrayList<String> decoupeDate = new ArrayList<>(List.of(decoupeRes.get(0).split(" - ")));
            ArrayList<String> decoupe = new ArrayList<>(List.of(decoupeDate.get(1).split(" : ")));
            ArrayList<String> decoupeRecherche = new ArrayList<>(List.of(decoupe.get(2).split(",")));
            //Decouper ligne en 2 String la première contenant la recherche et la deuxième les resultats
            date=decoupeDate.get(0);
            type=decoupe.get(0);
            recherches=decoupeRecherche.get(0);
            bans=decoupe.get(3);
            resultats=decoupeRes.get(1);
            listeDeListe.add(new Recherche(date,type,recherches,bans,resultats));
        }
    }

    public static ArrayList<Recherche> getListeDeListe() {
        return listeDeListe;
    }


}