#!/usr/bin/env python3

import copy
import itertools
import re
import sys

"""
This was a real pain and took me a while to solve.

My solution uses the following strategies:
- Sole candidates
- Unique candidates
- Naked candidates (singles, pairs, triplets, quads)
- Hidden candidates (singles, pairs, triplets, quads)
- Backtracking

Unlike /python/sudoku_solver.py, I decided to use a bit board to represent the
  possible candidates in a group (blocks, columns, rows). This still requires
  one loop [Cell.sole_candidate()], but I think it is probably faster than using
  set() and set() operations.

In the future, I would like to make a Sudoku project (probably in Ruby, sorry
  Python) that implements more strategies.

author: Jonathan Bradley Whited
see:    https://www.codewars.com/kata/hard-sudoku-solver/python
see:    https://www.learn-sudoku.com/basic-techniques.html
see:    https://www.kristanix.com/sudokuepic/sudoku-solving-techniques.php
rank:   3 kyu
"""

def solve(board):
  if isinstance(board,list):
    board = Board(board)

  board.solve()

  if board.invalid: return None
  if board.solved: return board.board

  empty,empty_i = board.min_empty()

  for candidate in empty.candidates:
    guess = Board(board)
    guess.set_cell(empty,candidate,empty_i)

    solution = solve(guess)
    if solution is not None: return solution

  return None

# 1 = not a candidate; 0 = a candidate.
# The binary's place is the number, so there are 9 places.
#
# For example: 101010101
#              987654321
# This means [2,4,6,8] are candidates for this cell.
class Cell:
  ALL_BIT_CANDIDATES = 0
  NO_BIT_CANDIDATES = 511 # 111111111

  def __init__(self,x,y):
    self.bit_candidates = self.ALL_BIT_CANDIDATES
    self.candidates = set()
    self.x = x
    self.y = y

  def __eq__(self,other):
    return self.x == other.x and self.y == other.y

  def __hash__(self):
    return hash((x,y))

  def sole_candidate(self):
    self.candidates = set()
    result = 0

    for i in range(9):
      if (self.bit_candidates & (1 << i)) == 0:
        self.candidates.add(i + 1)

    if len(self.candidates) == 1:
      for result in self.candidates: break

    return result

class Board:
  def __init__(self,board):
    self.board = None
    self.cached_hash = None
    self.empties = []

    self.bit_blocks = []
    self.bit_columns = []
    self.bit_rows = []

    self.invalid = False
    self.solved = False
    self.solved_cell = False

    if isinstance(board,list):
      self.board = copy.deepcopy(board)

      for i in range(9):
        self.bit_blocks.append(0)
        self.bit_columns.append(0)
        self.bit_rows.append(0)

      for y in range(9):
        for x in range(9):
          num = self.board[y][x]

          if num == 0:
            self.empties.append(Cell(x,y))
          else:
            bit_num = 1 << (num - 1)

            self.bit_blocks[self.block_i(x,y)] |= bit_num
            self.bit_columns[x] |= bit_num
            self.bit_rows[y] |= bit_num
    else:
      self.board = copy.deepcopy(board.board)
      self.cached_hash = board.cached_hash
      self.empties = copy.deepcopy(board.empties)

      for i in range(9):
        self.bit_blocks.append(copy.deepcopy(board.bit_blocks[i]))
        self.bit_columns.append(copy.deepcopy(board.bit_columns[i]))
        self.bit_rows.append(copy.deepcopy(board.bit_rows[i]))

  def __eq__(self,other):
    return self.board == other.board

  def __hash__(self):
    if self.cached_hash is None:
      self.cached_hash = hash(tuple(itertools.chain.from_iterable(self.board)))
    return self.cached_hash

  # Used for guesses/backtracking.
  def block_empty_count(self,x,y):
    bit_block = self.bit_block(x,y)
    count = 0

    for i in range(9):
      if (bit_block & (1 << i)) == 0:
        count += 1

    return count

  def end_solve(self):
    return self.invalid or self.solved

  # Used for guesses/backtracking.
  def min_empty(self):
    min_cell = None
    min_count = 11
    min_index = -1

    for i,cell in enumerate(self.empties):
      self.sole_candidate(cell)
      count = self.block_empty_count(cell.x,cell.y)

      if count < min_count:
        min_cell = cell
        min_count = count
        min_index = i

    return (min_cell,min_index)

  def sole_candidate(self,cell):
    cell.bit_candidates |= self.bit_block(cell.x,cell.y)
    cell.bit_candidates |= self.bit_columns[cell.x]
    cell.bit_candidates |= self.bit_rows[cell.y]

    return cell.sole_candidate()

  def solve(self):
    while True:
      self.solved_cell = False

      if self.solve_sole_candidates().end_solve(): return
      if self.solve_unique_candidates().end_solve(): return

      if not self.solved_cell: break

  def solve_cell(self,cell,index=None):
    if cell.bit_candidates == Cell.NO_BIT_CANDIDATES:
      self.invalid = True
      self.solved = False
      return False

    candidate = self.sole_candidate(cell)

    if candidate > 0:
      self.set_cell(cell,candidate,index)
      self.solved_cell = True
      return True

    return False

  def solve_sole_candidates(self):
    self.solved = True

    i = 0 # Inside the loop, we might delete empties
    while i < len(self.empties):
      cell = self.empties[i]

      if self.solve_cell(cell,i): continue
      if self.invalid: return self

      self.solved = False
      i += 1

    return self

  def solve_unique_candidates(self):
    block_uniques = Uniques()
    column_uniques = Uniques()
    row_uniques = Uniques()

    for i in range(Uniques.MAX_UNIQUES):
      block_uniques.init()
      column_uniques.init()
      row_uniques.init()

      for j in range(9):
        block_uniques.init_group(i)
        column_uniques.init_group(i)
        row_uniques.init_group(i)

    for cell in self.empties:
      self.sole_candidate(cell)

      for i in range(Uniques.MAX_UNIQUES):
        block_group = block_uniques.group(i,self.block_i(cell.x,cell.y))
        column_group = column_uniques.group(i,cell.x)
        row_group = row_uniques.group(i,cell.y)

        for combo in itertools.combinations(cell.candidates,i + 1):
          block_group.add_combo(combo,cell)
          column_group.add_combo(combo,cell)
          row_group.add_combo(combo,cell)

    if block_uniques.eliminate_candidates(): self.solved_cell = True
    if column_uniques.eliminate_candidates(): self.solved_cell = True
    if row_uniques.eliminate_candidates(): self.solved_cell = True

    return self

  def set_cell(self,cell,num,index=None):
    bit_num = 1 << (num - 1)

    self.bit_blocks[self.block_i(cell.x,cell.y)] |= bit_num
    self.bit_columns[cell.x] |= bit_num
    self.bit_rows[cell.y] |= bit_num
    self.board[cell.y][cell.x] = num
    self.cached_hash = None

    if index is None:
      try:
        self.empties.remove(cell)
      except ValueError:
        pass
    else:
      try:
        del self.empties[index]
      except IndexError:
        pass

  def bit_block(self,x,y):
    return self.bit_blocks[self.block_i(x,y)]

  def block_i(self,x,y):
    return x // 3 + (y // 3 * 3)

  # I used this for debugging unique candidates.
  def print_candidates(self,title=None):
    if title is not None: print(title)

    candidates = [['---' for x in range(9)] for y in range(9)]
    max_len = 3 # Must be > 0

    for cell in self.empties:
      candidates[cell.y][cell.x] = ''.join(map(str,sorted(cell.candidates)))
      candidates_len = len(cell.candidates)

      if candidates_len > max_len: max_len = candidates_len

    for y in range(9):
      for x in range(9):
        print('{:{}s}'.format(candidates[y][x],max_len),end=' ')
        if ((x + 1) % 3) == 0: print(end=' ')

      print()
      if ((y + 1) % 3) == 0: print()

class UniqueCombo:
  def __init__(self):
    self.cells = []
    self.count = 0

class UniqueGroup:
  def __init__(self):
    self.combos = {}

  def add_combo(self,candidates,cell):
    combo = self.combos.get(candidates)

    if combo is None:
      combo = UniqueCombo()
      self.combos[candidates] = combo

    combo.cells.append(cell)
    combo.count += 1

# This is for unique, naked, and hidden candidates.
class Uniques:
  MAX_UNIQUES = 4 # 0 = singles; 1 = pairs; 2 = triplets; 3 = quads; etc.

  def __init__(self):
    self.uniques = []

  def init(self):
    self.uniques.append([])

  def init_group(self,i):
    self.uniques[i].append(UniqueGroup())

  def eliminate_candidates(self):
    eliminated = False

    # Singles
    for group in self.uniques[0]:
      for candidates,combo in group.combos.items():
        if combo.count == 1:
          # Should only be 1 cell
          bit_candidates = Cell.NO_BIT_CANDIDATES ^ (1 << (candidates[0] - 1))
          cell = combo.cells[0]

          # Avoid infinite loop
          if cell.bit_candidates != bit_candidates:
            cell.bit_candidates = bit_candidates
            eliminated = True

    # Pairs, triplets, quads, etc.
    for i in range(1,self.MAX_UNIQUES):
      for j,group in enumerate(self.uniques[i]):
        for candidates,combo in group.combos.items():
          k = i + 1 # k => 2 = pairs; 3 = triplets; 4 = quads; etc.

          # Example for pairs:
          #   If (2,4) == 2 and (2) == 2 and (4) == 2, then must only be (2,4).
          if combo.count == k:
            bit_candidates = Cell.NO_BIT_CANDIDATES
            is_valid = True
            sibling_bit_candidates = Cell.ALL_BIT_CANDIDATES

            for candidate in candidates:
              # Check singles
              if self.uniques[0][j].combos[(candidate,)].count != k:
                is_valid = False
                break

              bit_candidate = 1 << (candidate - 1)
              bit_candidates ^= bit_candidate
              sibling_bit_candidates |= bit_candidate

            if is_valid:
              # Naked candidates
              for cell in combo.cells:
                # Avoid infinite loop
                if len(cell.candidates) > k:
                  cell.bit_candidates = bit_candidates
                  eliminated = True

              # Hidden candidates
              for sibling_combo in self.uniques[0][j].combos.values():
                cell = sibling_combo.cells[0]

                # In the previous singles/nakeds/hiddens we could have eliminated all, so check if none
                if (cell.bit_candidates | sibling_bit_candidates) != Cell.NO_BIT_CANDIDATES:
                  old_bit_candidates = cell.bit_candidates
                  cell.bit_candidates |= sibling_bit_candidates

                  # Avoid infinite loop
                  if cell.bit_candidates != old_bit_candidates:
                    eliminated = True

    return eliminated

  def group(self,i,j):
    return self.uniques[i][j]

def print_sudoku(board,title=None):
  if title is not None: print(title)
  y = 0

  for row in board:
    x = 0
    print(end='  ')

    for column in row:
      x += 1
      print('_' if column == 0 else column,end='  ' if (x % 3) == 0 else ' ')

    y += 1
    print()
    if (y % 3) == 0 and y != 8: print()

# I used this for debugging 1D lists.
def print_sudoku_group(group,title=None):
  if title is not None: print(title,end="\n  ")
  print("\n  ".join(map(str,group)))

# For args, non-digits are stripped, and you can use '-', '_', or '0'.
#
# Example: $ python3 hard_sudoku_solver.py "
#          >   9 - -  - 8 -  - - 1
#          >   - - -  4 - 6  - - -
#          >   - - 5  - 7 -  3 - -
#          >
#          >   - 6 -  - - -  - 4 -
#          >   4 - 1  - 6 -  5 - 8
#          >   - 9 -  - - -  - 2 -
#          >
#          >   - - 7  - 3 -  2 - -
#          >   - - -  7 - 5  - - -
#          >   1 - -  - 4 -  - - 7
#          > "
if len(sys.argv) > 1:
  for i in range(1,len(sys.argv)):
    arg = sys.argv[i]
    puzzle = []

    for row in re.split(r"\n+",arg):
      row = re.sub(r'[^\d\-_]+','',row)
      row = re.sub(r'[\-_]','0',row)
      row = list(map(int,row))

      if len(row) == 0: continue
      if len(row) != 9: break

      puzzle.append(row)

    if len(puzzle) != 9:
      print('Skipping arg[{}]: len != 9x9'.format(i))
      continue

    print_sudoku(puzzle,'Puzzle from arg[{}]:'.format(i))
    print_sudoku(solve(puzzle),'Result from arg[{}]:'.format(i))

  exit()

# Author's test
problem = [
  [9,0,0, 0,8,0, 0,0,1],
  [0,0,0, 4,0,6, 0,0,0],
  [0,0,5, 0,7,0, 3,0,0],

  [0,6,0, 0,0,0, 0,4,0],
  [4,0,1, 0,6,0, 5,0,8],
  [0,9,0, 0,0,0, 0,2,0],

  [0,0,7, 0,3,0, 2,0,0],
  [0,0,0, 7,0,5, 0,0,0],
  [1,0,0, 0,4,0, 0,0,7]
]
solution = [
  [9,2,6, 5,8,3, 4,7,1],
  [7,1,3, 4,2,6, 9,8,5],
  [8,4,5, 9,7,1, 3,6,2],

  [3,6,2, 8,5,7, 1,4,9],
  [4,7,1, 2,6,9, 5,3,8],
  [5,9,8, 3,1,4, 7,2,6],

  [6,5,7, 1,3,8, 2,9,4],
  [2,8,4, 7,9,5, 6,1,3],
  [1,3,9, 6,4,2, 8,5,7]
]
my_solution = solve(problem)
print_sudoku(problem,'Problem:')
print_sudoku(solution,'Solution:')
print_sudoku(my_solution,'My solution:')
print('My solution = solution? ',my_solution == solution)
print()

# Super hard puzzle (just 1-2 givens in each block)
puzzle = [
  [0,0,0, 0,7,0, 0,0,0],
  [0,3,0, 1,0,0, 0,9,0],
  [0,0,4, 0,0,0, 8,0,0],

  [0,0,6, 0,0,0, 0,4,0],
  [0,0,0, 0,0,0, 0,0,5],
  [0,0,0, 0,0,0, 0,0,0],

  [0,4,3, 2,0,0, 5,0,0],
  [0,0,0, 0,0,0, 0,3,0],
  [0,0,0, 0,3,0, 0,0,0]
]
print_sudoku(puzzle,'Super hard puzzle:')
print_sudoku(solve(puzzle),'Super hard solution:')

# Yonban hard test
puzzle = [
  [9,0,6, 0,7,0, 4,0,3],
  [0,0,0, 4,0,0, 2,0,0],
  [0,7,0, 0,2,3, 0,1,0],

  [5,0,0, 0,0,0, 1,0,0],
  [0,4,0, 2,0,8, 0,6,0],
  [0,0,3, 0,0,0, 0,0,5],

  [0,3,0, 7,0,0, 0,5,0],
  [0,0,7, 0,0,5, 0,0,0],
  [4,0,5, 0,1,0, 7,0,8]
]
print_sudoku(puzzle,'Yonban hard puzzle:')
print_sudoku(solve(puzzle),'Yonban hard solution:')

# Sanban hard test
puzzle = [
  [0,0,3, 2,0,0, 0,0,4],
  [0,2,0, 0,9,0, 0,6,0],
  [8,0,0, 0,0,5, 1,0,0],

  [6,0,0, 0,0,7, 4,0,0],
  [0,9,0, 0,5,0, 0,1,0],
  [0,0,7, 9,0,0, 0,0,6],

  [0,0,4, 3,0,0, 0,0,2],
  [0,3,0, 0,7,0, 0,4,0],
  [7,0,0, 0,0,4, 5,0,0]
]
print_sudoku(puzzle,'Sanban hard puzzle:')
print_sudoku(solve(puzzle),'Sanban hard solution:')

# Niban hard test
puzzle = [
  [0,0,5, 0,0,0, 8,0,0],
  [0,2,0, 8,0,9, 0,7,0],
  [3,0,0, 0,4,0, 0,0,1],

  [0,3,0, 2,0,6, 0,1,0],
  [0,0,2, 0,0,0, 5,0,0],
  [0,7,0, 5,0,4, 0,6,0],

  [2,0,0, 0,6,0, 0,0,4],
  [0,8,0, 4,0,2, 0,9,0],
  [0,0,7, 0,0,0, 2,0,0]
]
print_sudoku(puzzle,'Niban hard puzzle:')
print_sudoku(solve(puzzle),'Niban hard solution:')

# Ichiban hard test
puzzle = [
  [0,8,0, 0,0,9, 7,4,3],
  [0,5,0, 0,0,8, 0,1,0],
  [0,1,0, 0,0,0, 0,0,0],

  [8,0,0, 0,0,5, 0,0,0],
  [0,0,0, 8,0,4, 0,0,0],
  [0,0,0, 3,0,0, 0,0,6],

  [0,0,0, 0,0,0, 0,7,0],
  [0,3,0, 5,0,0, 0,8,0],
  [9,7,2, 4,0,0, 0,5,0]
]
print_sudoku(puzzle,'Ichiban hard puzzle:')
print_sudoku(solve(puzzle),'Ichiban hard solution:')

# Test sole candidate
puzzle = [
  [5,3,0, 0,7,0, 0,0,0],
  [6,0,0, 1,9,5, 0,0,0],
  [0,9,8, 0,0,0, 0,6,0],

  [8,0,0, 0,6,0, 0,0,3],
  [4,0,0, 8,0,3, 0,0,1],
  [7,0,0, 0,2,0, 0,0,6],

  [0,6,0, 0,0,0, 2,8,0],
  [0,0,0, 4,1,9, 0,0,5],
  [0,0,0, 0,8,0, 0,7,9]
]
print_sudoku(puzzle,'Sole candidate puzzle:')
b = Board(puzzle)
b.solve()
print_sudoku(b.board,'Sole candidate solution:')

# Test unique candidate (4 below 5)
puzzle = [
  [0,0,4, 0,0,0, 0,0,0],
  [0,0,0, 0,0,0, 0,0,0],
  [0,0,0, 0,0,0, 0,0,0],

  [0,0,0, 0,0,0, 0,0,0],
  [0,4,0, 0,0,0, 0,0,0],
  [0,0,0, 0,0,0, 0,0,0],

  [5,0,0, 0,0,0, 0,0,0],
  [0,0,0, 0,0,0, 0,0,0],
  [0,0,0, 0,0,4, 0,0,0]
]
print_sudoku(puzzle,'Unique candidate puzzle:')
b = Board(puzzle)
b.solve()
print_sudoku(b.board,'Unique candidate solution:')
