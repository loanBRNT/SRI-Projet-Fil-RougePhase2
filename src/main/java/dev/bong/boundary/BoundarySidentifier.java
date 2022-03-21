package dev.bong.boundary;

import dev.bong.control.ControlSidentifier;

import java.io.IOException;

public class BoundarySidentifier {

    private ControlSidentifier controlSidentifier;

    public BoundarySidentifier(ControlSidentifier controlSidentifier){
        this.controlSidentifier=controlSidentifier;
    }



    public boolean verifierIdentite() throws IOException {
        String login;
        String mdp;
        boolean verif=false;
        int essai=3;
        do{
            System.out.println("Veuillez saisir votre identifiant");
            login=Clavier.entrerClavierString();
            System.out.println("Veuillez saisir votre mot de passe");
            mdp=Clavier.entrerClavierString();
            verif= this.controlSidentifier.verifieridentite(login,mdp);
            essai--;
        }while(essai!=0 && verif==false);
        return verif;

    }
}


