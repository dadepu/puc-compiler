package pretty

import Expr

data class Lambda(

    override val indent: Int,

    val content: Expr.Lambda

) : Printable {

    private val exprBody: (Int) -> Printable = swapArg(parseExpr)(content.body)

    private val nextIndent: Int
        get() = indent + 1

    override val flatLength: Int
        get() = flattenOutput(concatLines)(singleLineOutput).length

    override val isMultiline: Boolean
        get() = exprBody(indent).isMultiline || flatLength > 60

    override fun modifyIndentRelative(x: Int): Printable =
        this.copy(indent = indent + x)

    override val output: List<Line>
        get() {
            return if (!isMultiline)
                singleLineOutput
            else if (content.body is Expr.Lambda)
                sameLineMultiLineOutput
            else
                multiLineOutput
        }


    /*

        \x -> \y ->
            ....


     */

    private val singleLineOutput: List<Line>
        get() {
            val exprContent = flattenOutput(concatLines)(exprBody(indent).output)
            val output = "( \\${content.binder} -> $exprContent )"
            return listOf(Line(indent, output))
        }

    private val sameLineMultiLineOutput: List<Line>
        get() {
            val exprBody = exprBody(nextIndent).output
            val firstLine = Line(indent, "( \\${content.binder} -> ${exprBody[0].content}")
            val middleLines: List<Line> = exprBody.drop(1).dropLast(1)
            val lastLine: Line = exprBody.last() andThen appendParentheses

            return listOf(listOf(firstLine), middleLines, listOf(lastLine)).flatten()
        }

    private val multiLineOutput: List<Line>
        get() {
            val exprBody = exprBody(nextIndent).output
            val firstLine = Line(indent, "( \\${content.binder} ->")
            val middleLines: List<Line> = exprBody.dropLast(1)
            val lastLine: Line = exprBody.last() andThen appendParentheses

            return listOf(listOf(firstLine), middleLines, listOf(lastLine)).flatten()
        }

    private val appendParentheses: (Line) -> Line = { x -> x.copy(content = x.content + " )") }
}