#include "Parser.h"
using namespace std;

int main (const int argc, const char **argv)
{
	if(argc != 3) {
		cerr << "Usage: " << argv[0] << " <input> <output>" << endl;
		return 1;
	}
	
	const char *input_filename = argv[1];
	const char *output_filename = argv[2];
	
	cout << "Reading input from: " << input_filename << endl;
	
	MATRIX dataset = MATRIX();
	read_input_data(dataset, input_filename);
	
	cout << "Found " << dataset.size() << " time courses." << endl;
	
	BOUNDS bounds = find_largest_table_bounds(dataset);
		
	cout << "Max time course dimensions: " << (bounds.right_col - bounds.left_col) << "x" << (bounds.last_row - bounds.first_row) << endl;

	output_dataset(output_filename, dataset, bounds);
	
	cout << "Wrote output to: " << output_filename << endl;

	return 0;
}


