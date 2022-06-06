package pretty

data class LetBinding(

    override val indent: Int,

    val content: Expr.Let

) : Printable {

    private val exprExpr: (Int) -> Printable = swapArg(parseExpr)(content.expr)

    private val exprBody: (Int) -> Printable = swapArg(parseExpr)(content.body)

    private val appendInIdentifier: (Line) -> Line = { x -> x.copy(content = x.content + " in") }


    private val fixedCharLenSingleLine: Int = "let ${content.binder} =  in".length

    private val fixedCharLenFirstMultiLine: Int = "let ${content.binder} = ".length


    override val flatLength: Int
        get() = output[0].content.length

    override val isMultiline: Boolean
        get() = exprExpr(indent).isMultiline

    override fun modifyIndentRelative(x: Int): Printable =
        this.copy(indent = indent + x)

    override val output: List<Line>
        get() = sameLineMultiLineOutput

    private val sameLineMultiLineOutput: List<Line>
        get() {
            val expr = exprExpr(indent).output
            val body = exprBody(indent).output

            if (exprExpr(indent).isMultiline) {
                val firstLine = Line(indent, "let ${content.binder} = ${expr[0].content}")
                val middleLines: List<Line> = expr.drop(1).dropLast(1)
                val lastLine: Line = expr.last() andThen appendInIdentifier

                return listOf(listOf(firstLine), middleLines, listOf(lastLine), body).flatten()
            } else {
                return listOf(listOf(Line(indent, "let ${content.binder} = ${expr[0].content} in")), body).flatten()
            }
        }
}