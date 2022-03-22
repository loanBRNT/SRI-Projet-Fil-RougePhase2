package dev.bong.control;


import dev.bong.entity.TypeRequete;

import java.util.List;

public class ControlRechercheFichier {

    public void rechercheFichier(List<String> nomFichiers, List<Integer> polarite){
        ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_FICHIER);

        //Lancer la Com
        controlRequete.lancerCommunicationBus();










        //Stopper la Com
        controlRequete.fermerCommunicationBus();
    }
}
