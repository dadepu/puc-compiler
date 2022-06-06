package pretty

import Expr

data class Conditional(

    override val indent: Int,

    val content: Expr.If

) : Printable {

    override val flatLength: Int
        get() = flattenOutput(concatLines)(singleLineOutput).length

    override val isMultiline: Boolean
        get() {
            val getExpr = parseExpr(indent)
            val trueExpr = getExpr(content.thenBranch)
            val elseExpr = getExpr(content.elseBranch)
            return trueExpr.isMultiline || elseExpr.isMultiline || flatLength > 50 // TODO("replace with config && pay attention to indentations")
        }

    override fun modifyIndentRelative(x: Int): Printable {
        return this.copy(indent = indent + x)
    }

    override val output: List<Line>
        get() = if (!isMultiline) singleLineOutput else multiLineOutput

    private val singleLineOutput: List<Line>
        get() {
            val flatOutput = flattenOutput(concatLines)
            val condition = flatOutput(parseExpr(0)(content.condition).output)
            val thenOutput = flatOutput(parseExpr(0)(content.thenBranch).output)
            val elseOutput = flatOutput(parseExpr(0)(content.elseBranch).output)
            return listOf(Line(indent, "if $condition then $thenOutput else $elseOutput"))
        }

    private val multiLineOutput: List<Line>
        get() {
            val condition = flattenOutput(concatLines)(parseExpr(0)(content.condition).output)
            val thenOutput = parseExpr(indent + 1)(content.thenBranch).output
            val elseOutput = parseExpr(indent + 1)(content.elseBranch).output
            return listOf(
                listOf(Line(indent, "if $condition then")),
                thenOutput,
                listOf(Line(indent, "else")),
                elseOutput
            ).flatten()
        }
}