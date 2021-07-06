#!/usr/bin/env ruby

###
# In Ruby, this was super easy.
#
# @author Jonathan Bradley Whited
# @see    https://www.codewars.com/kata/calculating-with-functions/ruby
# @rank   5 kyu
###
%w(zero one two three four five six seven eight nine).each_with_index do |nw,i|
  define_method(nw) do |y=nil|
    return y.nil?() ? i : i.method(y[0]).call(y[1])
  end
end

def plus(y)       ['+',y] end
def minus(y)      ['-',y] end
def times(y)      ['*',y] end
def divided_by(y) ['/',y] end

puts seven times five   # 35
puts four plus nine     # 13
puts eight minus three  # 5
puts six divided_by two # 3
puts one minus seven    # -6
