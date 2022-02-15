/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2006
 *	Centre d'Études de la Navigation Aérienne
 *
 *	Bind syntax for extracting message comtent 
 *  using regexp or other 
 *
 *	Authors: François-Régis Colin <fcolin@cena.fr>
 *
 *	$Id: ivybind.h 1297 2006-06-22 10:47:46Z fcolin $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */
/* Module de gestion de la syntaxe des messages Ivy */

typedef struct _binding *IvyBinding;

/* Mise en place des Filtrages */
int IvyBindingGetFilterCount();
void IvyBindingSetFilter( int argc, const char ** argv );
int IvyBindingFilter( const char *expression );
void IvyBindindFilterCheck( const char *message );

/* Creation, Compilation */
IvyBinding IvyBindingCompile( const char *expression, int *erroffset, const char **errmessage );
void IvyBindingFree( IvyBinding bind );

/* Execution , extraction */
int IvyBindingExec( IvyBinding bind, const char * message );
/* Get Argument */
void IvyBindingMatch( IvyBinding bind, const char *message, int argnum, int *arglen, const char **arg );

