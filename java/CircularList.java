/**
 * <pre>
 * I just wanted to make a circular linked list for fun.
 * However, an array list requires far less code.
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/circular-list/java
 * @rank   7 kyu
 */
public class CircularList<T> {
  private Node<T> curr = null;
  private int lastOp = 0; // In production, should be an enum
  
  public static void main(String[] args) {
    final CircularList<String> cl1 = new CircularList<>("one","two","three");
    test(cl1.next(),"one"); test(cl1.next(),"two");   test(cl1.next(),"three");
    test(cl1.next(),"one"); test(cl1.prev(),"three"); test(cl1.prev(),"two");
    test(cl1.prev(),"one"); test(cl1.prev(),"three");
    
    final CircularList<Integer> cl2 = new CircularList<>(1,2,3,4,5);
    test(cl2.prev(),5); test(cl2.prev(),4); test(cl2.next(),5); test(cl2.next(),1);
    test(cl2.next(),2); test(cl2.next(),3); test(cl2.next(),4); test(cl2.prev(),3);
    test(cl2.prev(),2); test(cl2.next(),3); test(cl2.next(),4); test(cl2.next(),5);
    test(cl2.next(),1); test(cl2.next(),2); test(cl2.next(),3);
  }
  
  public static <E> void test(E val,E expectedVal) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(val.equals(expectedVal) ? "- " : "X ");
    sb.append(val).append(" =? ").append(expectedVal);
    
    System.out.println(sb);
  }
  
  @SafeVarargs
  public CircularList(final T... elements) {
    curr = new Node<>(elements[0]);
    Node<T> prev = curr;
    
    for(int i = 1; i < elements.length; ++i) {
      prev.next = new Node<>(prev,elements[i]);
      prev = prev.next;
    }
    
    prev.next = curr;
    curr.prev = prev;
  }
  
  public T next() {
    if(lastOp == -1) {
      curr = curr.getNext().getNext();
    }
    
    T value = curr.getValue();
    curr = curr.getNext();
    lastOp = 1;
    return value;
  }
  
  public T prev() {
    switch(lastOp) {
      case 0: curr = curr.getPrev(); break;
      case 1: curr = curr.getPrev().getPrev(); break;
    }
    
    T value = curr.getValue();
    curr = curr.getPrev();
    lastOp = -1;
    return value;
  }
  
  private class Node<T> {
    private Node<T> prev,next;
    private T value;
    
    public Node(Node<T> prev,T value,Node<T> next) {
      this.prev = prev;
      this.value = value;
      this.next = next;
    }
    
    public Node(Node<T> prev,T value) {
      this(prev,value,null);
    }
    
    public Node(T value) {
      this(null,value,null);
    }
    
    public Node<T> getPrev() { return prev; }
    public T getValue() { return value; }
    public Node<T> getNext() { return next; }
  }
}
