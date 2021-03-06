#include "Parser.h"
using namespace std;

void init_bounds(BOUNDS *bounds) {
	(*bounds).first_row = INT_MAX;
	(*bounds).left_col = INT_MAX;
	(*bounds).right_col = 0;
	(*bounds).last_row = 0;
}

void find_table_bounds(const TABLE& table, BOUNDS& bounds) {
	TABLE::const_iterator table_iterator = table.begin();
	TABLE::const_iterator table_end = table.end();	
	ROW::const_iterator row_iterator;
	ROW::const_iterator row_end;
	int row_count = 0;
	int col_count = 0;
	
	while(table_iterator != table_end) {
		row_iterator = (*table_iterator).begin();	
		row_end = (*table_iterator).end();
		while(row_iterator != row_end) {
			if(*row_iterator > 0) {
				if(col_count < bounds.left_col) {
					bounds.left_col = col_count;
				}
				if(col_count > bounds.right_col) {
					bounds.right_col = col_count;
				}
				if(row_count < bounds.first_row) {
					bounds.first_row = row_count;
				}
				if(row_count > bounds.last_row) {
					bounds.last_row = row_count;
				}
			}
			
			++col_count;
			++row_iterator;
		}
		
		++row_count;
		col_count = 0;
		++table_iterator;
	}
}

BOUNDS find_largest_table_bounds(const MATRIX& matrix) {
	BOUNDS bounds;
	init_bounds(&bounds);
	
	MATRIX::const_iterator matrix_iterator = matrix.begin();	
	
	while(matrix_iterator != matrix.end()) {
		find_table_bounds(*matrix_iterator, bounds);
		++matrix_iterator;
	}
	
	return bounds;
}


