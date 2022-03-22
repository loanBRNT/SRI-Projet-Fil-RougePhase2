package dev.bong.entity;

public enum TypeRequete {
    RECHERCHE_MOT_CLE, RECHERCHE_FICHIER, RECHERCHE_COULEUR, INDEXATION;

   public String toString(){
       switch(this){
           case RECHERCHE_MOT_CLE -> {
               return "rechercheMotCle";
           }
           case RECHERCHE_FICHIER -> {
               return "rechercheFichier";
           }
           case RECHERCHE_COULEUR -> {
               return "rechercheCouleur";
           }
           case INDEXATION -> {
               return "indexation";
           }
           default -> {
               return "erreur";
           }
       }
   }
}
