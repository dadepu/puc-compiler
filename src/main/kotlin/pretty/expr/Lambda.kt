package pretty.expr

import Expr
import functions.andThen
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.*
import pretty.utilities.config


/*
    (1) A Lambda will preferably print its children in the same line:

    Eg.: ( \x -> 3 + ... )

    (2) If the children's content spans multiple lines, distinctions are made regarding the
        children's type. If the children is a lambda as well, an attempt is made to output at
        least its binder in the same line:

    Eg.: ( \x -> ( \y ->
            ...) )

    (3) If that isn't possible, the output continues in the next line:

    Eg.: ( \x ->
            ...
            ... )

    Therefore, three different output formats exist for a lambda expression.
 */
data class Lambda(

    val content: Expr.Lambda

) : Printable {

    private val exprBinder: String = content.binder

    private val exprBody: Printable = parseExpr(content.body)

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        val parentSingleFormat = f(LineMode.SINGLE)
        val parentMultiFormat = f(LineMode.MULTI)
        val newSingleFormat = chooseSingleLineFormat(content.body)
        val newMultiFormat = chooseMultiLineFormat(content.body)
        val childContent = exprBody.generateOutput(enrichFormat(Pair(newSingleFormat, newMultiFormat)) (f))

        return if (printsInSingleLine(parentSingleFormat) (childContent)) {
            Pair(
                LineMode.SINGLE,
                listOf((getFirstLine andThen sameLineOutput(parentSingleFormat)) (childContent.second)
                )
            )
        } else {
            Pair(
                LineMode.MULTI,
                nextLineMultiLineOutput(parentMultiFormat) (childContent.second)
            )
        }
    }


    /*
        Merges with the child's first and only line.

        eg.: ( \x -> x + ...... )
     */
    private val sameLineOutput: (Format) -> (Line) -> Line
        get() = { format -> { line ->
            Line(format.regularIndent, mergeFirstLineContent(line.content))
        }}

    /*
        Merges with the child's first line. In order to work, make sure the first
            line fits in size.

        eg.: ( \x -> \y ->
                ...
                ... )
     */
    private val sameLineMultiLineOutput: (Format) -> (Pair<Line, List<Line>>) -> List<Line>
        get() = { format -> { pair ->
            wrapLinesInParentheses(
                listOf(Line(format.regularIndent, mergeContent(pair.first.content))) + pair.second
            )
        }}

    /*
        Appends the whole child's content.

        eg.: ( \x ->
                ...
                ... )
     */
    private val nextLineMultiLineOutput: (Format) -> (List<Line>) -> List<Line>
        get() = { format -> { lines ->
            wrapLinesInParentheses(
                listOf(Line(format.regularIndent, mergeContent(""))) + lines
            )
        }}


    /*
        TODO
        Chooses which multiline format should be selected.
        There are two types of multiline formats. One which continues in the same line and the other which continues in
            the consecutive line. The decision is made based on the parent's format and the child's expression-type.

        For example, a child-lambda should continue in the same line with its binding, whereas a binary or conditional
            should always continue in the next consecutive line.
     */
    private val chooseSingleLineFormat: (Expr) -> (Format) -> Format
        get() = { expr -> { parentSingleFormat ->
            when (expr) {
                is Expr.Lambda -> enrichSingleFormatContinued(parentSingleFormat)
                else -> enrichSingleFormat(parentSingleFormat)
            }
        }}


    private val chooseMultiLineFormat: (Expr) -> (Format) -> Format
        get() = { expr -> { parentMultiFormat ->
            when(expr) {
                is Expr.Lambda -> enrichMultiFormatContinued(parentMultiFormat)
                else -> enrichMultiFormat(parentMultiFormat)
            }
        }}

    /*
        Extends the received parent's single line format.
        Therefore, a child's content should continue in the first line and characters occupied by this lambda are added
            to the reserved characters.
     */
    private val enrichSingleFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = true,
                firstLineReservedChars = format.firstLineReservedChars + removeColor(mergeFirstLineContent("")).length,
                regularIndent = format.regularIndent + 1
            )
        }

    private val enrichSingleFormatContinued: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = true,
                firstLineReservedChars = format.firstLineReservedChars + removeColor(mergeFirstLineContent("")).length,
                regularIndent = format.regularIndent
            )
        }

    /*
        Defines a format in which the child's content should continue in the first line in a multiline output.
        Therefore, this format should only be used if this effect is desired. Otherwise, the default multiline format
            should be used which continues in the consecutive line.
     */
    private val enrichMultiFormatContinued: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = false,
                firstLineReservedIndent = 0,
                firstLineReservedChars = 0,
                regularIndent = format.regularIndent
            )
        }

    /*
        Extends the multiline format which orders children to output their content in the next consecutive line instead
            of the first line.
     */
    private val enrichMultiFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = false,
                firstLineReservedIndent = 0,
                firstLineReservedChars = 0,
                regularIndent = format.regularIndent + 1
            )
        }

    /*
        Specific types should continue in the same line with a fraction of their content. This fraction is its first
            line. One of those types is Lambda. If a child-lambda does not fit fully in the same line, an attempt is
            made to fit at least its binding.
     */
    private val childShouldContinueLine: () -> Boolean
        get() = {
            when(content.body) {
                is Expr.Lambda  -> true
                else            -> false
            }
        }

    /*
        Validates if the child's content is single-line and fits fully in a single line.
     */
    private val printsInSingleLine: (Format) -> (Pair<LineMode, List<Line>>) -> Boolean
        get() = { format -> { childContent ->
            if (childContent.first == LineMode.MULTI) {
                false
            } else {
                fitsInSingleLine(Pair(format, getFirstLine(childContent.second)))
            }
        }}

    private val fitsInSingleLine: (Pair<Format, Line>) -> Boolean
        get() = { pair ->
            removeColor(mergeFirstLineContent(pair.second.content)).length <= calcRemainingUnoccupiedChars(pair.first)
        }

    private val wrapStringInParentheses: (String) -> String
        get() = { s -> if (config.lambda.wrappedInParentheses) config.colorLambdaLRParent + "(" + config.colorReset + " " + s  + " " + config.colorLambdaLRParent + ")" + config.colorReset else s }

    private val wrapLinesInParentheses: (List<Line>) -> List<Line>
        get() = { lines ->
            if (config.lambda.wrappedInParentheses) {
                (prependTokenToFirstLine(config.colorLambdaLRParent + "("  + config.colorReset + " ") andThen appendTokenToLastLine(" " + config.colorLambdaLRParent + ")" + config.colorReset))(lines)
            } else {
                lines
            }
        }

    private val mergeFirstLineContent: (String) -> String
        get() = { content ->
            wrapStringInParentheses(mergeContent(content))
        }

    private val mergeContent: (String) -> String
        get() = { content ->
            "\\${exprBinder} ${config.lambda.connectionArrow} $content"
            config.colorLambdaBackslash + "\\" + config.colorReset +  config.colorLambdaBinder + exprBinder + config.colorReset + " " + config.colorLambdaArrow +  config.lambda.connectionArrow + config.colorReset + " "  + content
        }
}