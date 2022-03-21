package dev.bong.boundary;

import dev.bong.control.ControlIndexation;
import dev.bong.control.ControlMenuAdmin;
import dev.bong.control.ControlModifierConfig;
import dev.bong.control.ControlSidentifier;

import java.io.IOException;

public class MainTextuelle {
    //Attributs

    //Constructeur

    //Méthodes
    public static void main(String[] args) throws IOException {
        boolean isRunning =true;
        boolean saisie=false;
        int choix=0;
        int res;
        BoundaryMenuAdmin menuAdmin = new BoundaryMenuAdmin(new ControlMenuAdmin(new ControlIndexation()),new BoundaryModifierConfig(new ControlModifierConfig()));
        BoundaryMenuClient menuClient= new BoundaryMenuClient();
        BoundarySidentifier boundarySidentifier=new BoundarySidentifier(new ControlSidentifier());

        while(isRunning){
            System.out.println("###########################################\n"+
                    "#                                         #\n"+
                    "#            MENU PRINCIPAL               #\n"+
                    "#                                         #\n"+
                    "###########################################\n"+
                    "#                                         #\n"+
                    "#            1:ADMINISTRATEUR             #\n"+
                    "#                                         #\n"+
                    "#            2:UTILISATEUR                #\n"+
                    "#                                         #\n"+
                    "#            3:QUITTER                    #\n"+
                    "#                                         #\n"+
                    "#       !! choisissez le Mode !!          #\n"+
                    "#                                         #\n"+
                    "###########################################\n");
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
                    if(boundarySidentifier.verifierIdentite()){
                        res=menuAdmin.menu();
                    }
                    else
                        res=0;
                    if(res==1)
                        isRunning=false;
                    break;
                case 2 :
                    System.out.println("acces menu user");
                    break;
                case 3:
                    isRunning=false;
                    break;
                default:
                    System.out.println("Saisi incorrecte");

            }
        }
        System.out.println("bye bye");


    }
}
