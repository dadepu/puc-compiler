package pretty

import Expr
import Operator

data class Line(val indent: Int, val content: String)

interface Printable {

    val indent: Int

    val flatLength: Int         // without indentations

    val isMultiline: Boolean

    val output: List<Line>

    fun modifyIndentRelative(x: Int): Printable
}

val parseExpr: (Int) -> (Expr) -> Printable = { indent -> { expr ->
    when (expr) {
        is Expr.Binary      -> Binary(indent, expr)
        is Expr.If          -> Conditional(indent, expr)
        is Expr.App         -> App(indent, expr)
        is Expr.BoolLiteral -> BoolLiteral(indent, expr)
        is Expr.IntLiteral  -> IntLiteral(indent, expr)
        is Expr.Lambda      -> Lambda(indent, expr)
        is Expr.Let         -> LetBinding(indent, expr)
        is Expr.Var         -> Variable(indent, expr)
    }
}}

val parseOp: (Operator) -> String = { op ->
    when (op) {
        Operator.Add        -> "+"
        Operator.Subtract   -> "-"
        Operator.Multiply   -> "*"
        Operator.Divide     -> "/"
        Operator.Power      -> "^"
        Operator.Equality   -> "=="
        Operator.And        -> "&&"
        Operator.Or         -> "||"
    }
}

val parseIntLiteral: (Expr.IntLiteral) -> String = { x -> x.num.toString() }

val parseBoolLiteral: (Expr.BoolLiteral) -> String = { x -> x.bool.toString() }

val parseVariable: (Expr.Var) -> String = { x -> x.name }

val parseString: (List<String>) -> String = {x -> x.fold("") { acc, s -> if (acc.isBlank()) s else "$acc $s"} }

val parseIndent: (Int) -> String = { x -> IntRange(0, (x * 2) - 1).fold("") { acc, _ -> "$acc " } }

val concatLines: (String, Line) -> String = { acc, s -> "$acc${s.content}" }

val firstLine: (String, Line) -> String = { acc, s -> if (acc.isBlank()) s.content else acc }

val flattenOutput: ((String, Line) -> String) -> (List<Line>) -> String = { operation -> { list -> list.fold("", operation) }}

fun <A, B, C> compose(f: (A) -> B, g: (B) -> C): (A) -> C = { a: A -> g(f(a)) }

infix fun <A, B, C> ((A) -> B).andThen(that: (B) -> C): (A) -> C = compose(this, that)

infix fun <A, B> (A).andThen(that: (A) -> B): B = that(this)

fun <A, B, C> swapArg(f: (A) -> (B) -> C): (B) -> (A) -> C = { b -> { a -> f(a)(b) } }