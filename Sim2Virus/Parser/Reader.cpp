#include "Parser.h"
using namespace std;

MATRIX read_input_data(const char *filename) {
	MATRIX dataset;
	char current = 0;
	char previous = 0;
	ifstream inputstream;

	inputstream.open(filename);

	ROW row = ROW();
	TABLE table = TABLE(400);
	while(inputstream.good()) {
		inputstream.get(current);
		switch (current) {
			case 32:
				continue;
		    case 10:
		        if(previous == 10) {
					if(table.size() > 0) {
						dataset.push_back(table);
			        	table = TABLE();
					}
		        	continue;
		        }
		        previous = current;
		        table.push_back(row);
		        row = ROW();
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
		previous = current;
	}
	
	return dataset;
}
