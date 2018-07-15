#!/usr/bin/env python3

import sys

"""
My solution was not the sexiest, but I just liked this Kata.

author: Jonathan Bradley Whited (@esotericpig)
see:    https://www.codewars.com/kata/who-likes-it/python
rank:   6 kyu
"""

def likes(names):
    names_len = len(names)
    
    if names_len == 0:
        result = 'no one likes this'
    elif names_len == 1:
        result = '{} likes this'.format(names[0])
    elif names_len == 2:
        result = '{} and {} like this'.format(names[0],names[1])
    else:
        result = '{}, {} and '.format(names[0],names[1])
        
        if names_len == 3:
            result += '{}'.format(names[2])
        else:
            result += '{} others'.format(names_len - 2)
        
        result += ' like this'
    
    return result

print(likes([]))                            # 'no one likes this'
print(likes(['Peter']))                     # 'Peter likes this'
print(likes(['Jacob','Alex']))              # 'Jacob and Alex like this'
print(likes(['Max','John','Mark']))         # 'Max, John and Mark like this'
print(likes(['Alex','Jacob','Mark','Max'])) # 'Alex, Jacob and 2 others like this'

if len(sys.argv) > 1:
  print()
  names = sys.argv[1:]
  print('{} => {}'.format(names,likes(names)))
