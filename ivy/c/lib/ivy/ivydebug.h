#ifndef IVY_DEBUG_H
#define IVY_DEBUG_H

#ifdef WIN32
#ifdef DEBUG
#define TRACE(format,...)  \
 fprintf (stderr, format , __VA_ARGS__ )

#define TRACE_IF( cond, format, ...)  \
	if ( cond ) fprintf (stderr, format , __VA_ARGS__ )

#else
#define TRACE(format, ...) /**/
#define TRACE_IF( cond, format, ...)  /**/
#endif
#else
#ifdef DEBUG
#define TRACE(format, args...)  \
 fprintf (stderr, format , ## args)

#define TRACE_IF( cond, format, args...)  \
	if ( cond ) fprintf (stderr, format , ## args)

#else
#define TRACE(format, args...) /**/
#define TRACE_IF( cond, format, args...)  /**/
#endif
#endif

#endif
