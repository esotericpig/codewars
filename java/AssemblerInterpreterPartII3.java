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

import java.util.function.ToIntFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * For this solution, I tried to reduce the # of lines of code using Java 8
 *   lambdas, just for fun.
 * This was the smallest solution I saw. You can probably reduce the # of lines
 *   even more, but I'm not sure how right now.
 * </pre>
 *
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/assembler-interpreter-part-ii/java
 * @see    https://en.wikipedia.org/wiki/Assembly_language
 * @see    https://en.wikipedia.org/wiki/Assembly_language#Number_of_passes
 * @rank   2 kyu
 */
public class AssemblerInterpreterPartII3 {
  private Map<String,Integer> funcs     = new HashMap<>();
  private Deque<Integer>      gotoStack = new LinkedList<>();
  private List<Inst>          insts     = new ArrayList<>();
  private StringBuilder       out       = new StringBuilder();
  private Map<String,Integer> regs      = new HashMap<>();

  private int cmp = 0,index = 0;

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
    return (new AssemblerInterpreterPartII3(input)).run();
  }

  public AssemblerInterpreterPartII3(final String input) {
    for(String line: input.split("\n+")) {
      // Empty or comment?
      if((line = line.trim()).isEmpty() || line.charAt(0) == ';') {
        continue;
      }

      // <instruction> | <single quoted string> | <comma> | <comment>
      Matcher matcher = Pattern.compile("[^\\s',;]+|'[^']*'|,|;.*").matcher(line);
      List<String> instArgs = new ArrayList<String>(2); // Usually 2

      while(matcher.find()) {
        String group = matcher.group();
        char firstChar = group.charAt(0);

        if(firstChar == ';') { break; }    // Comment?
        if(firstChar == ',') { continue; } // Ignore commas
        instArgs.add(group);
      }

      Inst inst = new Inst(instArgs);

      // Function/Label?
      if(inst.args.length == 0 && inst.name.charAt(inst.name.length() - 1) == ':') {
        inst.func = (args) -> -1; // no op
        inst.name = inst.name.substring(0,inst.name.length() - 1);
        funcs.put(inst.name,index);
      }
      else {
        switch(inst.name) {
          case "add":  inst.func = (args) -> setVar(args[0],getVar(args[0]) + getNumOrVar(args[1])); break;
          case "call": inst.func = (args) -> jmp(args[0],true,true); break;
          case "cmp":  inst.func = (args) -> {cmp = getNumOrVar(args[0]) - getNumOrVar(args[1]); return -1;}; break;
          case "dec":  inst.func = (args) -> setVar(args[0],getVar(args[0]) - 1); break;
          case "div":  inst.func = (args) -> setVar(args[0],getVar(args[0]) / getNumOrVar(args[1])); break;
          case "end":  inst.func = (args) -> -2; break;
          case "inc":  inst.func = (args) -> setVar(args[0],getVar(args[0]) + 1); break;
          case "je":   inst.func = (args) -> jmp(args[0],cmp == 0); break;
          case "jg":   inst.func = (args) -> jmp(args[0],cmp > 0); break;
          case "jge":  inst.func = (args) -> jmp(args[0],cmp >= 0); break;
          case "jl":   inst.func = (args) -> jmp(args[0],cmp < 0); break;
          case "jle":  inst.func = (args) -> jmp(args[0],cmp <= 0); break;
          case "jmp":  inst.func = (args) -> jmp(args[0],true); break;
          case "jne":  inst.func = (args) -> jmp(args[0],cmp != 0); break;
          case "mov":  inst.func = (args) -> setVar(args[0],getNumOrVar(args[1])); break;
          case "msg":  inst.func = (args) -> msg(args); break;
          case "mul":  inst.func = (args) -> setVar(args[0],getVar(args[0]) * getNumOrVar(args[1])); break;
          case "ret":  inst.func = (args) -> gotoStack.isEmpty() ? -1 : gotoStack.pop(); break;
          case "sub":  inst.func = (args) -> setVar(args[0],getVar(args[0]) - getNumOrVar(args[1])); break;
        }
      }

      if(inst.func != null) {
        insts.add(inst);
        ++index;
      }
      else {
        throw new RuntimeException("Invalid instruction: " + inst.name);
      }
    }
  }

  public String run() {
    boolean hasEnd = false;

    for(index = 0; index < insts.size(); ++index) {
      Inst inst = insts.get(index);
      int gotoIndex = inst.exec();

      if(gotoIndex == -2) { hasEnd = true; break; }
      if(gotoIndex != -1) { index = gotoIndex; }
    }

    return hasEnd ? out.toString() : null;
  }

  public int jmp(String funcName,boolean isJmp) {
    return jmp(funcName,isJmp,false);
  }

  public int jmp(String funcName,boolean isJmp,boolean isCall) {
    if(isJmp) {
      if(isCall) { gotoStack.push(index); }
      return funcs.get(funcName);
    }
    return -1;
  }

  public int msg(String[] args) {
    for(String arg: args) {
      out.append(arg.charAt(0) == '\'' ? arg.substring(1,arg.length() - 1) : regs.get(arg));
    }
    return -1;
  }

  public int setVar(String var,Integer num) {
    regs.put(var,num);
    return -1;
  }

  public int getNumOrVar(String arg) {
    return arg.matches("\\d+") ? Integer.parseInt(arg) : regs.get(arg);
  }

  public int getVar(String var) {
    return regs.get(var);
  }

  public static class Inst {
    public String[] args = null;
    public Func     func = null;
    public String   name = null;

    public Inst(List<String> args) {
      this.name = args.remove(0);
      this.args = args.toArray(new String[0]);
    }

    public int exec() { return func.applyAsInt(args); }

    public static interface Func extends ToIntFunction<String[]> {}
  }
}
