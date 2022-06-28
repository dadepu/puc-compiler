package pretty.expr

import pretty.*
import pretty.functions.*
import pretty.utilities.config

data class IntLiteral(

    val content: Expr.IntLiteral

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(Line(f(LineMode.SINGLE).regularIndent, parseIntLiteral(content)))
        )
    }
}