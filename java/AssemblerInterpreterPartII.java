import java.io.BufferedReader;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
 * This was my original solution. Originally, I was going to make a class for
 *   each instruction (Inst), but I thought this was too verbose for codewars.
 * For the verbose solution, see AssemblerInterpreterPartII2.java, which is also
 *   faster because of less switches.
 * </pre>
 *
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/assembler-interpreter-part-ii/java
 * @see    https://en.wikipedia.org/wiki/Assembly_language
 * @see    https://en.wikipedia.org/wiki/Assembly_language#Number_of_passes
 * @rank   2 kyu
 */
public class AssemblerInterpreterPartII {
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

    Charset charset = StandardCharsets.UTF_8; // Charset.forName("UTF-8");
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
    return (new AssemblerInterpreterPartII(input)).run();
  }

  public AssemblerInterpreterPartII(final String input) {
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

    // <word> | <single quoted string> | <comma> | <comment>
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
        inst = new FuncInst(args,instName);
      }
      else {
        switch(instName) {
          case "dec":
          case "inc":  inst = new Num1Inst(); break;

          case "add":
          case "div":
          case "mov":
          case "mul":
          case "sub":  inst = new Num2Inst(); break;

          case "call":
          case "je":
          case "jg":
          case "jge":
          case "jl":
          case "jle":
          case "jmp":
          case "jne":  inst = new JmpInst();  break;

          case "cmp":  inst = new CmpInst();  break;
          case "end":  inst = new EndInst();  break;
          case "msg":  inst = new MsgInst();  break;
          case "ret":  inst = new RetInst();  break;
        }
      }

      if(inst != null) {
        inst.args = args;
        inst.index = currInstIndex++;
      }
      // Else, ignore invalid instructions
    }

    return inst;
  }

  public abstract class Inst {
    protected List<String> args;
    protected int index;

    public abstract void exec();
  }

  public class Num1Inst extends Inst {
    protected int num1;
    protected String var1;

    public void exec() {
      var1 = args.get(1);
      num1 = var1.matches("\\d+") ? Integer.parseInt(var1) : regs.getOrDefault(var1,0);

      switch(args.get(0)) {
        case "dec": --num1; break;
        case "inc": ++num1; break;
      }

      regs.put(var1,num1);
    }
  }

  public class Num2Inst extends Num1Inst {
    protected int num2;
    protected String var2;

    public void exec() {
      super.exec();
      var2 = args.get(2);
      num2 = var2.matches("\\d+") ? Integer.parseInt(var2) : regs.get(var2);

      switch(args.get(0)) {
        case "add": num1 += num2; break;
        case "div": num1 /= num2; break;
        case "mov": num1  = num2; break;
        case "mul": num1 *= num2; break;
        case "sub": num1 -= num2; break;
      }

      regs.put(var1,num1);
    }
  }

  public class CmpInst extends Num2Inst {
    public void exec() {
      super.exec();
      prevCmp = num1 - num2;
    }
  }

  public class EndInst extends Inst {
    public void exec() {
      hasEnd = true;
    }
  }

  public class FuncInst extends Inst {
    public FuncInst(List<String> args,String name) {
      args.set(0,name = name.substring(0,name.length() - 1));
      funcs.put(name,this);
    }
    public void exec() {}
  }

  public class JmpInst extends Inst {
    public void exec() {
      boolean doJmp = false;

      switch(args.get(0)) {
        case "call": gotoIndexStack.push(currInstIndex);
        case "jmp":  doJmp = true; break;

        case "je":   doJmp = prevCmp == 0; break;
        case "jg":   doJmp = prevCmp >  0; break;
        case "jge":  doJmp = prevCmp >= 0; break;
        case "jl":   doJmp = prevCmp <  0; break;
        case "jle":  doJmp = prevCmp <= 0; break;
        case "jne":  doJmp = prevCmp != 0; break;
      }

      if(doJmp) {
        FuncInst funcInst = funcs.get(args.get(1));
        gotoIndex = funcInst.index;
      }
    }
  }

  public class MsgInst extends Inst {
    public void exec() {
      for(int i = 1; i < args.size(); ++i) {
        String arg = args.get(i);

        if(arg.charAt(0) == '\'') {
          out.append(arg.substring(1,arg.length() - 1));
        }
        else { out.append(regs.get(arg)); }
      }
    }
  }

  public class RetInst extends Inst {
    public void exec() {
      if(!gotoIndexStack.isEmpty()) {
        gotoIndex = gotoIndexStack.pop();
      }
    }
  }
}
