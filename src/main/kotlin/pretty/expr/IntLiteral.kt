package pretty.expr

import pretty.*
import pretty.functions.*

data class IntLiteral(

    val content: Expr.IntLiteral

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(generateSingleLineOutput(f))
        )
    }

    private fun generateSingleLineOutput(f: (LineMode) -> Format): Line {
        return Line(f(LineMode.SINGLE).regularIndent, parseIntLiteral(content))
    }
}