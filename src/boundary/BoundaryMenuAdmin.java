package boundary;

import control.ControlMenuAdmin;

import java.io.IOException;

public class BoundaryMenuAdmin {
    private ControlMenuAdmin controlMenuAdmin;
    private BoundaryModifierConfig boundaryModifierConfig;

    public BoundaryMenuAdmin(ControlMenuAdmin controlMenuAdmin,BoundaryModifierConfig boundaryModifierConfig){
        this.controlMenuAdmin=controlMenuAdmin;
        this.boundaryModifierConfig=boundaryModifierConfig;
    }



    public int menu() throws IOException {
        boolean isRunning =true;
        boolean saisie=false;
        int choix=0;
        int res=0;

        while(isRunning){
            System.out.println("###########################################\n"+
                    "#                                         #\n"+
                    "#  Bienvenue sur le MODE ADMINISTRATEUR   #\n"+
                    "#                                         #\n"+
                    "###########################################\n"+
                    "#                                         #\n"+
                    "#         1: OUVRIR PANNEAU CONFIG        #\n"+
                    "#                                         #\n"+
                    "#         2: LANCER INDEXATION            #\n"+
                    "#                                         #\n"+
                    "#         3: RETOUR MENU PRINCIPAL        #\n"+
                    "#                                         #\n"+
                    "#         4: QUITTER                      #\n"+
                    "#                                         #\n"+
                    "#        << choisissez un menu >>         #\n"+
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
                    this.boundaryModifierConfig.afficheConfig();
                    break;
                case 2 :
                    System.out.println("indexation en cours");
                    this.controlMenuAdmin.indexation();
                    break;
                case 3:
                    isRunning=false;
                    break;
                case 4 :
                    isRunning=false;
                    res=1;
                default:
                    System.out.println("Saisi incorrecte");

            }
        }
        return res;
    }
}
