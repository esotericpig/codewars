import java.util.Arrays;

import java.util.stream.Collectors;

/**
 * <pre>
 * This was good practice for using Java 8 stuff, but then I saw a solution
 *   which used one regex, dumb.
 * </pre>
 *
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/simple-pig-latin/java
 * @rank   5 kyu
 */
public class SimplePigLatin {
  public static void main(String[] args) {
    System.out.println(pigIt("Pig latin is cool")); // "igPay atinlay siay oolcay"
    System.out.println(pigIt("This is my string")); // "hisTay siay ymay tringsay"

    for(String arg: args) {
      System.out.println(pigIt(arg));
    }
  }

  public static String pigIt(String str) {
    // Inefficient, not something that I would use in production, but nice looking
    return Arrays.stream(str.split("(?<=\\s+)|(?=\\s+)")).map(word ->
      word.matches("[\\s\\p{Punct}]+") ? word
        : word.substring(1) + word.charAt(0) + "ay"
    ).collect(Collectors.joining(""));
  }
}
