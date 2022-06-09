package pretty.expr

import Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.*

data class BoolLiteral(

    val content: Expr.BoolLiteral

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(Line(f(LineMode.SINGLE).regularIndent, parseBoolLiteral(content)))
        )
    }
}