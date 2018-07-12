###
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/breaking-chocolate-problem/coffeescript
# @rank   7 kyu
###

breakChocolate = (n,m) -> if n && m then (n * m) - 1 else null

console.log breakChocolate(5,5) # 24
console.log breakChocolate(7,4) # 27
console.log breakChocolate(1,1) # 0

if process.argv.length >= 4
  [n,m] = [process.argv[2],process.argv[3]]
  console.log ''
  console.log "(#{n},#{m}) => #{breakChocolate(n,m)}"
