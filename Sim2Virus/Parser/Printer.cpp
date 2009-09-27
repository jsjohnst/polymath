#include "Parser.h"
using namespace std;

void output_dataset(const char *filename, const MATRIX& matrix, const BOUNDS& min_bounds) {
	ofstream output(filename);
	
	MATRIX::const_iterator matrix_iterator = matrix.begin();
	MATRIX::const_iterator matrix_end = matrix.end();	
	
	while(matrix_iterator != matrix_end) {
		output_table(output, *matrix_iterator, min_bounds);
		++matrix_iterator;
	}
	
	output.close();
}

void output_table(ostream& output, const TABLE& table, const BOUNDS& min_bounds) {
	int row_count = 0;
	
	TABLE::const_iterator table_iterator = table.begin();
	TABLE::const_iterator table_end = table.end();	
	
	while(table_iterator != table_end) {
		if(row_count >= min_bounds.first_row && row_count <= min_bounds.last_row) {
			output_row(output, *table_iterator, min_bounds.left_col, min_bounds.right_col);
		}
		if(row_count > min_bounds.last_row) {
			break;
		}
		++row_count;
		++table_iterator;
	}
	
	output << endl;	
}

void output_row(ostream& output, const ROW& row, const int& start, const int& end) {
	int column_count = 0;
	ROW::const_iterator row_iterator = row.begin();
	ROW::const_iterator row_end = row.end();	
	
	while(row_iterator != row_end) {
		if(column_count >= start && column_count <= end) {
			output << (*row_iterator) << " ";
		}
		if(column_count > end) {
			return;
		}
		++column_count;
		++row_iterator;
	}
}


