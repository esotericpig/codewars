#!/usr/bin/env ruby

###
# For this solution, I used the computed values from the first solution to make
#   the code size smaller (and technically faster).
# 
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/roman-numerals-helper/ruby
# @rank   4 kyu
###
class RomanNumerals
  NULLA = 'nulla'
  
  # In reverse order for to_roman(...)
  ROMAN_NUMS = {'M'=>1000,'CM'=>900,'D'=>500,'CD'=>400,'C'=>100,'XC'=>90,
                'L'=>50,'XL'=>40,'X'=>10,'IX'=>9,'V'=>5,'IV'=>4,'I'=>1}
  NUM_ROMANS = ROMAN_NUMS.invert
  
  def self.to_roman(num)
    return NULLA if num == 0 || !num
    roman = NUM_ROMANS[num]
    return roman unless roman.nil?
    
    roman = ''
    NUM_ROMANS.each do |value,numeral|
      while num >= value
        num -= value
        roman << numeral
      end
    end
    roman
  end
  
  def self.from_roman(roman)
    return 0 if roman == NULLA
    num = ROMAN_NUMS[roman]
    return num unless num.nil?
    
    num,prev_value = 0,0
    (roman.length - 1).downto(0) do |i|
      num += ((value = ROMAN_NUMS[roman[i]]) < prev_value) ? -value : value
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
