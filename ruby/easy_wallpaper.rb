#!/usr/bin/env ruby

###
# The site in @see helped me to visualize it lol.
# 
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/easy-wallpaper/ruby
# @see    https://www.ducksters.com/kidsmath/finding_the_volume_of_a_cube_or_box.php
# @rank   7 kyu
###
def wallpaper(l,w,h)
  total_w = w * h * 2.0
  total_l = l * h * 2.0
  return get_num_word(0) if total_w == 0 || total_l == 0
  total = total_w + total_l
  rolls = total / (0.52 * 10.0)
  rolls += (rolls * 0.15)
  rolls = rolls.ceil()
  rolls = (rolls < 1) ? 0 : rolls
  get_num_word(rolls)
end

def get_num_word(num)
  num_words = %w(zero one two three four five six seven eight nine ten
    eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen
    nineteen twenty)
  num_words[num]
end

puts wallpaper(6.3,4.5,3.29) # "sixteen"
puts wallpaper(7.8,2.9,3.29) # "sixteen"
puts wallpaper(6.3,5.8,3.13) # "seventeen"
puts wallpaper(6.1,6.7,2.81) # "sixteen"

if ARGV.length >= 3
  puts
  l,w,h = ARGV[0].to_f,ARGV[1].to_f,ARGV[2].to_f
  puts "(#{l} x #{w} x #{h}) => #{wallpaper(l,w,h)}"
end
