package pretty.expr

import Expr
import functions.andThen
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.*


/*
    A LetBinding consists of a binder, an expression that is bound to that expression and a body that tails the Let.
    There are two possible ways to print a let.

    (1) Continue in the same line:

        E.g.:   let x = ...

    (2) Continue in the next line. Normally a let is continued in the same line, unless its content doesn't fit:

        E.g.:   let looooooooongName =
                    ...

    A Let is always multi-line in nature and must always start in a new line. Therefore, the parent's continuesFirstLine
        configuration is ignored. It follows, that the parent must also recognize, that Lets can not be printed
        in the same line.

        E.g.: NOT:  \x -> let z = 3 in
                        x + z

        Instead:
        E.g.:       \x ->
                        let z = 3 in
                        x + z

    The Let's body tails its expression and follows after the 'in'-token in the next line.

        E.g.:    let z = 3 in
                3 + z

    Because of that, the format used to output the body must inherit the let's indentation but must also
        start in a new line.

 */
data class LetBinding(

    val content: Expr.Let

) : Printable {

    private val exprBinder: String = content.binder

    private val exprExpr: Printable = parseExpr(content.expr)

    private val exprBody: Printable = parseExpr(content.body)


    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(LineMode.MULTI ,exprOutput(f) + bodyOutput(f))
    }

    private val exprOutput: ((LineMode) -> Format) -> List<Line>
        get() = { parentFormats ->
            val multiFormat = parentFormats(LineMode.MULTI)
            val childExpr = exprExpr.generateOutput(
                enrichFormat(Pair(enrichSingleExprFormat, enrichMultiExprFormat)) (parentFormats)
            )
            val continuesLine = fitsInSingleLine(Pair(multiFormat, getFirstLine(childExpr.second)))

            if (continuesLine) {
                (separateFirstLine andThen sameLineMultiLineOutput(multiFormat)) (childExpr.second)
            } else {
                nextLineMultiLineOutput(multiFormat) (childExpr.second)
            }
        }

    private val bodyOutput: ((LineMode) -> Format) -> List<Line>
        get() = { parentFormats ->
            val childBody = exprBody.generateOutput(
                enrichFormat(Pair(enrichSingleBodyFormat, enrichMultiBodyFormat)) (parentFormats)
            )
            childBody.second
        }

    private val sameLineMultiLineOutput: (Format) -> (Pair<Line, List<Line>>) -> List<Line>
        get() = { format -> { pair ->
            val exprLines = listOf(Line(format.regularIndent, firstLineContent(pair.first.content))) + pair.second
            appendTokenToLastLine(" in") (exprLines)
        }}

    private val nextLineMultiLineOutput: (Format) -> (List<Line>) -> List<Line>
        get() = { format -> { lines ->
            val exprLines = listOf(Line(format.regularIndent, firstLineContent(null))) + lines
            appendTokenToLastLine(" in") (exprLines)
        }}

    private val enrichSingleExprFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = true,
                firstLineReservedChars = format.firstLineReservedChars + firstLineContent(null).length,
                regularIndent = format.regularIndent + 1
            )
        }

    private val enrichMultiExprFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = true,
                firstLineReservedChars = format.firstLineReservedChars + firstLineContent(null).length,
                regularIndent = format.regularIndent + 1
            )
        }

    private val enrichSingleBodyFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = false,
                firstLineReservedIndent = 0,
                firstLineReservedChars = 0
            )
        }

    private val enrichMultiBodyFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = false,
                firstLineReservedIndent = 0,
                firstLineReservedChars = 0
            )
        }

    private val fitsInSingleLine: (Pair<Format, Line>) -> Boolean
        get() = { pair ->
            firstLineContent(pair.second.content).length <= calcRemainingUnoccupiedChars(pair.first)
        }

    private val firstLineContent: (String?) -> String
        get() = { s ->
            val content = if (s == null) "" else " $s"
            "let $exprBinder =$content"
        }
}