<?php

$tables = array();
$table = array();

print("Starting data loading\n");

foreach(file("data.txt") as $line) {
	if(trim($line) == "") {
		$tables[] = serialize($table);
		$table = array();
		continue;
	}
	$table[] = explode(" ", $line);
}

print("Data loaded.\n");

$tables = array_reverse($tables);

$first_row = PHP_INT_MAX;
$left_col = PHP_INT_MAX;
$right_col = 0;
$last_row = 0;

foreach($tables as $serialized_table) {
	$table = unserialize($serialized_table);
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
}

$tables = array_reverse($tables);

$fp = fopen("output.txt", "w");

foreach($tables as $serialized_table) {
	$table = unserialize($serialized_table);
	if(count($table) < 1) continue;
	
	foreach($table as $rcount=>$row) {
		if($rcount < $first_row) continue;
		if($rcount > $last_row) break;

		fwrite($fp, implode(" ", array_slice($row, $left_col, $right_col - $left_col)) . " ");
	}
	
	fwrite($fp, "\n");
	
	printf("Found and output table with dimensions: %d x %d\n", $right_col - $left_col, $last_row - $first_row);
}

fclose($fp);

print("Done!\n");

