/*
 *
 *	Ivy, C interface
 *
 *	Copyright 1997-2000
 *	Centre d'Etudes de la Navigation Aerienne
 *
 *	Main functions
 *
 *	Authors: Francois-Regis Colin,Stephane Chatty
 *
 *	$Id: ivy.c 1383 2006-09-22 09:41:53Z bustico $
 *
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */


#include <stdlib.h>
#ifdef WIN32
#include <windows.h>
#else
#include <sys/time.h>
#include <arpa/inet.h>
#endif
#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include <ctype.h>

#include <fcntl.h>

#include "intervalRegexp.h"
#include "ivychannel.h"
#include "ivysocket.h"
#include "list.h"
#include "ivybuffer.h"
#include "ivydebug.h"
#include "ivybind.h"
#include "ivy.h"

#define VERSION 3

#define MAX_MATCHING_ARGS 40

#define ARG_START "\002"
#define ARG_END "\003"

#define DEFAULT_DOMAIN 127.255.255.255

/* stringification et concatenation du domaine et du port en 2 temps :
 * Obligatoire puisque la substitution de domain, et de bus n'est pas
 * effectuée si on stringifie directement dans la macro GenerateIvyBus */
#define str(bus) #bus
#define GenerateIvyBus(domain,bus) str(domain)":"str(bus)
static char* DefaultIvyBus = GenerateIvyBus(DEFAULT_DOMAIN,DEFAULT_BUS);

typedef enum {

	Bye,			/* l'application emettrice se termine */
	AddRegexp,		/* expression reguliere d'un client */
	Msg,			/* message reel */
	Error,			/* error message */
	DelRegexp,		/* Remove expression reguliere */
	EndRegexp,		/* end of the regexp list */
	StartRegexp,		/* debut des expressions */
	DirectMsg,		/* message direct a destination de l'appli */
	Die,			/* demande de terminaison de l'appli */
	Ping,			/* message de controle ivy */
	Pong			/* ivy doit renvoyer ce message à la reception d'un ping */
} MsgType;	

typedef struct _msg_snd *MsgSndPtr;

struct _msg_rcv {			/* requete d'emission d'un client */
	MsgRcvPtr next;
	int id;
	const char *regexp;		/* regexp du message a recevoir */
	MsgCallback callback;		/* callback a declanche a la reception */
	void *user_data;		/* stokage d'info client */
};

struct _msg_snd {			/* requete de reception d'un client */
	MsgSndPtr next;
	int id;
	char *str_regexp;		/* la regexp sous forme inhumaine */
	IvyBinding binding;		/* la regexp sous forme machine */
};

struct _clnt_lst {
	IvyClientPtr next;
	Client client;			/* la socket  client */
	MsgSndPtr msg_send;		/* liste des requetes recues */
	char *app_name;			/* nom de l'application */
	unsigned short app_port;	/* port de l'application */
};

/* flag pour le debug en cas de Filter de regexp */
int debug_filter = 0;

/* server  pour la socket application */
static Server server;

/* numero de port TCP en mode serveur */
static unsigned short ApplicationPort;

/* numero de port UDP */
static unsigned short SupervisionPort;

/* client pour la socket supervision */
static Client broadcast;

static const char *ApplicationName = 0;
static const char *ApplicationID = 0;


/* callback appele sur reception d'un message direct */
static MsgDirectCallback direct_callback = 0;
static  void *direct_user_data = 0;

/* callback appele sur changement d'etat d'application */
static IvyApplicationCallback application_callback;
static void *application_user_data = 0;

/* callback appele sur ajout suppression de regexp */
static IvyBindCallback application_bind_callback;
static void *application_bind_data = 0;

/* callback appele sur demande de terminaison d'application */
static IvyDieCallback application_die_callback;
static void *application_die_user_data = 0;

/* liste des messages a recevoir */
static MsgRcvPtr msg_recv = 0;

/* liste des clients connectes */
static IvyClientPtr clients = 0;

static const char *ready_message = 0;
static void substituteInterval (IvyBuffer *src);

/*
 * function like strok but do not eat consecutive separator
 * */
static char * nextArg( char *s, const char *separator )
{
	static char *start = NULL;
	static char *end = NULL;
	if ( s ) 
	{
		end = s;
	}
	start = end;

	while ( *end && *end != *separator )
		end++;
	if ( *end == *separator ) *end++ = '\0';  
	if ( end == start ) return NULL;
	return start;
}
static int MsgSendTo( Client client, MsgType msgtype, int id, const char *message )
{
	return SocketSend( client, "%d %d" ARG_START "%s\n", msgtype, id, message);
}

static void IvyCleanup()
{
	IvyClientPtr clnt,next;

	/* destruction des connexions clients */
	IVY_LIST_EACH_SAFE( clients, clnt, next )
	{
		/* on dit au revoir */
		MsgSendTo( clnt->client, Bye, 0, "" );
		SocketClose( clnt->client );
		IVY_LIST_EMPTY( clnt->msg_send );
	}
	IVY_LIST_EMPTY( clients );

	/* destruction des sockets serveur et supervision */
	SocketServerClose( server );
	SocketClose( broadcast );
}

static int MsgCall (const char *message, MsgSndPtr msg,  IvyClientPtr client)
{
	int waiting = 0;
	static IvyBuffer buffer = { NULL, 0, 0 }; /* Use satic mem to eliminate multiple call to malloc /free */
	int err;
	int index;
	int arglen;
	const char *arg;
  	
	int rc= IvyBindingExec( msg->binding, message );
	
	if (rc<1) return 0; /* no match */
	
	TRACE( "Sending message id=%d '%s'\n",msg->id,message);

	buffer.offset = 0;
	// il faut essayer d'envoyer le message en une seule fois sur la socket
	// pour eviter au maximun de passer dans le select plusieur fois par message du protocole Ivy
	// pour eviter la latence ( PB de perfo detecte par ivyperf ping roudtrip )
	err = make_message_var( &buffer, "%d %d" ARG_START ,Msg, msg->id);

	TRACE( "Send matching args count %d\n",rc);

	for(  index=1; index < rc ; index++ )
	{
		IvyBindingMatch( msg->binding, message, index, &arglen, & arg );
		err = make_message_var( &buffer,  "%.*s" ARG_END , arglen, arg );
	}
	err = make_message_var( &buffer, "\n");
	waiting = SocketSendRaw(client->client, buffer.data , buffer.offset);
	if ( waiting )
		fprintf(stderr, "Ivy: Slow client : %s\n", client->app_name );
	return 1;
}


static int
ClientCall (IvyClientPtr clnt, const char *message)
{
	MsgSndPtr msg;
	int match_count = 0;
	/* recherche dans la liste des requetes recues de ce client */
	IVY_LIST_EACH (clnt->msg_send, msg) {
		match_count+= MsgCall (message, msg, clnt);
	}
	return match_count;
}

static int CheckConnected( IvyClientPtr clnt )
{
	IvyClientPtr client;
	struct in_addr *addr1;
	struct in_addr *addr2;

	if ( clnt->app_port == 0 ) /* Old Ivy Protocol Dont check */
		return 0;
	/* recherche dans la liste des clients de la presence de clnt */
	IVY_LIST_EACH( clients, client )
	{
		/* client different mais port identique */
		if ( (client != clnt) && (clnt->app_port == client->app_port) )
			{
			/* et meme machine */
			addr1 = SocketGetRemoteAddr( client->client );
			addr2 = SocketGetRemoteAddr( clnt->client );
			if ( addr1->s_addr == addr2->s_addr )
				return 1;
			}
		/* client different mais applicationID identique */
		/* TODO est-ce utile ??? verif dans UDP 
		if ( (client != clnt) && (clnt->app_id == client->app_id) )
			{
				return 1;
			}
		*/
			
	}
	return 0;
}

static void Receive( Client client, void *data, char *line )
{
	IvyClientPtr clnt;
	int err,id;
	MsgSndPtr snd;
	MsgRcvPtr rcv;
	int argc = 0;
	char *argv[MAX_MATCHING_ARGS];
	char *arg;
	int kind_of_msg = Bye;
	IvyBinding bind;

	const char *errbuf;
	int erroffset;

	err = sscanf( line ,"%d %d", &kind_of_msg, &id );
	arg = strstr( line , ARG_START );
	if ( (err != 2) || (arg == 0)  )
		{
		printf("Quitting bad format  %s\n",  line);
		MsgSendTo( client, Error, Error, "bad format request expected 'type id ...'" );
		MsgSendTo( client, Bye, 0, "" );
		SocketClose( client );
		return;
		}
	arg++;
	clnt = (IvyClientPtr)data;
	switch( kind_of_msg )
		{
		case Bye:
			
			TRACE("Quitting  %s\n",  line);

			SocketClose( client );
			break;
		case Error:
			printf ("Received error %d %s\n",  id, arg);
			break;
		case AddRegexp:


			TRACE("Regexp  id=%d exp='%s'\n",  id, arg);

			if ( !IvyBindingFilter( arg ) )
				{

				TRACE("Warning: regexp '%s' illegal, removing from %s\n",arg,ApplicationName);

				if ( application_bind_callback )
					  {
					    (*application_bind_callback)( clnt, application_bind_data, id, arg, IvyFilterBind );
					  }
				return;
				}

			bind = IvyBindingCompile( arg, & erroffset, & errbuf );
			if ( bind != NULL )
				{
				  // On teste si c'est un change regexp : changement de regexp d'une id existante
				  IVY_LIST_ITER( clnt->msg_send, snd, ( id != snd->id ));
				  if ( snd )     {
				    free (snd->str_regexp);
				    snd->str_regexp = strdup( arg );
				    snd->binding = bind;
				    if ( application_bind_callback )
				      {
					(*application_bind_callback)( clnt, application_bind_data, id, snd->str_regexp, IvyChangeBind );
				      }
				  } else {
				    IVY_LIST_ADD_START( clnt->msg_send, snd )
				      snd->id = id;
				    snd->str_regexp = strdup( arg );
				    snd->binding = bind;
				    if ( application_bind_callback )
				      {
					(*application_bind_callback)( clnt, application_bind_data, id, snd->str_regexp, IvyAddBind );
				      }
				    IVY_LIST_ADD_END( clnt->msg_send, snd )
				      
				      }
				}
			else
			{
			printf("Error compiling '%s', %s\n", arg, errbuf);
			MsgSendTo( client, Error, erroffset, errbuf );
			}

			break;
		case DelRegexp:

			TRACE("Regexp Delete id=%d\n",  id);

			IVY_LIST_ITER( clnt->msg_send, snd, ( id != snd->id ));
			if ( snd )
				{
				  if ( application_bind_callback )
				    {
				      (*application_bind_callback)( clnt, application_bind_data, id, snd->str_regexp, IvyRemoveBind );
				    }
				IvyBindingFree( snd->binding );
				IVY_LIST_REMOVE( clnt->msg_send, snd  );
				}
			break;
		case StartRegexp:

			TRACE("Regexp Start id=%d Application='%s'\n",  id, arg);

			clnt->app_name = strdup( arg );
			clnt->app_port = id;
			if ( CheckConnected( clnt ) )
			{			

			TRACE("Quitting  already connected %s\n",  line);

			IvySendError( clnt, 0, "Application already connected" );
			SocketClose( client );
			}
			break;
		case EndRegexp:
			
			TRACE("Regexp End id=%d\n",  id);

			if ( application_callback )
				{
				(*application_callback)( clnt, application_user_data, IvyApplicationConnected );
				}
			if ( ready_message )
				{
				int count;
				count = ClientCall( clnt, ready_message );
				
				TRACE(" Sendind ready message %d\n", count);
				}
			break;
		case Msg:
			
			TRACE("Message id=%d msg='%s'\n", id, arg);

			IVY_LIST_EACH( msg_recv, rcv )
				{
				if ( id == rcv->id )
					{
					arg = nextArg( arg, ARG_END);	
					while ( arg )
						{
						argv[argc++] = arg;
						arg = nextArg( 0, ARG_END );
						}
					TRACE("Calling  id=%d argc=%d for %s\n", id, argc,rcv->regexp);
					if ( rcv->callback ) (*rcv->callback)( clnt, rcv->user_data, argc, argv );
					return;
					}
				}
			printf("Callback Message id=%d not found!!!'\n", id);
			break;
		case DirectMsg:
			
			TRACE("Direct Message id=%d msg='%s'\n", id, arg);

			if ( direct_callback)
				(*direct_callback)( clnt, direct_user_data, id, arg );
			break;

		case Die:
			
			TRACE("Die Message\n");

			if ( application_die_callback)
				(*application_die_callback)( clnt, application_die_user_data, id );
			IvyCleanup();
			exit(0);
			break;

		case Ping:
			
			TRACE("Ping Message\n");
			MsgSendTo( client, Pong, id, "" );
			break;

		case Pong:
			
			TRACE("Pong Message\n");
			printf("Receive unhandled Pong message (ivy-c not able to send ping)\n");
			break;

		default:
			printf("Receive unhandled message %s\n",  line);
			break;
		}
		
}

static IvyClientPtr SendService( Client client, const char *appname )
{
	IvyClientPtr clnt;
	MsgRcvPtr msg;
	IVY_LIST_ADD_START( clients, clnt )

		clnt->msg_send = 0;
		clnt->client = client;
		clnt->app_name = strdup(appname);
		clnt->app_port = 0;
		MsgSendTo( client, StartRegexp, ApplicationPort, ApplicationName);
		IVY_LIST_EACH(msg_recv, msg )
			{
			MsgSendTo( client, AddRegexp,msg->id,msg->regexp);
			}
		MsgSendTo( client, EndRegexp, 0, "");
		
	IVY_LIST_ADD_END( clients, clnt )
	return clnt;
}

static void ClientDelete( Client client, void *data )
{
	IvyClientPtr clnt;
	MsgSndPtr msg;
#ifdef DEBUG
	char *remotehost;
	unsigned short remoteport;
#endif
	clnt = (IvyClientPtr)data;
	if ( application_callback )
				{
				(*application_callback)( clnt, application_user_data, IvyApplicationDisconnected );
				}
	
#ifdef DEBUG
	/* probably bogus call, but this is for debug only anyway */
	SocketGetRemoteHost( client, &remotehost, &remoteport );
	TRACE("Deconnexion de %s:%hu\n", remotehost, remoteport );
#endif //DEBUG

	if ( clnt->app_name ) free( clnt->app_name );
	IVY_LIST_EACH( clnt->msg_send, msg)
	{
		/*regfree(msg->regexp);*/
		free( msg->str_regexp);
	}
	IVY_LIST_EMPTY( clnt->msg_send );
	IVY_LIST_REMOVE( clients, clnt );
}

static void *ClientCreate( Client client )
{

#ifdef DEBUG
	char *remotehost;
	unsigned short remoteport;
	SocketGetRemoteHost( client, &remotehost, &remoteport );
	TRACE("Connexion de %s:%hu\n", remotehost, remoteport );
#endif //DEBUG

	return SendService (client, "Unknown");
}

static void BroadcastReceive( Client client, void *data, char *line )
{	
	Client app;
	int err;
	int version;
	unsigned short serviceport;
	char appid[2048];
	char appname[2048];
#ifdef DEBUG
	unsigned short remoteport;
	char *remotehost = 0;
#endif
	memset( appid, 0, sizeof( appid ) );
	memset( appname, 0, sizeof( appname ) );
	err = sscanf (line,"%d %hu %s %[^\n]", &version, &serviceport, appid, appname);
	if ( err < 2 ) {
		/* ignore the message */
		unsigned short remoteport;
		char *remotehost;
		SocketGetRemoteHost (client, &remotehost, &remoteport );
		printf (" Bad supervision message, expected 'version port' from %s:%d\n",
				remotehost, remoteport);
		return;
	}
	if ( version != VERSION ) {
		/* ignore the message */
		unsigned short remoteport;
		char *remotehost = 0;
		SocketGetRemoteHost (client, &remotehost, &remoteport );
		fprintf (stderr, "Bad Ivy version, expected %d and got %d from %s:%d\n",
			VERSION, version, remotehost, remoteport);
		return;
	}
	/* check if we received our own message. SHOULD ALSO TEST THE HOST */
	if ( strcmp( appid , ApplicationID) ==0 ) return;
	if (serviceport == ApplicationPort) return;
	
#ifdef DEBUG
	SocketGetRemoteHost (client, &remotehost, &remoteport );
	TRACE(" Broadcast de %s:%hu port %hu\n", remotehost, remoteport, serviceport );
#endif //DEBUG

	/* connect to the service and send the regexp */
	app = SocketConnectAddr(SocketGetRemoteAddr(client), serviceport, 0, Receive, ClientDelete );
	if (app) {
		IvyClientPtr clnt;
		clnt = SendService( app, appname );
		SocketSetData( app, clnt);
	}
}
static unsigned long currentTime()
{
#define MILLISEC 1000
	unsigned long current;
#ifdef WIN32
	current = GetTickCount();
#else
	struct timeval stamp;
        gettimeofday( &stamp, NULL );
        current = stamp.tv_sec * MILLISEC + stamp.tv_usec/MILLISEC;
#endif
        return  current;
}

static const char * GenApplicationUniqueIdentifier()
{
	static char appid[2048];
	unsigned long curtime;
	curtime = currentTime();
	srand( curtime );
	sprintf(appid,"%d:%lu:%d",rand(),curtime,ApplicationPort);
	return appid;
}
void IvyInit (const char *appname, const char *ready, 
			 IvyApplicationCallback callback, void *data,
			 IvyDieCallback die_callback, void *die_data
			 )
{
	SocketInit();

	ApplicationName = appname;
	application_callback = callback;
	application_user_data = data;
	application_die_callback = die_callback;
	application_die_user_data = die_data;
	ready_message = ready;
}

void IvySetBindCallback( IvyBindCallback bind_callback, void *bind_data )
{
  application_bind_callback=bind_callback;
  application_bind_data=bind_data;
}

void IvySetFilter( int argc, const char **argv)
{
	IvyBindingSetFilter( argc, argv );
	if ( getenv( "IVY_DEBUG_FILTER" )) debug_filter = 1;

}

void IvyStop (void)
{
	IvyChannelStop();
}


void IvyStart (const char* bus)
{

	struct in_addr baddr;
	unsigned long mask = 0xffffffff; 
	unsigned char elem = 0;
	int numdigit = 0;
	int numelem = 0;
	int error = 0;
	const char* p = bus;	/* used for decoding address list */
	const char* q;		/* used for decoding port number */
	int port;

	
	/*
	 * Initialize TCP port
	 */
	server = SocketServer (ANYPORT, ClientCreate, ClientDelete, Receive);
	ApplicationPort = SocketServerGetPort (server);
	ApplicationID = GenApplicationUniqueIdentifier();
	        
	/*
	 * Find network list as well as broadcast port
	 * (we accept things like 123.231,123.123:2000 or 123.231 or :2000),
	 * Initialize UDP port
	 * Send a broadcast handshake on every network
	 */

	/* first, let's find something to parse */
	if (!p || !*p)
		p = getenv ("IVYBUS");
	if (!p || !*p) 
		p = DefaultIvyBus;

	/* then, let's get a port number */
	q = strchr (p, ':');
	if (q && (port = atoi (q+1)))
		SupervisionPort = port;
	else
		SupervisionPort = DEFAULT_BUS;

	/*
	 * Now we have a port number it's time to initialize the UDP port
	 */
	broadcast =  SocketBroadcastCreate (SupervisionPort, 0, BroadcastReceive );

		
	/* then, if we only have a port number, resort to default value for network */
	if (p == q)
		p = DefaultIvyBus;

	/* and finally, parse network list and send broadcast handshakes.
	   This is painful but inet_aton is sloppy.
	   If someone knows other builtin routines that do that... */
	for (;;) {
		/* address elements are up to 3 digits... */
		if (!error && isdigit (*p)) {
			if (numdigit < 3 && numelem < 4) {
				elem = 10 * elem +  *p -'0';
			} else {
				error = 1;
			}

		/* ... terminated by a point, a comma or a colon, or the end of string */
		} else if (!error && (*p == '.' || *p == ',' || *p == ':' || *p == '\0')) {
			mask = (mask ^ (0xff << (8*(3-numelem)))) | (elem << (8*(3-numelem)));

			/* after a point, expect next address element */
			if (*p == '.') {
				numelem++;

			/* addresses are terminated by a comma or end of string */
			} else {

				baddr.s_addr = htonl(mask);
				printf ("Broadcasting on network %s, port %d\n", inet_ntoa(baddr), SupervisionPort);
				// test mask value agaisnt CLASS D
				if ( IN_MULTICAST( mask ) )
					SocketAddMember (broadcast , mask );

				SocketSendBroadcast (broadcast, mask, SupervisionPort, "%d %hu %s %s\n", VERSION, ApplicationPort, ApplicationID, ApplicationName); 
				numelem = 0;
				mask = 0xffffffff;
			}
			numdigit = 0;
			elem = 0;

		/* recover from bad addresses at next comma or colon or at end of string */
		} else if (*p == ',' || *p == ':' || *p == '\0') {
			fprintf (stderr, "bad broadcast address\n");
			elem = 0;
			numelem = 0;
			numdigit = 0;
			mask = 0xffffffff;
			error = 0;

		/* ignore spaces */
		} else if (*p == ' ') {

		  /* everything else is illegal */
		} else {
			error = 1;
		}

		/* end of string or colon */
		if (*p == '\0' || *p == ':')
			break;
		++p;
	}

	TRACE ("Listening on TCP:%hu\n",ApplicationPort);

}

/* desabonnements */
void
IvyUnbindMsg (MsgRcvPtr msg)
{
	IvyClientPtr clnt;
	/* Send to already connected clients */
	IVY_LIST_EACH (clients, clnt ) {
		MsgSendTo( clnt->client, DelRegexp,msg->id, "");
	}
	IVY_LIST_REMOVE( msg_recv, msg  );
}

/* demande de reception d'un message */

MsgRcvPtr
IvyBindMsg (MsgCallback callback, void *user_data, const char *fmt_regex, ... )
{
	static IvyBuffer buffer = { NULL, 0, 0};
	va_list ap;
	static int recv_id = 0;
	IvyClientPtr clnt;
	MsgRcvPtr msg;

	va_start (ap, fmt_regex );
	buffer.offset = 0;
	make_message( &buffer, fmt_regex, ap );
	va_end  (ap );

	substituteInterval (&buffer);

	/* add Msg to the query list */
	IVY_LIST_ADD_START( msg_recv, msg )
		msg->id = recv_id++;
		msg->regexp = strdup(buffer.data);
		msg->callback = callback;
		msg->user_data = user_data;
	IVY_LIST_ADD_END( msg_recv, msg )
	/* Send to already connected clients */
	/* recherche dans la liste des requetes recues de mes clients */
	IVY_LIST_EACH( clients, clnt ) {
		MsgSendTo( clnt->client, AddRegexp,msg->id,msg->regexp);
	}
	return msg;
}

/* changement de regexp d'un bind existant precedement fait avec IvyBindMsg */
MsgRcvPtr
IvyChangeMsg (MsgRcvPtr msg, const char *fmt_regex, ... )
{
	static IvyBuffer buffer = { NULL, 0, 0};
	va_list ap;
	IvyClientPtr clnt;

	va_start (ap, fmt_regex );
	buffer.offset = 0;
	make_message( &buffer, fmt_regex, ap );
	va_end  (ap );

	substituteInterval (&buffer);

	/* change Msg in the query list */
	msg->regexp = strdup(buffer.data);
	
	/* Send to already connected clients */
	/* recherche dans la liste des requetes recues de mes clients */
	IVY_LIST_EACH( clients, clnt ) {
	  MsgSendTo( clnt->client, AddRegexp,msg->id,msg->regexp);
	}
	return msg;
}
/* emmission d'un message avec formatage a la printf */
int IvySendMsg(const char *fmt, ...)
{
	IvyClientPtr clnt;
	int match_count = 0;
	static IvyBuffer buffer = { NULL, 0, 0}; /* Use satic mem to eliminate multiple call to malloc /free */
	va_list ap;
	
	if( fmt == 0 || strlen(fmt) == 0 ) return 0;	
	va_start( ap, fmt );
	buffer.offset = 0;
	make_message( &buffer, fmt, ap );
	va_end ( ap );

	/* recherche dans la liste des requetes recues de mes clients */
	IVY_LIST_EACH (clients, clnt) {
		match_count += ClientCall (clnt, buffer.data);
	}
	TRACE_IF( match_count == 0, "Warning no recipient for %s\n",buffer.data);
	/* si le message n'est pas emit et qu'il y a des filtres alors WARNING */
	if ( match_count == 0 && debug_filter )
	{
		IvyBindindFilterCheck( buffer.data );
	}
	return match_count;
}

void IvySendError( IvyClientPtr app, int id, const char *fmt, ... )
{
	static IvyBuffer buffer = { NULL, 0, 0}; /* Use satic mem to eliminate multiple call to malloc /free */
	va_list ap;
	
	va_start( ap, fmt );
	buffer.offset = 0;
	make_message( &buffer, fmt, ap );
	va_end ( ap );
	MsgSendTo( app->client, Error, id, buffer.data);
}

void IvyBindDirectMsg( MsgDirectCallback callback, void *user_data)
{
	direct_callback = callback;
	direct_user_data = user_data;
}

void IvySendDirectMsg( IvyClientPtr app, int id, char *msg )
{
	MsgSendTo( app->client, DirectMsg, id, msg);
}

void IvySendDieMsg( IvyClientPtr app )
{
	MsgSendTo( app->client, Die, 0, "" );
}

char *IvyGetApplicationName( IvyClientPtr app )
{
	if ( app && app->app_name ) 
		return app->app_name;
	else return "Unknown";
}

char *IvyGetApplicationHost( IvyClientPtr app )
{
	if ( app && app->client ) 
		return SocketGetPeerHost (app->client );
	else return 0;
}

void IvyDefaultApplicationCallback( IvyClientPtr app, void *user_data, IvyApplicationEvent event)
{
	switch ( event )  {
	case IvyApplicationConnected:
		printf("Application: %s ready on %s\n", IvyGetApplicationName( app ), IvyGetApplicationHost(app));
		break;
	case IvyApplicationDisconnected:
		printf("Application: %s bye on %s\n", IvyGetApplicationName( app ), IvyGetApplicationHost(app));
		break;
	default:
		printf("Application: %s unkown event %d\n",IvyGetApplicationName( app ), event);
		break;
	}
}
void IvyDefaultBindCallback( IvyClientPtr app, void *user_data, int id, char* regexp,  IvyBindEvent event)
{
	switch ( event )  {
	case IvyAddBind:
		printf("Application: %s on %s add regexp %d : %s\n", IvyGetApplicationName( app ), IvyGetApplicationHost(app), id, regexp);
		break;
	case IvyRemoveBind:
		printf("Application: %s on %s remove regexp %d :%s\n", IvyGetApplicationName( app ), IvyGetApplicationHost(app), id, regexp);
		break;
	case IvyFilterBind:
		printf("Application: %s on %s as been filtred regexp %d :%s\n", IvyGetApplicationName( app ), IvyGetApplicationHost(app), id, regexp);
		break;
	case IvyChangeBind:
	        printf("Application: %s on %s change regexp %d : %s\n", IvyGetApplicationName( app ), IvyGetApplicationHost(app), id, regexp);
		break;
		break;
	default:
		printf("Application: %s unkown event %d\n",IvyGetApplicationName( app ), event);
		break;
	}
}

IvyClientPtr IvyGetApplication( char *name )
{
	IvyClientPtr app = 0;
	IVY_LIST_ITER( clients, app, strcmp(name, app->app_name) != 0 );
	return app;
}

char *IvyGetApplicationList(const char *sep)
{
	static char applist[4096]; /* TODO remove that ugly Thing */
	IvyClientPtr app;
	applist[0] = '\0';
	IVY_LIST_EACH( clients, app )
		{
		strcat( applist, app->app_name );
		strcat( applist, sep );
		}
	return applist;
}

char **IvyGetApplicationMessages( IvyClientPtr app )
{
#define MAX_REGEXP 4096
	static char *messagelist[MAX_REGEXP+1];/* TODO remove that ugly Thing */
	MsgSndPtr msg;
	int msgCount= 0;
	memset( messagelist, 0 , sizeof( messagelist ));
	/* recherche dans la liste des requetes recues de ce client */
	IVY_LIST_EACH( app->msg_send, msg )
	{
	messagelist[msgCount++]= msg->str_regexp;
	if ( msgCount >= MAX_REGEXP )
		{
		fprintf(stderr,"Too Much expression(%d) for buffer\n",msgCount);
		break;
		}
	}
	return messagelist;
}

static void substituteInterval (IvyBuffer *src)
{
  // pas de traitement couteux s'il n'y a rien à interpoler
  if (strstr (src->data, "(?I") == NULL) {
    return;
  } else {
	char *curPos;
    char *itvPos;
    IvyBuffer dst = {NULL, 0, 0};
    dst.size = 8192;
    dst.data = malloc (dst.size);

    curPos = src->data;
    while ((itvPos = strstr (curPos, "(?I")) != NULL) {
      // copie depuis la position courante jusqu'à l'intervalle
      int lenCp, min,max;
      char withDecimal;
      lenCp = itvPos-curPos;
      memcpy (&(dst.data[dst.offset]), curPos, lenCp);
      curPos=itvPos;
      dst.offset += lenCp;

      // extraction des paramètres de l'intervalle
      sscanf (itvPos, "(?I%d#%d%c", &min, &max, &withDecimal);

      //      printf ("DBG> substituteInterval min=%d max=%d withDecimal=%d\n", 
      //      min, max, (withDecimal != 'i'));   
  
      // generation et copie de l'intervalle
      regexpGen (&(dst.data[dst.offset]), dst.size-dst.offset, min, max, (withDecimal != 'i'));
      dst.offset = strlen (dst.data);

      // consommation des caractères décrivant intervalle dans la chaine source
      curPos = strstr (curPos, ")");
      curPos++;
    }
    strncat (dst.data, curPos, dst.size-dst.offset);
    free (src->data);
    src->data = dst.data;
  }
}
