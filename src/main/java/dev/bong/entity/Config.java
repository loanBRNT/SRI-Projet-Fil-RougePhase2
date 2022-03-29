package dev.bong.entity;

import dev.bong.control.ControlIndexation;
import fr.dgac.ivy.IvyException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class  Config {

    //attributs
    private List<String> listeDesConfig = new ArrayList<>();
    private int tauxSim;
    private int nbMaxMotParTexte;
    private int seuilOccMot;
    private int nbrIntervalleAudio;
    private int nbrPointsAudio;
    private int bitQuantification;

    private boolean mode=false;   // false pour fermer et true pour ouvert
    private boolean multiMoteur = false;
    private boolean maj = false;

    private Config(){
        //ajout des config des differents moteur de recherche
        listeDesConfig.add("./moteur/.config");
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

    public void majConfig() throws Exception {
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
                fw.write("tauxSim " + this.tauxSim+"\n" +
                        "nbMaxMotParTexte " + this.nbMaxMotParTexte + "\n" +
                        "seuilOccurenceMot " + this.seuilOccMot+ "\n" +
                        "nombreIntervalleAudio " + this.nbrIntervalleAudio + "\n" +
                        "nombrePointsAudio " + this.nbrPointsAudio + "\n" +
                        "nombreBitsQuantification " + this.bitQuantification+ "\n"
                );
                fw.flush();
                fw.close();

                if (maj){
                    ControlIndexation.indexationForcee();
                }
            }
    }

    public void chargementConfig() throws IOException {
        FileReader flux = new FileReader("./moteur/.config");
        BufferedReader buffer = new BufferedReader(flux);
        List<String> listeLigne;
        String line,val,nom;
        while((line= buffer.readLine())!= null){
            listeLigne = List.of(line.split(" "));
            nom = listeLigne.get(0);
            val = listeLigne.get(1);
            switch (nom){
                case "tauxSim":
                    this.tauxSim=Integer.parseInt(val);
                    break;
                case "nbMaxMotParTexte" :
                    this.nbMaxMotParTexte=Integer.parseInt(val);
                    break;
                case "seuilOccurenceMot" :
                    this.seuilOccMot=Integer.parseInt(val);
                    break;
                case "nombreIntervalleAudio" :
                    this.nbrIntervalleAudio=Integer.parseInt(val);
                    break;
                case "nombrePointsAudio" :
                    this.nbrPointsAudio=Integer.parseInt(val);
                    break;
                case "nombreBitsQuantification" :
                    this.bitQuantification=Integer.parseInt(val);
                    break;
                default:
                    break;
            }
        }

    }

    public int getTauxSim() {
        return tauxSim;
    }

    public void setTauxSim(int tauxSim) {
        if (this.tauxSim != tauxSim){
            maj = true;
            this.tauxSim = tauxSim;
        }
    }

    public int getNbMaxMotParTexte() {
        return nbMaxMotParTexte;
    }

    public void setNbMaxMotParTexte(int nbMaxMotParTexte) {
        if (this.nbMaxMotParTexte != nbMaxMotParTexte){
            maj = true;
            this.nbMaxMotParTexte = nbMaxMotParTexte;
        }
    }

    public int getSeuilOccMot() {
        return seuilOccMot;
    }

    public void setSeuilOccMot(int seuilOccMot) {
        if (this.seuilOccMot != seuilOccMot){
            maj = true;
            this.seuilOccMot = seuilOccMot;
        }
    }

    public int getNbrIntervalleAudio() {
        return nbrIntervalleAudio;
    }

    public void setNbrIntervalleAudio(int nbrIntervalleAudio) {
        if (this.nbrIntervalleAudio != nbrIntervalleAudio){
            maj = true;
            this.nbrIntervalleAudio = nbrIntervalleAudio;;
        }
    }

    public int getNbrPointsAudio() {
        return nbrPointsAudio;
    }

    public void setNbrPointsAudio(int nbrPointsAudio) {
        if (this.nbrPointsAudio != nbrPointsAudio){
            maj = true;
            this.nbrPointsAudio = nbrPointsAudio;
        }
    }

    public int getBitQuantification() {
        return bitQuantification;
    }

    public void setBitQuantification(int bitQuantification) {
        if (this.bitQuantification != bitQuantification){
            maj = true;
            this.bitQuantification = bitQuantification;
        }
    }

    public boolean getMode(){return mode;}

    public void setModeOuvert(){this.mode=true;}
    public void setModeFerme(){this.mode=false;}

    public boolean getMultiMoteur(){return multiMoteur;}
    public void setMultiMoteurOn(){this.multiMoteur=true;}
    public void setMultiMoteurOff(){this.multiMoteur=false;}


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
