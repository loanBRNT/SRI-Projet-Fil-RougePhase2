/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2000
 *	Centre d'Études de la Navigation Aérienne
 *
 * 	Main loop based on select
 *
 *	Authors: François-Régis Colin <fcolin@cena.dgac.fr>
 *		 Stéphane Chatty <chatty@cena.dgac.fr>
 *
 *	$Id: ivyloop.c 1302 2006-06-28 09:07:28Z fcolin $
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

#ifndef WIN32
#include <unistd.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <signal.h>
#endif

#include "list.h"
#include "ivychannel.h"
#include "ivyloop.h"
#include "timer.h"

struct _channel {
	Channel next;
	HANDLE fd;
	void *data;
	int tobedeleted;
	ChannelHandleDelete handle_delete;
	ChannelHandleRead handle_read;
};

static Channel channels_list = NULL;

static int channel_initialized = 0;

static fd_set open_fds;
static int MainLoop = 1;

/* Hook callback & data */
static IvyHookPtr BeforeSelect = NULL;
static IvyHookPtr AfterSelect = NULL;

static void *BeforeSelectData = NULL;
static void *AfterSelectData = NULL;

#ifdef WIN32
WSADATA WsaData;
#endif

void
IvyChannelRemove (Channel channel)
{
	channel->tobedeleted = 1;
}

static void
IvyChannelDelete (Channel channel)
{
	if (channel->handle_delete)
		(*channel->handle_delete) (channel->data);

	FD_CLR (channel->fd, &open_fds);
	IVY_LIST_REMOVE (channels_list, channel);
}

static void
ChannelDefferedDelete ()
{
	Channel channel, next;
	IVY_LIST_EACH_SAFE (channels_list, channel,next)	{
		if (channel->tobedeleted ) {
			IvyChannelDelete (channel);
		}
	}
}

Channel IvyChannelAdd (HANDLE fd, void *data, 
				ChannelHandleDelete handle_delete,
				ChannelHandleRead handle_read
				)						
{
	Channel channel;

	IVY_LIST_ADD_START (channels_list, channel)
	channel->fd = fd;
	channel->tobedeleted = 0;
	channel->handle_delete = handle_delete;
	channel->handle_read = handle_read;
	channel->data = data;
	IVY_LIST_ADD_END (channels_list, channel)
	
	FD_SET (channel->fd, &open_fds);

	return channel;
}

static void
IvyChannelHandleRead (fd_set *current)
{
	Channel channel, next;
	
	IVY_LIST_EACH_SAFE (channels_list, channel, next) {
		if (FD_ISSET (channel->fd, current)) {
			(*channel->handle_read)(channel,channel->fd,channel->data);
		}
	}
}

static void
IvyChannelHandleExcpt (fd_set *current)
{
	Channel channel,next;
	IVY_LIST_EACH_SAFE (channels_list, channel, next) {
		if (FD_ISSET (channel->fd, current)) {
			if (channel->handle_delete)
				(*channel->handle_delete)(channel->data);
/*			IvyChannelClose (channel); */
		}
	}
}

void IvyChannelInit (void)
{
#ifdef WIN32
	int error;
#else 
	/* pour eviter les plantages quand les autres applis font core-dump */
	signal (SIGPIPE, SIG_IGN);
#endif
	if (channel_initialized) return;

	FD_ZERO (&open_fds);

#ifdef WIN32
	error = WSAStartup (0x0101, &WsaData);
	  if (error == SOCKET_ERROR) {
	      printf ("WSAStartup failed.\n");
	  }
#endif
	channel_initialized = 1;
}

void IvyChannelStop (void)
{
	MainLoop = 0;
}

void IvyMainLoop(void)
{

	fd_set rdset;
	fd_set exset;
	int ready;

	while (MainLoop) {
		
		ChannelDefferedDelete();
	   	
	   	if (BeforeSelect)
			(*BeforeSelect)(BeforeSelectData);
		rdset = open_fds;
		exset = open_fds;
		ready = select(64, &rdset, 0,  &exset, TimerGetSmallestTimeout());
		
		if (AfterSelect) 
			(*AfterSelect)(AfterSelectData);
		
		if (ready < 0 && (errno != EINTR)) {
         		fprintf (stderr, "select error %d\n",errno);
			perror("select");
			return;
		}
		TimerScan(); /* should be spliited in two part ( next timeout & callbacks */
		if (ready > 0) {
			IvyChannelHandleExcpt(&exset);
			IvyChannelHandleRead(&rdset);
		}
	}
}

void IvyIdle()
{

	fd_set rdset;
	fd_set exset;
	int ready;
	struct timeval timeout = {0,0}; 

	
	ChannelDefferedDelete();
	rdset = open_fds;
	exset = open_fds;
	ready = select(64, &rdset, 0,  &exset, &timeout);
	if (ready < 0 && (errno != EINTR)) {
         	fprintf (stderr, "select error %d\n",errno);
		perror("select");
		return;
	}
	if (ready > 0) {
		IvyChannelHandleExcpt(&exset);
		IvyChannelHandleRead(&rdset);
	}
	
}


void IvySetBeforeSelectHook(IvyHookPtr before, void *data )
{
	BeforeSelect = before;
	BeforeSelectData = data;
}
void IvySetAfterSelectHook(IvyHookPtr after, void *data )
{
	AfterSelect = after;
	AfterSelectData = data;
}
