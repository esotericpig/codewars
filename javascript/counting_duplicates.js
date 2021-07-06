/**
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/counting-duplicates/javascript
 * @rank   6 kyu
 */
function duplicateCount(text) {
  var dups = {}; // Duplicates

  return text.split('').map(c => {
    c = c.toLowerCase();

    if(c in dups) {
      if(dups[c]) {
        dups[c] = false;
        return c;
      }
    }
    else {
      dups[c] = true;
      return null;
    }
  }).join('').length;
}

console.log(duplicateCount(""));                 // 0
console.log(duplicateCount("abcde"));            // 0 (no characters repeats more than once)
console.log(duplicateCount("aabbcde"));          // 2 ('a' and 'b')
console.log(duplicateCount("aabBcde"));          // 2 (should ignore case)
console.log(duplicateCount("Indivisibility"));   // 1 ('i' occurs six times)
console.log(duplicateCount("Indivisibilities")); // 2 (characters may not be adjacent)

if(process.argv.length > 2) {
  console.log();

  for(var i = 2; i < process.argv.length; ++i) {
    var text = process.argv[i];
    console.log(text + ' => ' + duplicateCount(text));
  }
}
