package pretty.expr

import compiler.Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.utilities.config

data class StringLiteral(

    val content: Expr.StringLiteral

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(
                Line(f(LineMode.SINGLE).regularIndent, colorLiteral("\"${content.string}\"")))
        )
    }

    private val colorLiteral: (String) -> String
        get() = { s -> "${config.colorStringLiteral}${s}${config.colorReset}"}
}