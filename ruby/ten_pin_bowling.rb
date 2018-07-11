#!/usr/bin/env ruby

###
# My solution uses multipliers (factors) for the previous spares/strikes.
# I didn't see anyone else use this idea.
# 
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/ten-pin-bowling/ruby
# @rank   4 kyu
###
def bowling_score(frames)
  factors = [1,1,1] # For spares & strikes
  prev_strike = false
  
  frames.split(/\s+/).reduce(0) do |score,frame|
    spare,strike = frame[1] == '/',frame[0] == 'X'
    
    prev_roll = 0
    rolls = frame.chars.map do |f|
      prev_roll = (f == '/') ? (10 - prev_roll) :
                  (f == 'X') ? 10 : f.to_i
    end
    rolls = rolls.zip(factors).map{|r,f| r * f}.reduce(&:+)
    
    factors = [spare  ? 2 :
               strike ? (prev_strike ? [factors[0] + 1,3].min : 2) : 1,
               strike ? 2 : 1,1]
    
    prev_strike = strike
    score + rolls
  end
end

puts bowling_score('11 11 11 11 11 11 11 11 11 11') # 20
puts bowling_score('X X X X X X X X X XXX')         # 300
puts bowling_score('5/ 4/ 3/ 2/ 1/ 0/ X 9/ 4/ 8/8') # 150
puts bowling_score('18 9/ X 70 08 9/ 50 12 81 XXX') # 123
puts bowling_score('5/ 4/ 3/ 2/ 1/ 0/ X 9/ 4/ 8/X') # 152
puts bowling_score('5/ 4/ 3/ 2/ 1/ 0/ X 9/ 4/ X8/') # 154

ARGV.each do |arg|
  puts "#{bowling_score(arg)}: #{arg}"
end
