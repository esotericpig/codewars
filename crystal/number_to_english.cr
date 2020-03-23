###
# I expanded my original solution to include numbers over 99,999.
# 
# For a library, it would need UInt64/BigInt/String overloads.
# 
# AUTHOR: Jonathan Bradley Whited (@esotericpig)
# SEE:    https://www.codewars.com/kata/ninety-nine-thousand-nine-hundred-ninety-nine/crystal
# RANK:   5 kyu
###

def number_to_english(n)
  return "" if !n.is_a?(Int) || n < 0 || n > MAX_ENG_NUM
  return ENG_NUMS[n][0] if n < 10
  
  number_to_english(n.to_i,1,1).reverse.join(' ')
end

# In newer versions of Crystal, can do `(n // 10)` instead of `(n / 10).to_i`.
def number_to_english(n : Int,small_place : Int,big_place : Int) : Array(String)
  eng_num = [] of String
  
  return eng_num if n <= 0
  
  digit = n % 10
  
  case small_place
  when 1
    next_digit = (n / 10).to_i % 10
    
    if next_digit == 1
      eng_num << ENG_NUMS[digit][1]
      
      n = (n / 10).to_i
      small_place *= 10
    elsif digit != 0
      eng_num << ENG_NUMS[digit][0]
    end
  when 10
    eng_num << ENG_NUMS[digit][2] if digit >= 2
  when 100
    eng_num << "hundred" << ENG_NUMS[digit][0] if digit != 0
  when 1000
    eng_num << BIG_ENG_NUMS[big_place]
    
    return eng_num.concat(number_to_english(n,1,big_place + 1))
  end
  
  eng_num.concat(number_to_english((n / 10).to_i,small_place * 10,big_place))
end

MAX_ENG_NUM = Int32::MAX

ENG_NUMS = {
  0 => %w[zero  ten            ],1 => %w[one   eleven            ],
  2 => %w[two   twelve   twenty],3 => %w[three thirteen   thirty ],
  4 => %w[four  fourteen forty ],5 => %w[five  fifteen    fifty  ],
  6 => %w[six   sixteen  sixty ],7 => %w[seven seventeen  seventy],
  8 => %w[eight eighteen eighty],9 => %w[nine  nineteen   ninety ],
}

BIG_ENG_NUMS = {
  1 => "thousand",2 => "million",3 => "billion",4 => "trillion"
}

puts MAX_ENG_NUM

if ARGV.empty?
  puts "'#{number_to_english(0)}'"      # "zero"
  puts "'#{number_to_english(7)}'"      # "seven"
  puts "'#{number_to_english(11)}'"     # "eleven"
  puts "'#{number_to_english(20)}'"     # "twenty"
  puts "'#{number_to_english(47)}'"     # "forty seven"
  puts "'#{number_to_english(100)}'"    # "one hundred"
  puts "'#{number_to_english(305)}'"    # "three hundred five"
  puts "'#{number_to_english(4_002)}'"  # "four thousand two"
  puts "'#{number_to_english(3_892)}'"  # "three thousand eight hundred ninety two"
  puts "'#{number_to_english(6_800)}'"  # "six thousand eight hundred"
  puts "'#{number_to_english(14_111)}'" # "fourteen thousand one hundred eleven"
  puts "'#{number_to_english(20_005)}'" # "twenty thousand five"
  puts "'#{number_to_english(99_999)}'" # "ninety nine thousand nine hundred ninety nine"
  puts "'#{number_to_english(95.99)}'"  # ""
  puts "'#{number_to_english(-4)}'"     # ""
else
  ARGV.each do |arg|
    puts "'#{number_to_english(arg.gsub(/[^\d\.\-]+/,"").to_i)}'"
  end
end
