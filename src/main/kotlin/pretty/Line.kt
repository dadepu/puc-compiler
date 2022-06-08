package pretty

data class Line(

    /*
        Specifies how many indentations are leading the first character.
     */
    val indent: Int,

    /*
        Print-ready output.
     */
    val content: String
)
