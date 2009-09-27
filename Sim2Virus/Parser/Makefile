CC=g++
CFLAGS=-c -ftemplate-depth-128 -O3 -finline-functions -Wno-inline -Wall -no-cpp-precomp -gdwarf-2

parser: Parser.o Reader.o Analyzer.o Printer.o
	$(CC) Parser.o Reader.o Analyzer.o Printer.o -o parser
	
Parser.o: Parser.cpp
	$(CC) Parser.cpp $(CFLAGS)
	
Reader.o: Reader.cpp
	$(CC) Reader.cpp $(CFLAGS)
	
Analyzer.o: Analyzer.cpp
	$(CC) Analyzer.cpp $(CFLAGS)
	
Printer.o: Printer.cpp
	$(CC) Printer.cpp $(CFLAGS)
	
clean:
	rm *.o parser