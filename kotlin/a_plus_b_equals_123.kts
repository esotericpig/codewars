/**
 * Seemed easy, but B can't be negative.
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/a-plus-b-equals-equals-123/kotlin
 * @rank   6 kyu
 */

object Dinglemouse {
  fun int123(a:Int):Long {
    val b:Long = (123L - a)
    return if(b >= 0) b else (Long.MAX_VALUE - -b + 1)
  }
}

object Dinglemouse2 {
  fun int123(a:Int):Long {
    return (123L - a) and Long.MAX_VALUE
  }
}

println(Dinglemouse.int123(0))    // 123
println(Dinglemouse2.int123(0))   // 123
println(Dinglemouse.int123(123))  // 0
println(Dinglemouse2.int123(123)) // 0
println(Dinglemouse.int123(500))  // 9223372036854775431
println(Dinglemouse2.int123(500)) // 9223372036854775431

if(args.any()) {
  println()
  
  for(arg in args) {
    var a:Int = arg.toInt()
    var b1:Long = Dinglemouse.int123(a)
    var b2:Long = Dinglemouse2.int123(a)
    
    println("1: ($a + $b1).toInt() => ${(a + b1).toInt()}")
    println("2: ($a + $b2).toInt() => ${(a + b2).toInt()}")
  }
}
