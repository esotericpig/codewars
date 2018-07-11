#!/usr/bin/env ruby

###
# This was a fun Kata that could be useful for real-world projects.
# 
# For my solution, I simply shifted/aligned the words to find the optimal (min)
#   difference of changes.
# 
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/did-you-mean-dot-dot-dot/ruby
# @rank   5 kyu
###
class Dictionary
  def initialize(words)
    @words = words
  end
  
  def find_most_similar(term)
    most_similar = @words.first
    most_similar_diff = diff(most_similar,term)
    
    for i in 1...@words.length
      word = @words[i]
      diff = diff(word,term)
      
      if diff < most_similar_diff
        most_similar = word
        most_similar_diff = diff
      end
    end
    
    puts "#{term} => #{most_similar}"
    most_similar
  end
  
  def diff(word,term,index=0)
    min,max = (word.length < term.length) ? [word,term] : [term,word]
    return max.length if (index + min.length) > max.length
    
    result = max.length - min.length
    
    for i in 0...min.length
      result += 1 if min[i] != max[index + i]
    end
    
    [result,diff(word,term,index + 1)].min
  end
end

languages = Dictionary.new(['javascript','java','ruby','php','python','coffeescript'])
languages.find_most_similar('heaven') # 'java'
languages.find_most_similar('fun')    # 'ruby' of course ;)

words=['cherry','peach','pineapple','melon','strawberry','raspberry','apple','coconut','banana']
test_dict=Dictionary.new(words)
test_dict.find_most_similar('strawbery') # 'strawberry'
test_dict.find_most_similar('berry')     # 'cherry'
test_dict.find_most_similar('aple')      # 'apple'

puts unless ARGV.empty?
ARGV.each do |arg|
  languages.find_most_similar(arg)
  test_dict.find_most_similar(arg)
end
