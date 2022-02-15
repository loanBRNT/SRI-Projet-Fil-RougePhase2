#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

#include "intervalRegexp.h"

#ifdef __PRETTY_FUNCTION__
#else
#define __PRETTY_FUNCTION__ __FUNCTION__
#endif


#define MAXINT( a , b ) ((a) > (b) ? (a) : (b))
#define MININT( a , b ) ((a) < (b) ? (a) : (b))

#define Perr(...) (perr ( __PRETTY_FUNCTION__, __VA_ARGS__))
#define CHECK_AND_RETURN(a)   if (strlen (locBuf) <= buflen) {	\
			    strcpy (a, locBuf); \
			    return success; \
			 } else { \
                            return Perr ("CHECK_AND_RETURN"); }

#define EndLocBuf (&(locBuf[strlen(locBuf)]))
#ifdef WIN32
#define snprintf _snprintf
#endif
#define AddLocBuf(...) snprintf (EndLocBuf, sizeof (locBuf)-strlen(locBuf), __VA_ARGS__)

typedef struct  {
  int max;
  int rank;
} NextMax ;


typedef  char bool;

const bool success = 1;
const bool fail = 0;


static bool strictPosRegexpGen (char *regexp, size_t buflen, long min, long max, const char* decimalPart, 
								   const char* boundDecimalPart);
static bool genAtRank (char *regexp, size_t buflen, const char *min, const char *max, int rank);
static bool genPreRank (char *preRank, size_t buflen, const char *min, const char *max, int rank);
static bool genRank (char *outRank, size_t buflen, const char *min, const char *max, int rank);
static bool genPostRank (char *postRank, size_t buflen, int rank);
static bool substr (char *substring, size_t buflen, const char* expr, size_t pos, size_t len);
static char* reverse (char *string);
static char* longtoa (char *string, size_t buflen, long n);
static NextMax nextMax (const char *min, const char *max);
static bool perr (const char* func, const char *fmt, ...);



/*
#                                __ _                  _ __     _____
#                               / _` |                | '_ \   / ____|
#                 _ __    ___  | (_| |   ___  __  __  | |_) | | |  __    ___   _ __
#                | '__|  / _ \  \__, |  / _ \ \ \/ /  | .__/  | | |_ |  / _ \ | '_ \
#                | |    |  __/   __/ | |  __/  >  <   | |     | |__| | |  __/ | | | |
#                |_|     \___|  |___/   \___| /_/\_\  |_|      \_____|  \___| |_| |_|
*/
int regexpGen (char *regexp, size_t buflen, long min, long max, int flottant)
{
  char *decimalPart = "";
  char *boundDecimalPart = "";
  char locBuf [8192] = "(?:";

  
  if (flottant) {
    decimalPart = "(?:\\.\\d+)?";
    boundDecimalPart = "(?:\\.0+)?";
  }

  if (min > max) {
    int nmin = max;
    max = min;
    min = nmin;
  }

  if (min == max) {
    AddLocBuf ("%ld%s", min, decimalPart);
  } else if  (min < 0) {
    if  (max < 0) {
      //      reg = '\-(?:' .  strictPosRegexpGen (-max, -min, decimalPart, boundDecimalPart). ')';
      AddLocBuf ("\\-(?:");
      if (strictPosRegexpGen (EndLocBuf, sizeof (locBuf)-strlen(locBuf), -min, -max, decimalPart, 
			      boundDecimalPart) == fail) return fail;
      AddLocBuf (")");
    } else if  (max == 0) {
      AddLocBuf ("(?:0%s)|(?:-0%s)|-(?:", boundDecimalPart, decimalPart);
      if (strictPosRegexpGen (EndLocBuf, sizeof (locBuf)-strlen(locBuf), 1, -min, decimalPart, 
			  boundDecimalPart)== fail) return fail;
      AddLocBuf (")");
    } else {
      //reg ='(?:' . regexpGen (min, 0,withDecimal) . '|' .  regexpGen (0, max, withDecimal). ')' ;
      AddLocBuf ("(?:");
      if (regexpGen (EndLocBuf, sizeof (locBuf)-strlen(locBuf), min, 0, flottant)== fail) return fail;
      AddLocBuf ("|");
      if (regexpGen (EndLocBuf, sizeof (locBuf)-strlen(locBuf), 0, max, flottant)== fail) return fail;
      AddLocBuf (")");
    }
  } else if  (min == 0) {
    // reg = "(?:0{decimalPart})|" . strictPosRegexpGen (1, max, decimalPart,boundDecimalPart) ;
    AddLocBuf ("(?:0%s)|",decimalPart);
     if (strictPosRegexpGen (EndLocBuf, sizeof (locBuf)-strlen(locBuf), 1, max, decimalPart, 
			boundDecimalPart)== fail) return fail;
  } else {
     if (strictPosRegexpGen (EndLocBuf, sizeof (locBuf)-strlen(locBuf), min, max, decimalPart, 
			boundDecimalPart)== fail) return fail;
  }

  AddLocBuf (")(?![\\d.])");
  CHECK_AND_RETURN (regexp);
}

/*
#                        _             _           _      _____
#                       | |           (_)         | |    |  __ \
#                 ___   | |_    _ __   _     ___  | |_   | |__) |   ___    ___
#                / __|  | __|  | '__| | |   / __| | __|  |  ___/   / _ \  / __|
#                \__ \  \ |_   | |    | |  | (__  \ |_   | |      | (_) | \__ \
#                |___/   \__|  |_|    |_|   \___|  \__|  |_|       \___/  |___/
#                 _____            __ _                  _ __     _____
#                |  __ \          / _` |                | '_ \   / ____|
#                | |__) |   ___  | (_| |   ___  __  __  | |_) | | |  __    ___   _ __
#                |  _  /   / _ \  \__, |  / _ \ \ \/ /  | .__/  | | |_ |  / _ \ | '_ \
#                | | \ \  |  __/   __/ | |  __/  >  <   | |     | |__| | |  __/ | | | |
#                |_|  \_\  \___|  |___/   \___| /_/\_\  |_|      \_____|  \___| |_| |_|
*/
static bool strictPosRegexpGen (char *regexp, size_t buflen, long min, long max, const char* decimalPart, 
								   const char* boundDecimalPart)
{

#define maxSubReg 64
#define digitRegSize 128

  char regList[maxSubReg][digitRegSize];
  char locBuf[maxSubReg*digitRegSize] ;
  size_t regIndex=0,i;
  size_t nbRank;
  char maxAsString[32], minAsString[32];
  NextMax nMax;


  if ((min <= 0) || (max <= 0)) return Perr ("min or max <= 0");
  if (min == max) {
    sprintf (EndLocBuf, "%ld", max);
  } else {
      
    max--;
  
    nbRank = strlen (longtoa (maxAsString, sizeof (maxAsString), max));
    do {
      nMax = nextMax (longtoa (minAsString, sizeof (minAsString), min), 
		      longtoa (maxAsString, sizeof (maxAsString), max));
      if (genAtRank (regList[regIndex++], digitRegSize, minAsString, 
		     longtoa (maxAsString, sizeof (maxAsString), 
			   nMax.max), nMax.rank) == fail) return fail;
      if (regIndex == maxSubReg) return Perr ("regIndex == maxSubReg");
      min = nMax.max +1;
    } while (nMax.max != max);

    locBuf[0] = 0;
    for (i=0; i<regIndex; i++) {
      sprintf (EndLocBuf, "(?:%s%s)|", regList[i], decimalPart);
    }
    
    if (locBuf[strlen(locBuf)-1] == '|') {
      locBuf[strlen(locBuf)-1] = 0;
    }
    max++;
    sprintf (EndLocBuf, "|(?:%s%s)",  
	     longtoa (maxAsString, sizeof (maxAsString), max), boundDecimalPart);
  }

  CHECK_AND_RETURN (regexp);
}

/*
#                                        _      __  __
#                                       | |    |  \/  |
#                 _ __     ___  __  __  | |_   | \  / |   __ _  __  __
#                | '_ \   / _ \ \ \/ /  | __|  | |\/| |  / _` | \ \/ /
#                | | | | |  __/  >  <   \ |_   | |  | | | (_| |  >  <
#                |_| |_|  \___| /_/\_\   \__|  |_|  |_|  \__,_| /_/\_\
*/
static NextMax nextMax (const char *min, const char *max)
{
  NextMax nextMax ={0,0};
  char revMin[32], revMax[32];
  size_t nbDigitsMin, nbDigitsMax;
  size_t rankRev=0, rankForw, rank=0;
  int i;
  int currMax; 

  nbDigitsMin = strlen (min);
  nbDigitsMax = strlen (max);

  for (i=nbDigitsMin-1; i >= 0; i--) {
    revMin[nbDigitsMin-i-1]= min[i];
    //    printf ("DBG> nextMax  revMin[%d]= %c\n", nbDigitsMin-i-1, min[i]);
  }
  for (i=nbDigitsMax-nbDigitsMin; i >= 0; i--) {
    revMin[nbDigitsMax-i]= '0';
    // printf ("DBG> nextMax  revMin[%d]= %c\n", nbDigitsMax-i, '0');
  }

  for (i=nbDigitsMax-1; i >= 0; i--) {
    revMax[nbDigitsMax-i-1]= max[i];
  }
  revMin[nbDigitsMax] = revMax[nbDigitsMax] = 0;
  rankForw = nbDigitsMax -1;

  //  printf ("DBG> nextMax rev(%s)=%s rev(%s)=%s rankForw=%d\n", min, revMin, max, revMax, rankForw);

  //  en partant des unitées (digit de poids faible), premier digit de min != 0
  while ((revMin[rankRev] == '0') && (rankRev < nbDigitsMax)) rankRev++; 
  //  en partant du digit de poids fort, premier digit de max != du même digit de revMin
  while ((revMin[rankForw] == revMax[rankForw]) && rankForw > 0) rankForw--;

  if (rankForw <= rankRev) {
    rank = rankForw;
    revMin[rankForw]= revMax[rankForw] - (rankForw ? 1 : 0);
    for (i=0; i<rankForw; i++) revMin[i] = '9';
  } else {
    rank =  rankRev; 
    for (i=0; i<=rankRev; i++) revMin[i] = '9';
  }

  nextMax.max = atoi (reverse (revMin));
  nextMax.rank = rank+1;
  
  currMax = atoi (max);
  if (nextMax.max > currMax) nextMax.max = currMax;

  //  printf ("DBG> nextMax ('%s', '%s') = %d@%d\n", min, max, nextMax.max, nextMax.rank);
  return (nextMax);
}


/*
#                  __ _                    ____                           _
#                 / _` |                  / __ \                         | |
#                | (_| |   ___   _ __    / / _` |  _ __    __ _   _ __   | | _
#                 \__, |  / _ \ | '_ \  | | (_| | | '__|  / _` | | '_ \  | |/ /
#                  __/ | |  __/ | | | |  \ \__,_| | |    | (_| | | | | | |   <
#                 |___/   \___| |_| |_|   \____/  |_|     \__,_| |_| |_| |_|\_\
*/
static bool genAtRank (char *regexp, size_t buflen, const char *min, const char *max, int rank)
{
  char locBuf [512];

  if (genPreRank (locBuf, sizeof (locBuf), min, max, rank) == fail) return (fail);
  if (genRank (EndLocBuf, sizeof (locBuf)-strlen(locBuf), min, max, rank) == fail) return (fail);
  if (genPostRank (EndLocBuf, sizeof (locBuf)-strlen(locBuf), rank) == fail) return (fail);
  

  CHECK_AND_RETURN (regexp);
}

/*
#                  __ _                  _____                  _____                    _
#                 / _` |                |  __ \                |  __ \                  | |
#                | (_| |   ___   _ __   | |__) |  _ __    ___  | |__) |   __ _   _ __   | | _
#                 \__, |  / _ \ | '_ \  |  ___/  | '__|  / _ \ |  _  /   / _` | | '_ \  | |/ /
#                  __/ | |  __/ | | | | | |      | |    |  __/ | | \ \  | (_| | | | | | |   <
#                 |___/   \___| |_| |_| |_|      |_|     \___| |_|  \_\  \__,_| |_| |_| |_|\_\
*/
static bool genPreRank (char *preRank, size_t buflen, const char *min, const char *max, int rank)
{
  char locBuf [512], locBufMax[512];
  const char *lmin, *lmax;
  int i=0, j=0;

  while (min[i] == '0') i++;
  while (max[j] == '0') j++;

  lmin =  &(min[i]);
  lmax =  &(max[j]);
  
  //  printf ("DBG> genPreRank (lmin='%s'[%d], lmax='%s'[%d], rank=%d\n", lmin, (int) strlen (lmin), lmax,  
  //  (int) strlen (lmax), rank);

  if (substr (locBuf, sizeof (locBuf), lmin, 0, strlen (lmin) - rank) == fail) return fail;
  if (substr (locBufMax, sizeof (locBufMax), lmax, 0, strlen (lmax) - rank) == fail) return fail;

  if (strncmp (locBuf, locBufMax, MININT (sizeof (locBuf), sizeof (locBufMax))) != 0) 
    return Perr ("min=%s[%s] and max=%s[%s] should be invariants at rank %d", locBuf, min, locBufMax, max, rank);
  
  //  printf ("DBG> genPreRank ('%s', '%s', %d) = '%s'\n", min, max, rank, locBuf);

  CHECK_AND_RETURN (preRank);
}


/*
#                  __ _                  _____                    _
#                 / _` |                |  __ \                  | |
#                | (_| |   ___   _ __   | |__) |   __ _   _ __   | | _
#                 \__, |  / _ \ | '_ \  |  _  /   / _` | | '_ \  | |/ /
#                  __/ | |  __/ | | | | | | \ \  | (_| | | | | | |   <
#                 |___/   \___| |_| |_| |_|  \_\  \__,_| |_| |_| |_|\_\
*/
static bool genRank (char *outRank, size_t buflen, const char *min, const char *max, int rank)
{
  char locBuf [512];

  char a,b,lmin,lmax;
  a = min[strlen(min)-rank];
  b = max[strlen(max)-rank];

  lmin = MININT (a,b);
  lmax = MAXINT (a,b);
  
  if ((lmin == '0') && (lmax == '9')) {
    strcpy (locBuf, "\\d");
  } else if (lmin == lmax) {
    locBuf[0] = lmin;
    locBuf[1] = 0;
  } else if (lmax == (lmin+1)) {
    sprintf (locBuf, "[%c%c]", lmin, lmax);
  } else {
    sprintf (locBuf, "[%c-%c]", lmin, lmax);
  }

  CHECK_AND_RETURN (outRank);
}

/*
#                  __ _                  _____                   _      _____
#                 / _` |                |  __ \                 | |    |  __ \
#                | (_| |   ___   _ __   | |__) |   ___    ___   | |_   | |__) |   __ _   _ __
#                 \__, |  / _ \ | '_ \  |  ___/   / _ \  / __|  | __|  |  _  /   / _` | | '_ \
#                  __/ | |  __/ | | | | | |      | (_) | \__ \  \ |_   | | \ \  | (_| | | | | |
#                 |___/   \___| |_| |_| |_|       \___/  |___/   \__|  |_|  \_\  \__,_| |_| |_|
*/
static bool genPostRank (char *postRank, size_t buflen, int rank)
{
 char locBuf [512];

  if (rank <= 1) {
    strcpy (locBuf, "");
  } else if (rank == 2) {
    sprintf (locBuf, "\\d");
  } else {
    sprintf (locBuf, "\\d{%d}", rank -1);
  }

  CHECK_AND_RETURN (postRank);
}

/*
#                                _              _
#                               | |            | |
#                 ___    _   _  | |__    ___   | |_    _ __
#                / __|  | | | | | '_ \  / __|  | __|  | '__|
#                \__ \  | |_| | | |_) | \__ \  \ |_   | |
#                |___/   \__,_| |_.__/  |___/   \__|  |_|
*/
static bool substr (char *substring, size_t buflen, const char* expr, size_t pos, size_t len)
{
  char locBuf [512];
  size_t i, j=0;

  len = MAXINT (0, MININT (len, strlen (expr) - pos));
  for (i=pos; i<(pos+len); i++) {
    locBuf[j++]= expr[i];
  }
  locBuf[j] = 0;

  //  printf ("DBG> substr ('%s', %d, %d) = '%s'\n", expr, pos, len, locBuf);
  CHECK_AND_RETURN (substring);
}

/*
#
#
#                 _ __    ___  __   __   ___   _ __   ___     ___
#                | '__|  / _ \ \ \ / /  / _ \ | '__| / __|   / _ \
#                | |    |  __/  \ V /  |  __/ | |    \__ \  |  __/
#                |_|     \___|   \_/    \___| |_|    |___/   \___|
*/
static char* reverse (char *string)
{
  char *locBuf ;
  int i;
  size_t len = strlen (string);

  locBuf = malloc (len+1);
  for (i=len-1; i >= 0; i--) {
    locBuf[len-i-1]= string[i];
    //printf ("DBG> reverse  locBuf[%d]= %c\n",len- i-1, string[i]);
  }
  locBuf [len] = 0;

  //  printf ("DBG> reverse '%s' = '%s'\n", string, locBuf);
  strcpy (string, locBuf);
  free (locBuf);
  return (string);
}

static char* longtoa (char *string, size_t buflen, long n)
{
  snprintf (string, buflen, "%ld", n);
  return (string);
}


/*
#                 _ __
#                | '_ \
#                | |_) |   ___   _ __   _ __
#                | .__/   / _ \ | '__| | '__|
#                | |     |  __/ | |    | |
#                |_|      \___| |_|    |_|
*/
static bool perr (const char* func, const char *fmt, ...)
{
  char err[4096], buffer[2048];
  va_list args;
  va_start( args, fmt );     
  vsprintf( buffer, fmt, args );
  va_end( args );


  sprintf (err, "Erreur %s @ %s\n", buffer, func);
  fprintf (stderr, err);
  return (fail);
}
