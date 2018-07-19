#!/usr/bin/env ruby

###
# This is actually the reverse of standard form, but Katas do this sometimes to
#   help prevent against using pre-built libraries.
# 
# @author Jonathan Bradley Whited (@esotericpig)
# @see    https://www.codewars.com/kata/simplifying-multilinear-polynomials/ruby
# @see    https://en.wikipedia.org/wiki/Multilinear_polynomial
# @see    https://www.mathsisfun.com/algebra/polynomials.html
# @rank   4 kyu
###
def simplify(poly)
  terms = {}
  
  # [(-/+)num, letters]
  poly.scan(/([\-\+]?\d*)([[:alpha:]]+)/) do |t|
    term = Term.new(t)
    
    if terms.key?(term.var)
      terms[term.var].coeff += term.coeff
      terms.delete(term.var) if terms[term.var].coeff == 0
    elsif term.coeff != 0
      terms[term.var] = term
    end
  end
  
  # There is probably a better way than using first...
  first,result = true,''
  
  terms.values.sort.each do |t|
    result << t.to_s(first)
    first = false
  end
  result
end

class Term
  attr_accessor :coeff,:var
  
  def initialize(t)
    @coeff = (t[0].empty? || t[0] == '+') ? 1 : (t[0] == '-') ? -1 : t[0].to_i
    @var = t[1].chars.sort.join
  end
  
  def <=>(other)
    result = 0
    return result if (result = (@var.length <=> other.var.length)) != 0
    return result if (result = (@var <=> other.var)) != 0
    (@coeff <=> other.coeff)
  end
  
  def to_s(first=false)
    s = (@coeff == -1) ? '-' : (!first && @coeff >= 0) ? '+' : ''
    s << @coeff.to_s if @coeff.abs != 1
    s << @var
  end
end

# Test reduction by equivalence
puts simplify('dc+dcba')        # 'cd+abcd'
puts simplify('2xy-yx')         # 'xy'
puts simplify('-a+5ab+3a-c-2a') # '-c+5ab'

# Test monomial length ordering
puts simplify('-abc+3a+2ac') # '3a+2ac-abc'
puts simplify('xyz-xz')      # '-xz+xyz'

# Test lexicographic ordering
puts simplify('a+ca-ab') # 'a-ab+ac'
puts simplify('xzy+zby') # 'byz+xyz'

# Test no leading +
puts simplify('-y+x') # 'x-y'
puts simplify('y-x')  # '-x+y'

puts unless ARGV.empty?
ARGV.each do |arg|
  puts "#{arg} => #{simplify(arg)}"
end
