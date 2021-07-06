#!/usr/bin/env ruby

###
# I liked this Kata because it used The Big Bang Theory characters.
#
# The best solution used a similar approach but divided by 2 down, which is far
#   less code and more clever.
#
# @author Jonathan Bradley Whited
# @see    https://www.codewars.com/kata/double-cola/ruby
# @rank   5 kyu
###
def whoIsNext(names,r)
  clones = 1 # Number of clones (doubles) at this shift
  shifts = 0 # Number of shifts done

  while (s = shifts + (names.length * clones)) < r
    shifts = s
    clones *= 2
  end

  result = nil

  # Go to r (shift) from current shifts
  names.each do |name|
    result = name
    break if (shifts += clones) >= r
  end
  result
end

names = ['Sheldon','Leonard','Penny','Rajesh','Howard']

puts whoIsNext(names,1802) # Penny
puts whoIsNext(names,50)   # Leonard

puts unless ARGV.empty?
ARGV.each do |arg|
  puts "#{arg} => #{whoIsNext(names,arg.to_i)}"
end
