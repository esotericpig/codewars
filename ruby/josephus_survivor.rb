#!/usr/bin/env ruby

###
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/josephus-survivor/ruby
# @rank   5 kyu
###

# Iterative (non-recursive) solution
# 
# @param [Integer] n number of people
# @param [Integer] k steps after to then kill
def josephus_survivor(n,k)
  survivors = (1..n).to_a
  
  i = 0
  k -= 1
  
  while survivors.length > 1
    survivors.delete_at(i = (i + k) % survivors.length)
  end
  
  survivors[0]
end

# Recursive solution
def josephus_survivor_rec(n,k)
  (n <= 1) ? 1 : (((josephus_survivor_rec(n - 1,k) + k - 1) % n) + 1)
end

puts josephus_survivor(7,3)       # 4
puts josephus_survivor_rec(7,3)   # 4
puts josephus_survivor(11,19)     # 10
puts josephus_survivor_rec(11,19) # 10
puts josephus_survivor(1,300)     # 1
puts josephus_survivor_rec(1,300) # 1
puts josephus_survivor(14,2)      # 13
puts josephus_survivor_rec(14,2)  # 13
puts josephus_survivor(100,1)     # 100
puts josephus_survivor_rec(100,1) # 100

if ARGV.length >= 2
  n,k = ARGV[0].to_i,ARGV[1].to_i
  
  puts
  puts "(n=#{n},k=#{k}) => i:#{josephus_survivor(n,k)},r:#{josephus_survivor_rec(n,k)}"
end
