# frozen_string_literal: true

###
# For fun, I added an additional challenge of not using String/Array ops
#   [except for Array.include?()].
#
# @author Bradley Whited
# @see    https://www.codewars.com/kata/catching-car-mileage-numbers/ruby
# @rank   4 kyu
###
def is_interesting(num,awesome_phrases,score = 2)
  return score if awesome_phrases.include?(num)

  # No num.to_s.length added challenge :)
  len,n = 1,num
  while (n /= 10) > 0
    len += 1
  end

  if len >= 3
    return score if zeros?(num,len)              #  100, 90000
    return score if same_or_palindrome?(num,len) # 1111,  1221, 73837
    return score if seq_inc_or_dec?(num,len)     # 1234,  4321
  end

  return 0 if score == 1
  return score if (score = is_interesting(num + 1,awesome_phrases,1)) > 0
  return is_interesting(num + 2,awesome_phrases,1)
end

def zeros?(num,len)
  tens = 10**(len - 1)
  ((num.to_f / tens) - (num / tens)).zero?
end

def same_or_palindrome?(num,len)
  half_len = len / 2.0
  half1 = num / (10**half_len.round)

  half_len = half_len.to_i
  half2 = 0
  (1..half_len).each do |i|
    n = num % (10**i)         # shift digits left
    n /= (10**(i - 1))        # current digit
    n *= (10**(half_len - i)) # reverse pos (places)
    half2 += n
  end

  half1 == half2
end

def seq_inc_or_dec?(num,len)
  prev_digit = num / (10**(len - 1)) # first digit
  is_inc,is_dec = true,true

  while (len -= 1) >= 1
    digit = num % (10**len)  # shift digits left
    digit /= (10**(len - 1)) # current digit

    is_inc = false if digit != inc_or_dec(prev_digit,1)
    is_dec = false if digit != inc_or_dec(prev_digit,-1)
    return false unless is_inc || is_dec

    prev_digit = digit
  end

  is_inc || is_dec
end

def inc_or_dec(num,inc_or_dec)
  num += inc_or_dec
  (num < 0) ? 9 : ((num > 9) ? 0 : num)
end

puts is_interesting(     3,[1337,256]) # 0
puts is_interesting( 1_336,[1337,256]) # 1
puts is_interesting( 1_337,[1337,256]) # 2
puts is_interesting(11_208,[1337,256]) # 0
puts is_interesting(11_209,[1337,256]) # 1
puts is_interesting(11_211,[1337,256]) # 2
puts is_interesting(90_000,[1337,256]) # 2
puts is_interesting(99_999,[1337,256]) # 2
puts is_interesting(12_821,[1337,256]) # 2
puts is_interesting(10_801,[1337,256]) # 2
puts is_interesting(12_345,[1337,256]) # 2
puts is_interesting(54_321,[1337,256]) # 2

puts unless ARGV.empty?
ARGV.each do |arg|
  puts "#{arg} => #{is_interesting(arg.to_i,[1337,256,1987,2012,42])}"
end
