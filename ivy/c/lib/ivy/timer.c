/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2000
 *	Centre d'Études de la Navigation Aérienne
 *
 *	Timers used in select based main loop
 *
 *	Authors: François-Régis Colin <fcolin@cena.dgac.fr>
 *
 *	$Id: timer.c 1303 2006-06-28 09:22:19Z fcolin $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */

/* Module de gestion des timers autour d'un select */
#include <stdio.h>
#include <sys/types.h>
#include <time.h>
#include <stdlib.h>
#include <memory.h> 
#ifdef WIN32
#include <windows.h>
#else
#include <sys/time.h>
#endif
#include "list.h"
#include "timer.h"

#define BIGVALUE 2147483647
#define MILLISEC 1000

static struct timeval *timeoutptr = NULL;
static struct timeval selectTimeout = { BIGVALUE, 0 };
/* la prochaine echeance */
static unsigned long nextTimeout = BIGVALUE;

struct _timer {
	struct _timer *next;
	int	repeat;
	unsigned long period;
	unsigned long when;
	TimerCb callback;
	void *user_data;
	};

/* liste des timers */
TimerId timers = NULL;

static long currentTime()
{	
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
static void SetNewTimeout( unsigned long current, unsigned long when )
{
	unsigned long time;
	time = when - current;
	nextTimeout = when;
	selectTimeout.tv_sec = time / MILLISEC;
	selectTimeout.tv_usec = (time - selectTimeout.tv_sec* MILLISEC) * MILLISEC;
	if ( timeoutptr == NULL )
				timeoutptr = &selectTimeout;
	/*printf("New timeout %lu\n", time );*/
}
static void AdjTimeout(unsigned long current)
{
	unsigned long newTimeout;
	TimerId timer;
	if ( timers )
	{
	/* recherche de la plus courte echeance dans la liste */
	newTimeout =  timers->when ; /* remise a la premiere valeur */
	IVY_LIST_EACH( timers , timer )
		{
		if ( timer->when < newTimeout  )
				newTimeout = timer->when;
		
		}
	SetNewTimeout( current, newTimeout );
	}
	else
	{
	timeoutptr = NULL;
	}
}

/* API */

TimerId TimerRepeatAfter( int count, long time, TimerCb cb, void *user_data )
{
	unsigned long stamp;
	TimerId timer;

	/* si y a rien a faire et ben on fait rien */
	if ( cb == NULL ) return NULL;

	IVY_LIST_ADD_START( timers, timer )
		timer->repeat = count;
		timer->callback = cb;
		timer->user_data = user_data;
		stamp = currentTime();
		timer->period = time;
		timer->when =  stamp + time;
		if ( (timer->when < nextTimeout) || (timeoutptr == NULL))
			SetNewTimeout( stamp, timer->when );
	IVY_LIST_ADD_END( timers, timer )
	return timer;
}
void TimerRemove( TimerId timer )
{
	unsigned long stamp;
	if ( !timer ) return;
	IVY_LIST_REMOVE( timers, timer );
	stamp = currentTime();
	AdjTimeout(stamp);
}
void TimerModify( TimerId timer, long time )
{
	unsigned long stamp;
	if ( !timer ) return;

	stamp = currentTime();
	timer->period = time;
	timer->when = stamp + time;
	AdjTimeout(stamp);
}
/* Interface avec select */

struct timeval *TimerGetSmallestTimeout()
{
	unsigned long stamp;
	/* recalcul du prochain timeout */
	stamp = currentTime();
	AdjTimeout( stamp );
	return timeoutptr;
}

void TimerScan()
{
	unsigned long stamp;
	TimerId timer;
	TimerId next;
	unsigned long delta;
	int timer_echu = 0;
	
	stamp = currentTime();
	/* recherche des timers echu dans la liste */
	IVY_LIST_EACH_SAFE( timers , timer, next )
	{
	if ( timer->when <= stamp )
		{
		timer_echu++;
		delta = stamp - timer->when;
		/* call callback */
		(*timer->callback)( timer, timer->user_data, delta );
		if ( timer->repeat == TIMER_LOOP || --(timer->repeat) )
			{
			timer->when = stamp + timer->period;
			}
			else
			{
			IVY_LIST_REMOVE( timers, timer );
			}
		}
	}
	
}
