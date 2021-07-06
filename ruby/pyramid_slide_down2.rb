#!/usr/bin/env ruby

###
# For fun, I made this solution that shows the pyramid and the longest slide
#   down.
#
# @author Jonathan Bradley Whited
# @see    https://www.codewars.com/kata/pyramid-slide-down/ruby
# @rank   4 kyu
###
def longest_slide_down(pyramid,y=pyramid.length - 1,bottom=[Slide.new])
  if y == 0
    bottom[0].cost += pyramid[0][0]

    # Show the pyramid and the longest slide down as dots
    child = Slide.new(0,bottom[0],0,0)
    until (child = child.child).nil?
      print (' ' * ((pyramid.length - 1 - child.y) * 2))
      pyramid[child.y].each_with_index{|c,x| print (x == child.x) ? ' . ' : ('%3d ' % [c])}
      puts
    end
    puts "Longest slide down: #{bottom[0].cost}"

    return bottom[0].cost
  end

  row_len = pyramid[y].length

  # Can't use *= because we need new objects (not all the same reference)
  bottom = Array.new(row_len){|x| Slide.new(0,nil,x,y)} if bottom.length == 1
  bottom.map!.with_index{|slide,x| slide.cost += pyramid[y][x]; slide}

  row = []
  y -= 1

  for x in 0..row_len - 2
    child = bottom[x..x + 1].max
    row.push(Slide.new(child.cost,child,x,y))
  end

  longest_slide_down(pyramid,y,row)
end

class Slide
  attr_accessor :child,:cost,:x,:y

  def initialize(cost=0,child=nil,x=0,y=0)
    @child = child
    @cost = cost
    @x = x
    @y = y
  end

  # Needed for [].max()
  def <=>(other)
    @cost <=> other.cost
  end
end

# 23
longest_slide_down([
      [3],
     [7, 4],
   [2, 4, 6],
  [8, 5, 9, 3]
])
puts

# 1074
longest_slide_down([
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
