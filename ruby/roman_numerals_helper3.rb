# frozen_string_literal: true

#require 'nokogiri'
require 'open-uri'

###
# For this solution, I decided to have fun and use Google in order to be hackier
#   and also have the smallest code size lol.
#
# It is super slow, but passed.
#
# Originally, I used nokogiri because I expected to use its functionality, but
#   then all I ended up doing was parsing the content as a String out of laziness.
#
# @author Bradley Whited
# @see    https://www.codewars.com/kata/roman-numerals-helper/ruby
# @rank   4 kyu
###
class RomanNumerals
  def self.google_it(value,query)
    #doc = Nokogiri::HTML(URI("https://www.google.com/search?q=#{value}+#{query}").open,nil,'utf-8').to_s
    #doc = URI("https://www.google.com/search?q=#{value}+#{query}").read
    #doc.slice(doc.index("#{value} = "),100).gsub(/\A.*=[[:space:]]+/,'').gsub(/<.*\z/,'')

    # UPDATE: In 2025, changed to use DuckDuckGo, as Google & Bing require User-Agent, etc.
    url = "https://duckduckgo.com/?q=#{value}+#{query}&ia=answer"
    doc = URI(url).read
    doc.match(/"\s*output_value\s*"\s*:[\s"]*(?<answer>[^",]+)/)[:answer].strip
  end

  def self.to_roman(num)
    google_it(num,'to+roman+numerals')
  end

  def self.from_roman(roman)
    google_it(roman,'to+arabic+numerals').to_i
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

puts unless ARGV.empty?
ARGV.each do |arg|
  print "#{arg} => "
  puts (arg =~ /\d/) ? RomanNumerals.to_roman(arg.to_i) : RomanNumerals.from_roman(arg)
end
