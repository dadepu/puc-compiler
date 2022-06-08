package pretty.expr

import Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.*

data class Variable(

    val content: Expr.Var

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(generateSingleLineOutput(f))
        )
    }

    private fun generateSingleLineOutput(f: (LineMode) -> Format): Line {
        return Line(f(LineMode.SINGLE).regularIndent, parseVariable(content))
    }
}