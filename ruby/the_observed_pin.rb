#!/usr/bin/env ruby

###
# The best solution was only 2 lines and used reduce()!
# The basic idea was the same as my code, using product().
#
# The adjacent digits (neighbors) are basically a von Neumann neighborhood,
#   including self.
#
# I could have sworn I did a similar thing to this in college, related to Gray
#   codes or something else, but I couldn't find the project and/or remember.
#
# @author Jonathan Bradley Whited
# @see    https://www.codewars.com/kata/the-observed-pin/ruby
# @see    https://en.wikipedia.org/wiki/Von_Neumann_neighborhood
# @see    https://en.wikipedia.org/wiki/Gray_code
# @rank   4 kyu
###

# Adjacent digits (neighbors); looks better as ints
DIGITS = {
  1=>[1,2,4]  ,2=>[1,2,3,5]  ,3=>[2,3,6],
  4=>[1,4,5,7],5=>[2,4,5,6,8],6=>[3,5,6,9],
  7=>[4,7,8]  ,8=>[0,5,7,8,9],9=>[6,8,9],
               0=>[0,8]
}

# Convert to strings
# - If you're using an older version of Ruby, you can comment this out and use
#     observed[...].to_i() in get_pins() instead.
DIGITS.transform_keys!(&:to_s)              # Ruby v2.5+
DIGITS.transform_values!{|v| v.map(&:to_s)} # Ruby v2.4+

def get_pins(observed)
  result = []

  DIGITS[observed[0]].each do |digit|
    pins = [[digit]] # Double array for length of 1

    for i in 1...observed.length
      pins = pins.product(DIGITS[observed[i]])
    end

    pins.map!{|pin| pin.flatten.join}
    result.push(pins)
  end

  result.flatten!
  result
end

# [0, 5, 7, 8, 9]
puts get_pins('8').sort.map(&:to_i).inspect
# [11, 12, 14, 21, 22, 24, 41, 42, 44]
puts get_pins('11').sort.map(&:to_i).inspect
# [236, 238, 239, 256, 258, 259, 266, 268, 269, 296, 298, 299, 336, 338, 339,
#  356, 358, 359, 366, 368, 369, 396, 398, 399, 636, 638, 639, 656, 658, 659,
#  666, 668, 669, 696, 698, 699]
puts get_pins('369').sort.map(&:to_i).inspect

#puts get_pins('2458').inspect
#puts get_pins('14290').inspect
#puts get_pins('683102).inspect

puts unless ARGV.empty?
ARGV.each do |arg|
  puts "#{arg} => #{get_pins(arg).sort.map(&:to_i).inspect}"
end
