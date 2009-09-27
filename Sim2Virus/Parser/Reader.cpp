#include "Parser.h"
using namespace std;

MATRIX read_input_data(const char *filename) {
	MATRIX dataset;
	char current = NULL;
	char previous = NULL;
	ifstream inputstream;

	inputstream.open(filename);

	ROW row = ROW();
	TABLE table = TABLE();
	while(inputstream.good()) {
		current = inputstream.get();
		if(current == 32) {
			continue;
		}
		if(current == 10) {
			if(previous == 10) {
				dataset.push_back(table);
				table = TABLE();
				continue;
			}
			previous = current;
			table.push_back(row);
			row = ROW();
			continue;
		}

		previous = current;
		row.push_back(atoi(&current));
	}
	
	return dataset;
}
