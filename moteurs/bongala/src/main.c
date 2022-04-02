#include <stdio.h>
#include <stdlib.h>


#include "../include/admin.h"
#include "../include/pwd.h"
#include "../include/interface.h"
#include "../include/indexation.h"
#include "../include/indexation_txt.h"
#include "../include/img.h"
#include "../include/pile_Img.h"
#include "../include/descripteurAudio.h"
#include "../include/pile_Audio.h"
#include "../include/pile_Texte.h"
#include "../include/recherche.h"
#include "../include/comparaison.h"

#include "../include/pcre.h"

#include "../include/ivy.h"
#include "../include/ivyloop.h"

void ivyEnvoie(char* chaine_resultat, char* mot){
	char chaine_envoie[1100];
	strcpy(chaine_envoie,"Moteur mot=");
	strcat(chaine_envoie,mot);
	strcat(chaine_envoie," liste=");
	strcat(chaine_envoie,chaine_resultat);

	printf("\n%ld : %s|\n",strlen(chaine_envoie), chaine_envoie);

	IvySendMsg(chaine_envoie);
}

void RechercheCallback (IvyClientPtr app, void *data, int argc, char **argv)
{

	char chaine[1000];
	char sauvNom[100];
	strcpy(sauvNom,argv[1]);

	if (!strcmp(argv[0],"rechercheMotCle")){
		lanceRechercheViaMotCle(argv[1],chaine);
		ivyEnvoie(chaine, sauvNom);
	} else if (!strcmp(argv[0],"rechercheFichier")){
		lanceRechercheViaNom(argv[1],chaine);
		ivyEnvoie(chaine, sauvNom);
	} else if (!strcmp(argv[0],"rechercheCouleur")){
		printf("recherche a coder");
		//ivyEnvoie(chaine, argv[1]);
	} else if (!strcmp(argv[0], "indexation")){
		if (!strcmp(argv[1],"reset")){
			system("echo ' ' > ./Database/Descripteur/liste_base_image.txt");
			system("echo ' ' > ./Database/Descripteur/dI.txt");
			system("echo ' ' > ./Database/Descripteur/liste_base_audio.txt");
			system("echo ' ' > ./Database/Descripteur/dA.txt");
			system("echo ' ' > ./Database/Descripteur/liste_base_texte.txt");
			system("echo ' ' > ./Database/Descripteur/dT.txt");
		}
		Indexation();
		IvySendMsg("Moteur message=indexation ok");
	}
	else {
		strcpy(chaine,"Requete non conforme");
	}
}

void TestCallback (IvyClientPtr app, void *data, int argc, char **argv)
{
	if (!strcmp(argv[0],"test")){
		printf("Moteur message=testOk");
		IvySendMsg("Moteur message=testOk");
	}
}

/* callback associated to "Bye" messages */
void StopCallback (IvyClientPtr app, void *data, int argc, char **argv)
{
	// On termine le traitement 
	IvyStop ();
}

int main(int argc, char const *argv[]){
	
	/* initialisation */
	IvyInit("Moteur", "Le moteur est sur le reseau", 0, 0, 0, 0);

	/* On Eoute et on traite les messages qui commencent par n'importe quoi */
	IvyBindMsg(RechercheCallback, 0, "^Interface message=(.*) source=(.*)");

	/* On Eoute et on traite les messages qui commencent par n'importe quoi */
	IvyBindMsg(TestCallback, 0, "^Interface message=(.*)");

	/* On Eoute et on traite les messages 'Bye' */
	IvyBindMsg(StopCallback, 0, "^Stop$");

	IvyStart("127.255.255.255:2010"); // On lance l'agent sur le bus ivy

	/* main loop */
	IvyMainLoop();
	return 0;
}





/*
int main(int argc, char const *argv[])
{

	// lance l'affichage du menu principal
	//  puis demande le numero du menu souhaite et lance la fonction de gestion du menu selectionne 
	//  si le numero n'est pas bon on redemande un numero de menu
	//  le choix numero 0 modifie la valeur de la variable event afin de sortir de la boucle et pouvoir arreter le programme
	srand(time(NULL));
	afficheAccueil();
	int event = 1 ;
	long choixMenu;
	while(event != -1){
		afficheMenuPrincipal();
		choixMenu=lireLong();
		printf("\n");
		switch(choixMenu)
		{



			case 1:
				if (menuAdminVerif()== 0) event = -1;
				break;
			case 2:
				if (menuUtilisateur() == 0) event = -1;
				break;
			case 3:
				event = -1;
				break;
			default:
				afficheErreurMenu();
				break;
		}
	printf("\n");
	}
	printf("###########################################\n");
 	printf("#       fermeture de l'application        #\n");
 	printf("###########################################\n");


	return 0;

}*/

/*
int main(int argc, char *argv[])
{
	char chaine[1000];
	lanceRechercheViaNom(*(argv+1),chaine);
	printf("%s\n",chaine);
	return 0;
}*/

/*
int main(int argc, char *argv[])
{
	char chaine[10000];
	char adresse[100];
	strcpy(adresse, "./Requete/copiesMauvaisCompte.xml");
	lanceRechercheViaAdresse(adresse,chaine);
	printf("\n==========\n%s",chaine);
	return 0;
}
*/
