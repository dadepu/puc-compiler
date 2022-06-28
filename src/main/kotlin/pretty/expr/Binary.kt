package pretty.expr

import Expr
import functions.*
import pretty.*
import pretty.functions.*
import pretty.utilities.config

/*
    Binaries are chained by nesting.

        E.g.: Binary(Binary(Int, Op, Int), Op, Binary(Int, Op, Int))

    Once a Binary is encountered while printing, all embedded binaries are resolved and parsed into
        strings. Therefore, the top level Binary of a nested set of binaries is responsible for
        the whole structure.

    There are two possible outputs:

        (1) As single-line:

                E.g.: 3 + 3 + 3

        (2) Over multiple lines:

                E.g.: 3 + 3 + ....
                        + 3 ...
                        + ...

            Outputs over multiple lines must always (!) start with an operator.
 */
data class Binary(

    val content: Expr.Binary

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        val singleFormat = f(LineMode.SINGLE)
        val multiFormat = f(LineMode.MULTI)
        val parsedBinary = parseBinary(content)

        return if (printsInSingleLine(singleFormat) (parsedBinary)) {
            Pair(LineMode.SINGLE, listOf(generateSingleLineOutput(singleFormat)))
        } else {
            Pair(LineMode.MULTI, generateMultiLineOutput(multiFormat))
        }
    }

    /*
        Recursively resolves as nested structure of Binaries into a list of strings. A distinction is made
            between operators and literals.

        TODO: add Lambda, App, If
     */
    private val parseBinary: (Expr) -> List<Pair<ContentType, String>>
        get() = { expr ->
            when(expr) {
                is Expr.Binary      ->
                    parseBinary(expr.left) + listOf(Pair(ContentType.OP, parseOp(expr.op))) + parseBinary(expr.right)
                is Expr.IntLiteral  ->
                    listOf(Pair(ContentType.LIT, parseIntLiteral(expr)))
                is Expr.BoolLiteral ->
                    listOf(Pair(ContentType.LIT, parseBoolLiteral(expr)))
                is Expr.Var         ->
                    listOf(Pair(ContentType.LIT, parseVariable(expr)))

                else -> throw Exception("Found undefined type ${expr.javaClass.simpleName} while parsing binary-bottom.")
            }
        }

    /*
        Outputs into a single line.

            E.g.:   3 + 3 + 3 + 3 + ...
     */
    private val generateSingleLineOutput: (Format) -> Line
        get() = { format ->
            Line(
                format.regularIndent,
                (parseBinary andThen extractStrings andThen parseToString) (content)
            )
        }

    /*
        Outputs over multiple lines.

            E.g.:   ... 3 + 3 + 3
                        + 3 + 3 ...
                        + ...
     */
    private val generateMultiLineOutput: (Format) -> List<Line>
        get() = { format ->
            val indent = format.regularIndent
            val indexToIndentation: (Int) -> Int = { index -> if (index == 0) indent else indent + 1 }

            formatToLines (listOf()) (parseBinary(content)) (format)
                .mapIndexed { index, s -> Line(indexToIndentation(index), s) }
        }

    /*
        Checks if it's possible to print the whole binary structure into a single line given the
            already occupied characters by parents.
     */
    private val printsInSingleLine: (Format) -> (List<Pair<ContentType, String>>) -> Boolean
        get() = { format -> { parsedBinary ->
            val parsed = (extractStrings andThen parseToString)(parsedBinary)
            fitsFirstLine(Pair(format, parsed))
        }}

    private val fitsFirstLine: (Pair<Format, String>) -> Boolean
        get() = { pair ->
            removeColor(pair.second).length <= calcRemainingUnoccupiedChars(pair.first)
        }

    private val parseToString: (List<String>) -> String
        get() = { x -> x.fold("") { acc, s -> if (acc.isBlank()) s else "$acc $s"} }

    private val extractStrings: (List<Pair<ContentType, String>>) -> List<String>
        get() = { x -> x.map { y -> y.second } }

    /*
        Recursively formats all elements into a list of formatted lines, respecting the parent's
            formatting boundaries.

        It is assumed, that the very first element on the first call of this function is an indent.
            After that, operators and indents must be alternately in order.

            E.g.:   3 + 2 == 4 + 1

        When the output exceeds a single line, the consecutive line starts with an operator.

            E.g.:   3 + 3 + ...
                    + 3 + 3 ...
     */
    private val formatToLines: (List<String>) -> (List<Pair<ContentType, String>>) -> (Format) -> List<String>
        get() = { output -> { remaining -> { format ->
            if (remaining.isEmpty()) {
                output
            } else if (output.isEmpty()) {
                val newOutput = listOf(remaining.first().second)
                val newRemaining = remaining.drop(1)
                formatToLines(newOutput)(newRemaining) (format)
            } else {
                val op = remaining[0].second
                val lit = remaining[1].second
                val newRemaining = remaining.drop(2)
                val newOutput = appendContent(output) (op) (lit) (format)
                formatToLines(newOutput)(newRemaining) (format)
            }
        }}}

    /*
        Appends an operator and an indent to the last line in a given list of lines. If the
            line size is exceeded, a new line is started leading by an operator.

            E.g.:   3 + 3 + ...
                    + 3 ...
     */
    private val appendContent: (List<String>) -> (String) -> (String) -> (Format) -> List<String>
        get() = { existingContent -> { op -> { lit -> { format ->
            val lastLine = existingContent.last()
            val fitsLastLine = fitsLine(existingContent.size - 1) ("$lastLine $op $lit") (format)

            if (fitsLastLine) {
                existingContent.dropLast(1) + listOf("$lastLine $op $lit")
            } else {
                existingContent + listOf("$op $lit")
            }
        }}}}

    /*
        Validates if the concatenated string is below the maximum allowed size in length.
        Occupied characters by parents are taken into consideration.
     */
    private val fitsLine: (Int) -> (String) -> (Format) -> Boolean
        get() = { lineIndex -> { appendedString -> { format ->
            config.lineWrap - occupiedCharsInLine(lineIndex) (format) > removeColor(appendedString).length
        }}}

    /*
        Calculates how many characters for a given line-index are already occupied by parents.

        Occupied characters may only exist in the first line.
     */
    private val occupiedCharsInLine: (Int) -> (Format) -> Int
        get() = { lineIndex -> { format ->
            if (lineIndex == 0) {
                if (format.continuesFirstLine) {
                    calcIndentSpace(format.firstLineReservedIndent) + format.firstLineReservedChars
                } else {
                    calcIndentSpace(format.regularIndent)
                }
            } else {
                calcIndentSpace(format.regularIndent)
            }
        }}

    private enum class ContentType {
        OP,
        LIT
    }
}