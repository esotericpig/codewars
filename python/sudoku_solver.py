#!/usr/bin/env python3

import numpy as np
import sys

from copy import deepcopy

"""
For this solution, I first created a set with the numbers 1-9.
Then I deleted the numbers found from the columns, rows, and groups.
If there was only 1 number left, I had found the value for that position.

A group is basically a Moore neighborhood (3x3), but only 9 groups.

author: Jonathan Bradley Whited (@esotericpig)
see:    https://www.codewars.com/kata/sudoku-solver/python
see:    https://en.wikipedia.org/wiki/Sudoku
see:    https://en.wikipedia.org/wiki/Moore_neighborhood
rank:   3 kyu
"""

def sudoku(puzzle):
  while not solve(puzzle):
    pass
  return puzzle

def solve(puzzle):
  cols = []
  for x in range(0,9):
    col = set()
    for y in range(0,9):
      col.add(puzzle[y][x])
    cols.append(col)
  
  groups = []
  for group_y in range(0,9,3):
    for group_x in range(0,9,3):
      group = set()
      for y in range(group_y,group_y + 3):
        for x in range(group_x,group_x + 3):
          group.add(puzzle[y][x])
      groups.append(group)
  
  solved_all = True
  for y in range(0,9):
    for x in range(0,9):
      if puzzle[y][x] != 0: continue
      
      nums = set(range(1,10))
      nums -= cols[x]
      nums -= set(puzzle[y]) # rows
      nums -= groups[x // 3 + (y // 3 * 3)]
      
      if len(nums) == 1:
        puzzle[y][x] = nums.pop()
      else:
        solved_all = False
  
  return solved_all

def print_2da(title,a):
  print(title)
  
  for row in a:
    print(end='  ')
    for col in row:
      print(col,end=' ')
    print()

puzzle = [[5,3,0,0,7,0,0,0,0],
          [6,0,0,1,9,5,0,0,0],
          [0,9,8,0,0,0,0,6,0],
          [8,0,0,0,6,0,0,0,3],
          [4,0,0,8,0,3,0,0,1],
          [7,0,0,0,2,0,0,0,6],
          [0,6,0,0,0,0,2,8,0],
          [0,0,0,4,1,9,0,0,5],
          [0,0,0,0,8,0,0,7,9]]

solution = [[5,3,4,6,7,8,9,1,2],
            [6,7,2,1,9,5,3,4,8],
            [1,9,8,3,4,2,5,6,7],
            [8,5,9,7,6,1,4,2,3],
            [4,2,6,8,5,3,7,9,1],
            [7,1,3,9,2,4,8,5,6],
            [9,6,1,5,3,7,2,8,4],
            [2,8,7,4,1,9,6,3,5],
            [3,4,5,2,8,6,1,7,9]]

result = sudoku(deepcopy(puzzle))
print('Correct? ',result == solution)
print_2da('Puzzle:',puzzle)
print_2da('Result:',result)

# If you really want to type it all in...
# Example: python3 sudoku_solver.py 530070000600195000098000060800060003400803001700020006060000280000419005000080079
if len(sys.argv) > 1:
  for i in range(1,len(sys.argv)):
    print()
    arg = sys.argv[i]
    
    if len(arg) != 81:
      print('Skipping arg[{}]: len of {} != 81'.format(i,len(arg)))
      continue
    
    puzzle = np.reshape(list(map(int,arg)),(9,9)).tolist()
    result = sudoku(deepcopy(puzzle))
    
    print_2da('Puzzle from arg[{}]:'.format(i),puzzle)
    print_2da('Result from arg[{}]:'.format(i),result)
