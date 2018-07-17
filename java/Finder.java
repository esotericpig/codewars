import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

/**
 * <pre>
 * This implements the A* algorithm to traverse a maze perfectly.
 * 
 * At first, I used a cost (heuristic), but then found out that this wouldn't
 *   work because this Kata wanted the best solution, not just any solution.
 * 
 * Later, I added code in #main(...) to generate a random maze using this
 *   algorithm for fun.
 * 
 * "Glader" comes from the book/film series called "Maze Runner."
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/path-finder-number-2-shortest-path/java
 * @see    http://www.growingwiththeweb.com/2012/06/a-pathfinding-algorithm.html
 * @see    https://en.wikipedia.org/wiki/Maze_Runner_(film_series)
 * @rank   4 kyu
 */
public class Finder {
  public static int pathFinder(String mazeStr) {
    return pathFinder(mazeStr,null);
  }
  
  public static int pathFinder(String mazeStr,Maze m) {
    Maze maze = (m == null) ? new Maze(mazeStr) : m;
    Glader glader = new Glader();
    
    int result = glader.run(maze);
    
    glader.printPath(maze);
    System.out.println("Path:   " + glader);
    System.out.println("Result: " + result);
    
    return result;
  }
  
  public static void main(String[] args) {
    StringBuilder mazeStr = new StringBuilder();
    int mazeSize = 33; // width & height
    
    for(int y = 0; y < mazeSize; ++y) {
      for(int x = 0; x < mazeSize; ++x) {
        mazeStr.append(Maze.EMPTY);
      }
      mazeStr.append('\n');
    }
    
    Maze maze = new Maze(mazeStr.toString());
    int numBlocks = (int)Math.round(maze.getSize() * 0.64); // Percentage of blocks
    Random rand = new Random();
    
    for(int i = 0; i < numBlocks; ++i) {
      Glader glader = null;
      int j = 0,x = -1,y = -1;
      
      System.out.print("\rPlacing block #" + (i+1) + " of " + numBlocks + " / " + maze.getSize() + "          ");
      
      for(; j < 1000; ++j) {
        int k = 0;
        
        for(; k < 1000; ++k) {
          x = rand.nextInt(mazeSize);
          y = rand.nextInt(mazeSize);
          
          if(maze.isEmpty(x,y)) { break; }
        }
        
        if(k >= 1000) { j = 1000; break; }
        
        maze.setWall(x,y);
        glader = new Glader();
        
        if(glader.run(maze) == -1) {
          maze.setEmpty(x,y);
        }
        else {
          break;
        }
      }
      
      if(j >= 1000) { break; }
    }
    System.out.println();
    
    pathFinder(null,maze);
    
    // big_maze.txt
    /*java.io.BufferedReader fin = null;
    try {
      fin = new java.io.BufferedReader(new java.io.FileReader("../data/finder_big_maze.txt"));
      
      String line = null;
      StringBuilder mazeStr = new StringBuilder();
      
      while((line = fin.readLine()) != null) {
        mazeStr.append(line + "\n");
      }
      
      pathFinder(mazeStr.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
      if(fin != null) {
        try { fin.close(); } catch(Exception e) {}
      }
    }*/

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

// Do you like Maze Runner?
class Glader {
  private LinkedList<Step> path = new LinkedList<>();
  private int x,y,goalX,goalY;
  
  public int run(Maze maze) {
    return run(maze,0,0,maze.getWidth() - 1,maze.getHeight() - 1);
  }
  
  // A* algorithm
  // http://www.growingwiththeweb.com/2012/06/a-pathfinding-algorithm.html
  public int run(Maze maze,int x,int y,int goalX,int goalY) {
    path.clear();
    this.x = x; this.y = y;
    this.goalX = goalX; this.goalY = goalY;
    
    LinkedList<Pos> open = new LinkedList<>();
    LinkedList<Pos> closed = new LinkedList<>();
    
    open.addLast(new Pos(x,y,goalX,goalY));
    
    Pos goalPos = null;
    
    while(!open.isEmpty()) {
      Pos pos = open.removeLast();
      
      // Goal!
      if(pos.getX() == goalX && pos.getY() == goalY) {
        // Don't break because this Kata requires the best solution
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
                // Don't use cost (heuristic) because this Kata requires the best solution
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
    
    if(goalPos != null) {
      for(Pos p = goalPos.getParent(); p != null; p = p.getParent()) {
        if(p.getStep() != null) { path.addFirst(p.getStep()); }
      }
    }
    
    return (goalPos != null) ? goalPos.getSteps() : -1;
  }
  
  // Use this to watch a Glader run for fun
  public void printPath(Maze maze) {
    maze = maze.clone();
    int x = this.x,y = this.y;
    Scanner stdin = new Scanner(System.in);
    
    for(Step s: path) {
      int lookX = s.takeStepX(x);
      int lookY = s.takeStepY(y);
      
      if(maze.isEmpty(lookX,lookY)) {
        maze.setOg(x,y,s); // Just a little o.g.
        x = lookX; y = lookY;
        
        System.out.println("Step: " + s);
        System.out.println(maze.toString(x,y));
        
        if(x == goalX && y == goalY) { break; }
        
        stdin.nextLine();
      }
    }
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder(path.size());
    for(Step s: path) { sb.append(s.toString().charAt(0)); }
    return sb.toString();
  }
}

class Maze implements Cloneable {
  public static final char GOAL = '$';
  public static final char GLADER = '@';
  public static final char EMPTY = ' ';
  public static final char OG = '.';
  public static final char WALL  = 'o';
  
  private char[][] maze;
  private int width,height;
  
  public Maze(Maze maze) {
    width = maze.width;
    height = maze.height;
    
    // Should be NxN
    this.maze = new char[maze.maze.length][maze.maze.length];
    for(int y = 0; y < this.maze.length; ++y) {
      for(int x = 0; x < this.maze.length; ++x) {
        this.maze[x][y] = maze.maze[x][y];
      }
    }
  }
  
  public Maze(String mazeStr) {
    String[] rows = mazeStr.split("\n");
    width = rows.length; // Should be NxN
    height = rows.length;
    maze = new char[width + 2][height + 2]; // +2 for outer walls
    
    for(int y = 0; y < height; ++y) {
      for(int x = 0; x < width; ++x) {
        setSpace(x,y,Character.toUpperCase(rows[y].charAt(x)));
      }
    }
    // Top & Bottom outer walls
    for(int x = 0; x < maze.length; ++x) {
      maze[x][0] = WALL; maze[x][maze.length - 1] = WALL;
    }
    // Left & Right outer walls
    for(int y = 0; y < maze.length; ++y) {
      maze[0][y] = WALL; maze[maze.length - 1][y] = WALL;
    }
  }
  
  public Maze clone() { return new Maze(this); }
  
  public void setEmpty(int x,int y) { setSpace(x,y,EMPTY); }
  public void setOg(int x,int y) { setSpace(x,y,OG); }
  public void setOg(int x,int y,Step step) { setSpace(x,y,step.getOg()); }
  public void setWall(int x,int y) { setSpace(x,y,WALL); }
  public void setSpace(int x,int y,char c) { maze[x + 1][y + 1] = c; }
  public char getSpace(int x,int y) { return maze[x + 1][y + 1]; }
  
  public boolean isEmpty(int x,int y) { char s = getSpace(x,y); return s == EMPTY || s == OG; }
  public boolean isWall(int x,int y) { return getSpace(x,y) == WALL; }
  
  public int getWidth() { return width; }
  public int getHeight() { return height; }
  public int getSize() { return width * height; }
  
  public String toString() { return toString(0,0); }
  public String toString(int gladerX,int gladerY) {
    // *2/+1 for spaces/newlines
    //StringBuilder sb = new StringBuilder((width * 2 + 1) * height);
    
    /*for(int y = 0; y < height; ++y) {
      for(int x = 0; x < width; ++x) {
        if(x == gladerX && y == gladerY) {
          sb.append(Maze.GLADER);
        } else {
          sb.append((char)getSpace(x,y));
        }
        sb.append(' '); // Prettier
      }
      sb.append('\n');
    }*/
    
    int goalX = width,goalY = height; // Because of walls, no -1
    StringBuilder sb = new StringBuilder((maze.length * 2 + 1) * maze.length);
    
    ++gladerX; ++gladerY;
    
    for(int y = 0; y < maze.length; ++y) {
      for(int x = 0; x < maze.length; ++x) {
        if(x == gladerX && y == gladerY) {
          sb.append(GLADER);
        } else if(x == goalX && y == goalY) {
          sb.append(GOAL);
        } else {
          sb.append(maze[x][y]);
        }
        sb.append(EMPTY);
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
    // Manhattan distance; not used anymore
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
  NORTH(0,-1,'^'),SOUTH(0,1,'Y'),EAST(1,0,'>'),WEST(-1,0,'<');
  
  private char og;
  private int stepX,stepY;
  
  private Step(int stepX,int stepY,char og) {
    this.og = og;
    this.stepX = stepX;
    this.stepY = stepY;
  }
  
  public Pos takeStep(int x,int y) {
    return new Pos(takeStepX(x),takeStepY(y),this);
  }
  public int takeStepX(int x) { return x + stepX; }
  public int takeStepY(int y) { return y + stepY; }
  
  public char getOg() { return og; }
  public int getStepX() { return stepX; }
  public int getStepY() { return stepY; }
}
