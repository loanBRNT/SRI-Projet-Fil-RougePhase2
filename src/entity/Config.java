package entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    //attributs
    private List<String> listeDesConfig = new ArrayList<>();
    private int tauxSim;
    private int nbMaxMotParTexte;
    private int seuilOccMot;
    private int nbrIntervalleAudio;
    private int nbrPointsAudio;
    private int bitQuantification;

    private Config(){
        //ajout des config des differents moteur de recherche
        listeDesConfig.add("./moteur/phase2.config");
        try {
            chargementConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /** Holder **/
    private static class ConfigHolder {
        private final static Config instance = new Config();
    }

    public static Config getInstance(){
        return  ConfigHolder.instance;
    }

    public void majConfig()  {
        try {
            for (String config : listeDesConfig) {
                File f = new File(config);
                f.createNewFile();

                //Vider le fichier
                FileWriter fw;
                fw = new FileWriter(f, false);
                fw.write("");
                fw.flush();
                fw.close();

                //Ecrire dans le fichier
                fw = new FileWriter(f, true);
                fw.write(this.tauxSim+"\n" +
                        this.nbMaxMotParTexte + "\n" +
                        this.seuilOccMot+ "\n" +
                        this.nbrIntervalleAudio + "\n" +
                        this.nbrPointsAudio + "\n" +
                        this.bitQuantification+ "\n"
                );
                fw.flush();
                fw.close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }



    }

    public void chargementConfig() throws IOException {
        FileReader flux = new FileReader("./moteur/phase2.config");
        BufferedReader buffer = new BufferedReader(flux);
        String line;
        int i=0;
        while((line= buffer.readLine())!= null){
            switch (i){
                case 0:
                    this.tauxSim=Integer.parseInt(line);
                    break;
                case 1 :
                    this.nbMaxMotParTexte=Integer.parseInt(line);
                    break;
                case 2 :
                    this.seuilOccMot=Integer.parseInt(line);
                    break;
                case 3 :
                    this.nbrIntervalleAudio=Integer.parseInt(line);
                    break;
                case 4 :
                    this.nbrPointsAudio=Integer.parseInt(line);
                    break;
                case 5 :
                    this.bitQuantification=Integer.parseInt(line);
                    break;
                default:
                    break;
            }
            i++;
        }

    }

    public int getTauxSim() {
        return tauxSim;
    }

    public void setTauxSim(int tauxSim) {
        this.tauxSim = tauxSim;
    }

    public int getNbMaxMotParTexte() {
        return nbMaxMotParTexte;
    }

    public void setNbMaxMotParTexte(int nbMaxMotParTexte) {
        this.nbMaxMotParTexte = nbMaxMotParTexte;
    }

    public int getSeuilOccMot() {
        return seuilOccMot;
    }

    public void setSeuilOccMot(int seuilOccMot) {
        this.seuilOccMot = seuilOccMot;
    }

    public int getNbrIntervalleAudio() {
        return nbrIntervalleAudio;
    }

    public void setNbrIntervalleAudio(int nbrIntervalleAudio) {
        this.nbrIntervalleAudio = nbrIntervalleAudio;
    }

    public int getNbrPointsAudio() {
        return nbrPointsAudio;
    }

    public void setNbrPointsAudio(int nbrPointsAudio) {
        this.nbrPointsAudio = nbrPointsAudio;
    }

    public int getBitQuantification() {
        return bitQuantification;
    }

    public void setBitQuantification(int bitQuantification) {
        this.bitQuantification = bitQuantification;
    }

    public String toString(){
        String s= "taux sim ="+tauxSim +
                "\n Mot par texte ="+ nbMaxMotParTexte +
                "\n seuil occ mot ="+ seuilOccMot +
                "\n Intervalle audio="+ nbrIntervalleAudio +
                "\n nombre points Audio =" + nbrPointsAudio +
                "\n bit Quantification =" + bitQuantification ;
        return  s;
    }
}
