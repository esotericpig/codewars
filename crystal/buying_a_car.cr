###
# This one seems pretty useful if you need to save up to buy a new car
#   (or anything).
#
# AUTHOR: Jonathan Bradley Whited
# SEE:    https://www.codewars.com/kata/buying-a-car/crystal
# RANK:   6 kyu
###

LOSS_INCREASE = 0.5 / 100.0 # Percent

def nb_months(priceOld,priceNew,saving,loss)
  loss /= 100.0 # Percent
  money,months = 0,0

  loop do
    money = priceOld + (saving * months)

    break if money >= priceNew

    loss = (loss + LOSS_INCREASE).round(4) if (months += 1).even?

    priceOld -= (priceOld * loss)
    priceNew -= (priceNew * loss)
  end

  [months,(money - priceNew).round.to_i]
end

if ARGV.size < 4
  puts nb_months(2000 ,8000 ,1000,1.5 ) # [6, 766 ]
  puts nb_months(12000,8000 ,1000,1.5 ) # [0, 4000]
  puts nb_months(8000 ,12000,500 ,1   ) # [8, 597 ]
  puts nb_months(18000,32000,1500,1.25) # [8, 332 ]
else
  puts nb_months(ARGV[0].to_f,ARGV[1].to_f,ARGV[2].to_f,ARGV[3].to_f)
end
