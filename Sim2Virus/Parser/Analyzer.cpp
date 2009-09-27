#include "Parser.h"
using namespace std;

void init_bounds(BOUNDS *bounds) {
	(*bounds).first_row = INT_MAX;
	(*bounds).left_col = INT_MAX;
	(*bounds).right_col = 0;
	(*bounds).last_row = 0;
}

BOUNDS find_table_bounds(const TABLE& table) {
	BOUNDS bounds;
	init_bounds(&bounds);
	
	TABLE::const_iterator table_iterator = table.begin();	
	ROW row;
	ROW::const_iterator row_iterator;
	int column;
	int row_count = 0;
	int col_count = 0;
	
	while(table_iterator != table.end()) {
		row = *table_iterator;
		row_iterator = row.begin();	
		while(row_iterator != row.end()) {
			column = *row_iterator;

			if(column > 0) {
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
	
	return bounds;
}

BOUNDS find_largest_table_bounds(const MATRIX& matrix) {
	BOUNDS bounds;
	init_bounds(&bounds);
	BOUNDS ret;
	init_bounds(&ret);
	TABLE table;
	
	MATRIX::const_iterator matrix_iterator = matrix.begin();	
	
	while(matrix_iterator != matrix.end()) {
		table = *matrix_iterator;
		
		bounds = find_table_bounds(table);
		
		if(bounds.left_col < ret.left_col) {
			ret.left_col = bounds.left_col;
		}
		if(bounds.right_col > ret.right_col) {
			ret.right_col = bounds.right_col;
		}
		if(bounds.first_row < ret.first_row) {
			ret.first_row = bounds.first_row;
		}
		if(bounds.last_row > ret.last_row) {
			ret.last_row = bounds.last_row;
		}
		
		++matrix_iterator;
	}
	
	return ret;
}


