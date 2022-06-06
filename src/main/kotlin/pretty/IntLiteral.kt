package pretty

data class IntLiteral(

    override val indent: Int,

    val content: Expr.IntLiteral

) : Printable {

    override val flatLength: Int
        get() = parseIntLiteral(content).length

    override val isMultiline: Boolean
        get() = false

    override val output: List<Line>
        get() = listOf(Line(indent, parseIntLiteral(content)))

    override fun modifyIndentRelative(x: Int): Printable {
        return this.copy(indent = indent + x)
    }
}