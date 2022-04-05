MAKE = make
GCC = gcc
SRC = $(wildcard src/*.c)
EXC = ./bingbong.out
LIB = $(wildcard lib/*.a)


all :
	@$(MAKE) compile -s
	@$(MAKE) run -s

compile :
	@$(GCC) $(SRC) $(LIB) -lm -Wall -o $(EXC)

run :
	@$(EXC)