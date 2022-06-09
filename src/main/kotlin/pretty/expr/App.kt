package pretty.expr

import Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable

data class App(

    val content: Expr.App

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        TODO("Not yet implemented")
    }
}

    //    override val flatLength: Int
//        get() = flattenOutput(firstLine)(singleLineOutput).length
//
//    override val isMultiline: Boolean
//        get() = false
//
//    override fun modifyIndentRelatively(x: Int): Printable {
//        return this.copy(indent = indent + 1)
//    }
//
//    override val output: List<Line>
//        get() = singleLineOutput
//
//    // add 3
//    private val singleLineOutput: List<Line>
//        get() {
//            val func = flattenOutput(concatLines)(parseExpr(0)(content.func).generateOutput)
//            val arg = flattenOutput(concatLines)(parseExpr(0)(content.arg).generateOutput)
//            return listOf(Line(indent, "$func $arg"))
//        }
//
//    // ... add \x ->
//    //     3 + 3
//    private val sameLineMultiLineOutput: List<Line>
//        get() = TODO()
//
//    // ... add
//    //     \x -> 3 + 3
//    private val multiLineOutput: List<Line>
//        get() = TODO()
