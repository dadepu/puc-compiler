package pretty.expr

import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.*
import v15project_print_v2.Expr

data class Variable(

    val content: Expr.Var

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(Line(f(LineMode.SINGLE).regularIndent, parseVariable(content)))
        )
    }
}