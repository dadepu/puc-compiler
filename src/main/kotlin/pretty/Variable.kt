package pretty

data class Variable(

    override val indent: Int,

    val content: Expr.Var

) :Printable {

    override val flatLength: Int
        get() = parseVariable(content).length

    override val isMultiline: Boolean
        get() = false

    override val output: List<Line>
        get() = listOf(Line(indent, parseVariable(content)))

    override fun modifyIndentRelative(x: Int): Printable {
        return this.copy(indent = indent + x)
    }
}