<?php

$tables = array();
$table_count = 0;
$tables[$table_count] = array();

foreach(file("data.txt") as $line) {
	if(trim($line) == "") {
		$table_count++;
		continue;
	}
	if(!is_array($tables[$table_count])) {
		$tables[$table_count] = array();
	}
	$tables[$table_count][] = explode(" ", $line);
}

$tables = array_reverse($tables);

$first_row = count($tables[0]); // set to the maximum number of rows
$left_col = count($tables[0][0]); // set to the # of columns in first row, should be good enough
$right_col = 0;
$last_row = 0;

$ouput = "";

foreach($tables as $table) {
	if(count($table) < 1) continue;

	foreach($table as $rcount=>$row) {
		foreach($row as $ccount=>$col) {
			if($col > 0) {
				if($ccount < $left_col) {
					$left_col = $ccount;
				}
				if($ccount > $right_col) {
					$right_col = $ccount;
				}
				if($rcount < $first_row) {
					$first_row = $rcount;
				}
				if($rcount > $last_row) {
					$last_row = $rcount;
				}
			}
		}
	}



	foreach($table as $rcount=>$row) {
		if($rcount < $first_row) continue;
		if($rcount > $last_row) break;

		$output .= implode(" ", array_slice($row, $left_col, $right_col - $left_col)) . " ";
	}
	
	$output .= "\n";
	
	printf("%d x %d\n\n", $right_col - $left_col, $last_row - $first_row);
}

file_put_contents("output.txt", $output);

