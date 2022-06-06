import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlin.math.pow

sealed class Expr {
    data class Var(val name: String) : Expr()
    data class Lambda(val binder: String, val body: Expr) : Expr()
    data class App(val func: Expr, val arg: Expr) : Expr()
    data class If(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr()
    data class Binary(val left: Expr, val op: Operator, val right: Expr) : Expr()
    data class Let(val binder: String, val expr: Expr, val body: Expr) : Expr()

    data class IntLiteral(val num: Int) : Expr()
    data class BoolLiteral(val bool: Boolean) : Expr()
}

typealias Env = PersistentMap<String, Value>

enum class Operator {
    Add,
    Subtract,
    Multiply,
    Divide,
    Power,
    Equality,
    And,
    Or
}

sealed class Value {
    data class Int(val num: kotlin.Int) : Value()
    data class Bool(val bool: Boolean) : Value()
    data class Closure(val env: Env, val binder: String, val body: Expr) : Value()
}

fun eval(env: Env, expr: Expr): Value {
    return when(expr) {
        is Expr.BoolLiteral -> Value.Bool(expr.bool)
        is Expr.IntLiteral -> Value.Int(expr.num)
        is Expr.If -> {
            val condition = eval(env, expr.condition)
            if (condition is Value.Bool) {
                if (condition.bool) eval(env, expr.thenBranch) else eval(env, expr.elseBranch)
            } else {
                throw Exception("If condition is of invalid type.")
            }
        }
        is Expr.Var -> {
            env[expr.name] ?: throw Exception("${expr.name} not defined in environment")
        }
        is Expr.Lambda -> {
            Value.Closure(env, expr.binder, expr.body)
        }
        is Expr.App -> {
            val func = eval(env, expr.func)
            if (func !is Value.Closure) {
                throw Exception("Invalid value.")
            }
            val arg = eval(env, expr.arg)
            val newEnv = func.env.put(func.binder, arg)
            eval(newEnv, func.body)
        }
        is Expr.Binary -> {
            val left = eval(env, expr.left)
            val right = eval(env, expr.right)
            numericBinary(left, right, nameForOp(expr.op)) { x, y -> applyOp(expr.op, x, y) }
        }
        is Expr.Let -> {
            val extendedEnv = env.put(expr.binder, eval(env, expr.expr))
            eval(extendedEnv, expr.body)
        }
    }
}

fun applyOp(op: Operator, x: Value, y: Value): Value {
    return when (op) {
        Operator.Add -> {
            if (x is Value.Int && y is Value.Int) Value.Int(x.num + y.num)
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
        Operator.Subtract -> {
            if (x is Value.Int && y is Value.Int) Value.Int(x.num - y.num)
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
        Operator.Multiply -> {
            if (x is Value.Int && y is Value.Int) Value.Int(x.num * y.num)
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
        Operator.Divide -> {
            if (x is Value.Int && y is Value.Int) Value.Int(x.num / y.num)
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
        Operator.Equality -> {
            if (x is Value.Int && y is Value.Int) Value.Bool(x == y)
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
        Operator.Power -> {
            if (x is Value.Int && y is Value.Int) Value.Int(x.num.toDouble().pow(y.num.toDouble()).toInt())
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
        Operator.And -> {
            if (x is Value.Bool && y is Value.Bool) Value.Bool(x.bool && y.bool)
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
        Operator.Or -> {
            if (x is Value.Bool && y is Value.Bool) Value.Bool(x.bool || y.bool)
            else throw Exception(applyOpExceptionMsg(op, x, y))
        }
    }
}

fun applyOpExceptionMsg(op: Operator, x: Value, y: Value): String =
    "Binary consisting of invalid types ${x.javaClass} and ${y.javaClass} for operation ${nameForOp(op)}."

fun nameForOp(op: Operator): String {
    return when (op) {
        Operator.Add -> "add"
        Operator.Subtract -> "subtract"
        Operator.Multiply -> "multiply"
        Operator.Divide -> "divide"
        Operator.Equality -> "compare"
        Operator.Power -> "power"
        Operator.And -> "and"
        Operator.Or -> "or"
    }
}

fun numericBinary(left: Value, right: Value, operation: String, combine: (Value, Value) -> Value): Value {
    return combine(left, right)
}

val emptyEnv: Env = persistentHashMapOf()
val x = Expr.Var("x")
val y = Expr.Var("y")
val v = Expr.Var("v")
val f = Expr.Var("f")

val innerZ = Expr.Lambda("v", Expr.App(Expr.App(x, x), v))
val innerZ1 = Expr.Lambda("x", Expr.App(f, innerZ))
val z = Expr.Lambda("f", Expr.App(innerZ1, innerZ1))

fun main() {

}