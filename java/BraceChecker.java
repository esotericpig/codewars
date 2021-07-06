import java.util.Deque;
import java.util.LinkedList;

/**
 * <pre>
 * I was thinking about the shunting-yard algorithm when I was doing this.
 * Because I had been exposed to more complex algorithms, I wasn't even
 *   thinking that you can do this with just a simple regex because there
 *   are nothing but braces (nothing inside)! Dumb.
 *
 * This shows how intelligent people (not saying I am or am not one) can
 *   overlook simple solutions.
 * </pre>
 *
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/valid-braces/java
 * @see    https://en.wikipedia.org/wiki/Shunting-yard_algorithm
 * @rank   6 kyu
 */
public class BraceChecker {
  public static void main(String[] args) {
    BraceChecker checker = new BraceChecker();
    String[] testers = new String[]{
      "()",  // true
      "[(])" // false
    };

    for(String tester: testers) {
      System.out.println(tester + "? " + checker.isValid(tester));
    }
    for(String arg: args) {
      System.out.println(arg + "? " + checker.isValid(arg));
    }
  }

  public boolean isValid(String braces) {
    Deque<Character> open = new LinkedList<>();

    for(int i = 0; i < braces.length(); ++i) {
      char c = braces.charAt(i);
      char openBrace = getOpenBrace(c);

      if(openBrace == 0) {
        open.push(c);
      }
      else if(open.isEmpty() || open.pop() != openBrace) {
        return false;
      }
    }

    return open.isEmpty();
  }

  public char getOpenBrace(char closeBrace) {
    switch(closeBrace) {
      case ')': return '(';
      case ']': return '[';
      case '}': return '{';
    }
    return 0;
  }
}
