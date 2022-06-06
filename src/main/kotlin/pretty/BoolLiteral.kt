package pretty

data class BoolLiteral(

    override val indent: Int,

    val content: Expr.BoolLiteral

) :Printable {

    override val flatLength: Int
        get() = parseBoolLiteral(content).length

    override val isMultiline: Boolean
        get() = false

    override val output: List<Line>
        get() = listOf(Line(indent, parseBoolLiteral(content)))

    override fun modifyIndentRelative(x: Int): Printable {
        return this.copy(indent = indent + x)
    }
}