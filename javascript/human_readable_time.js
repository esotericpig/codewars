/**
 * I have no idea why this is 5 kyu...
 *
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/human-readable-time/javascript
 * @rank   5 kyu
 */
function humanReadable(seconds) {
  return ""  + formatNum(Math.floor(seconds / 60 / 60)) +
         ":" + formatNum(Math.floor(seconds / 60) % 60) +
         ":" + formatNum(seconds % 60);
}

function formatNum(num) {
  return ((num < 10) ? '0' : '') + num;
}

console.log(humanReadable(0));      // '00:00:00'
console.log(humanReadable(5));      // '00:00:05'
console.log(humanReadable(60));     // '00:01:00'
console.log(humanReadable(86399));  // '23:59:59'
console.log(humanReadable(359999)); // '99:59:59'

if(process.argv.length > 2) {
  console.log();

  for(var i = 2; i < process.argv.length; ++i) {
    var secs = process.argv[i];
    console.log(secs + ' => ' + humanReadable(secs));
  }
}
