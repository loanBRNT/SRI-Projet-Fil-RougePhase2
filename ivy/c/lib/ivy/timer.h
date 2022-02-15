/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2000
 *	Centre d'Études de la Navigation Aérienne
 *
 *	Timers for select-based main loop
 *
 *	Authors: François-Régis Colin <fcolin@cena.dgac.fr>
 *
 *	$Id: timer.h 1222 2006-04-21 08:37:01Z fcolin $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */

/* Module de gestion des timers autour d'un select */

typedef struct _timer *TimerId;
typedef void (*TimerCb)( TimerId id , void *user_data, unsigned long delta );

/* API  le temps est en millisecondes */
#define TIMER_LOOP -1			/* timer en boucle infinie */
TimerId TimerRepeatAfter( int count, long time, TimerCb cb, void *user_data );

void TimerModify( TimerId id, long time );

void TimerRemove( TimerId id );

/* Interface avec select */

struct timeval *TimerGetSmallestTimeout();

void TimerScan();
