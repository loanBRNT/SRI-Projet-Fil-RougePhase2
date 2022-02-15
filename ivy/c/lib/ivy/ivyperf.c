/*
 *	Ivy perf mesure le temp de round trip
 *
 *	Copyright (C) 1997-2004
 *	Centre d'Études de la Navigation Aérienne
 *
 * 	Main and only file
 *
 *	Authors: François-Régis Colin <fcolin@cena.fr>
 * 	         Yannick Jestin <jestin@cena.fr>
 *
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */

#include "version.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#ifdef WIN32
#include <windows.h>
#ifdef __MINGW32__
#include <regex.h> 
#include <getopt.h>
#endif
#else
#include <sys/time.h>
#include <unistd.h>
#ifdef __INTERIX
extern char *optarg;
extern int optind;
#endif
#endif


#include "ivysocket.h"
#include "ivy.h"
#include "timer.h"
#include "ivyloop.h"
#define MILLISEC 1000.0

const char *mymessages[] = { "IvyPerf", "ping", "pong" };
static double origin = 0;

static double currentTime()
{
        double current;
#ifdef WIN32
        current = GetTickCount();
#else
        struct timeval stamp;
        gettimeofday( &stamp, NULL );
        current = (double)stamp.tv_sec * MILLISEC + (double)(stamp.tv_usec/MILLISEC);
#endif
        return  current;
}

void Reply (IvyClientPtr app, void *user_data, int argc, char *argv[])
{
	IvySendMsg ("pong ts=%s tr=%f", *argv, currentTime()- origin);
}
void Pong (IvyClientPtr app, void *user_data, int argc, char *argv[])
{
	double current = currentTime() - origin ;
	double ts = atof( *argv++ );
	double tr = atof( *argv++ );
	double roundtrip1 = tr-ts;
	double roundtrip2 = current - tr;
	double roundtrip3 = current - ts;
	fprintf(stderr,"roundtrip %f %f %f \n", roundtrip1, roundtrip2, roundtrip3 );
}

void TimerCall(TimerId id, void *user_data, unsigned long delta)
{
	int count = IvySendMsg ("ping ts=%f", currentTime() - origin );
	if ( count == 0 ) fprintf(stderr, "." );
}

void binCB( IvyClientPtr app, void *user_data, int id, char* regexp,  IvyBindEvent event ) 
{
	char *app_name = IvyGetApplicationName( app );
	switch ( event )
	{
	case IvyAddBind:
		printf("Application:%s bind '%s' ADDED\n", app_name, regexp );
		break;
	case IvyRemoveBind:
		printf("Application:%s bind '%s' REMOVED\n", app_name, regexp );
		break;
	case IvyFilterBind:
		printf("Application:%s bind '%s' FILTRED\n", app_name, regexp );
		break;

	}
}
int main(int argc, char *argv[])
{
	long time=200;

	/* Mainloop management */
	if ( argc > 1 ) time = atol( argv[1] );

	IvyInit ("IvyPerf", "IvyPerf ready", NULL,NULL,NULL,NULL);
	IvySetFilter( sizeof( mymessages )/ sizeof( char *),mymessages );
	IvySetBindCallback( binCB, 0 ),
	IvyBindMsg (Reply, NULL, "^ping ts=(.*)");
	IvyBindMsg (Pong, NULL, "^pong ts=(.*) tr=(.*)");
	origin = currentTime();
	IvyStart (0);

	TimerRepeatAfter (TIMER_LOOP, time, TimerCall, (void*)1);
	

	IvyMainLoop ();
	return 0;
}
