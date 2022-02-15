/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2000
 *	Centre d'Études de la Navigation Aérienne
 *
 * 	Main loop based on Tcl
 *
 *	Authors: François-Régis Colin <fcolin@cena.dgac.fr>
 *
 *	$Id: ivytcl.c 3061 2007-02-13 08:35:08Z fourdan $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */

#include <sys/time.h>
#include <unistd.h>
#ifdef __APPLE__
#include <malloc/malloc.h>
#else
#include <malloc.h>
#endif
#include <string.h>
#include <stdlib.h>
#include <signal.h>

#include <tcl.h>

#include "ivytcl.h"
#include "ivysocket.h"
#include "ivychannel.h"
#include "ivy.h"
#include "timer.h"

// defined or not in the Makefile only
#ifdef TCL_CHANNEL_INTEGRATION
// On utilisa la boucle Standard TCL 
// mais il y a des problemes sur les socket server 
// Il n'y a pas de Tcl_MakeTCPserver

struct _channel {
	HANDLE fd;
	void *data;
	Tcl_Channel tcl_channel;
        ChannelHandleDelete handle_delete;
	ChannelHandleRead handle_read;
	};


static int channel_initialized = 0;



#ifdef WIN32
WSADATA WsaData;
#endif

void IvyChannelInit(void)
{
#ifdef WIN32
	int error;
#endif 
	if ( channel_initialized ) return;

	/* pour eviter les plantages quand les autres applis font core-dump */
#ifndef WIN32
	signal( SIGPIPE, SIG_IGN);
#endif
#ifdef WIN32
	error = WSAStartup (0x0101, &WsaData);
	  if (error == SOCKET_ERROR) {
	      printf ("WSAStartup failed.\n");
	  }
#endif
	channel_initialized = 1;
}
static void
IvyHandleFd(ClientData	cd,
	    int		mask)
{
  Channel channel = (Channel)cd;

  /*printf("handle event %d\n", mask);*/
  if (mask == TCL_READABLE) {
    (*channel->handle_read)(channel,channel->fd,channel->data);
  }
  else if (mask == TCL_EXCEPTION) {
    (*channel->handle_delete)(channel->data);
  }
}

void IvyChannelRemove( Channel channel )
{

	if ( channel->handle_delete )
		(*channel->handle_delete)( channel->data );
	Tcl_DeleteChannelHandler(channel->tcl_channel, IvyHandleFd, (ClientData) channel);
	ckfree((char *) channel);
}


Channel IvyChannelAdd(HANDLE fd, void *data,
				ChannelHandleDelete handle_delete,
				ChannelHandleRead handle_read
				)						
{
	Channel channel;

	channel = (Channel)ckalloc( sizeof (struct _channel) );
	if ( !channel )
		{
		fprintf(stderr,"NOK Memory Alloc Error\n");
		exit(0);
		}

	channel->handle_delete = handle_delete;
	channel->handle_read = handle_read;
	channel->data = data;
	channel->fd = fd;

	/*printf("Create handle fd %d\n", fd);*/
	channel->tcl_channel = Tcl_MakeTcpClientChannel((void*)fd);
  	Tcl_CreateChannelHandler(channel->tcl_channel,  TCL_READABLE|TCL_EXCEPTION,	IvyHandleFd, (ClientData) channel);
	return channel;
}

#else
// On utilisa la procedure Idle et la boucle Standard Ivy
void IvyIdleProc(ClientData clientData)
{
	IvyIdle();
	Tcl_DoWhenIdle(IvyIdleProc,0);
}
#endif

void
IvyChannelStop ()
{
  /* To be implemented */
#ifndef TCL_CHANNEL_INTEGRATION
	Tcl_CancelIdleCall(IvyIdleProc,0);
#endif
}
/* Code from PLC */

#define INTEGER_SPACE 30

typedef struct _filter_struct {
  MsgRcvPtr	id;
  char		*script;
  char		*filter;
  Tcl_Interp	*interp;
} filter_struct;


static Tcl_HashTable	filter_table;
static Tcl_HashTable	app_table;
static int		filter_id = 0;
static char		*nom_de_l_appli = NULL;
static char		*message_de_bienvenue;


static void
IvyAppCB(IvyClientPtr	app,
	 void		*user_data, /* script a appeler */
	 IvyApplicationEvent event)
{
  static char	*app_event_str[] = {
    "Connected", "Disconnected" };
  filter_struct	*filter = (filter_struct *) user_data;
  int		result, size, dummy;
  char		*script_to_call;
  Tcl_HashEntry	*entry;
  
  entry = Tcl_FindHashEntry(&app_table, IvyGetApplicationName(app));
  if (event == IvyApplicationConnected) {
    if (!entry) {
      entry = Tcl_CreateHashEntry(&app_table, IvyGetApplicationName(app), &dummy);
      Tcl_SetHashValue(entry, (ClientData) app);
    }
  }

  size = strlen(filter->script) + INTEGER_SPACE;
  if (entry) {
    size += strlen(IvyGetApplicationName(app)) + 3;
  }
  else {
    size += 4;
  }
  script_to_call = ckalloc(size);
  strcpy(script_to_call, filter->script);
  strcat(script_to_call, " ");
  if (entry) {
	strcat(script_to_call, " \"");
    strcat(script_to_call, IvyGetApplicationName(app));
	strcat(script_to_call, "\"");
  }
  else {
    strcat(script_to_call, "???");
  }
  strcat(script_to_call, " \"");
  strcat(script_to_call, app_event_str[event%2]);
  strcat(script_to_call, "\"");
  
  Tcl_Preserve(filter->interp);
  result = Tcl_GlobalEval(filter->interp, script_to_call);
  ckfree(script_to_call);

  if (result != TCL_OK) {
    Tcl_BackgroundError(filter->interp);
  }
  Tcl_Release(filter->interp);

  if (event == IvyApplicationDisconnected) {
    if (entry) {
      Tcl_DeleteHashEntry(entry);
    }
  }
}

static void
IvyDieCB(IvyClientPtr	app,
	 void		*user_data, /* script a appeler */
	 int		id)
{
  filter_struct	*filter = (filter_struct *) user_data;
  int		result, size;
  char		idstr[INTEGER_SPACE];
  char		*script_to_call;

  sprintf(idstr, "%d", id);
  size = strlen(filter->script) + INTEGER_SPACE + 1;
  script_to_call = ckalloc(size);
  strcpy(script_to_call, filter->script);
  strcat(script_to_call, " \"");
  strcat(script_to_call, idstr);
  strcat(script_to_call, "\"");
  
  Tcl_Preserve(filter->interp);
  result = Tcl_GlobalEval(filter->interp, script_to_call);
  ckfree(script_to_call);

  if (result != TCL_OK) {
    Tcl_BackgroundError(filter->interp);
  }
  Tcl_Release(filter->interp);
}

static void
IvyMsgCB(IvyClientPtr	app,
	 void		*user_data,
	 int		argc,
	 char		**argv)
{
  filter_struct	*filter = (filter_struct *) user_data;
  int		result, i, size;
  char		*script_to_call;
  
  size = strlen(filter->script) + 3;
  for (i = 0; i < argc; i++) {
    size += strlen(argv[i]) + 3;
  }
  size ++;
  size += strlen(IvyGetApplicationName(app))+4;
  script_to_call = ckalloc(size);
  strcpy(script_to_call, filter->script);
  strcat(script_to_call, " \"");
  strcat(script_to_call, IvyGetApplicationName(app));
  strcat(script_to_call, "\"");
  /* strcat(script_to_call, " {"); */
  for (i = 0; i < argc; i++) {
    strcat(script_to_call, " \"");
	strcat(script_to_call, argv[i]);
    strcat(script_to_call, "\"");
  }
  /* strcat(script_to_call, " }"); */
  
  Tcl_Preserve(filter->interp);
  result = Tcl_GlobalEval(filter->interp, script_to_call);
  ckfree(script_to_call);
  
  if (result != TCL_OK) {
    Tcl_BackgroundError(filter->interp);
  }
  Tcl_Release(filter->interp);
}

static void
IvyDirectMsgCB(IvyClientPtr	app,
	       void		*user_data,
	       int		id,
	       char		*msg)
{
  filter_struct	*filter = (filter_struct *) user_data;
  int		result, size;
  char		*script_to_call;
  char		int_buffer[INTEGER_SPACE];

  sprintf(int_buffer, "%d", id);
  
  size = strlen(filter->script) + 1;
  size += strlen(int_buffer) + 1;
  size += strlen(msg) + 1;
  
  script_to_call = ckalloc(size);
  strcpy(script_to_call, filter->script);
  strcat(script_to_call, " ");
  strcat(script_to_call, int_buffer);
  strcat(script_to_call, " \"");
  strcat(script_to_call, msg);
  strcat(script_to_call, "\"");
  
  Tcl_Preserve(filter->interp);
  result = Tcl_GlobalEval(filter->interp, script_to_call);
  ckfree(script_to_call);
  
  if (result != TCL_OK) {
    Tcl_BackgroundError(filter->interp);
  }
  Tcl_Release(filter->interp);
}
/* Real TCL interface */
static int
IvyInitCmd(ClientData	clientData,
	   Tcl_Interp	*interp,
	   int		argc,
	   const char	**argv)
{
  filter_struct	*app;
  filter_struct	*die;
     
  if (nom_de_l_appli) {
    Tcl_AppendResult(interp, "Ivy has been already inited", (char *) NULL);
    return TCL_ERROR;
  }

  if (argc != 5) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " appName readyMsg connectScript dieScript\"",
		     (char *) NULL);
    return TCL_ERROR;
  }
  nom_de_l_appli = ckalloc(strlen(argv[1])+1);
  strcpy(nom_de_l_appli, argv[1]);
  message_de_bienvenue = ckalloc(strlen(argv[2])+1);
  strcpy(message_de_bienvenue, argv[2]);
  app = (filter_struct *) ckalloc(sizeof(filter_struct));
  die = (filter_struct *) ckalloc(sizeof(filter_struct));
  app->interp = interp;
  die->interp = interp;
  app->script = ckalloc(strlen(argv[3])+1);
  strcpy(app->script, argv[3]);
  die->script = ckalloc(strlen(argv[4])+1);
  strcpy(die->script, argv[4]);
  
  Tcl_InitHashTable(&filter_table, TCL_ONE_WORD_KEYS);
  Tcl_InitHashTable(&app_table, TCL_STRING_KEYS);
  /*printf("appel de businit avec %s %d %s\n", argv[1], port, argv[3]);*/
  IvyInit(nom_de_l_appli,  message_de_bienvenue,
	  IvyAppCB, (void *) app, IvyDieCB, (void *) die);
  
  return TCL_OK;
}
static int
IvyStartCmd(ClientData	clientData,
	   Tcl_Interp	*interp,
	   int		argc,
	   const char	**argv)
{
  if (argc != 2) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " msg\"", (char *) NULL);
    return TCL_ERROR;
  }

  IvyStart(argv[1]);
  
  return TCL_OK;
}

static int
IvyBindCmd(ClientData	clientData,
	   Tcl_Interp	*interp,
	   int		argc,
	   const char	**argv)
{
  filter_struct	*filter;
  Tcl_HashEntry	*entry;
  int		dummy;
  char		msg[INTEGER_SPACE];
  
  if (argc != 3) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " filter script\"", (char *) NULL);
    return TCL_ERROR;
  }

  filter = (filter_struct *) ckalloc(sizeof(filter_struct));
  filter->filter = ckalloc(strlen(argv[1])+1);  // regexp
  strcpy(filter->filter, argv[1]);
  filter->script = ckalloc(strlen(argv[2])+1);  // callback proc name
  strcpy(filter->script, argv[2]);
  filter->id = IvyBindMsg(IvyMsgCB, (void *) filter, filter->filter, NULL); // MsgPtr id
  filter->interp = interp;
  entry = Tcl_CreateHashEntry(&filter_table, (char *) filter_id, &dummy);
  Tcl_SetHashValue(entry, (ClientData) filter);
  sprintf(msg, "%d", filter_id); 
  filter_id++;
  
  Tcl_SetResult(interp, msg, TCL_VOLATILE);
  return TCL_OK;
}

static int
IvyUnbindCmd(ClientData	clientData,
	     Tcl_Interp	*interp,
	     int	argc,
	     const char	**argv)
{
  unsigned long	filter_id;
  char		*end;
  filter_struct	*filter;
  Tcl_HashEntry	*entry;
  if (argc != 2) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " filterId\"", (char *) NULL);
    return TCL_ERROR;
  }
  filter_id = strtol(argv[1], &end, 10);
  if (*end) {
    Tcl_AppendResult(interp, argv[0], " wrong filter id: \"", argv[1],
		     (char *) NULL);
    return TCL_ERROR;
  }
  entry = Tcl_FindHashEntry(&filter_table, (char *) filter_id);
  if (!entry) {
    Tcl_AppendResult(interp, argv[0], " unknown filter: \"", argv[1],
		     (char *) NULL);
    return TCL_ERROR;
  }

  filter = (filter_struct *) Tcl_GetHashValue(entry);
  Tcl_DeleteHashEntry(entry);
  IvyUnbindMsg(filter->id);
  ckfree(filter->script);
  ckfree(filter->filter);
  ckfree((char *) filter);

  return TCL_OK;
}

static int
IvySendCmd(ClientData	clientData,
	   Tcl_Interp	*interp,
	   int		argc,
	   const char	**argv)
{
  if (argc != 2) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " msg\"", (char *) NULL);
    return TCL_ERROR;
  }

  IvySendMsg(argv[1], NULL);
  
  return TCL_OK;
}

static int
IvyApplicationListCmd(ClientData	clientData,
		      Tcl_Interp	*interp,
		      int		argc,
		      const char	**argv)
{
  Tcl_HashEntry	 *entry;
  Tcl_HashSearch search;

  if (argc != 1) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], "\"", (char *) NULL);
    return TCL_ERROR;    
  }

  entry = Tcl_FirstHashEntry(&app_table, &search);

  while (entry) {
    Tcl_AppendElement(interp, Tcl_GetHashKey(&app_table, entry));
    entry = Tcl_NextHashEntry(&search);
  }

  return TCL_OK;
}

static int
IvyApplicationHostCmd(ClientData	clientData,
		      Tcl_Interp	*interp,
		      int		argc,
		      const char	**argv)
{
  Tcl_HashEntry	 *entry;

  if (argc != 2) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " appName\"", (char *) NULL);
    return TCL_ERROR;    
  }

  entry = Tcl_FindHashEntry(&app_table, argv[1]);
  if (!entry) {
    Tcl_AppendResult(interp, "Application \"",
		     argv[1], "\" not connected", (char *) NULL);
    return TCL_ERROR;    
  }

  Tcl_SetResult(interp, IvyGetApplicationHost((IvyClientPtr) Tcl_GetHashValue(entry)),
		TCL_STATIC);
  
  return TCL_OK;
}

static int
IvyApplicationMsgsCmd(ClientData	clientData,
		      Tcl_Interp	*interp,
		      int		argc,
		      const char	**argv)
{
  Tcl_HashEntry	*entry;
  char		**msgs, **scan;
  
  if (argc != 2) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " appName\"", (char *) NULL);
    return TCL_ERROR;    
  }

  entry = Tcl_FindHashEntry(&app_table, argv[1]);
  if (!entry) {
    Tcl_AppendResult(interp, "Application \"",
		     argv[1], "\" not connected", (char *) NULL);
    return TCL_ERROR;    
  }

  msgs = IvyGetApplicationMessages((IvyClientPtr) Tcl_GetHashValue(entry));
  for (scan = msgs; *scan; scan++) {
    Tcl_AppendElement(interp, *scan);
  }

  return TCL_OK;
}

static int
IvySendDirectCmd(ClientData	clientData,
		 Tcl_Interp	*interp,
		 int		argc,
		 const char	**argv)
{
  Tcl_HashEntry	*entry;
  unsigned long	id;
  char		*end;

  if (argc != 4) {
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " appName id msg\"", (char *) NULL);
    return TCL_ERROR;    
  }

  entry = Tcl_FindHashEntry(&app_table, argv[1]);
  if (!entry) {
    Tcl_AppendResult(interp, "Application \"",
		     argv[1], "\" not connected", (char *) NULL);
    return TCL_ERROR;    
  }
  
  id = strtol(argv[2], &end, 10);
  if (*end) {
    Tcl_AppendResult(interp, argv[0], " wrong id: \"", argv[2],
		     (char *) NULL);
    return TCL_ERROR;
  }

  IvySendDirectMsg((IvyClientPtr) Tcl_GetHashValue(entry), id, (char*)argv[3]);
  
  return TCL_OK;
}

static int
IvyBindDirectCmd(ClientData	clientData,
		 Tcl_Interp	*interp,
		 int		argc,
		 const char	**argv)
{
  static filter_struct	*filter = NULL;

  if (argc != 2) { 
    Tcl_AppendResult(interp, "wrong # of args: \"",
		     argv[0], " script\"", (char *) NULL);
    return TCL_ERROR;    
  }
  
  if (!filter) {
    filter = (filter_struct *) ckalloc(sizeof(filter_struct));
    filter->interp = interp;
    IvyBindDirectMsg(IvyDirectMsgCB, (void *) filter);
  }
  else {
    ckfree(filter->script);
  }
  filter->script = ckalloc(strlen(argv[1])+1);
  strcpy(filter->script, argv[1]);
  
  return TCL_OK;
}

int
Tclivy_Init(Tcl_Interp *interp)
{

  Tcl_CreateCommand(interp, "Ivy::init", (Tcl_CmdProc*)IvyInitCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::start", (Tcl_CmdProc*)IvyStartCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::bind", (Tcl_CmdProc*)IvyBindCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::unbind", (Tcl_CmdProc*)IvyUnbindCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::send", (Tcl_CmdProc*)IvySendCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::senddirect", (Tcl_CmdProc*)IvySendDirectCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::binddirect", (Tcl_CmdProc*)IvyBindDirectCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::applist", (Tcl_CmdProc*)IvyApplicationListCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::apphost", (Tcl_CmdProc*)IvyApplicationHostCmd, NULL, NULL);
  Tcl_CreateCommand(interp, "Ivy::appmsgs", (Tcl_CmdProc*)IvyApplicationMsgsCmd, NULL, NULL);
#ifndef TCL_CHANNEL_INTEGRATION
	Tcl_DoWhenIdle(IvyIdleProc,0);
#endif
  return TCL_OK;
}



