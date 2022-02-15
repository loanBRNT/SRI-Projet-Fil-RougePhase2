#ifndef INTERVALREGEXP_H
#define INTERVALREGEXP_H

#ifdef __cplusplus
extern "C" {
#endif

int regexpGen (char *regexp, size_t buflen, long min, long max, int flottant);

#ifdef __cplusplus
}
#endif

#endif
