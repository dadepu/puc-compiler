package pretty

import Expr

data class Binary(

    override val indent: Int,

    val content: Expr.Binary

) : Printable {

    override val flatLength: Int
        get() = (parseBinary andThen parseString)(content).length

    override val isMultiline: Boolean
        get() = false

    override fun modifyIndentRelative(x: Int): Printable {
        return this.copy(indent = indent + x)
    }

    override val output: List<Line>
        get() = listOf(Line(indent, (parseBinary andThen parseString)(content)))

    private val parseBinary: (Expr.Binary) -> List<String> = { x -> parseBinary(x) }

    private fun parseBinary(expr: Expr): List<String> {
        if (expr is Expr.Binary)        return parseBinary(expr.left) + listOf(parseOp(expr.op)) + parseBinary(expr.right)
        if (expr is Expr.IntLiteral)    return listOf(parseIntLiteral(expr))
        if (expr is Expr.BoolLiteral)   return listOf(parseBoolLiteral(expr))
        if (expr is Expr.Var)           return listOf(parseVariable(expr))
        throw Exception("Found undefined type ${expr.javaClass.simpleName} while parsing binary-bottom.")
    }
}