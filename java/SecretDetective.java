import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import java.util.stream.Collectors;

/**
 * <pre>
 * A lot of the other solutions used topological sorting algorithms.
 * 
 * My approach uses a scoring method and adjusts the scores accordingly,
 *   shifts values up/down, and then finally sorts the scores.
 * For longer lengths, SCORE_INC will need to be increased.
 * 
 * In the worst case, this is probably O(NN) I think lol, but in the best case,
 *   it is probably faster than the other solutions that I looked at.
 * 
 * Later, I added #makeSecret(...) for fun.
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/recover-a-secret-string-from-random-triplets/java
 * @rank   4 kyu
 */
public class SecretDetective {
  public static final int SCORE_INC = 1000;
  
  private Map<Character,Letter> map = new HashMap<>();
  private int nextScore = 0;
  
  public static void main(String[] args) {
    SecretDetective sd = new SecretDetective();
    
    char[][] triplets = {
      {'t','u','p'},
      {'w','h','i'},
      {'t','s','u'},
      {'a','t','s'},
      {'h','a','p'},
      {'t','i','s'},
      {'w','h','s'}
    };
    // whatisup
    System.out.println(sd.recoverSecret(triplets));
    
    triplets = new char[][]{
      {'c','d','e'},
      {'a','b','c'}
    };
    // abcde
    System.out.println(sd.recoverSecret(triplets));
    
    // Make a secret
    triplets = sd.makeSecret("monkey burger");
    
    System.out.println('[');
    for(char[] triplet: triplets) {
      System.out.println("  " + Arrays.toString(triplet));
    }
    System.out.println(']');
    
    System.out.println("" + triplets.length + ": " + sd.recoverSecret(triplets));
  }
  
  public String recoverSecret(char[][] triplets) {
    return recoverSecret(triplets,true);
  }
  
  public String recoverSecret(char[][] triplets,boolean showScores) {
    clear();
    
    for(char[] triplet: triplets) {
      for(int i = 0; i < triplet.length; ++i) {
        char value = triplet[i];
        Letter letter = getLetterOrDefault(value);
        
        letter.addNeighbors(triplet,i);
        letter.checkLows();
        letter.checkHighs();
      }
    }
    
    if(showScores) {
      map.values().stream().sorted()
         .forEach((l) -> System.out.print(l + "[" + l.score + "] "));
      System.out.println();
    }
    
    return map.values()
              .stream()
              .sorted()
              .map(Object::toString)
              .collect(Collectors.joining());
  }
  
  /**
   * <pre>
   * I made this method afterward for fun.
   * It is probably pretty inefficient.
   * 
   * I originally did this idea in Ruby, which was far less code.
   * It looks kind of weird in Java.
   * </pre>
   */
  public char[][] makeSecret(String secret) {
    List<Letter> letters = new ArrayList<>(secret.length());
    Random rand = new Random();
    char[][] result = null;
    List<char[]> triplets = new ArrayList<>();
    
    // Get rid of duplicate chars
    Set<Character> dups = new HashSet<>();
    StringBuilder newSecret = new StringBuilder();
    
    for(int i = 0; i < secret.length(); ++i) {
      char c = secret.charAt(i);
      
      if(!dups.contains(c)) {
        dups.add(c);
        newSecret.append(c);
      }
    }
    
    secret = newSecret.toString();
    if(secret.length() < 3) { throw new RuntimeException("Too few chars"); }
    
    // Convert secret to Letters for the scores
    clear(); // Reset #nextScore
    for(int i = 0; i < secret.length(); ++i) {
      letters.add(new Letter(secret.charAt(i)));
    }
    
    while(true) {
      List<Letter> sampleLetters = new ArrayList<>(letters);
      char[] triplet = new char[3];
      Letter[] tripletLetters = new Letter[3];
      
      // Take random samples and sort
      for(int i = 0; i < 3; ++i) {
        tripletLetters[i] = sampleLetters.remove(rand.nextInt(sampleLetters.size()));
      }
      Arrays.sort(tripletLetters);
      
      // Convert sorted samples to char[] (triplet) and add to triplets
      for(int i = 0; i < 3; ++i) {
        triplet[i] = tripletLetters[i].value;
      }
      triplets.add(triplet);
      
      // Test these triplets to see if they work
      result = triplets.toArray(new char[triplets.size()][3]);
      if(recoverSecret(result,false).equals(secret)) { break; }
    }
    
    return result;
  }
  
  private Letter getLetterOrDefault(char value) {
    Letter letter = map.get(value);
    // If-statement instead of Map#putIfAbsent(...) so that #nextScore() isn't called needlessly
    if(letter == null) { map.put(value,letter = new Letter(value)); }
    return letter;
  }
  
  private int nextScore() {
    return nextScore += SCORE_INC;
  }
  
  public void clear() {
    map.clear();
    nextScore = 0;
  }
  
  private class Letter implements Comparable<Letter> {
    private Set<Letter> lows = new HashSet<Letter>(),highs = new HashSet<Letter>();
    private int score;
    private char value;
    
    public Letter(char value) {
      this.score = nextScore();
      this.value = value;
    }
    
    public void addNeighbors(char[] triplet,int index) {
      addNeighbors(triplet,index,-1); // Low neighbors
      addNeighbors(triplet,index,1);  // High neighbors
    }
    
    public void addNeighbors(char[] triplet,int index,int step) {
      for(index += step; index >= 0 && index < triplet.length; index += step) {
        char value = triplet[index];
        Letter neighbor = getLetterOrDefault(value);
        
        if(step < 0) { lows.add(neighbor); }
        else { highs.add(neighbor); }
      }
    }
    
    public void checkLows() {
      boolean checkHighs = false;
      
      for(Letter low: lows) {
        if(score <= low.score) { // Score should be higher
          score = low.score + (SCORE_INC / 2);
          checkHighs = true;
        }
      }
      
      if(checkHighs) { // Shift highs up
        for(Letter high: highs) {
          high.checkLows();
        }
      }
    }
    
    public void checkHighs() {
      boolean checkLows = false;
      
      for(Letter high: highs) {
        if(score >= high.score) { // Score should be lower
          score = high.score / 2;
          checkLows = true;
        }
      }
      
      if(checkLows) { // Shift lows down
        for(Letter low: lows) {
          low.checkHighs();
        }
      }
    }
    
    public int compareTo(Letter l) {
      return score - l.score;
    }
    
    public boolean equals(Object o) {
      return o instanceof Letter && value == ((Letter)o).value;
    }
    
    public int hashCode() {
      return Objects.hashCode(value);
    }
    
    public String toString() {
      return String.valueOf(value);
    }
  }
}
