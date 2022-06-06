package substitution

sealed class Expr {
    // Lambda Calculus
    data class Var(val name: String): Expr()
    data class Lambda(val binder: String, val body: Expr): Expr()
    data class App(val func: Expr, val arg: Expr): Expr()

    data class Literal(val num: Int) : Expr()
    data class BoolLiteral(val bool: Boolean): Expr()
    data class Addition(val left: Expr, val right: Expr) : Expr()
    data class If(val condition: Expr, val trueBody: Expr, val falseBody: Expr): Expr()
}

fun substitute(binder: String, arg: Expr, expr: Expr): Expr {
    return when (expr) {
        is Expr.Literal, is Expr.BoolLiteral -> expr
        is Expr.Addition -> Expr.Addition(
            substitute(binder, arg, expr.left),
            substitute(binder, arg, expr.right)
        )
        is Expr.Var -> if (expr.name == binder) arg else expr
        is Expr.Lambda -> if (expr.binder == binder) expr else Expr.Lambda(expr.binder, substitute(binder, arg, expr.body))
        is Expr.App -> Expr.App(substitute(binder, arg, expr.func), substitute(binder, arg, expr.arg))
        is Expr.If -> Expr.If(
            substitute(binder, arg, expr.condition),
            substitute(binder, arg, expr.trueBody),
            substitute(binder, arg, expr.falseBody)
        )
    }
}

fun eval(expr: Expr): Expr {
    return when(expr) {
        is Expr.Addition -> {
            val left = eval(expr.left)
            val right = eval(expr.right)
            if (left is Expr.Literal && right is Expr.Literal) {
                Expr.Literal(left.num + right.num)
            } else {
                Expr.Addition(left, right)
            }
        }
        is Expr.App -> {
            val left = eval(expr.func)
            val right = eval(expr.arg)
            if (left is Expr.Lambda) {
                eval(substitute(left.binder, right, left.body))
            } else {
                Expr.App(left, right)
            }
        }
        is Expr.If -> {
            val condition = eval(expr.condition)
            if (condition is Expr.BoolLiteral) {
                if (condition.bool) eval(expr.trueBody) else eval(expr.falseBody)
            } else {
                Expr.If(condition, eval(expr.trueBody), eval(expr.falseBody))
            }
        }
        is Expr.Var, is Expr.Literal, is Expr.Lambda, is Expr.BoolLiteral -> expr
    }
}



val addOne = Expr.Lambda("x", Expr.Addition(Expr.Var("x"), Expr.Literal(2)))

fun main(args: Array<String>) {
    val x = Expr.If(
        Expr.BoolLiteral(true),
        Expr.Literal(2),
        Expr.Literal(3)
    )
    println(eval(x))
}