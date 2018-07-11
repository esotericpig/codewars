#!/usr/bin/env ruby

###
# I love pyramids.
# 
# For this solution, I started at the bottom, compared pairs for the max, and
#   then continued to move up the pyramid.
# 
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/pyramid-slide-down/ruby
# @rank   4 kyu
###
def longest_slide_down(pyramid,y=pyramid.length - 1,bottom=[0])
  return bottom[0] + pyramid[0][0] if y == 0
  
  row_len = pyramid[y].length
  bottom *= row_len if bottom.length == 1
  bottom.map!.with_index{|cost,x| cost + pyramid[y][x]}
  row = []
  
  for x in 0..row_len - 2
    row.push(bottom[x..x + 1].max)
  end
  
  longest_slide_down(pyramid,y - 1,row)
end

# 23
puts longest_slide_down([
      [3],
     [7, 4],
   [2, 4, 6],
  [8, 5, 9, 3]
])

# 1074
puts longest_slide_down([
                              [75],
                            [95, 64],
                          [17, 47, 82],
                        [18, 35, 87, 10],
                      [20,  4, 82, 47, 65],
                    [19,  1, 23, 75,  3, 34],
                  [88,  2, 77, 73,  7, 63, 67],
                [99, 65,  4, 28,  6, 16, 70, 92],
              [41, 41, 26, 56, 83, 40, 80, 70, 33],
            [41, 48, 72, 33, 47, 32, 37, 16, 94, 29],
          [53, 71, 44, 65, 25, 43, 91, 52, 97, 51, 14],
        [70, 11, 33, 28, 77, 73, 17, 78, 39, 68, 17, 57],
      [91, 71, 52, 38, 17, 14, 91, 43, 58, 50, 27, 29, 48],
    [63, 66,  4, 68, 89, 53, 67, 30, 73, 16, 69, 87, 40, 31],
  [ 4, 62, 98, 27, 23,  9, 70, 98, 73, 93, 38, 53, 60,  4, 23]
])
