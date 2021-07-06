import java.util.Iterator;
import java.util.TreeSet;

/**
 * <pre>
 * I liked my solution using TreeSet, which none of the other solutions did.
 *
 * I could have also used a PriorityQueue (i.e., a sorted linked list) or just
 *   Arrays#sort(...)/Collections#sort(...).
 * </pre>
 *
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/length-of-missing-array/java
 * @rank   6 kyu
 */
public class LengthOfMissingArray {
  public static void main(String[] args) {
    // 3
    System.out.println(getLengthOfMissingArray(new Object[][]{
      new Object[]{1,2},new Object[]{4,5,1,1},new Object[]{1},new Object[]{5,6,7,8,9}}));
    // 2
    System.out.println(getLengthOfMissingArray(new Object[][]{
      new Object[]{5,2,9},new Object[]{4,5,1,1},new Object[]{1},new Object[]{5,6,7,8,9}}));
    // 2
    System.out.println(getLengthOfMissingArray(new Object[][]{
      new Object[]{null},new Object[]{null,null,null}}));
    // 5
    System.out.println(getLengthOfMissingArray(new Object[][]{
      new Object[]{'a','a','a'},new Object[]{'a','a'},new Object[]{'a','a','a','a'},
      new Object[]{'a'},new Object[]{'a','a','a','a','a','a'}}));
    // 0
    System.out.println(getLengthOfMissingArray(new Object[][]{}));
  }

  public static int getLengthOfMissingArray(Object[][] aoa) {
    if(aoa == null || aoa.length == 0) { return 0; }
    TreeSet<Integer> tree = new TreeSet<>();

    for(Object[] a: aoa) {
      if(a == null || a.length == 0) { return 0; }
      tree.add(a.length);
    }

    Iterator<Integer> it = tree.iterator();
    int nextSeq = it.next() + 1;

    while(it.hasNext()) {
      int seq = it.next();
      if(seq != nextSeq) { return nextSeq; }
      nextSeq = seq + 1;
    }
    return 0;
  }
}
