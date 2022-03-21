package dev.bong.control;

import dev.bong.entity.Requete;
import dev.bong.entity.TypeRequete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlRechercheMotCle {
    private int pourcentgeFinit = 0;

    public void rechercheMultiple(List<String> motcle, List<String> motBan) {
        // creation des set servant a recuperer les resultats des recherches
        Set<String> resMotCle;
        Set<String> resMotBan;
        Set<String> resTotal = new HashSet<>();

        // appel des fonctions de recherches
        resMotCle=rechercheMotCle(motcle);
        resMotBan=rechercheMotCle(motBan);


        // ajout des recherches a polarité positives et suppression des polarité négatives
        resTotal.addAll(resMotCle);
        resTotal.removeAll(resMotBan);

        // affichage du resultat total
        System.out.println("resultat final : \n"+resTotal);

        //envoie resultats


    }

    public Set<String> rechercheMotCle(List<String> motCle) {
        ControlRequete controlRequete = new ControlRequete(TypeRequete.RECHERCHE_MOT_CLE);
        List<String> res = new ArrayList<>();
        Set<String> resTotal = new HashSet<>();


        //LANCER LA COM
        controlRequete.lancerCommunicationBus();

        controlRequete.creerEtenvoyerListeRequete(motCle);

        while (!controlRequete.touteRequeteFinit()){
            pourcentgeFinit = controlRequete.nombreRequeteFinit();
            System.out.println(pourcentgeFinit); //ne marche pas si je l'affiche pas
        }
        System.out.println(controlRequete.getListeResultat());

        res=controlRequete.getListeResultat();
        /* test en attedant ivy ok
        String resultatIvy="momo, ; christophe, TestJean.xml,05-Jean-Christophe_Bette_a_dû_attendre.xml; jeanjean, ;\n" +
                "         jeanjean, ; jean, TestJean.xml,12-Jean-Marie_Sevestre_libraire_et_PDG.xml,05-Jean-Christophe_Bette_a_dû_attendre.xml;\n" +
                "          chris, ; mon, ; moti, ; lala, ";
        res=List.of(resultatIvy.split(";"));

         */
        for (String resultatRecherche : res) {
            ArrayList<String> fichiersTrouvee = new ArrayList<>(List.of(resultatRecherche.split(",")));
            fichiersTrouvee.remove(0);
            resTotal.addAll(fichiersTrouvee);
        }

        //STOPPER LA COM
        controlRequete.fermerCommunicationBus();

        for (String titre : resTotal) {
            System.out.println(titre);
        }

        /* exemple reception
        [momo, ; christophe, TestJean.xml,05-Jean-Christophe_Bette_a_dû_attendre.xml; jeanjean, ;
         jeanjean, ; jean, TestJean.xml,12-Jean-Marie_Sevestre_libraire_et_PDG.xml,05-Jean-Christophe_Bette_a_dû_attendre.xml;
          chris, ; mon, ; moti, ; lala, ]

         */
        return resTotal;
    }


}
