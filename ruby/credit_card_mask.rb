#!/usr/bin/env ruby

###
# @author Jonathan Bradley Whited
# @see    https://www.codewars.com/kata/credit-card-mask/ruby
# @rank   7 kyu
###
def maskify(cc)
  cc.each_char.with_index.map{|c,i| (i < (cc.length - 4)) ? '#' : c}.join
end

def maskify2(cc)
  (cc.length <= 4) ? cc : (('#' * (cc.length - 4)) << cc[-4..-1])
end

puts maskify('4556364607935616')  # '############5616'
puts maskify2('4556364607935616') # '############5616'
puts maskify('1')                 # '1'
puts maskify2('1')                # '1'
puts "'#{maskify('')}'"           # ''
puts "'#{maskify2('')}'"          # ''

puts unless ARGV.empty?
ARGV.each do |arg|
  puts "#{arg} => #{maskify(arg)},#{maskify2(arg)}"
end
