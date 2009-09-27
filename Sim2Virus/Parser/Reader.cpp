#include "Parser.h"
#include <stdlib.h>
#include <fcntl.h>
using namespace std;

#define BUFFER_SIZE 65536
char buffer[BUFFER_SIZE+1] = {0};

void read_input_data(MATRIX& dataset, char const *filename) {
	char previous = 0;
	char *current = 0;
	char *end = 0;
	int input = 0;

    ROW row = ROW();
    TABLE table = TABLE();

	input = open(filename, O_RDONLY);
	if(!input) {
		cerr << "couldn't read from: " << filename << endl;
		exit(2);
	}

	while( int length = read(input, buffer, BUFFER_SIZE) ) {
		current = buffer;
		end = buffer + length;

		while(current < end) {
        	switch (*current) {
            	case 32:
					++current;
					continue;
           		case 10:
                	if(previous == 10 && !table.empty()) {
                		dataset.push_back(table);
	                	table = TABLE();
						++current;
						continue;
	            	}
					if(!row.empty()) {
						table.push_back(row);
	                	row.clear();
					}
					previous = *current;
					++current;
					continue;
            	case 48:
                	row.push_back(0);
                	break;
            	case 49:
                	row.push_back(1);
                	break;
            	case 50:
               		row.push_back(2);
                	break;
        	}
			previous = *current;
			++current;
    	}

		bzero(buffer, BUFFER_SIZE);
	}
	
    close(input);
}
