#include <iostream>
#include <fstream>
#include <vector>

typedef std::vector<int> ROW;
typedef std::vector< ROW > TABLE;
typedef std::vector< TABLE > MATRIX;

struct BOUNDS {
	int left_col;
	int right_col;
	int first_row;
	int last_row;
};

MATRIX read_input_data(char *filename);
BOUNDS find_table_bounds(TABLE table);
BOUNDS find_largest_table_bounds(MATRIX matrix);
void init_bounds(BOUNDS *bounds);
void output_dataset(char *filename, MATRIX dataset, BOUNDS min_bounds);
void output_table(std::ostream *output, TABLE table, BOUNDS min_bounds);
void output_row(std::ostream *output, ROW row, int start, int end);