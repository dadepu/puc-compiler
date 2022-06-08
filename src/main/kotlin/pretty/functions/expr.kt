package pretty.functions

import Expr
import Operator
import pretty.*
import pretty.expr.*
import pretty.utilities.config


val parseExpr: (Expr) -> Printable = { expr ->
    when (expr) {
        is Expr.App         -> App(expr)
        is Expr.Let         -> LetBinding(expr)
        is Expr.Lambda      -> Lambda(expr)
        is Expr.If          -> Conditional(expr)
        is Expr.Binary      -> Binary(expr)
        is Expr.IntLiteral  -> IntLiteral(expr)
        is Expr.BoolLiteral -> BoolLiteral(expr)
        is Expr.Var         -> Variable(expr)
    }
}

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

val parseIntLiteral: (Expr.IntLiteral) -> String
    get() = { x -> x.num.toString() }

val parseBoolLiteral: (Expr.BoolLiteral) -> String
    get() = { x -> x.bool.toString() }

val parseVariable: (Expr.Var) -> String
    get() = { x -> x.name }

val parseIndent: (Int) -> String
    get() = { x -> IntRange(0, (x * 2) - 1).fold("") { acc, _ -> "$acc " } }

val separateFirstLine: (List<Line>) -> Pair<Line, List<Line>>
    get() = { lines ->
        Pair(
            getFirstLine(lines),
            if (lines.size > 1) lines.subList(1, lines.size) else listOf())
    }

val getFirstLine: (List<Line>) -> Line
    get() = { lines -> lines.first() }

val calcIndentSpace: (Int) -> Int
    get() = { indents -> indents * config.indentSize }

/*
    Extends the format-decision-function with updated formats.
 */
val enrichFormat: (Pair<(Format) -> Format, (Format) -> Format>) -> ((LineMode) -> Format) -> ((LineMode) -> Format)
    get() = { transform -> { f -> { mode ->
        when (mode) {
            LineMode.SINGLE -> transform.first(f(LineMode.SINGLE))
            LineMode.MULTI -> transform.second(f(LineMode.MULTI))
        }
    }}}

/*
    Calculates based on the passed format how many unoccupied characters remain in the line.
 */
val calcRemainingUnoccupiedChars: (Format) -> Int
    get() = { format ->
        if (!format.continuesFirstLine) {
            config.lineWrap
        } else {
            config.lineWrap
                .minus(calcIndentSpace(format.firstLineReservedIndent))
                .minus(format.firstLineReservedChars)
        }
    }

val prependTokenToFirstLine: (String) -> (List<Line>) -> List<Line>
    get() = { token -> { lines ->
        val firstLine = lines.first()
        val followingLines = if (lines.size > 1) lines.subList(1, lines.size) else listOf()
        listOf(firstLine.copy(content = "$token${firstLine.content}")) + followingLines
    }}

val appendTokenToLastLine: (String) -> (List<Line>) -> List<Line>
    get() = { token -> { lines ->
        val lastLine = lines.last()
        val previousLines = if (lines.size > 1) lines.subList(0, lines.size - 1) else listOf()
        previousLines + listOf(lastLine.copy(content = "${lastLine.content}$token"))
    }}