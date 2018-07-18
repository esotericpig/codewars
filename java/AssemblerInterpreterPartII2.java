import java.io.BufferedReader;
import java.io.IOException;

import java.nio.charset.Charset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * This solution is a multi-pass assembler.
 * 
 * This is my verbose solution, which is faster because of less switches.
 * I didn't submit this solution because it's too verbose for codewars.
 * Instead, I submitted AssemblerInterpreterPartII.java.
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/assembler-interpreter-part-ii/java
 * @see    https://en.wikipedia.org/wiki/Assembly_language
 * @see    https://en.wikipedia.org/wiki/Assembly_language#Number_of_passes
 * @rank   2 kyu
 */
public class AssemblerInterpreterPartII2 {
  protected int currInstIndex = 0;
  protected Map<String,FuncInst> funcs = new HashMap<>();
  protected int gotoIndex = -1;
  protected Deque<Integer> gotoIndexStack = new LinkedList<>();
  protected boolean hasEnd = false;
  protected List<Inst> insts; // Defined in constructor
  protected StringBuilder out = new StringBuilder();
  protected int prevCmp = 0;
  protected Map<String,Integer> regs = new HashMap<>();
  
  public static void main(String[] args) {
    // "(5+1)/2 = 3"
    // "5! = 120"
    // "Term 8 of Fibonacci series is: 21"
    // "mod(11, 3) = 2"
    // "gcd(81, 153) = 9"
    // null
    // "2^10 = 1024"
    
    Charset charset = Charset.forName("UTF-8");
    String dirname = "data";
    String filename = "asm_interp_partii.asm"; // Party?
    Path[] paths = new Path[]{Paths.get("..",dirname,filename),Paths.get(dirname,filename)};
    
    for(Path path: paths) {
      if(Files.exists(path)) {
        try(BufferedReader fin = Files.newBufferedReader(path,charset)) {
          String line = null;
          StringBuilder prog = new StringBuilder();
          
          while((line = fin.readLine()) != null) {
            if(line.equals("---")) {
              System.out.println(interpret(prog.toString()));
              prog.setLength(0); // Clear/reset
            }
            else {
              prog.append(line).append('\n');
            }
          }
          
          if(prog.length() > 0) {
            System.out.println(interpret(prog.toString()));
          }
        }
        catch(IOException e) {
          e.printStackTrace();
        }
        break;
      }
    }
  }
  
  public static String interpret(final String input) {
    return (new AssemblerInterpreterPartII2(input)).run();
  }
  
  public AssemblerInterpreterPartII2(final String input) {
    String[] lines = input.split("\n+");
    
    insts = new ArrayList<>(lines.length);
    
    for(String line: lines) {
      Inst inst = newInst(line);
      if(inst != null) { insts.add(inst); }
    }
  }
  
  public String run() {
    for(currInstIndex = 0; currInstIndex < insts.size(); ++currInstIndex) {
      Inst inst = insts.get(currInstIndex);
      inst.exec();
      
      if(hasEnd) { break; }
      if(gotoIndex != -1) {
        currInstIndex = gotoIndex;
        gotoIndex = -1;
      }
    }
    
    return hasEnd ? out.toString() : null;
  }
  
  public Inst newInst(String line) {
    Inst inst = null;
    
    // Blank line or comment?
    if((line = line.trim()).isEmpty() || line.charAt(0) == ';') {
      return inst;
    }
    
    // <instruction> | <single quoted string> | <comma> | <comment>
    Matcher matcher = Pattern.compile("[^\\s',;]+|'[^']*'|,|;.*").matcher(line);
    List<String> args = new ArrayList<String>(2); // Usually 2
    
    while(matcher.find()) {
      String group = matcher.group();
      char firstChar = group.charAt(0);
      
      if(firstChar == ';') { break; }    // Comment?
      if(firstChar == ',') { continue; } // Ignore commas
      
      args.add(group);
    }
    
    if(!args.isEmpty()) {
      String instName = args.get(0);
      
      if(args.size() == 1 && instName.charAt(instName.length() - 1) == ':') {
        inst = new FuncInst();
      }
      else {
        switch(instName) {
          case "add":  inst = new AddInst();  break;
          case "call": inst = new CallInst(); break;
          case "cmp":  inst = new CmpInst();  break;
          case "dec":  inst = new DecInst();  break;
          case "div":  inst = new DivInst();  break;
          case "end":  inst = new EndInst();  break;
          case "inc":  inst = new IncInst();  break;
          case "je":   inst = new JeInst();   break;
          case "jg":   inst = new JgInst();   break;
          case "jge":  inst = new JgeInst();  break;
          case "jl":   inst = new JlInst();   break;
          case "jle":  inst = new JleInst();  break;
          case "jmp":  inst = new JmpInst();  break;
          case "jne":  inst = new JneInst();  break;
          case "mov":  inst = new MovInst();  break;
          case "msg":  inst = new MsgInst();  break;
          case "mul":  inst = new MulInst();  break;
          case "ret":  inst = new RetInst();  break;
          case "sub":  inst = new SubInst();  break;
        }
      }
      
      if(inst != null) {
        inst.args = args;
        inst.index = currInstIndex++;
        
        inst.init();
      }
      else {
        throw new RuntimeException("Invalid instruction: " + instName);
      }
    }
    
    return inst;
  }
  
  public abstract class Inst {
    protected List<String> args;
    protected int index;
    
    public abstract void exec();
    
    public void init() {
      args.remove(0); // Remove instruction name
    }
  }
  
  public abstract class NumInst extends Inst {
    protected int[] nums;
    protected String[] vars;
    
    public void exec() {
      for(int i = 0; i < args.size(); ++i) {
        String arg = args.get(i);
        
        if(arg.matches("\\d+")) {
          nums[i] = Integer.parseInt(arg);
        }
        else {
          nums[i] = regs.getOrDefault(arg,0);
          vars[i] = arg;
        }
      }
    }
    
    public void init() {
      super.init();
      nums = new int[args.size()];
      vars = new String[args.size()];
    }
    
    public void setVar(int index,int num) {
      regs.put(vars[index],nums[index] = num);
    }
  }
  
  public class AddInst extends NumInst {
    public void exec() {
      super.exec();
      setVar(0,nums[0] + nums[1]);
    }
  }
  
  public class CallInst extends JmpInst {
    public void exec() {
      super.exec();
      if(isJmp()) { gotoIndexStack.push(currInstIndex); }
    }
  }
  
  public class CmpInst extends NumInst {
    public void exec() {
      super.exec();
      prevCmp = nums[0] - nums[1];
    }
  }
  
  public class DecInst extends NumInst {
    public void exec() {
      super.exec();
      setVar(0,--nums[0]);
    }
  }
  
  public class DivInst extends NumInst {
    public void exec() {
      super.exec();
      setVar(0,nums[0] / nums[1]);
    }
  }
  
  public class EndInst extends Inst {
    public void exec() {
      hasEnd = true;
    }
  }
  
  public class FuncInst extends Inst {
    public void init() {
      String name = args.get(0);
      name = name.substring(0,name.length() - 1);
      
      args.set(0,name);
      funcs.put(name,this);
    }
    
    public void exec() {}
  }
  
  public class IncInst extends NumInst {
    public void exec() {
      super.exec();
      setVar(0,++nums[0]);
    }
  }
  
  public class JeInst extends JmpInst {
    public boolean isJmp() { return prevCmp == 0; }
  }
  
  public class JgInst extends JmpInst {
    public boolean isJmp() { return prevCmp > 0; }
  }
  
  public class JgeInst extends JmpInst {
    public boolean isJmp() { return prevCmp >= 0; }
  }
  
  public class JlInst extends JmpInst {
    public boolean isJmp() { return prevCmp < 0; }
  }
  
  public class JleInst extends JmpInst {
    public boolean isJmp() { return prevCmp <= 0; }
  }
  
  public class JmpInst extends Inst {
    public void exec() {
      if(isJmp()) {
        FuncInst funcInst = funcs.get(args.get(0));
        gotoIndex = funcInst.index;
      }
    }
    
    public boolean isJmp() { return true; }
  }
  
  public class JneInst extends JmpInst {
    public boolean isJmp() { return prevCmp != 0; }
  }
  
  public class MovInst extends NumInst {
    public void exec() {
      super.exec();
      setVar(0,nums[1]);
    }
  }
  
  public class MsgInst extends Inst {
    public void exec() {
      for(String arg: args) {
        out.append(arg.charAt(0) == '\'' ? arg.substring(1,arg.length() - 1) : regs.get(arg));
      }
    }
  }
  
  public class MulInst extends NumInst {
    public void exec() {
      super.exec();
      setVar(0,nums[0] * nums[1]);
    }
  }
  
  public class RetInst extends Inst {
    public void exec() {
      if(!gotoIndexStack.isEmpty()) {
        gotoIndex = gotoIndexStack.pop();
      }
    }
  }
  
  public class SubInst extends NumInst {
    public void exec() {
      super.exec();
      setVar(0,nums[0] - nums[1]);
    }
  }
}
