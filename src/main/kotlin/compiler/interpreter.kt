package compiler

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import compiler.Operator.*
import java.util.*

sealed class Expr {

  data class Var(val name: String) : Expr()
  data class Lambda(val binder: String, val tyBinder: MonoType?, val body: Expr) : Expr()
  data class App(val func: Expr, val arg: Expr) : Expr()
  data class If(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr()
  data class Binary(val left: Expr, val op: Operator, val right: Expr) : Expr()
  data class Let(val recursive: Boolean, val binder: String, val expr: Expr, val body: Expr) : Expr()
  data class Read(val type: ReadType) : Expr()                                                                          /**<====== READ =======>**/
  data class Print(val string: String, val color: Color?) : Expr()                                                      /**<====== PRINT =======>**/

  data class IntLiteral(val num: Int) : Expr()
  data class BoolLiteral(val bool: Boolean) : Expr()
  data class StringLiteral(val string: String) : Expr()
}

enum class ReadType {                                                                                                   /**<====== READ =======>**/
  Int,
  String
}

enum class Color {                                                                                                      /**<====== PRINT =======>**/
  Red,
  Green,
  Yellow,
  Blue,
  Purple,
  Cyan,
  White
}

enum class Operator {                                                                                                   /**<====== OPERTAORE =======>**/
  Add,
  Subtract,
  Multiply,
  Divide,
  Power,
  Modulo,
  Equality,
  Inequality,
  GreaterThan,
  GreaterEqualThan,
  LessThan,
  LessEqualThan,
  And,
  Or,
  Concat
}

typealias Env = PersistentMap<String, Value>

sealed class Value {
  data class Int(val num: kotlin.Int) : Value()
  data class Bool(val bool: Boolean) : Value()
  data class String(val string: kotlin.String) : Value()
  data class Closure(var env: Env, val binder: kotlin.String, val body: Expr) : Value()
}

fun eval(env: Env, expr: Expr): Value {
  return when (expr) {
    is Expr.Read -> {                                                                                                   /**<====== READ =======>**/
      val reader = Scanner(System.`in`)
      when(expr.type) {
        ReadType.Int -> Value.Int(
                                  try {
                                    reader.nextInt()
                                  } catch (e: InputMismatchException) {
                                    reader.next()
                                    -2147483647
                                  })
        ReadType.String -> Value.String(reader.next())
      }
    }
    is Expr.IntLiteral -> Value.Int(expr.num)
    is Expr.BoolLiteral -> Value.Bool(expr.bool)
    is Expr.StringLiteral -> Value.String(expr.string)
    is Expr.Print -> {                                                                                                  /**<====== PRINT =======>**/
      val placeholder = findPrintVariables(expr.string)
      var formatedString = expr.string
      placeholder.forEach{
        val value  = env.get(it) ?: throw Exception("illegal placeholder in print: §$it")
        val replacementPattern = Regex("§$it((?= )|(?=$)|(?=\u001B)|(?=,)|(?=:))")
        when (value) {
          is Value.Int -> formatedString = formatedString.replace(replacementPattern, value.num.toString())
          is Value.String ->  formatedString = formatedString.replace(replacementPattern, value.string)
          is Value.Bool ->  formatedString = formatedString.replace(replacementPattern, value.bool.toString())
          is Value.Closure -> throw Exception("function is Not allowed as replacement: $value")
        }
      }
      val colorCode: String? = when (expr.color) {
            Color.Red -> PrintColor.RED
            Color.Green -> PrintColor.GREEN
            Color.Yellow -> PrintColor.YELLOW
            Color.Blue -> PrintColor.BLUE
            Color.Purple -> PrintColor.PURPLE
            Color.Cyan -> PrintColor.CYAN
            Color.White -> PrintColor.WHITE
            null -> null
      }
      if (colorCode == null)
        println(formatedString)
      else
        println(colorCode + formatedString + PrintColor.RESET)
      Value.Int(expr.string.count() * 2)
    }
    is Expr.Binary -> {
      val left = eval(env, expr.left)
      val right = eval(env, expr.right)
      applyOp(expr.op,left,right)                                                                                       /**<======  REFACTORED =======>**/
//      return when (expr.op) {
//        Equality, Inequality ->
//          if (left is Value.Int && right is Value.Int) {
//            binary<Int>(left, right, nameForOp(expr.op)) { x, y -> applyIntOp(expr.op, x, y) }
//          }
//          else if (left is Value.Bool && right is Value.Bool) {
//            binary<Boolean>(left, right, nameForOp(expr.op)) { x, y -> applyBoolOp(expr.op, x, y) }
//          }
//          else if (left is Value.String && right is Value.String) {
//            binary<String>(left, right, nameForOp(expr.op)) { x, y -> applyStringOp(expr.op, x, y) }
//          } else {
//            throw Error("Comparing incompatible values: $left and $right")
//          }
//        Concat -> if (left is Value.String && right is Value.String) {
//          Value.String(left.string + right.string)
//        } else {
//          throw Error("Can't concatenate non-string values: $left and $right")
//        }
//        else -> binary<Int>(left, right, nameForOp(expr.op)) { x, y -> applyIntOp(expr.op, x, y) }
//      }
    }
    is Expr.If -> {
      val condition = eval(env, expr.condition)
      if (condition !is Value.Bool) {
        throw Exception("Expected a boolean condition, but got $condition")
      }
      return if (condition.bool) {
        eval(env, expr.thenBranch)
      } else {
        eval(env, expr.elseBranch)
      }
    }
    is Expr.Let -> {
      val evaledExpr = eval(env, expr.expr)
      if (expr.recursive && evaledExpr is Value.Closure) {
        evaledExpr.env = evaledExpr.env.put(expr.binder, evaledExpr)
      }
      val extendedEnv = env.put(expr.binder, evaledExpr)
      eval(extendedEnv, expr.body)
    }
    is Expr.Lambda -> Value.Closure(env, expr.binder, expr.body)
    is Expr.Var ->
      when (expr.name) {
        "#firstChar" -> {
          val s = env["x"]!! as Value.String
          Value.String(s.string.take(1))
        }
        "#remainingChars" -> {
          val s = env["x"]!! as Value.String
          Value.String(s.string.drop(1))
        }
        "#charCode" -> {
          val s = env["x"]!! as Value.String
          Value.Int(s.string[0].code)
        }
        "#codeChar" -> {
          val x = env["x"]!! as Value.Int
          Value.String(x.num.toChar().toString())
        }
        else -> env.get(expr.name) ?: throw Exception("Unbound variable ${expr.name}")
      }
    is Expr.App -> {
      val func = eval(env, expr.func)
      if (func !is Value.Closure) {
        throw Exception("$func is not a function")
      } else {
        val arg = eval(env, expr.arg)
        val newEnv = func.env.put(func.binder, arg)
        eval(newEnv, func.body)
      }
    }
  }
}

fun applyOp(op: Operator, x: Value, y: Value) : Value {                                                                 /**<====== OPERTAORE =======>**/
  if (x is Value.Int && y is Value.Int)
    return applyIntOp(op, x.num, y.num)
  if (x is Value.Bool && y is Value.Bool)
    return applyBoolOp(op, x.bool, y.bool)
  if (x is Value.String && y is Value.String)
    return applyStringOp(op, x.string, y.string)
  throw (Exception("Can't ${nameForOp(op)} different kinds of values, $x, $y"))
}

fun applyIntOp(op: Operator, x: Int, y: Int): Value {                                                                   /**<====== OPERTAORE =======>**/
  return when (op) {
    Add -> Value.Int(x + y)
    Subtract -> Value.Int(x - y)
    Multiply -> Value.Int(x * y)
    Divide -> Value.Int(x / y)
    Power -> Value.Int(Math.pow(x.toDouble(),y.toDouble()).toInt())
    Modulo -> Value.Int(x % y)
    GreaterThan -> Value.Bool(x > y)
    GreaterEqualThan -> Value.Bool(x >= y)
    LessThan  -> Value.Bool(x < y)
    LessEqualThan  -> Value.Bool(x <= y)
    Equality -> Value.Bool(x == y)
    Inequality -> Value.Bool(x != y)
    else -> throw Error("Can't ${op.name} ints")
  }
}

fun applyBoolOp(op: Operator, x: Boolean, y: Boolean): Value {                                                          /**<====== OPERTAORE =======>**/
  return when (op) {
    Equality -> Value.Bool(x == y)
    Inequality -> Value.Bool(x != y)
    And -> Value.Bool(x && y)
    Or -> Value.Bool(x || y)
    else -> throw Error("Can't ${op.name} bools")
  }
}

fun applyStringOp(op: Operator, x: String, y: String): Value {                                                          /**<====== OPERTAORE =======>**/
  return when (op) {
    Equality -> Value.Bool(x == y)
    Inequality -> Value.Bool(x != y)
    Concat -> Value.String(x + y)
    else -> throw Error("Can't ${op.name} strings")
  }
}



fun nameForOp(op: Operator): String {                                                                                   /**<====== OPERTAORE =======>**/
  return when (op) {
    Add -> "add"
    Subtract -> "subtract"
    Multiply -> "multiply"
    Divide -> "divide"
    Power -> "power"
    Modulo -> "modulo"
    Equality -> "compare"
    Inequality -> "compare"
    GreaterThan -> "compare"
    GreaterEqualThan -> "compare"
    LessThan -> "compare"
    LessEqualThan -> "compare"
    And -> "and"
    Or -> "or"
    Concat -> "concat"
  }
}

fun findPrintVariables(string: String) : List<String> {                                                                 /**<====== PRINT =======>**/
  val indexOfPlaceholder = string.indexOf('§')
  if (indexOfPlaceholder == -1)
    return emptyList()
  val examineString = string.substring(indexOfPlaceholder,string.length)
  var cutIndex = examineString.indexOfAny(charArrayOf(' ','§','\u001B',':',','),1)
  if (cutIndex == -1)
    cutIndex = examineString.length
  val placeholder = examineString.substring(1,cutIndex)
  val restString = string.substringAfter("§"," ")
  return listOf<String>(placeholder) + findPrintVariables(restString)
}

//fun numericBinary(left: Value, right: Value, operation: String, combine: (Int, Int) -> Value): Value {
//  if (left is Value.Int && right is Value.Int) {
//    return combine(left.num, right.num)
//  } else {
//    throw (Exception("Can't $operation non-numbers, $left, $right"))
//  }
//}


//inline fun <reified T>binary(left: Value, right: Value, operation: String, combine: (T, T) -> Value): Value {           /**<====== OPERTAORE =======>**/
//  if (left is Value.Int && right is Value.Int && left.num is T && right.num is T) {
//    return combine(left.num, right.num)
//  }
//  else if (left is Value.Bool && right is Value.Bool && left.bool is T && right.bool is T) {
//    return combine(left.bool, right.bool)
//  }
//  else if (left is Value.String && right is Value.String && left.string is T && right.string is T) {
//    return combine(left.string, right.string)
//  }
//  else {
//    throw (Exception("Can't $operation types, $left, $right"))
//  }
//}




