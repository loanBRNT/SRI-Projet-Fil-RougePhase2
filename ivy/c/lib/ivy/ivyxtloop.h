/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2000
 *	Centre d'Études de la Navigation Aérienne
 *
 * 	Main loop based on the X Toolkit
 *
 *	Authors: François-Régis Colin <colin@cena.dgac.fr>
 *		 Stéphane Chatty <chatty@cena.dgac.fr>
 *
 *	$Id: ivyxtloop.h 1231 2006-04-21 16:34:15Z fcolin $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */

#ifndef IVYXTLOOP_H
#define IVYXTLOOP_H

#ifdef __cplusplus
extern "C" {
#endif

#include <X11/Intrinsic.h>

extern void IvyXtChannelAppContext( XtAppContext cntx );

#ifdef __cplusplus
}
#endif

#endif

