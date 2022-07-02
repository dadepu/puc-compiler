package pretty.functions

import compiler.Expr
import compiler.Operator
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
        is Expr.StringLiteral -> StringLiteral(expr)
        is Expr.Read        -> Read(expr)
        is Expr.Print       -> Print(expr)
    }
}

val parseOp: (Operator) -> String = { op ->
    when (op) {
        Operator.Add            -> config.colorAddOperator + "+" + config.colorReset
        Operator.Subtract       -> config.colorSubtractOperator + "-" + config.colorReset
        Operator.Multiply       -> config.colorMultiplyOperator + "*" + config.colorReset
        Operator.Divide         -> config.colorDivideOperator + "/" + config.colorReset
        Operator.Power          -> config.colorPowerOperator + "^" + config.colorReset
        Operator.Equality       -> config.colorEqualityOperator +"==" + config.colorReset
        Operator.And            -> config.colorAndOperator + "&&" + config.colorReset
        Operator.Or             -> config.colorOrOperator + "||" + config.colorReset
        Operator.Modulo         -> "%" // TODO("COLOR")
        Operator.Inequality     -> "!="
        Operator.GreaterThan    -> ">"
        Operator.GreaterEqualThan -> ">="
        Operator.LessThan       -> "<"
        Operator.LessEqualThan  -> "<="
        Operator.Concat         -> TODO("Type not implemented yet.")
    }
}

val parseIntLiteral: (Expr.IntLiteral) -> String
    get() = { x -> config.colorIntLiteral + x.num.toString() + config.colorReset }

val parseBoolLiteral: (Expr.BoolLiteral) -> String
    get() = { x -> config.colorBoolLiteral + x.bool.toString() + config.colorReset }

val parseVariable: (Expr.Var) -> String
    get() = { x -> config.colorVariable + x.name  + config.colorReset }

val parseIndent: (Int) -> String
    get() = { x -> IntRange(0, (x * config.indentSize) - 1).fold("") { acc, _ -> "$acc " } }

val separateFirstLine: (List<Line>) -> Pair<Line, List<Line>>
    get() = { lines ->
        Pair(
            getFirstLine(lines),
            if (lines.size > 1) lines.subList(1, lines.size) else listOf())
    }

val separateLastLine: (List<Line>) -> Pair<Line, List<Line>>
    get() = { lines ->
        Pair(
            lines.last(),
            if (lines.size > 1) lines.subList(0, lines.size - 1) else listOf()
        )
    }

val getFirstLine: (List<Line>) -> Line
    get() = { lines -> lines.first() }

val calcIndentSpace: (Int) -> Int
    get() = { indents -> indents * config.indentSize }

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

val removeColor: (String) -> String
    get() = { string -> string.replace(Regex("\u001B\\[[0-9];?[0-9]{0,2}m"), "") }