#!/usr/bin/env ruby

###
# For this solution, I decided to compute the sub(tract) roman numerals
#   during runtime.
# 
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/roman-numerals-helper/ruby
# @rank   4 kyu
###
class RomanNumerals
  NULLA = 'nulla'
  
  @@num_romans = nil
  @@roman_nums = nil
  
  def self.romans
    if @@num_romans.nil? || @@roman_nums.nil?
      romans = [[1,'I'],[5,'V'],[10,'X'],[50,'L'],[100,'C'],[500,'D'],[1000,'M']]
      
      # Each power of 10 (place) is a sub of the higher #s until the next one:
      # - 1, 10, 100, 1000; 1=>[2..10],10=>[11..100],100=>[101..1000]
      # - 1000 ('M') is skipped because there is nothing higher than it (in this Kata)
      # - It would probably be better to do a for-loop on the powers of 10 or test if a power of 10
      (0...romans.length - 1).step(2) do |i|
        sub_roman = romans[i]
        
        for j in 1..2
          roman = romans[i + j]
          # Ex: push([5 - 1,'I' + 'V'])
          romans.push([roman[0] - sub_roman[0],sub_roman[1] + roman[1]])
        end
      end
      
      # Sort the newly pushed sub(tract) roman numerals; reverse for to_roman(...)
      @@num_romans = romans.sort.reverse.to_h
      @@roman_nums = @@num_romans.invert
    end
  end
  
  def self.num_romans
    romans
    @@num_romans
  end
  
  def self.roman_nums
    romans
    @@roman_nums
  end
  
  def self.to_roman(num)
    return NULLA if num == 0 || !num
    roman = num_romans[num]
    return roman unless roman.nil?
    
    roman = ''
    num_romans.each do |value,numeral|
      while num >= value
        num -= value
        roman << numeral
      end
    end
    roman
  end
  
  def self.from_roman(roman)
    return 0 if roman == NULLA
    num = roman_nums[roman]
    return num unless num.nil?
    
    num,prev_value = 0,0
    (roman.length - 1).downto(0) do |i|
      num += ((value = roman_nums[roman[i]]) < prev_value) ? -value : value
      prev_value = value
    end
    num
  end
end

def test_to(num,roman)
  result = RomanNumerals.to_roman(num)
  puts "#{result == roman ? '-' : 'X'} To: #{num} => #{roman}: #{result}"
end

def test_from(roman,num)
  result = RomanNumerals.from_roman(roman)
  puts "#{result == num ? '-' : 'X'} From: #{roman} => #{num}: #{result}"
end

test_to(1000,'M')
test_to(4,'IV')
test_to(1,'I')
test_to(1990,'MCMXC')
test_to(2008,'MMVIII')

test_from('XXI',21)
test_from('I',1)
test_from('IV',4)
test_from('MMVIII',2008)
test_from('MDCLXVI',1666)

puts if ARGV.length > 0
ARGV.each do |arg|
  print "#{arg} => "
  puts (arg =~ /\d/) ? RomanNumerals.to_roman(arg.to_i) : RomanNumerals.from_roman(arg)
end
