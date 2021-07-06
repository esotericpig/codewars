#!/usr/bin/env ruby

###
# The best solution used Array.transpose() for the columns/blocks.
# I didn't know about that method! Learn and remember.
#
# For this solution, I pretty much just copied what I did for
#   /python/sudoku_solver.py and changed it to validate, instead of to solve.
# Because of this, I solved it very quickly.
#
# @author Jonathan Bradley Whited
# @see    https://www.codewars.com/kata/validate-sudoku-with-size-nxn/ruby
# @see    https://ruby-doc.org/core-2.5.1/Array.html#method-i-transpose
# @rank   4 kyu
###
class Sudoku
  def initialize(puzzle)
    @block_n = 0
    @n = puzzle.length
    @puzzle = puzzle
  end

  def is_valid
    return false if @n < 1 || @puzzle.any?{|row| row.length != @n}
    return false if ((@block_n = Math.sqrt(@n)) % 1) != 0

    @block_n = @block_n.to_i
    @blocks = Array.new(@n){[]}
    @columns = Array.new(@n){[]}

    for y in 0...@n
      for x in 0...@n
        value = @puzzle[y][x]

        return false if !value.is_a?(Integer) || value < 1 || value > @n

        get_block(x,y).push(value)
        @columns[x].push(value)
      end
    end

    for y in 0...@n
      for x in 0...@n
        value = @puzzle[y][x]

        return false if get_block(x,y).count(value) > 1
        return false if @columns[x].count(value) > 1
        return false if @puzzle[y].count(value) > 1
      end
    end

    true
  end

  def get_block(x,y)
    @blocks[x / @block_n + (y / @block_n * @block_n)]
  end
end

# Valid Sudoku
# - true; Testing valid 9x9
puts Sudoku.new([
  [7,8,4, 1,5,9, 3,2,6],
  [5,3,9, 6,7,2, 8,4,1],
  [6,1,2, 4,3,8, 7,5,9],

  [9,2,8, 7,1,5, 4,6,3],
  [3,5,7, 8,4,6, 1,9,2],
  [4,6,1, 9,2,3, 5,8,7],

  [8,7,6, 3,9,4, 2,1,5],
  [2,4,3, 5,6,1, 9,7,8],
  [1,9,5, 2,8,7, 6,3,4]
]).is_valid

# - true; Testing valid 4x4
puts Sudoku.new([
  [1,4, 2,3],
  [3,2, 4,1],

  [4,1, 3,2],
  [2,3, 1,4]
]).is_valid

# Invalid Sudoku
# - false; Values in wrong order
puts Sudoku.new([
  [0,2,3, 4,5,6, 7,8,9],
  [1,2,3, 4,5,6, 7,8,9],
  [1,2,3, 4,5,6, 7,8,9],

  [1,2,3, 4,5,6, 7,8,9],
  [1,2,3, 4,5,6, 7,8,9],
  [1,2,3, 4,5,6, 7,8,9],

  [1,2,3, 4,5,6, 7,8,9],
  [1,2,3, 4,5,6, 7,8,9],
  [1,2,3, 4,5,6, 7,8,9]
]).is_valid

# - false; 4x5 (invalid dimension)
puts Sudoku.new([
  [1,2,3,4,5],
  [1,2,3,4],
  [1,2,3,4],
  [1]
]).is_valid

# Example: ruby validate_sudoku_with_size_nxn.rb 1423-3241-4132-2314
ARGV.each do |arg|
  puts
  arg = arg.split('-').map{|row| row.gsub(/\s+/,'').chars.map(&:to_i)}
  arg.each{|row| puts row.join(' ')}
  puts "Is valid? #{Sudoku.new(arg).is_valid.to_s.capitalize}"
end
