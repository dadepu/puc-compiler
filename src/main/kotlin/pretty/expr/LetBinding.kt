package pretty.expr

import Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.enrichFormat
import pretty.functions.parseExpr

data class LetBinding(

    val content: Expr.Let

) : Printable {

    private val exprBinder: String = content.binder

    private val exprExpr: Printable = parseExpr(content.expr)

    private val exprBody: Printable = parseExpr(content.body)


    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(LineMode.MULTI, makeExprOutput(f) + makeBodyOutput(f))
    }

    private val makeExprOutput: ((LineMode) -> Format) -> List<Line>
        get() = { f ->
            val childExprContent = exprExpr.generateOutput(
                enrichFormat(Pair(enrichSingleFormat, enrichMultiFormat)) (f)
            )

            val exprContent = if (true) {
                1
            } else if (true) {
                1
            } else {
                1
            }

            listOf()
        }

    private val makeBodyOutput: ((LineMode) -> Format) -> List<Line>
        get() = TODO()

    private val sameLineOutput: (Format) -> (Line) -> Line = TODO()

    private val sameLineMultiLineOutput: (Format) -> (Pair<Line, List<Line>>) -> List<Line> = TODO()

    private val nextLineMultiLineOutput: (Format) -> (List<Line>) -> List<Line> = TODO()

    private val enrichSingleFormat: (Format) -> Format = TODO()

    private val enrichMultiFormat: (Format) -> Format = TODO()

    private val childPrintsInline: () -> Boolean
        get() = { when(content.body) {
            is Expr.Lambda -> true
            else -> false
        } }

    private val printsInSingleLine: (Pair<LineMode, List<Line>>) -> Boolean = TODO()

    private val calcLineLength: (Format) -> (Pair<LineMode, List<Line>>) -> Int = TODO()

    private val fitsInSingleLine: (Int) -> Boolean = TODO()

    private val appendInIdentifier: (List<Line>) -> List<Line> = TODO()

}

    //    private val exprExpr: (Int) -> Printable = swapArg(parseExpr)(content.expr)
//
//    private val exprBody: (Int) -> Printable = swapArg(parseExpr)(content.body)
//
//    private val appendInIdentifier: (Line) -> Line = { x -> x.copy(content = x.content + " in") }
//
//
//    private val fixedCharLenSingleLine: Int = "let ${content.binder} =  in".length
//
//    private val fixedCharLenFirstMultiLine: Int = "let ${content.binder} = ".length
//
//
//    override val flatLength: Int
//        get() = output[0].content.length
//
//    override val isMultiline: Boolean
//        get() = exprExpr(indent).isMultiLine
//
//    override fun modifyIndentRelatively(x: Int): Printable =
//        this.copy(indent = indent + x)
//
//    override val output: List<Line>
//        get() = sameLineMultiLineOutput
//
//    private val sameLineMultiLineOutput: List<Line>
//        get() {
//            val expr = exprExpr(indent).generateOutput
//            val body = exprBody(indent).generateOutput
//
//            if (exprExpr(indent).isMultiLine) {
//                val firstLine = Line(indent, "let ${content.binder} = ${expr[0].content}")
//                val middleLines: List<Line> = expr.drop(1).dropLast(1)
//                val lastLine: Line = expr.last() andThen appendInIdentifier
//
//                return listOf(listOf(firstLine), middleLines, listOf(lastLine), body).flatten()
//            } else {
//                return listOf(listOf(Line(indent, "let ${content.binder} = ${expr[0].content} in")), body).flatten()
//            }
//        }
