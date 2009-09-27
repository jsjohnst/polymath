#include "Parser.h"
using namespace std;

void output_dataset(const char *filename, const MATRIX& matrix, BOUNDS min_bounds) {
	ofstream output(filename);
	TABLE table;
	
	MATRIX::const_iterator matrix_iterator = matrix.begin();	
	
	while(matrix_iterator != matrix.end()) {
		table = *matrix_iterator;
		output_table(output, table, min_bounds);
		++matrix_iterator;
	}
	
	output.close();
}

void output_table(ostream& output, const TABLE& table, BOUNDS min_bounds) {
	int row_count = 0;
	ROW row;
	
	TABLE::const_iterator table_iterator = table.begin();	
	
	while(table_iterator != table.end()) {
		if(row_count >= min_bounds.first_row && row_count <= min_bounds.last_row) {
			row = *table_iterator;
			output_row(output, row, min_bounds.left_col, min_bounds.right_col);
		}
		if(row_count > min_bounds.last_row) {
			break;
		}
		++row_count;
		++table_iterator;
	}
	
	output << endl;	
}

void output_row(ostream& output, const ROW& row, int start, int end) {
	int column = 0;
	int column_count = 0;
	ROW::const_iterator row_iterator = row.begin();	
	
	while(row_iterator != row.end()) {
		if(column_count >= start && column_count <= end) {
			column = *row_iterator;
			output << column << " ";
		}
		if(column_count > end) {
			return;
		}
		++column_count;
		++row_iterator;
	}
}


