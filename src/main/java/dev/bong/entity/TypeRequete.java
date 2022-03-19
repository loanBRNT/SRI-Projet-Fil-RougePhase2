package dev.bong.entity;

public enum TypeRequete {
    RECHERCHE_MOT_CLE, RECHERCHE_FICHIER, RECHERCHE_COULEUR;

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
           default -> {
               return "erreur";
           }
       }
   }
}
