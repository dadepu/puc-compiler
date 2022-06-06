package pretty

data class App(

    override val indent: Int,

    val content: Expr.App

) : Printable {

    override val flatLength: Int
        get() = flattenOutput(firstLine)(singleLineOutput).length

    override val isMultiline: Boolean
        get() = false

    override fun modifyIndentRelative(x: Int): Printable {
        return this.copy(indent = indent + 1)
    }

    override val output: List<Line>
        get() = singleLineOutput

    // add 3
    private val singleLineOutput: List<Line>
        get() {
            val func = flattenOutput(concatLines)(parseExpr(0)(content.func).output)
            val arg = flattenOutput(concatLines)(parseExpr(0)(content.arg).output)
            return listOf(Line(indent, "$func $arg"))
        }

    // ... add \x ->
    //     3 + 3
    private val sameLineMultiLineOutput: List<Line>
        get() = TODO()

    // ... add
    //     \x -> 3 + 3
    private val multiLineOutput: List<Line>
        get() = TODO()
}