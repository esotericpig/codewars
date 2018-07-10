import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

/**
 * <pre>
 * This was actually my original idea, just because I wanted to solve a Kata
 *   using a Genetic Algorithm.
 * 
 * Unfortunately, because this Kata requires the best solution, it just didn't
 *   work. In order to get the best solution, you need a high gene size and
 *   many generations (evolutions), which is just too slow.
 * 
 * Originally, I was just using the Manhattan distance or distance formula for
 *   measuring fitness, but this wasn't getting the optimal solution, so then I
 *   modified the fitness evaluation to actually use the A* algorithm, which is
 *   kind of funny I think. For games, it would be best to use distance for speed.
 * 
 * Pros to using a GA over A* (for games):
 * - It looks more "human," not just a straight path with 100% maze knowledge.
 * - While the GA is thinking (evolving), you can pull results out w/o waiting.
 * Cons to using a GA:
 * - Requires more memory and processing time (for good solutions).
 * - Not guaranteed to find the ultimate solution (but this is more "human").
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/path-finder-number-2-shortest-path/java
 * @see    http://geneticalgorithms.ai-depot.com/Tutorial/Overview.html
 * @rank   4 kyu
 */
public class Finder2 {
  public static int pathFinder(String mazeStr) {
    Maze maze = new Maze(mazeStr);
    Glader.DEFAULT_LEN = maze.getSize() * 4;
    Gladers gladers = new Gladers(maze,100);
    
    // Generations
    for(int i = 0; i < 1000; ++i) {
      gladers.evolve(maze);
      System.out.print("\r" + (i + 1) + ": " + gladers.getBest().getMinDistance());
      System.out.print(", " + gladers.getBest().getMinSteps() + "          ");
      
      /*if(i == 100000) {
        System.out.println("Changing...");
        Gladers.mutateChance = 90.0 / 100.0;
        Gladers.mutatePercent = 90;
      }*/
      
      //if(gladers.getBest().getResult() != -1) { break; }
    }
    System.out.println();
    
    if(gladers.getBest().getResult() != -1) {
      gladers.getBest().fixAll(maze);
      gladers.getBest().printPath(maze);
    }
    
    System.out.println(maze.toString(gladers.getBest().getMinX(),gladers.getBest().getMinY()));
    System.out.println("" + gladers.getBestFitness() + ": " + gladers.getBest());
    System.out.println("Min Dist:  " + gladers.getBest().getMinDistance());
    System.out.println("Min Steps: " + gladers.getBest().getMinSteps());
    System.out.println("Result:    " + gladers.getBest().getResult());
    
    return gladers.getBest().getResult();
  }
  
  public static void main(String[] args) {
    // Random maze
    StringBuilder mazeStr = new StringBuilder();
    Random rand = new Random();
    int size = 25;
    
    for(int y = 0; y < size; ++y) {
      for(int x = 0; x < size; ++x) {
        if(y == 0 || (y == size-1 && x == size-1)) {
          mazeStr.append(Maze.EMPTY);
        }
        else {
          mazeStr.append(rand.nextInt(4) == 0 ? Maze.WALL : Maze.EMPTY);
        }
      }
      mazeStr.append("\n");
    }
    
    pathFinder(mazeStr.toString());
    
    /* 14
    pathFinder((
    " . . . . . . . .\n" +
    " . W . . . . . W\n" +
    " . . W . W . W .\n" +
    " W W . . . . W .\n" +
    " . W . . W . . .\n" +
    " . . W . . W W .\n" +
    " . . . W W . . .\n" +
    " . . W . W . . . ").replace(" ","")
    );*/
    
    /* 10
    pathFinder((
    ". . . . . .\n" +
    ". . . . . .\n" +
    ". . . . . .\n" +
    ". . . . . .\n" +
    ". . . . W W\n" +
    ". . . . W .").replace(" ","")
    );*/
    
    /* 96
    pathFinder((
    ". W . . . W . . . W . . .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". W . W . W . W . W . W .\n" +
    ". . . W . . . W . . . W .").replace(" ","")
    );*/
  }
}

/**
 * Gladers (shorter than MazeRunners)
 * - Collection of "Glader"; the "population"
 * Glader (shorter than MazeRunner)
 * - The "people" or collection of "genes"
 * Maze
 * - A 2-dimensional array of chars with outer walls
 * Step
 * - Enum of N(orth),E(ast),S(outh),W(est)
 */

class Gladers {
  private Glader best;
  private double bestFitness;
  private Glader[] gladers;
  
  // These are the percentages that worked best for me
  public static double mateChance = 90.0 / 100.0;
  public static double mutateChance = 35.0 / 100.0;
  public static double mutatePercent = 25.0 / 100.0;
  
  public Gladers(Maze maze,int initSize) {
    gladers = new Glader[initSize];
    
    for(int i = 0; i < gladers.length; ++i) {
      gladers[i] = new Glader(maze.getSize());
    }
    
    best = gladers[0];
    bestFitness = best.runForFitness(maze);
  }
  
  // For a good explanation: http://geneticalgorithms.ai-depot.com/Tutorial/Overview.html
  public void evolve(Maze maze) {
    double[] fitness = new double[gladers.length];
    double fitnessOfAll = 0.0;
    
    for(int i = 0; i < gladers.length; ++i) {
      Glader g = gladers[i];
      fitness[i] = g.runForFitness(maze);
      fitnessOfAll += fitness[i];
      
      // Shortest distance (min) is best fitness
      if(fitness[i] < bestFitness) {
        best = g;
        bestFitness = fitness[i];
      }
    }
    
    double[] chance = new double[gladers.length];
    double chanceOfAll = 0.0;
    
    for(int i = 0; i < gladers.length; ++i) {
      // Do "(fitnessOfAll - fitness[i])" because min fitness is best
      chance[i] = fitnessOfAll + ((fitnessOfAll - fitness[i]) / fitnessOfAll);
      chanceOfAll += chance[i];
    }
    
    Glader[] newGladers = new Glader[gladers.length];
    
    newGladers[0] = best; // Always carry best into new population
    
    // This is to get out of peaks/valleys
    newGladers[1] = new Glader(maze.getSize(),Step.N);
    newGladers[2] = new Glader(maze.getSize(),Step.S);
    newGladers[3] = new Glader(maze.getSize(),Step.E);
    newGladers[4] = new Glader(maze.getSize(),Step.W);
    newGladers[5] = new Glader(maze.getSize());
    
    //gladers[gladers.length - 5] = new Glader(maze.getSize(),Step.N);
    //gladers[gladers.length - 4] = new Glader(maze.getSize(),Step.S);
    //gladers[gladers.length - 3] = new Glader(maze.getSize(),Step.E);
    //gladers[gladers.length - 2] = new Glader(maze.getSize(),Step.W);
    //gladers[gladers.length - 1] = new Glader(maze.getSize());
    
    Random rand = new Random();
    
    for(int i = 6; i < gladers.length; ++i) {
      Glader g = gladers[i];
      Glader newG = g;
      
      if(rand.nextDouble() <= mateChance) {
        // Arbitrary names, could be dad & dad
        Glader mom = selectParent(rand,chance);
        Glader dad = selectParent(rand,chance);
        
        newG = mom.mateWith(dad);
      }
      if(rand.nextDouble() <= mutateChance) {
        newG = newG.mutateByPercent(mutatePercent);
      }
      
      newGladers[i] = newG;
    }
    
    gladers = newGladers;
  }
  
  public Glader selectParent(Random rand,double[] chance) {
    double randParent = rand.nextDouble();
    
    for(int i = 0; i < gladers.length; ++i) {
      if(randParent <= chance[i]) {
        return gladers[i];
      }
    }
    return gladers[gladers.length - 1];
  }
  
  public Glader getBest() { return best; }
  public double getBestFitness() { return bestFitness; }
}

class Glader {
  private int fitness = 0;
  private int minDistance;
  private int minSteps;
  private int minX;
  private int minY;
  private Step[] path;
  private int result = 0;
  
  public static int DEFAULT_LEN;
  
  public Glader(Glader g) {
    fitness = g.fitness;
    path = Arrays.copyOf(g.path,g.path.length);
    result = g.result;
  }
  
  public Glader(int initLen) {
    initLen = DEFAULT_LEN;
    path = Step.getRandomSteps(initLen);
  }
  
  public Glader(int initLen,Step step) {
    initLen = DEFAULT_LEN;
    path = new Step[initLen];
    
    for(int i = 0; i < initLen; ++i) {
      path[i] = step;
    }
  }
  
  public Glader mateWith(Glader other) {
    Glader g = new Glader(this);
    Random rand = new Random();
    
    // 1) Random divider placement; this worked best in my results
    // - -1/+1 so always include both parents
    int divider = (g.path.length <= 1) ? 1 : rand.nextInt(g.path.length - 1) + 1;
    
    // 2) Traditional divider which is perfectly half; not random enough
    //int divider = (int)Math.round(g.path.length / 2.0);
    
    for(int i = divider; i < g.path.length; ++i) {
      g.path[i] = other.path[i];
    }
    
    // 3) Randomly pick dad/mom gene; too random
    /*for(int i = 0; i < g.path.length; ++i) {
      g.path[i] = (rand.nextInt(2) == 0) ? g.path[i] : other.path[i];
    }*/
    
    // 4) 2 dividers (3 sections), just slightly better than the traditional one
    /*int divider3 = (int)Math.round(g.path.length / 3.0);
    
    if((int)(Math.random() * 2) == 0) {
      for(int i = divider3; i < (divider3 * 2); ++i) {
        g.path[i] = other.path[i];
      }
    }
    else {
      for(int i = divider3 * 2; i < g.path.length; ++i) {
        g.path[i] = other.path[i];
      }
    }*/
    
    return g;
  }
  
  public Glader mutate(int stepCount) {
    Glader g = new Glader(this);
    Random rand = new Random();
    
    for(; stepCount > 0; --stepCount) {
      int i = rand.nextInt(g.path.length); // Can do the same one twice; not good
      g.path[i] = Step.getRandomStep(rand); // Best in my results
      //g.path[i] = Step.flip(g.path[i]); // N<=>S, E<=>W
    }
    return g;
  }
  
  public Glader mutateByPercent(double percent) {
    int stepCount = (int)Math.round(path.length * percent);
    return (stepCount < 1) ? this : mutate(stepCount);
  }
  
  public int run(Maze maze,boolean isForFitness) {
    int x = 0,y = 0;
    int goalX = maze.getWidth() - 1,goalY = maze.getHeight() - 1;
    
    boolean hitGoal = false;
    //minDistance = Glader.getManhattanDistance(x,y,goalX,goalY);
    minDistance = getDistance(maze,x,y,goalX,goalY);
    minSteps = path.length;
    minX = 0; minY = 0;
    result = 0;
    int steps = 0;
    
    for(Step s: path) {
      int lookX = s.takeStepX(x);
      int lookY = s.takeStepY(y);
      
      if(maze.isEmpty(lookX,lookY)) {
        x = lookX; y = lookY;
        
        //int distance = Glader.getManhattanDistance(x,y,goalX,goalY);
        int distance = Glader.getDistance(maze,x,y,goalX,goalY);
        if(distance < minDistance) {
          minDistance = distance;
          minSteps = result + 1;
          minX = x; minY = y;
        }
        
        ++result;
        
        if(x == goalX && y == goalY) {
          hitGoal = true;
          break;
        }
      }
      
      ++steps;
    }
    
    //int realDistance = Glader.getDistance(maze,x,y,goalX,goalY);
    //fitness = minDistance + steps + realDistance;
    fitness = minDistance * 4 + minSteps; // * 4 is sweet spot
    
    result = hitGoal ? result : -1;
    
    return isForFitness ? fitness : result;
  }
  
  public int runForFitness(Maze maze) { return run(maze,true); }
  public int runForResult(Maze maze) { return run(maze,false); }
  
  public static final Map<String,Integer> savedDistances = new HashMap<String,Integer>();
  
  // Uses A* algorithm with cache (#savedDistances)
  public static int getDistance(Maze maze,int x,int y,int goalX,int goalY) {
    int distance = 0;
    String key = "" + x + "," + y;
    Integer value = savedDistances.get(key);
    
    if(value == null) {
      LinkedList<Pos> open = new LinkedList<>();
      LinkedList<Pos> closed = new LinkedList<>();
      
      open.addLast(new Pos(x,y,goalX,goalY));
      
      Pos goalPos = null;
      
      while(!open.isEmpty()) {
        Pos pos = open.removeLast();
        
        // Goal!
        if(pos.getX() == goalX && pos.getY() == goalY) {
          if(goalPos == null) {
            goalPos = pos;
          } else {
            if(pos.getSteps() < goalPos.getSteps()) {
              goalPos = pos;
            }
          }
        }
        
        closed.addLast(pos);
        
        // Process neighbors
        for(Step s: Step.values()) {
          Pos neighbor = s.takeStep(pos.getX(),pos.getY());
          
          // Not a wall?
          if(maze.isEmpty(neighbor.getX(),neighbor.getY())) {
            // Skip if in closed
            if(!closed.contains(neighbor)) {
              // Calc necessary stuff
              neighbor.setParent(pos);
              neighbor.setSteps(pos.getSteps() + 1);
              neighbor.updateDistance(goalX,goalY);
              
              // Find in open
              Pos neighborInOpen = null;
              for(Pos p: open) {
                if(p.equals(neighbor)) {
                  neighborInOpen = p;
                  break;
                }
              }
              
              // Not in open?
              if(neighborInOpen == null) {
                boolean isAdded = false;
                
                // Put in open, sorted by min cost last
                for(ListIterator<Pos> li = open.listIterator(); li.hasNext();) {
                  //if(neighbor.getCost() > li.next().getCost()) {
                  if(neighbor.getSteps() > li.next().getSteps()) {
                    li.previous();
                    li.add(neighbor);
                    isAdded = true;
                    break;
                  }
                }
                
                if(!isAdded) { open.addLast(neighbor); }
              } else {
                // Update the neighbor in open
                if(neighbor.getSteps() < neighborInOpen.getSteps()) {
                  neighborInOpen.setParent(neighbor.getParent());
                  neighborInOpen.setSteps(neighbor.getSteps());
                }
              }
            }
          }
        }
      }
      
      distance = (goalPos != null) ? goalPos.getSteps() : getManhattanDistance(x,y,goalX,goalY);
      savedDistances.put(key,distance);
    }
    else {
      distance = value;
    }
    
    return distance;
  }
  
  public static int getManhattanDistance(int x,int y,int goalX,int goalY) {
    return Math.abs(x - goalX) + Math.abs(y - goalY);
  }
  
  public static int getDistance(int x,int y,int goalX,int goalY) {
    x -= goalX; x *= x;
    y -= goalY; y *= y;
    return (int)Math.round(Math.sqrt(x + y));
  }
  
  public int getFitness() { return fitness; }
  public int getMinDistance() { return minDistance; }
  public int getMinSteps() { return minSteps; }
  public int getMinX() { return minX; }
  public int getMinY() { return minY; }
  public int getResult() { return result; }
  
  public void fixAll(Maze maze) {
    maze = new Maze(maze);
    int x = 0,y = 0;
    int goalX = maze.getWidth() - 1,goalY = maze.getHeight() - 1;
    
    int newResult = 0;
    
    for(Step s: path) {
      int lookX = s.takeStepX(x);
      int lookY = s.takeStepY(y);
      
      if(maze.isEmpty(lookX,lookY)) {
        maze.setWall(x,y);
        x = lookX; y = lookY;
        ++newResult;
        
        if(x == goalX && y == goalY) {
          if(newResult < this.result) {
            this.result = newResult;
          }
          break;
        }
      }
    }
  }
  
  // Use this to watch a Glader move; can be fun
  public void printPath(Maze maze) {
    maze = new Maze(maze);
    int x = 0,y = 0;
    int goalX = maze.getWidth() - 1,goalY = maze.getHeight() - 1;
    Scanner stdin = new Scanner(System.in);
    
    System.out.println(maze.toString(x,y));
    
    for(Step s: path) {
      int lookX = s.takeStepX(x);
      int lookY = s.takeStepY(y);
      
      if(maze.isEmpty(lookX,lookY)) {
        maze.setOG(x,y); // Just a little o.g.
        x = lookX; y = lookY;
        
        System.out.println("Step: " + s);
        System.out.println(maze.toString(x,y));
        
        if(x == goalX && y == goalY) { break; }
        
        stdin.nextLine();
      }
    }
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder(path.length);
    for(Step s: path) {
      sb.append(s);
    }
    return sb.toString();
  }
}

class Maze {
  public static final char EMPTY = '.';
  public static final char OG = 'o';
  public static final char WALL = 'W';
  
  private char[][] maze;
  private int width,height;
  
  public Maze(Maze maze) {
    this.maze = new char[maze.maze[0].length][maze.maze.length];
    this.height = maze.height;
    this.width = maze.width;
    
    for(int y = 0; y < maze.maze.length; ++y) {
      for(int x = 0; x < maze.maze[0].length; ++x) {
        this.maze[x][y] = maze.maze[x][y];
      }
    }
  }
  
  public Maze(String mazeStr) {
    String[] rows = mazeStr.split("\n");
    height = rows.length;
    width = rows[0].length(); // Supposed to be NxN
    maze = new char[width + 2][height + 2]; // +2 for outer walls
    
    for(int y = 0; y < height; ++y) {
      for(int x = 0; x < width; ++x) {
        maze[x + 1][y + 1] = Character.toUpperCase(rows[y].charAt(x));
      }
    }
    // Top & Bottom outer walls
    for(int x = 0; x < maze[0].length; ++x) {
      maze[x][0] = maze[x][maze.length - 1] = WALL;
    }
    // Left & Right outer walls
    for(int y = 0; y < maze.length; ++y) {
      maze[0][y] = maze[maze[0].length - 1][y] = WALL;
    }
  }
  
  public void setEmpty(int x,int y) { setSpace(x,y,EMPTY); }
  public void setOG(int x,int y) { setSpace(x,y,OG); }
  public void setWall(int x,int y) { setSpace(x,y,WALL); }
  public void setSpace(int x,int y,char c) { maze[x + 1][y + 1] = c; }
  public char getSpace(int x,int y) { return maze[x + 1][y + 1]; }
  
  public boolean isEmpty(int x,int y) { return getSpace(x,y) == EMPTY || getSpace(x,y) == OG; }
  public boolean isWall(int x,int y) { return getSpace(x,y) == WALL; }
  
  public int getWidth() { return width; }
  public int getHeight() { return height; }
  public int getSize() { return width * height; }
  
  public String toString() { return toString(0,0); }
  public String toString(int gladerX,int gladerY) {
    StringBuilder sb = new StringBuilder();
    
    for(int y = 0; y < getHeight(); ++y) {
      for(int x = 0; x < getWidth(); ++x) {
        if(x == gladerX && y == gladerY) {
          sb.append('g');
        } else {
          sb.append((char)getSpace(x,y));
        }
        sb.append(' '); // Prettier
      }
      sb.append('\n');
    }
    return sb.toString();
  }
}

class Pos {
  private int distance = 0;
  private Pos parent = null;
  private Step step = null;
  private int steps = 0;
  private int x = 0,y = 0;
  
  public Pos(int x,int y) { this(x,y,null); }
  public Pos(int x,int y,int goalX,int goalY) {
    this(x,y);
    updateDistance(goalX,goalY);
  }
  public Pos(int x,int y,Step step) {
    this.step = step;
    this.x = x; this.y = y;
  }
  
  public Pos updateDistance(int goalX,int goalY) {
    this.distance = Math.abs(x - goalX) + Math.abs(y - goalY);
    return this;
  }
  
  public void setParent(Pos parent) { this.parent = parent; }
  public void setSteps(int steps) { this.steps = steps; }
  
  public int getCost() { return distance + steps; }
  public int getDistance() { return distance; }
  public Pos getParent() { return parent; }
  public Step getStep() { return step; }
  public int getSteps() { return steps; }
  public int getX() { return x; }
  public int getY() { return y; }
  
  public boolean equals(Object o) {
    if(o == null || !(o instanceof Pos)) {
      return false;
    }
    Pos p = (Pos)o;
    return (this == p) || (x == p.x && y == p.y);
  }
  public int hashCode() { return Objects.hash(x,y); }
}

enum Step {
  N(0,-1),S(0,1),E(1,0),W(-1,0);
  
  public static final Step[] steps = Step.values();
  
  private int stepX,stepY;
  
  private Step(int stepX,int stepY) {
    this.stepX = stepX;
    this.stepY = stepY;
  }
  
  public static Step flip(Step step) {
    switch(step) {
      case N: return S;
      case S: return N;
      case E: return W;
      case W: return E;
    }
    return null;
  }
  
  public Pos takeStep(int x,int y) {
    return new Pos(takeStepX(x),takeStepY(y),this);
  }
  
  public int takeStepX(int x) {
    return x + this.stepX;
  }
  
  public int takeStepY(int y) {
    return y + this.stepY;
  }
  
  public static Step getRandomStep(Random rand) {
    return Step.steps[rand.nextInt(Step.steps.length)];
  }
  
  public static Step[] getRandomSteps(int len) {
    Random rand = new Random();
    Step[] randSteps = new Step[len];
    
    for(int i = 0; i < len; ++i) {
      randSteps[i] = getRandomStep(rand);
    }
    return randSteps;
  }
  
  public int getStepX() { return stepX; }
  public int getStepY() { return stepY; }
}
