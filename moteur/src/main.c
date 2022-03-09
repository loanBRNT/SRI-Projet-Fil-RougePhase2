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

void EcouteCallback (IvyClientPtr app, void *data, int argc, char **argv)
{
	printf("\nType de recherche : %s", argv[0]);
	printf("\nElement source : %s", argv[1]);
	printf("\nType du fichier : %s", argv[2]);

	char chaine[1000];
	lanceRechercheViaMotCle(argv[1],chaine);
	char chaine_envoie[1100];
	strcpy(chaine_envoie,"Moteur message=");
	strcat(chaine_envoie,chaine);

	IvySendMsg(chaine_envoie);
}

/* callback associated to "Bye" messages */
void StopCallback (IvyClientPtr app, void *data, int argc, char **argv)
{
	// On termine le traitement 
	IvyStop ();
}


int main(int argc, char const *argv[])
{
	/* initialisation */
	IvyInit("Moteur", "Le moteur est sur le reseau", 0, 0, 0, 0);

	/* On Eoute et on traite les messages qui commencent par n'importe quoi */
	IvyBindMsg(EcouteCallback, 0, "^Interface message=(.*) source=(.*) type=(.*)");

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
