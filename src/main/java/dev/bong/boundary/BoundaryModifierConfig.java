package dev.bong.boundary;


import dev.bong.control.ControlModifierConfig;
import dev.bong.entity.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoundaryModifierConfig {
    ControlModifierConfig controlModifierConfig;

    public BoundaryModifierConfig(ControlModifierConfig controlModifierConfig){
        this.controlModifierConfig=controlModifierConfig;
    }




    public void afficheConfig() throws IOException {
        Config config = Config.getInstance();
        System.out.println(config.toString());
        boolean isRunning =true;
        boolean saisie=false;
        int choix=0;

        System.out.println("Voulez vous modifier les parametres?"+
                "\n 1 oui"+
                "\n 2 non");
        while(isRunning){
            do{
                try{
                    choix=Clavier.entrerClavierInt();
                    saisie=true;
                }catch(java.util.InputMismatchException e){
                    System.out.println("saisie incorrecte") ;
                    Clavier.nextLine();
                }
            }while (!saisie);
            saisie=false;
            switch(choix){
                case 1 :
                    this.modifConfig();
                    isRunning=false;
                    break;
                case 2 :
                    isRunning=false;
                    break;

                default:
                    System.out.println("Saisi incorrecte");

            }
        }


    }

    private void modifConfig() throws IOException {
        Config config = Config.getInstance();
        List<Integer> listeValeur = new ArrayList<>();
        boolean saisie =false;
        int choix=0;
        int i=0;
        while(i!=6){
            do{
                switch (i){
                    case 0:
                        System.out.println("Veuillez saisir le nouveau taux de similitude");
                        break;
                    case 1 :
                        System.out.println("Veuillez saisir le nouveau nombre max de mot par texte");
                        config.setNbMaxMotParTexte(choix);
                        break;
                    case 2 :
                        System.out.println("Veuillez saisir le nouveau seuil d'occurence de mot");
                        config.setSeuilOccMot(choix);
                        break;
                    case 3 :
                        System.out.println("Veuillez saisir le nouveau nombre d'intervalle audio");
                        config.setNbrIntervalleAudio(choix);
                        break;
                    case 4 :
                        System.out.println("Veuillez saisir le nouveau nombre de points audio");
                        config.setNbrPointsAudio(choix);
                        break;
                    case 5 :
                        System.out.println("Veuillez saisir le nouveau nombre de bit de quantification");
                        config.setBitQuantification(choix);
                        break;
                    default:
                        break;
                }
                try{
                    choix=Clavier.entrerClavierInt();
                    listeValeur.add(choix);
                    saisie=true;
                }catch(java.util.InputMismatchException e){
                    System.out.println("saisie incorrecte") ;
                    Clavier.nextLine();
                }
            }while (!saisie);
            saisie=false;

            i++;
        }
        ControlModifierConfig.modifConfig(listeValeur);
    }


}
