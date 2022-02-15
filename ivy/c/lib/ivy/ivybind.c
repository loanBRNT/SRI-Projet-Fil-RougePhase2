/*
 *	Ivy, C interface
 *
 *	Copyright (C) 1997-2000
 *	Centre d'Études de la Navigation Aérienne
 *
 *	Bind syntax for extracting message comtent 
 *  using regexp or other 
 *
 *	Authors: François-Régis Colin <fcolin@cena.fr>
 *
 *	$Id: ivybind.c 1297 2006-06-22 10:47:46Z fcolin $
 * 
 *	Please refer to file version.h for the
 *	copyright notice regarding this software
 */
/* Module de gestion de la syntaxe des messages Ivy */
#include <stdio.h>
#include <sys/types.h>
#include <time.h>
#include <stdlib.h>
#include <memory.h> 
#include <string.h>
#include <stdarg.h>

#ifdef WIN32
#include <crtdbg.h>
#endif


#ifdef USE_PCRE_REGEX
#define OVECSIZE 60 /* must be multiple of 3, for regexp return */
#include <pcre.h>
#else  /* we don't USE_PCRE_REGEX */
#define MAX_MSG_FIELDS 200
#include <regex.h>
#endif /* USE_PCRE_REGEX */

#include "list.h"
#include "ivybind.h"

static int err_offset;

#ifdef USE_PCRE_REGEX
	static const char *err_buf;
#else  /* we don't USE_PCRE_REGEX */
	static char err_buf[4096];
#endif /* USE_PCRE_REGEX */

struct _binding {
#ifdef USE_PCRE_REGEX
	pcre *regexp;
	pcre_extra *inspect;
	int nb_match;
	int ovector[OVECSIZE];
#else  /* we don't USE_PCRE_REGEX */
	regex_t regexp;						/* la regexp sous forme machine */
	regmatch_t match[MAX_MSG_FIELDS+1];	/* resultat du match */
#endif /* USE_PCRE_REGEX */
	};

/* classes de messages emis par l'application utilise pour le filtrage */
static int	messages_classes_count = 0;
static const char **messages_classes = 0;
/* regexp d'extraction du mot clef des regexp client pour le filtrage des regexp , ca va c'est clair ??? */
static IvyBinding token_extract;

IvyBinding IvyBindingCompile( const char * expression,  int *erroffset, const char **errmessage )
{
	IvyBinding bind=0;
#ifdef USE_PCRE_REGEX
	pcre *regexp;
	regexp = pcre_compile(expression, PCRE_OPT,&err_buf,&err_offset,NULL);
	if ( regexp != NULL )
		{
			bind = (IvyBinding)malloc( sizeof( struct _binding ));
			if ( ! bind ) 
			{
				perror( "IvyBindingCompile malloc error: ");
				exit(-1);
			}
			memset( bind, 0, sizeof(*bind ) );
			bind->regexp = regexp;
			bind->inspect = pcre_study(regexp,0,&err_buf);
			if (err_buf!=NULL)
				{
					printf("Error studying %s, message: %s\n",expression,err_buf);
				}
		}
		else
		{
		*erroffset = err_offset;
		*errmessage = err_buf;
		printf("Error compiling '%s', %s\n", expression, err_buf);
		}
#else  /* we don't USE_PCRE_REGEX */
	regex_t regexp;
	int reg;
	reg = regcomp(&regexp, expression, REGCOMP_OPT|REG_EXTENDED);
	if ( reg == 0 )
		{
			bind = (IvyBinding)malloc( sizeof( struct _binding ));
			memset( bind, 0, sizeof(*bind ) );
			bind->regexp = regexp;
			bind->next = NULL;
		}
		else
		{
		regerror (reg, &regexp, err_buf, sizeof(err_buf) );
		*erroffset = err_offset;
		*errmessage = err_buf;
		printf("Error compiling '%s', %s\n", expression, err_buf);
		}
#endif /* USE_PCRE_REGEX */
	return bind;
}

void IvyBindingFree( IvyBinding bind )
{
#ifdef USE_PCRE_REGEX
	if (bind->inspect!=NULL)
		pcre_free(bind->inspect);
		pcre_free(bind->regexp);
#else  /* we don't USE_PCRE_REGEX */
	free( bind->regexp );
#endif /* USE_PCRE_REGEX */
	free ( bind );
}
int IvyBindingExec( IvyBinding bind, const char * message )
{
	int nb_match = 0;
#ifdef USE_PCRE_REGEX
	
	nb_match = pcre_exec(
					bind->regexp,
					bind->inspect,
					message,
					strlen(message),
					0, /* debut */
					0, /* no other regexp option */
					bind->ovector,
					OVECSIZE);
	if (nb_match<1) return 0; /* no match */
	bind->nb_match = nb_match;
#else  /* we don't USE_PCRE_REGEX */
	memset( bind->match, -1, sizeof(bind->match )); /* work around bug !!!*/
	nb_match = regexec (&bind->regexp, message, MAX_MSG_FIELDS, bind->match, 0) 
	if (nb_match == REG_NOMATCH)
		return 0;
	for ( index = 1; index < MAX_MSG_FIELDS; index++ )
	{
		if ( bind->match[i].rm_so != -1 )
			nb_match++;
	}
#endif /* USE_PCRE_REGEX */
	return nb_match;
}

void IvyBindingMatch( IvyBinding bind, const char *message, int argnum, int *arglen, const char **arg)
{
#ifdef USE_PCRE_REGEX
	
		*arglen = bind->ovector[2*argnum+1]- bind->ovector[2*argnum];
		*arg =   message + bind->ovector[2*argnum];
#else  /* we don't USE_PCRE_REGEX */
	
	regmatch_t* p;

	p = &bind->match[argnum];
	if ( p->rm_so != -1 ) {
			*arglen = p->rm_eo - p->rm_so;
			*arg = message + p->rm_so;
	} else { // ARG VIDE
			*arglen = 0;
			*arg = NULL;
	}
#endif /* USE_PCRE_REGEX */

}

//filter Expression Bind 
int IvyBindingGetFilterCount()
{
return messages_classes_count;
}
void IvyBindingSetFilter( int argc, const char **argv)
{
	const char *errbuf;
	int erroffset;

	messages_classes_count = argc;
	messages_classes = argv;
	/* compile the token extraction regexp */

	token_extract = IvyBindingCompile("^\\^([a-zA-Z_0-9-]+).*", & erroffset, & errbuf);
	if ( !token_extract )
	{
		printf("Error compiling Token Extract regexp: %s\n", errbuf);
	}
}
	
int IvyBindingFilter(const char *expression)
{
	int i;
	int err;
	int regexp_ok = 1; /* accepte tout par default */
	int tokenlen;
	const char *token;
	
	if ( *expression =='^' && messages_classes_count !=0 )
	{
		regexp_ok = 0;
		
		/* extract token */
		err = IvyBindingExec( token_extract, expression );
		if ( err < 1 ) return 1;
		IvyBindingMatch( token_extract, expression , 1, &tokenlen, &token );
		for ( i = 0 ; i < messages_classes_count; i++ )
		{
		  if (strncmp( messages_classes[i], token, tokenlen ) == 0) {
		    return 1; }
		  //		  else {
		  //printf ("DBG> %s eliminé [%s]\n", token, expression);
		  //}
		}
 	}
	return regexp_ok;
}
/* recherche si le message commence par un mot clef de la table */
void IvyBindindFilterCheck( const char *message )
{
	int i;
	for ( i = 0 ; i < messages_classes_count; i++ )
	{
	if (strcmp( messages_classes[i], message ) == 0)
		{
		return; 
	    }
	}
	fprintf(stderr,"*** WARNING *** message '%s' not sent due to missing keyword in filter table!!!\n", message );    
}
