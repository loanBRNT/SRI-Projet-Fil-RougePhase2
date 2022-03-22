/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2000
 *	Centre d'Études de la Navigation Aérienne
 *
 * 	Main functions
 *
 *	Authors: François-Régis Colin <fcolin@cena.dgac.fr>
 *		 Stéphane Chatty <chatty@cena.dgac.fr>
 *
 *	$Id: ivy.h 1383 2006-09-22 09:41:53Z bustico $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */

#ifndef IVY_H
#define IVY_H

#ifdef __cplusplus
extern "C" {
#endif

/* numero par default du bus */

#define DEFAULT_BUS 2010

typedef struct _clnt_lst *IvyClientPtr;

typedef enum { IvyApplicationConnected, IvyApplicationDisconnected } IvyApplicationEvent;
typedef enum { IvyAddBind, IvyRemoveBind, IvyFilterBind, IvyChangeBind } IvyBindEvent;

extern void IvyDefaultApplicationCallback( IvyClientPtr app, void *user_data, IvyApplicationEvent event ) ;
extern void IvyDefaultBindCallback( IvyClientPtr app, void *user_data, int id, char* regexp,  IvyBindEvent event ) ;


/* callback callback appele sur connexion deconnexion d'une appli */
typedef void (*IvyApplicationCallback)( IvyClientPtr app, void *user_data, IvyApplicationEvent event ) ;

/* callback callback appele sur ajout ou suppression d'un bind */
typedef void (*IvyBindCallback)( IvyClientPtr app, void *user_data, int id, char* regexp,  IvyBindEvent event ) ;

/* callback appele sur reception de die */
typedef void (*IvyDieCallback)( IvyClientPtr app, void *user_data, int id ) ;

/* callback appele sur reception de messages normaux */
typedef void (*MsgCallback)( IvyClientPtr app, void *user_data, int argc, char **argv ) ;

/* callback appele sur reception de messages directs */
typedef void (*MsgDirectCallback)( IvyClientPtr app, void *user_data, int id, char *msg ) ;

/* identifiant d'une expression reguliere ( Bind/Unbind ) */
typedef struct _msg_rcv *MsgRcvPtr;

/* filtrage des regexps */
void IvySetFilter( int argc, const char **argv);

void IvyInit(
	 const char *AppName,		/* nom de l'application */
	 const char *ready,		/* ready Message peut etre NULL */
	 IvyApplicationCallback callback, /* callback appele sur connection deconnection d'une appli */
	 void *data,			/* user data passe au callback */
	 IvyDieCallback die_callback,	/* last change callback before die */
	 void *die_data 		/* user data */
	 );
  
void IvySetBindCallback(	 
			  IvyBindCallback bind_callback,
			  void *bind_data );

void IvyStart (const char*);
void IvyStop ();

/* query sur les applications connectees */
char *IvyGetApplicationName( IvyClientPtr app );
char *IvyGetApplicationHost( IvyClientPtr app );
IvyClientPtr IvyGetApplication( char *name );
char *IvyGetApplicationList(const char *sep);
char **IvyGetApplicationMessages( IvyClientPtr app); /* demande de reception d'un message */

MsgRcvPtr IvyBindMsg( MsgCallback callback, void *user_data, const char *fmt_regexp, ... ); /* avec sprintf prealable */
MsgRcvPtr IvyChangeMsg (MsgRcvPtr msg, const char *fmt_regex, ... ); /* avec sprintf prealable */

void IvyUnbindMsg( MsgRcvPtr id );

/* emission d'un message d'erreur */
void IvySendError( IvyClientPtr app, int id, const char *fmt, ... );

/* emmission d'un message die pour terminer l'application */
void IvySendDieMsg( IvyClientPtr app );

/* emission d'un message retourne le nb effectivement emis */

int IvySendMsg( const char *fmt_message, ... );		/* avec sprintf prealable */

/* Message Direct Inter-application */

void IvyBindDirectMsg( MsgDirectCallback callback, void *user_data);
void IvySendDirectMsg( IvyClientPtr app, int id, char *msg );

#ifdef __cplusplus
}
#endif

#endif
