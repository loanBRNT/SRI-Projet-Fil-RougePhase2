package dev.bong.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Database {
    private static File fichierDatabse;

    public static List<String> getListeFichier(String extension) throws FileNotFoundException {
        ArrayList<String> liste;
        ArrayList<String> listeTotal = new ArrayList<>();
        String extensionEnBase;

        switch (extension) {
            case ".xml" -> {
                fichierDatabse = new File("./moteurs/Database/Descripteur/liste_base_texte.txt");
                extensionEnBase = ".xml";
            }
            case ".wav" -> {
                fichierDatabse = new File("./moteurs/Database/Descripteur/liste_base_audio.txt");
                extensionEnBase = ".bin";
            }
            default -> {
                fichierDatabse = new File("./moteurs/Database/Descripteur/liste_base_image.txt");
                extensionEnBase = ".txt";
            }
        }

        Scanner sc = new Scanner(fichierDatabse);
        String l;
        int i;
        while (sc.hasNext()){
            l = sc.nextLine();

            i=0;

            for (String s : List.of(l.split(" "))) {
                if (i == 1) {
                    for (String nomFic : List.of(s.split(extensionEnBase))) {
                        listeTotal.add(nomFic + extension);
                    }
                }
                i++;
            }
        }

        return listeTotal;
    }

}
