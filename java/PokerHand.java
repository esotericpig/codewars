import java.util.Arrays;

// Old idea I had:
// 102 - 114 = Spades
// 202 - 214 = Hearts
// 302 - 314 = Diamonds
// 402 - 414 = Clubs
// (c / 100) = Suit
// (c % 100) = Value

/**
 * <pre>
 * I was disappointed in the other solutions' readability.
 * While mine is also pretty hacky, I think it's easier to follow.
 * 
 * My solution is about O(2N) I believe.
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/ranking-poker-hands/java
 * @see    https://en.wikipedia.org/wiki/Texas_hold_%27em#Hand_values
 * @rank   4 kyu
 */
public class PokerHand {
  public enum Result {TIE,WIN,LOSS}
  
  private int[] cardValues = new int[5]; // In ascending order
  private int value = 0;
  
  public static void main(String[] args) {
    Result tie = Result.TIE,win = Result.WIN,loss = Result.LOSS;
    test("Highest straight flush wins",       loss,"2H 3H 4H 5H 6H","KS AS TS QS JS");
    test("Straight flush wins of 4 of a kind",win, "2H 3H 4H 5H 6H","AS AD AC AH JD");
    test("Highest 4 of a kind wins",          win, "AS AH 2H AD AC","JS JD JC JH 3D");
    test("4 Of a kind wins of full house",    loss,"2S AH 2H AS AC","JS JD JC JH AD");
    test("Full house wins of flush",          win, "2S AH 2H AS AC","2H 3H 5H 6H 7H");
    test("Highest flush wins",                win, "AS 3S 4S 8S 2S","2H 3H 5H 6H 7H");
    test("Flush wins of straight",            win, "2H 3H 5H 6H 7H","2S 3H 4H 5S 6C");
    test("Equal straight is tie",             tie, "2S 3H 4H 5S 6C","3D 4C 5H 6H 2S");
    test("Straight wins of three of a kind",  win, "2S 3H 4H 5S 6C","AH AC 5H 6H AS");
    test("3 of a kind wins of two pair",      loss,"2S 2H 4H 5S 4C","AH AC 5H 6H AS");
    test("2 pair wins of pair",               win, "2S 2H 4H 5S 4C","AH AC 5H 6H 7S");
    test("Highest pair wins",                 loss,"6S AD 7H 4S AS","AH AC 5H 6H 7S");
    test("Pair wins of nothing",              loss,"2S AH 4H 5S KC","AH AC 5H 6H 7S");
    test("Highest card loses",                loss,"2S 3H 6H 7S 9C","7H 3C TH 6H 9S");
    test("Highest card wins",                 win, "4S 5H 6H TS AC","3S 5H 6H TS AC");
    test("Equal cards is tie",                tie, "2S AH 4H 5S 6C","AD 4C 5H 6H 2C");
    test("Bicycle straight beats 3 of a kind",win, "AS 2H 3C 4D 5S","KS KH KD 6S 7S");
    
    if(args.length > 0) {
      System.out.println();
      
      // Example: java PokerHand 'AS 2H 3C 4D 5S' 'KS KH KD 6S 7S'
      for(String arg: args) {
        PokerHand pokerHand = new PokerHand(arg);
        System.out.println(arg + " => " + pokerHand.value + ":" + Arrays.toString(pokerHand.cardValues));
      }
    }
  }
  
  public static void test(String msg,Result result,String hand1,String hand2) {
    PokerHand pokerHand1 = new PokerHand(hand1);
    PokerHand pokerHand2 = new PokerHand(hand2);
    Result myResult = pokerHand1.compareWith(pokerHand2);
    
    StringBuilder sb = new StringBuilder();
    
    sb.append((myResult == result) ? "- " : "X ").append(result).append("? ").append(myResult);
    sb.append(" [").append(pokerHand1.value).append(',').append(pokerHand2.value).append("] ");
    sb.append(msg);
    
    System.out.println(sb);
  }
  
  // It would be nice to break this method out into multiple methods, but then
  //   all of the local variables would have to be passed as args or stored as
  //   instance variables.
  public PokerHand(String hand) {
    String[] cardStrs = hand.split("\\s+");
    
    int[] ofAKinds = new int[13];
    
    boolean isFlush = true;
    char prevSuit = 0;
    
    // Parse values, of a kinds, and flush
    for(int i = 0; i < cardStrs.length; ++i) {
      String cardStr = cardStrs[i];
      char suit = cardStr.charAt(1);
      char valueChr = cardStr.charAt(0);
      int value = 0;
      
      switch(valueChr) {
        case 'T': value = 10; break;
        case 'J': value = 11; break;
        case 'Q': value = 12; break;
        case 'K': value = 13; break;
        case 'A': value = 14; break;
        default: value = Character.getNumericValue(valueChr);
      }
      
      ++ofAKinds[value - 2];
      
      if(prevSuit != 0 && suit != prevSuit) { isFlush = false; }
      prevSuit = suit;
      
      cardValues[i] = value;
    }
    
    // Of a kinds?
    boolean is2OfAKind = false,is2Pairs = false;
    boolean is3OfAKind = false,is4OfAKind = false;
    
    for(int oak: ofAKinds) {
      switch(oak) {
        case 2:
          if(is2OfAKind) { is2Pairs = true; }
          else { is2OfAKind = true; }
          break;
        case 3: is3OfAKind = true; break;
        case 4: is4OfAKind = true; break;
      }
    }
    
    // Straight?
    Arrays.sort(cardValues);
    
    boolean isStraight = true;
    int cardLen = cardValues.length;
    int straightValue = cardValues[0];
    boolean isBicycle = straightValue == 2 && cardValues[cardLen - 1] == 14; // Ace (14) can be 1
    
    if(isBicycle) { --cardLen; }
    
    for(int i = 1; i < cardLen; ++i) {
      int value = cardValues[i];
      if(value != ++straightValue) { isStraight = false; break; }
    }
    
    if(isBicycle && isStraight) {
      // Change Ace value to 1 for a bicycle (wheel) straight
      cardValues[cardLen] = 1;
      Arrays.sort(cardValues);
    }
    
    // Calculate ultimate value
    value = cardValues[cardValues.length - 1]; // High card
    
    if(isStraight && isFlush) {
      // Royal flush?
      value += (cardValues[0] == 10) ? 1337 : 800;
    }
    else if(is4OfAKind) { value += 700; }
    // Full house
    else if(is3OfAKind && is2OfAKind) { value += 600; }
    else if(isFlush) { value += 500; }
    else if(isStraight) { value += 400; }
    else if(is3OfAKind) { value += 300; }
    else if(is2Pairs) { value += 200; }
    else if(is2OfAKind) { value += 100; }
  }
  
  public Result compareWith(PokerHand hand) {
    int diff = value - hand.value;
    
    if(diff == 0) {
      // Instead of this, you might be able to initialize #value to the sum in the constructor?
      for(int i = cardValues.length - 1; i >= 0; --i) {
        if((diff = cardValues[i] - hand.cardValues[i]) != 0) { break; }
      }
    }
    
    return (diff < 0) ? Result.LOSS : ((diff > 0) ? Result.WIN : Result.TIE);
  }
}
