package pretty.expr

import compiler.Expr
import compiler.ReadType
import functions.andThen
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.utilities.config

data class Read(

    val content: Expr.Read

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(Line(f(LineMode.SINGLE).regularIndent,
                StringBuilder()
                    .append("read(")
                    .append((readTypeToString andThen colorReadType)(content.type))
                    .append(")")
                .toString()
            ))
        )
    }

    private val colorReadType: (String) -> String
        get() = { s -> "${config.colorReadType}${s}${config.colorReset}"}

    private val readTypeToString: (ReadType) -> String
        get() = { readType -> when(readType) {
            ReadType.Int -> "Int"
            ReadType.String -> "String"
        } }
}