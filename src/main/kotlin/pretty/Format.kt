package pretty

/*
    Contains information that are passed on to the children regarding the
        embedding in the parent's output.
    A parent may wish to attempt to continue the child's output in the same
        line, or continue in a consecutive line.
 */
data class Format(

    /*
        Specifies wether the parent wishes to embed the child's content in a
            line.
        If so, both firstLine* attributes are to be considered, otherwise both
            must(!) be ignored.
     */
    val continuesFirstLine: Boolean,

    /*
        Indicates how many indentations are leading the first line.
        Note: indentations * indentation-size = white-spaces
     */
    val firstLineReservedIndent: Int = 0,

    /*
        Indicates how many characters are already occupied by parents in the line in which is
            to be continued.
        Note: Indent and Chars are disjunctive.
     */
    val firstLineReservedChars: Int = 0,

    /*
        Specifies at which indentation lines are to be continued after the first line. If
            'continuesFirstLine' is false, regularIndent is also used in the first line.
     */
    val regularIndent: Int = 0,

    /*
        Specifies how many characters are occupied in the last line by parents.
     */
    val lastLineReservedChars: Int = 0
)
