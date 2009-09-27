#include <iostream>
#include <fstream>
#include <vector>
#include <list>

typedef std::vector<char> ROW;
typedef std::vector< ROW > TABLE;
typedef std::list< TABLE > MATRIX;

struct BOUNDS {
	int left_col;
	int right_col;
	int first_row;
	int last_row;
};

void read_input_data(MATRIX& dataset, char const *filename);
void find_table_bounds(const TABLE& table, BOUNDS& bounds);
BOUNDS find_largest_table_bounds(const MATRIX& matrix);
void init_bounds(BOUNDS *bounds);
void output_dataset(const char *filename, const MATRIX& dataset, const BOUNDS& min_bounds);
void output_table(std::ostream& output, const TABLE& table, const BOUNDS& min_bounds);
void output_row(std::ostream& output, const ROW& row, const int& start, const int& end);


