#!/usr/bin/env php
<?php

/**
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/equal-sides-of-an-array/php
 * @rank   6 kyu
 */

function find_even_index($arr){
  $left_sum = 0;
  $right_sum = array_sum($arr);
  $result = -1;
  
  // $i is index
  foreach($arr as $i=>$value) {
    $right_sum -= $value;
    
    if($left_sum == $right_sum) {
      $result = $i;
      break;
    }
    
    $left_sum += $value;
  }
  
  return $result;
}

echo find_even_index(array(1,2,3,4,3,2,1)).PHP_EOL;    // 3
echo find_even_index([1,100,50,-51,1,1]).PHP_EOL;      // 1
echo find_even_index([1,2,3,4,5,6]).PHP_EOL;           // -1
echo find_even_index([20,10,30,10,10,15,35]).PHP_EOL;  // 3
echo find_even_index([20,10,-80,10,10,15,35]).PHP_EOL; // 0
echo find_even_index([10,-80,10,10,15,35,20]).PHP_EOL; // 6
echo find_even_index(range(1,100)).PHP_EOL;            // -1
echo find_even_index([0,0,0,0,0]).PHP_EOL;             // 0 (Should pick the first index if more cases are valid)
echo find_even_index([-1,-2,-3,-4,-3,-2,-1]).PHP_EOL;  // 3
echo find_even_index(range(-100,-1)).PHP_EOL;          // -1

if($argc > 1) {
  echo PHP_EOL;
  $arr = array_map(function($v){return (int)$v;},array_slice($argv,1));
  echo json_encode($arr)." => ".find_even_index($arr).PHP_EOL;
}

?>
