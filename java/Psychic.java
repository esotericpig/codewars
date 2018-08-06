import java.lang.reflect.Field;

import java.util.Random;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 * You either had to use reflection or copy & paste the math from the source code.
 * 
 * I decided to use reflection. However, less verbose solutions were made by
 *   just setting the seed to 0.
 * 
 * I looked at the OpenJDK source code, and just hoped that it would be the same.
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/psychic/java
 * @see    https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html
 * @see    https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html
 * @see    http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/share/classes/java/lang/Math.java
 * @see    http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/share/classes/java/util/Random.java
 * @rank   3 kyu
 */
public class Psychic {
  public static void main(String[] args) {
    for(int i = 0; i < 5; ++i) {
      double guess = Psychic.guess();
      double choice = Math.random();
      
      // https://docs.oracle.com/javase/tutorial/java/data/numberformat.html
      // https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax
      System.out.format("%1.20f =? %1.20f => %b%n",guess,choice,guess == choice);
    }
  }
  
  public static double guess() {
    try {
      return (new Psychic()).predict();
    }
    // Bad way to handle errors...
    catch(Exception e) {
      System.out.println(e);
    }
    return -11.11;
  }
  
  protected Random rand;
  protected AtomicLong seed;
  
  public Psychic() throws Exception {
    rand = getMathRand();
    seed = (AtomicLong)getObjVar(rand,"seed");
  }
  
  public double predict() throws Exception {
    Random rand = new Random();
    
    setObjVar(rand,"seed",new AtomicLong(seed.get()));
    
    return rand.nextDouble();
  }
  
  public static void setObjVar(Object obj,String id,Object value) throws Exception {
    Field field = obj.getClass().getDeclaredField(id);
    
    field.setAccessible(true);
    field.set(obj,value);
  }
  
  public static Random getMathRand() throws Exception {
    Class holder = Class.forName("java.lang.Math$RandomNumberGeneratorHolder");
    Field field = holder.getDeclaredField("randomNumberGenerator");
    
    field.setAccessible(true);
    
    return (Random)field.get(holder);
  }
  
  public static Object getObjVar(Object obj,String id) throws Exception {
    Field field = obj.getClass().getDeclaredField(id);
    
    field.setAccessible(true);
    
    return field.get(obj);
  }
}
