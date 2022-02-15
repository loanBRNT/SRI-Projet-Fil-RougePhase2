/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-1999
 *	Centre d'Études de la Navigation Aérienne
 *
 * 	Main loop based on GLUT ( OpenGL ) Toolkit
 *
 *	Authors: François-Régis Colin <colin@cenatoulouse.dgac.fr>
 *		 Stéphane Chatty <chatty@cenatoulouse.dgac.fr>
 *
 *	$Id: ivyglutloop.c 3061 2007-02-13 08:35:08Z fourdan $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */

#ifdef WIN32
#include <windows.h>
#endif
#include <stdlib.h>
#include <errno.h>
#include <stdio.h>
#include <stdarg.h>
#include <string.h>

#ifdef WIN32
#else
#include <unistd.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <signal.h>
#endif


#include <GL/glut.h>

#include "ivydebug.h"
#include "ivychannel.h"
#include "ivyglutloop.h"

struct _channel {
	GLUTInputId id_read;
	GLUTInputId id_delete;
	void *data;
	ChannelHandleDelete handle_delete;
	ChannelHandleRead handle_read;
	};


static int channel_initialized = 0;

void IvyChannelInit(void)
{

	if ( channel_initialized ) return;

	/* pour eviter les plantages quand les autres applis font core-dump */
#ifndef WIN32
	signal( SIGPIPE, SIG_IGN);
#endif
	channel_initialized = 1;
}

void IvyChannelRemove( Channel channel )
{

	if ( channel->handle_delete )
		(*channel->handle_delete)( channel->data );
	glutRemoveInput( channel->id_read );
	glutRemoveInput( channel->id_delete );
}


static void IvyGlutHandleChannelRead( int source, GLUTInputId id, void *data )
{
	Channel channel = (Channel)data;
	TRACE("Handle Channel read %d\n",source );
	(*channel->handle_read)(channel,source,channel->data);
}

static void IvyGlutHandleChannelDelete( int source, GLUTInputId id, void *data )
{
	Channel channel = (Channel)data;
	TRACE("Handle Channel delete %d\n",source );
	(*channel->handle_delete)(channel->data);
}

Channel IvyChannelAdd(HANDLE fd, void *data,
				ChannelHandleDelete handle_delete,
				ChannelHandleRead handle_read
				)						
{
	Channel channel;

	channel = (Channel)malloc( sizeof(struct _channel) );
	if ( !channel )
		{
		fprintf(stderr,"NOK Memory Alloc Error\n");
		exit(0);
		}

	channel->handle_delete = handle_delete;
	channel->handle_read = handle_read;
	channel->data = data;

	channel->id_read = glutAddInput( fd,  IvyGlutHandleChannelRead, channel);
	channel->id_delete = glutAddInput( fd, IvyGlutHandleChannelDelete, channel);

	return channel;
}


void
IvyChannelStop ()
{
  /* To be implemented */
}

