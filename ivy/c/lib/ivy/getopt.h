#ifndef __GETOPT_H_
#define __GETOPT_H_

#ifdef __cplusplus
extern "C" {
#endif

extern int             optind ;
extern char            *optarg ;
extern int	getopt (int argc, char **argv, char *optstring) ;

#ifdef __cplusplus
}
#endif

#endif
