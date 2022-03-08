MAKE = make
GCC = gcc
SRC = $(wildcard src/*.c)
EXC = ./moteur.out
LIB = $(wildcard lib/*.a)
INCLUDE = include/


all :
	@$(MAKE) compile -s
	@$(MAKE) run -s

compile :
	@$(GCC) $(SRC) $(LIB) -lm -Wall -o $(EXC)

run :
	@$(EXC)
