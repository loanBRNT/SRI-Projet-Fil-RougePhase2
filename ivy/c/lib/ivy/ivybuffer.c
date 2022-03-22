/*
 *	Ivy, C interface
 *
 *	Copyright 1997-2000
 *	Centre d'Etudes de la Navigation Aerienne
 *
 *	Sockets
 *
 *	Authors: Francois-Regis Colin <fcolin@cena.fr>
 *
 *	$Id: ivybuffer.c 1290 2006-06-06 13:59:49Z fcolin $
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

#include "ivybuffer.h"

#define BUFFER_SIZE 4096	/* taille buffer initiale on multiple par deux a chaque realloc */

// fonction de formtage a la printf d'un buffer avec reallocation dynamique  
int make_message(IvyBuffer* buffer, const char *fmt, va_list ap)
{
    /* Guess we need no more than BUFFER_INIT_SIZE bytes. */
    int n;
	if ( buffer->size == 0 || buffer->data == NULL )
		{
		buffer->size = BUFFER_SIZE;
		buffer->offset = 0;
		buffer->data = malloc (BUFFER_SIZE);
		if ( buffer->data == NULL )
			{
			perror(" Ivy make message MALLOC error: " );
			return -1;
			}
		}
    while (1) {
    /* Try to print in the allocated space. */
#ifdef WIN32
	n = _vsnprintf (buffer->data + buffer->offset, buffer->size - buffer->offset, fmt, ap);
#else
    n = vsnprintf (buffer->data + buffer->offset, buffer->size - buffer->offset, fmt, ap);
#endif
    /* If that worked, return the string size. */
    if (n > -1 && n < buffer->size)
	{
		buffer->offset += n;
        return n;
	}
    /* Else try again with more space. */
    if (n > -1)    /* glibc 2.1 */
        buffer->size = n+1; /* precisely what is needed */
    else           /* glibc 2.0 */
        buffer->size *= 2;  /* twice the old size */
    if ((buffer->data = realloc (buffer->data, buffer->size)) == NULL)
		{
       		perror(" Ivy make message REALLOC error: " );
		return -1;
		}
    }
}
int make_message_var(IvyBuffer* buffer, const char *fmt, ... )
{
	va_list ap;
	int len;
	va_start (ap, fmt );
	len = make_message (buffer, fmt, ap );
	va_end (ap );
	return len;
}



