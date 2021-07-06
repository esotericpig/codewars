/**
 * This was my original solution.
 * I didn't know replace(...) could take a function.
 *
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/data-compression-using-run-length-encoding/javascript
 * @see    https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp/exec
 * @see    https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/repeat
 * @rank   6 kyu
 */

function encode(input) {
  var re = /(.)\1*/g; // '/g' to keep searching for exec(...); doesn't mean replace all
  var result = '';
  var run = null;

  while((run = re.exec(input)) !== null) {
    run = run[0];
    result += run.length + run[0];
  }

  return result;
}

function decode(input) {
  var re = /\d+/g;
  var result = '';
  var run = null;

  while((run = re.exec(input)) !== null) {
    // Don't need to do parseInt(run[0]), which is cool
    result += input[re.lastIndex].repeat(run[0]);
  }

  return result;
}

// Test encode
console.log(encode('A'));          // '1A'
console.log(encode('AAA'));        // '3A'
console.log(encode('AB'));         // '1A1B'
console.log(encode('AAABBBCCCA')); // '3A3B3C1A'

// Test decode
console.log(decode('1A'));       // 'A'
console.log(decode('3A'));       // 'AAA'
console.log(decode('1A1B'));     // 'AB'
console.log(decode('3A3B3C1A')); // 'AAABBBCCCA'

// Round trip
console.log(decode(encode('AAAAAAAAAAB')));                // 'AAAAAAAAAAB'
console.log(decode(encode('ABCDEFGHIJKLMNOPQRSTUVWXYZ'))); // 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
console.log(encode(decode('10A1B')));                      // '10A1B'
// - '1A1B1C1D1E1F1G1H1I1J1K1L1M1N1O1P1Q1R1S1T1U1V1W1X1Y1Z'
console.log(encode(decode('1A1B1C1D1E1F1G1H1I1J1K1L1M1N1O1P1Q1R1S1T1U1V1W1X1Y1Z')));

if(process.argv.length > 2) {
  console.log();

  for(var i = 2; i < process.argv.length; ++i) {
    var input = process.argv[i];

    if(/\d/.test(input)) {
      console.log(input + ' => ' + decode(input));
    }
    else {
      console.log(input + ' => ' + encode(input));
    }
  }
}
