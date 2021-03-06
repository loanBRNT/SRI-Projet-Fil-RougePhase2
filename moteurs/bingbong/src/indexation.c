#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include<math.h>

#include "../include/indexation.h"
#include "../include/descripteurAudio.h"
#include "../include/img.h"
#include "../include/indexation_txt.h"
#include "../include/pile_Audio.h"
#include "../include/pile_Img.h"
#include "../include/pile_Texte.h"
#include "../include/admin.h"
/* 
 ----------------------- Signature -------------------------
|                                                           |
|       Auteur : GAUDILLAT Eliott                           |
|       Date de creation : 10/12/21                         |
|       Date de derniere MAJ : 26/01/22                     |
|                                                           |
 ----------------------------------------------------------- 
*/

void changementNom(char* nom){ 
    char nom_modif[100];
    char commande[1000];
    char carac=32;
    char CHEMIN_TEXTE [100] =  "../Database/Texte/";
    int i = 0,j=0;

    carac = nom[0];
    while(carac != '\0'){
        if(carac == 130 || carac == 136 || carac == 137 || carac == 138 || carac == -87 || carac == -88 || carac == -86 || carac==-120 || carac==-119 || carac == -118 || carac==-117){
            nom_modif[j]=101; //le e
        }
        else if(carac == 131 || carac == 132 || carac==133 || carac == -94 || carac == -96 || carac == -95 || carac==-128 || carac==-127 || carac==-126 || carac==-125 || carac==-124){
            nom_modif[j]=97; //le a
        }
        else if (carac == -89){
            nom_modif[j]=99; //le c
        }
        else if(carac == 150 || carac == 151 || carac == -69 || carac == -68 || carac == -71 || carac == -70 || carac == -103 || carac == -102 || carac == -101 || carac == -100){
            nom_modif[j]=117; //le u
        }
        else if(carac == 147 || carac == 148 || carac==149){
            nom_modif[j]=111;
        } else if (carac == -61) { //marque le debut de chaque carac inconnu
            j--;
        } else if (carac == -84 || carac == -83 || carac == -82 || carac == -81 || carac == -85 || carac==-116 || carac==-115 || carac==-114 || carac==-113) {
            nom_modif[j] = 105; //le i
        } else if(carac == -76 || carac == -74 || carac== -77 || carac ==-106 || carac ==-107 || carac ==-108 || carac ==-109 || carac == -110){
        	nom_modif[j] = 111; // le o
        } else if(carac == 44 || carac==59){
        	j--;
        }
        else {
            nom_modif[j] = carac;
        }
        i++;
        j++;
        carac = nom[i];
    }

    nom_modif[j] = '\0';

    
    strcpy(commande,"mv ");
    strcat(commande,CHEMIN_TEXTE);
    strcat(commande,nom);
    strcat(commande," ");
    strcat(commande,CHEMIN_TEXTE);
    strcat(commande,nom_modif);
    fflush(stdout);
    system(commande);

    strcpy(nom,nom_modif);
    
	
}

/* VerificationTraitee(char* nom_fic) verifie si le fichier nom_fic ?? d??j?? ??t?? index?? 
return 1 si trait?? 0 sinon*/
int VerificationTraitee(char* nom_fic){
	// V1 => si fichier perso meme nom que un fichier de la base de donn??es alors renvoie deja traitee
	FILE * ptr_fic;
	char commande[1000] ;
	char CHEMIN_FICHIER_INDEXEE[100];
	char valeur[5];
	int traite;
	// on verife l'extension du texte pour savoir si on a affaire un fichier audio ,texte ou image et donc savoir quelle fichier contenant les fichiers d??j?? trait?? a ouvrir
	if (strstr(nom_fic, ".xml")){
    	strcpy( CHEMIN_FICHIER_INDEXEE,"../Database/Descripteur/liste_base_texte.txt");
    }
    else if(strstr(nom_fic, ".bin")){
		strcpy( CHEMIN_FICHIER_INDEXEE,"../Database/Descripteur/liste_base_audio.txt");
    }
    else if(strstr(nom_fic, ".txt")){
    	strcpy( CHEMIN_FICHIER_INDEXEE,"../Database/Descripteur/liste_base_image.txt");
    }
    else{
    	fprintf(stderr, "ERREUR :  PB avec type du fichier\n");
    	return 0;
    } 
    // execution commande unix : cat CHEMIN_FICHIER_INDEXEE |grep nom_fic |wc -l >fic-temp2
    // cat renvoie le contenue de liste_base choisi , grep renvoie les lignes contenant le nom du fichier  et wc -l conmpte le nombre de ligne recu et ecrit le resultat dans fictemp2
    strcpy(commande, "cat ");
	strcat(commande, CHEMIN_FICHIER_INDEXEE);
	strcat(commande, " | grep ");
	strcat(commande, nom_fic);
	strcat(commande, " | wc -l >fic_temp2 ");
	//affichage utile pour verifier bonne execution de la commande unix ( non utile pour le client donc mis en commentaire)
	//printf("execution de %s\n", commande);
	fflush(stdout);
	system(commande);

// ouverture de fic-temp2 pour lire la valeur de la commande si 1 alors le fichier est d??j?? trait?? si 0 non trait??.
	ptr_fic = fopen("fic_temp2", "r");

	if( ptr_fic != NULL){
		fscanf(ptr_fic, "%s", valeur);
		if(strstr(valeur, "1")){
			traite=1;
		}
		else
			traite=0;
	}
	else{
   		fprintf(stderr, "ERREUR :  PB avec liste_rep\n");
   	}
   	fclose(ptr_fic);
	return traite;	
		
};






void Indexation(){

	// mettre un chemin vers un rpertoire qui ne contient que des fichiers//
	char CHEMIN_TEXTE [100] =  "../Database/Texte/";
	char CHEMIN_AUDIO [100] =  "../Database/Audio/";
	char CHEMIN_IMAGE_NB [100] =  "../Database/Image/NB/";
	char CHEMIN_IMAGE_RGB [100] =  "../Database/Image/RGB/";
	char commande[1000] ;
	char nom_fic[100];
	char CHEMIN_INDEXATION [100];
	char* id = malloc(10*sizeof(char));	
	FILE * ptr_fic; 
	PILE_Audio pA =init_PILE_Audio();
	PILE_Img pI=init_PILE_Img();
	PILE_Texte pT=init_PILE_Texte();

	system("chmod 777 ../.config");

	// recuperation des valeurs du .config
	int NbMot=recupNbMotParTexteDuConfig();
	int OccMot=recupSeuilOccurenceMotDuConfig();
	int intervalleAudio=recupNbIntervalleDuConfig();
	int nbrPointAudio =recupNbPointsDuConfig();
	int bitQ=recupNbBitsDuConfig();






	// creation de la commande a executer 

	//------------------------------------------------------//
	// RECUPERATION DU CONTENU DU REPERTOIRE  CHEMIN_TEXTE //
	//------------------------------------------------------//
	strcpy(commande, "ls -l ");
	strcat(commande, CHEMIN_TEXTE);
	strcat(commande, " > fic_temp"); 
	//printf("execution de %s\n", commande); 
	fflush(stdout);
	system(commande);
	//---------------------------------------------------------------------//
	// AFFICHAGE DU CONTENU DU FICHIER CREE LORS DE LA PRECEDENTE COMMANDE//
	//---------------------------------------------------------------------//
	//system("cat fic_temp");
	//printf("---------------------------------\n");

//ouverture de fic-temp en mode read
	ptr_fic = fopen("fic_temp", "r");

	if( ptr_fic != NULL){   

	    //POUR CHAQUE LIGNE CONTENUE DANS LE FICHIER, ON RECUPERE LE NOM DU FICHIER //
 	    // FORMAT DE LA LIGNE :  -rw-r--r--   1 ferrane  minfg       4834 Sep 23  2008 28-Danse___Robyn_Orlin_et.xml //

	    fscanf( ptr_fic, "%*s %*s");    //  SAUTER LA PREMIERE LIGNE CONSTITUEE DE 2 CHAINES total xxxx //
    
	    fscanf(ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);  // %*s INGNORE LA CHAINE LUE //
	    // tant qu'on est pas a la fin du fichier on traite la donnee recuperer
 	    while ( !feof(ptr_fic) ){
 	    	//enleve les caractere accentue
 	    	changementNom(nom_fic);
 	    	// verifie si nom-fic a deja ete index?? ou pas
 	    	if(VerificationTraitee(nom_fic)){
 	    		//affichage de verification lors du developpement ( non necessaire lors de l'utilisation par le client)
 	    		//printf("fichier deja indexee\n");
 	    	}
 	    	else{
     			if (strstr(nom_fic, ".xml")){
     				//concatenation pour avoir une chaine conteanant le chemin menant au fichier depuis le prog.out
    				strcpy(CHEMIN_INDEXATION,"../Database/Texte/");	
    				strcat(CHEMIN_INDEXATION,nom_fic);
    				//indexation du fichier nom_fic
    				DescripteurTxt Dt =indexationTxt(CHEMIN_INDEXATION, NbMot,OccMot);
    				pT=emPILE_Texte(pT,Dt);
    				sprintf(id,"%d", Dt.ID);
    				//ecriture du nom du fichier et de son id unique dans la liste des fichiers trait??s
    				strcpy(commande, "echo ");
    				strcat(commande,id);
    				strcat(commande," " );
					strcat(commande, nom_fic);
					strcat(commande, " >> ../Database/Descripteur/liste_base_texte.txt");
					system(commande); 
    			}
    			else{
    			fprintf(stderr, "ERREUR :  PB avec type du fichier\n");
    			}    		
 	    	}
		 	// nom de fichier suivant //
			fscanf( ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);
    		}
    	fclose(ptr_fic);
    }
	else
	{
   		fprintf(stderr, "ERREUR :  PB avec liste_rep\n");
	}





	//------------------------------------------------------//
	// RECUPERATION DU CONTENU DU REPERTOIRE  CHEMIN_AUDIO //
	//------------------------------------------------------//

	strcpy(commande, "ls -l ");
	strcat(commande, CHEMIN_AUDIO);
	strcat(commande, "| grep bin$ > fic_temp"); 
	// affichage pour verifier deroulemement commande unix
	//printf("execution de %s\n", commande); 
	fflush(stdout);
	system(commande);

	//---------------------------------------------------------------------//
	// AFFICHAGE DU CONTENU DU FICHIER CREE LORS DE LA PRECEDENTE COMMANDE//
	//---------------------------------------------------------------------//
	//system("cat fic_temp");
	//printf("---------------------------------\n");
	ptr_fic = fopen("fic_temp", "r");
	if( ptr_fic != NULL){   

	    //POUR CHAQUE LIGNE CONTENUE DANS LE FICHIER, ON RECUPERE LE NOM DU FICHIER //
 	    // FORMAT DE LA LIGNE :  -rw-r--r--   1 ferrane  minfg       4834 Sep 23  2008 28-Danse___Robyn_Orlin_et.xml //
    
	    fscanf(ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);  // %*s INGNORE LA CHAINE LUE //
	   
	   // tant qu'on est pas a la fin du fichier on traite la donnee recuperer
 	    while ( !feof(ptr_fic) ){
 	    	if(VerificationTraitee(nom_fic)){
 	    		//affichage de verification lors du developpement ( non necessaire lors de l'utilisation par le client)
 	    		//printf("fichier deja indexee\n");
 	    	}
 	    	else{
    			if(strstr(nom_fic, ".bin")){
    				//concatenation pour avoir une chaine conteanant le chemin menant au fichier depuis le prog.out
    				strcpy(CHEMIN_INDEXATION,"../Database/Audio/");	
    				strcat(CHEMIN_INDEXATION,nom_fic);
    				//indexation du fichier nom_fic
    				DescripteurAudio DA =IndexationFichierAudio(CHEMIN_INDEXATION,intervalleAudio,nbrPointAudio);
    				pA=emPILE_Audio(pA,DA);
    				sprintf(id,"%d", DA->identifiant);
    				//ecriture du nom du fichier et de son id unique dans la liste des fichiers trait??s
    				strcpy(commande, "echo ");
    				strcat(commande,id);
    				strcat(commande," " );
					strcat(commande, nom_fic);
					strcat(commande, " >> ../Database/Descripteur/liste_base_audio.txt");
					system(commande); 
    			}
    			else{
    			fprintf(stderr, "ERREUR :  PB avec type du fichier\n");
    			}    		
 	    	}
		 	// nom de fichier suivant //
			fscanf( ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);
    	}
    	fclose(ptr_fic);
    }
	else
	{
   		fprintf(stderr, "ERREUR :  PB avec liste_rep\n");
	}









	//------------------------------------------------------//
	// RECUPERATION DU CONTENU DU REPERTOIRE CHEMIN_IMAGE_NB//
	//------------------------------------------------------//

	strcpy(commande, "ls -l ");
	strcat(commande, CHEMIN_IMAGE_NB);
	strcat(commande, "| grep txt$ > fic_temp"); 
	// affichage pour verifier deroulemement commande unix
	// printf("execution de %s\n", commande);
	fflush(stdout); 
	system(commande);


	//system("cat fic_temp");
	//printf("---------------------------------\n");
	ptr_fic = fopen("fic_temp", "r");

	if( ptr_fic != NULL){   

	    //POUR CHAQUE LIGNE CONTENUE DANS LE FICHIER, ON RECUPERE LE NOM DU FICHIER //
 	    // FORMAT DE LA LIGNE :  -rw-r--r--   1 ferrane  minfg       4834 Sep 23  2008 28-Danse___Robyn_Orlin_et.xml //
   
	    fscanf(ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);  // %*s INGNORE LA CHAINE LUE //
 	    // tant qu'on est pas a la fin du fichier on traite la donnee recuperer
 	    while ( !feof(ptr_fic) ){
 	    	if(VerificationTraitee(nom_fic)){
 	    		//affichage de verification lors du developpement ( non necessaire lors de l'utilisation par le client)
 	    		//printf("fichier deja indexee\n");
 	    	}
 	    	else{
    			if(strstr(nom_fic, ".txt")){
    				//concatenation pour avoir une chaine conteanant le chemin menant au fichier depuis le prog.out
    				strcpy(CHEMIN_INDEXATION,"../Database/Image/NB/");	
    				strcat(CHEMIN_INDEXATION,nom_fic);
    				//indexation du fichier nom_fic
    				Descripteur DI=indexer_image(CHEMIN_INDEXATION, bitQ);
    				pI=emPILE_Img(pI,DI);
    				sprintf(id,"%d", DI.ID);
    				//ecriture du nom du fichier et de son id unique dans la liste des fichiers trait??s
    				strcpy(commande, "echo ");
    				strcat(commande, id );
    				strcat(commande," " );
					strcat(commande, nom_fic);
					strcat(commande, " >> ../Database/Descripteur/liste_base_image.txt");
					system(commande); 
    			}
    			else{
    			fprintf(stderr, "ERREUR :  PB avec type du fichier\n");
    			}    		
 	    	}
		 	// nom de fichier suivant //
			fscanf( ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);
    		}
    	fclose(ptr_fic);
    }
	else
	{
   		fprintf(stderr, "ERREUR :  PB avec liste_rep\n");
	}


	//-------------------------------------------------------//
	// RECUPERATION DU CONTENU DU REPERTOIRE CHEMIN_IMAGE_RGB//
	//-------------------------------------------------------//

	strcpy(commande, "ls -l ");
	strcat(commande, CHEMIN_IMAGE_RGB);
	strcat(commande, "| grep txt$ > fic_temp"); 
	//printf("execution de %s\n", commande);
	fflush(stdout); 
	system(commande);


	//---------------------------------------------------------------------//
	// AFFICHAGE DU CONTENU DU FICHIER CREE LORS DE LA PRECEDENTE COMMANDE//
	//---------------------------------------------------------------------//
	//system("cat fic_temp");
	//printf("---------------------------------\n");
	ptr_fic = fopen("fic_temp", "r");

	if( ptr_fic != NULL){   

	    //POUR CHAQUE LIGNE CONTENUE DANS LE FICHIER, ON RECUPERE LE NOM DU FICHIER //
 	    // FORMAT DE LA LIGNE :  -rw-r--r--   1 ferrane  minfg       4834 Sep 23  2008 28-Danse___Robyn_Orlin_et.xml //
   
	    fscanf(ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);  // %*s INGNORE LA CHAINE LUE //
	    // tant qu'on est pas a la fin du fichier on traite la donnee recuperer
 	    while ( !feof(ptr_fic) ){
 	    	if(VerificationTraitee(nom_fic)){
 	    		//affichage de verification lors du developpement ( non necessaire lors de l'utilisation par le client)
 	    		//printf("fichier deja indexee\n");
 	    	}
 	    	else{
    			if(strstr(nom_fic, ".txt")){
    				//concatenation pour avoir une chaine conteanant le chemin menant au fichier depuis le prog.out
    				strcpy(CHEMIN_INDEXATION,"../Database/Image/RGB/");	
    				strcat(CHEMIN_INDEXATION,nom_fic);
    				//indexation du fichier nom_fic
    				Descripteur DI=indexer_image(CHEMIN_INDEXATION, bitQ);
    				pI=emPILE_Img(pI,DI);
    				sprintf(id,"%d", DI.ID);
    				//ecriture du nom du fichier et de son id unique dans la liste des fichiers trait??s
    				strcpy(commande, "echo ");
    				strcat(commande, id );
    				strcat(commande," " );
					strcat(commande, nom_fic);
					strcat(commande, " >> ../Database/Descripteur/liste_base_image.txt");
					system(commande); 
    			}
    			else{
    			fprintf(stderr, "ERREUR :  PB avec type du fichier\n");
    			}    		
 	    	}
		 	// nom de fichier suivant //
			fscanf( ptr_fic, "%*s %*s %*s %*s %*s %*s %*s %*s %s", nom_fic);
    		}
    	fclose(ptr_fic);
    }
	else
	{
   		fprintf(stderr, "ERREUR :  PB avec liste_rep\n");
	}






	//---------------------------------------------------------------------//
	// Sauvegarde des descripteurs dans un fichier                         //
	//---------------------------------------------------------------------//

/* Meme procede pour les 3 piles si la pile n'est pas vide alors on ouvre le fichier de sauvegarde associ??  on depile la pile dans le fichier puis on ferme le fichier*/
	FILE * f;
	if(!PILE_Audio_estVide(pA)){
		f=fopen("../Database/Descripteur/dA.txt","a+");
		while(!PILE_Audio_estVide(pA)){
			pA=dePILE_Audio(pA,f);
		}
		fclose(f);
	}


	if(!PILE_Img_estVide(pI)){
		f=fopen("../Database/Descripteur/dI.txt","a+");
		while(!PILE_Img_estVide(pI)){
			pI=dePILE_Img(pI,f);
		}
		fclose(f);
	}


	if(!PILE_Texte_estVide(pT)){
		f=fopen("../Database/Descripteur/dT.txt","a+");
		while(!PILE_Texte_estVide(pT)){
			pT=dePILE_Texte(pT,f);
		}
		fclose(f);
		// on appel la fonction sauvegarderMotCle() pour sauver les termes present dans les descripteurs texte
		sauvegardeMotCle();
	}

// suppression des fichiers temporaire 
	strcpy(commande, "rm fic_temp fic_temp2 ");
	system(commande); 

};
