package pretty.expr

import compiler.Color
import compiler.Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable

data class Print(

    val content: Expr.Print

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(Line(f(LineMode.SINGLE).regularIndent, "print(\"${content.string}\"${colorToString(content.color)})"))
        )
    }

    private val colorToString: (Color?) -> String
        get() = { color -> ":${color?.toString()?.lowercase()}" }
}