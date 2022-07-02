package pretty.expr

import compiler.Color
import compiler.Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.utilities.config

data class Print(

    val content: Expr.Print

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(Line(f(LineMode.SINGLE).regularIndent, StringBuilder()
                .append("print(${colorString("\"${content.string}\"")}")
                .append("${colorColor(colorToString(content.color))})\"")
                .toString()
            ))
        )
    }

    private val colorString: (String) -> String
        get() = { s -> "${config.colorPrintString}${s}${config.colorReset}" }

    private val colorColor: (String) -> String
        get() = { s -> "${config.colorPrintColor}${s}${config.colorReset}"}

    private val colorToString: (Color?) -> String
        get() = { color -> ":${color?.toString()?.lowercase()}" }
}