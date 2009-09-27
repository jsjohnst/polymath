#include "Parser.h"
#include <stdlib.h>
using namespace std;

void read_input_data(MATRIX& dataset, char const *filename) {
	int current = 0;
	int previous = 0;

    FILE* input = fopen(filename, "r");
	if(!input) {
		cerr << "couldn't read from: " << filename << endl;
		exit(2);
	}

    ROW row = ROW();
    TABLE table = TABLE();

    current = fgetc(input);

    while( current != EOF ) {
        switch (current) {
            case 32:
                current = fgetc(input);
				continue;
            case 10:
                if(previous == 10 && 0 != table.size()) {
                	dataset.push_back(table);
	                table = TABLE();
					goto loop_end2;
	            }
				if(0 != row.size()) {
					table.push_back(row);
	                row = ROW();
				}
				goto loop_end;
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

loop_end:		
        previous = current;
loop_end2:
        current = fgetc(input);
    }

    fclose(input);
}
