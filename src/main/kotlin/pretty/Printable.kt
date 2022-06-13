package pretty

interface Printable {

    /*
        Requires a function that returns a Format for each possible LineMode. This way parents
            can pass on information which are required by the child in order to know how the output
            is to be formatted and embedded.

        Returns a pair that contains the LineMode and content. Regarding the LineMode, if the
            whole content fits a single line, LineMode is SINGLE. That includes the children's
            children as well.
     */
    fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>>
}