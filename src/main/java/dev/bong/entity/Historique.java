package dev.bong.entity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Historique {
    public static void ecrire(TypeRequete type, String texte) {
        try {
            File f = new File("./Historique.txt");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            fw.write(type.toString() + " : " + texte + "\n");
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
}
