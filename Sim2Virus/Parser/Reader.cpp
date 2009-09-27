#include "Parser.h"
#include <stdlib.h>
using namespace std;

void read_input_data(MATRIX& dataset, char const *filename) {
	auto char current = 0;
	auto char previous = 0;

    FILE* input = fopen(filename, "r");
	if(!input) {
		cerr << "couldn't read from: " << filename << endl;
		exit(2);
	}

    ROW row = ROW();
    TABLE table = TABLE();

    current = getc_unlocked(input);

	// this works despite current defined as a char
	// bad practice, yes, but works for our needs
    while( current != EOF ) {
        switch (current) {
            case 32:
                current = getc_unlocked(input);
				continue;
            case 10:
                if(previous == 10 && !table.empty()) {
                	dataset.push_back(table);
	                table = TABLE();
					goto loop_end2;
	            }
				if(!row.empty()) {
					table.push_back(row);
	                row.clear();
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
        current = getc_unlocked(input);
    }

    fclose(input);
}
