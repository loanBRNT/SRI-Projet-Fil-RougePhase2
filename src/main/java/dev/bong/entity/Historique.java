package dev.bong.entity;

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
    public static ArrayList<String> recherches = new ArrayList<>();
    public static ArrayList<String> resultats = new ArrayList<>();
    public static ArrayList<ArrayList<String>> setListe = new ArrayList<>();

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
        File f = new File("./Historique.txt");
        Scanner sc = new Scanner(f);
        recherches.clear();
        resultats.clear();
        while (sc.hasNext()){
            String ligne = sc.nextLine();
            ArrayList<String> decoupe = new ArrayList<>(List.of(ligne.split(";")));
            //Decouper ligne en 2 String la première contenant la recherche et la deuxième les resultats
            recherches.add(decoupe.get(0));
            resultats.add(decoupe.get(1));
        }
        setListe.add(recherches);
        setListe.add(resultats);
    }

    public static ArrayList<String> getRecherches(){
        return recherches;
    }

    public static ArrayList<String> getResultats(){
        return resultats;
    }

    public static ArrayList<ArrayList<String>> getListeDeListe(){
        return setListe;
    }

}