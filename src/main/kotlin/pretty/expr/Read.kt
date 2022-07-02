package pretty.expr

import compiler.Expr
import compiler.ReadType
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable

data class Read(

    val content: Expr.Read

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(Line(f(LineMode.SINGLE).regularIndent, "read(${readTypeToString(content.type)})"))
        )
    }

    private val readTypeToString: (ReadType) -> String
        get() = { readType -> when(readType) {
            ReadType.Int -> "Int"
            ReadType.String -> "String"
        } }
}