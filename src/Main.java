import fr.dgac.ivy.*;

import java.util.Scanner;

public class Main{
    private static Scanner scanner = new Scanner(System.in);
    //Attributs

    //Constructeur

    //Méthodes
    public static void main(String[] args) {
        CommunicationIvy communicationIvy = new CommunicationIvy();

        communicationIvy.lancerCommunication();

        int choix=1;
        while(choix != 0){
            if(communicationIvy.envoieMessage("Interface message=YO nombre=2")){
                System.out.println("mess envoyé");
            } else {
                System.out.println("echec envoie");
            }
            choix = scanner.nextInt();
        }

    communicationIvy.fermerCommunication();
    }
}
