package dev.bong.entity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Historique {
    public static void ecrire(TypeRequete type, String texte) {
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            File f = new File("./Historique.txt");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            fw.write(format.format(cal.getTime())+" - "+type.toString()+" : "+texte+"\n");
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
