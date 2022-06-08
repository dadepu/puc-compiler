package pretty.expr

import Expr
import pretty.*
import pretty.functions.*
import pretty.utilities.config


/*
    A conditional has three different print modes.

    (1) A conditional will preferably try to print its contents into a single line.

        Eg.: if ... then ... else ...

    (2) When the line length exceeds all available characters, or when one or both branches are multiline,
            but the condition is still single line and fits into the first line, then the condition is printed as
            follows.

        Eg.: if ... then
                ...
            else
                ...

    (3) Otherwise, the condition is spread over multiple lines as well.

        Eg.: if ...
                ...
            then
                ...
            else
                ...
 */
data class Conditional(

    val content: Expr.If

) : Printable {

    private val exprCond: Printable = parseExpr(content.condition)

    private val exprTrue: Printable = parseExpr(content.thenBranch)

    private val exprFalse: Printable = parseExpr(content.elseBranch)


    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        val parentSingleFormat = f(LineMode.SINGLE)
        val parentMultiFormat = f(LineMode.MULTI)

        val updatedCondFormats = enrichFormat(Pair(enrichSingleLineFormat, enrichCondMultiLineFormat)) (f)
        val updatedBranchFormats = enrichFormat(Pair(enrichSingleLineFormat, enrichBranchMultiLineFormat)) (f)

        val cond = exprCond.generateOutput(updatedCondFormats)
        val trueBranch = exprTrue.generateOutput(updatedBranchFormats)
        val falseBranch = exprFalse.generateOutput(updatedBranchFormats)

        return if (printsAllInSingleLine(parentSingleFormat) (Triple(cond, trueBranch, falseBranch))) {
            Pair(LineMode.SINGLE, makeSingleLineOutput(parentSingleFormat) (
                Triple(cond.second.first(), trueBranch.second.first(), falseBranch.second.first())
            ))
        } else if (printsCondInSingleLine(parentSingleFormat) (cond)) {
            Pair(LineMode.MULTI, makeBodyMultiLineOutput(parentMultiFormat) (
                Triple(cond.second.first(), trueBranch.second, falseBranch.second)
            ))
        } else {
            Pair(LineMode.MULTI, makeFullMultiLineOutput(parentMultiFormat) (
                Triple(cond.second, trueBranch.second, falseBranch.second)
            ))
        }
    }

    private val makeSingleLineOutput: (Format) -> (Triple<Line, Line, Line>) -> List<Line>
        get() = { format -> { childContents ->
            println("makeSingleLineOutput")
            listOf(
                Line(format.regularIndent, singleLineFullConditional(Triple(
                        childContents.first.content, childContents.second.content, childContents.third.content)
                    )
                )
            )
        }}

    private val makeBodyMultiLineOutput: (Format) -> (Triple<Line, List<Line>, List<Line>>) -> List<Line>
        get() = { format -> { childContents ->
            println("makeBodyMultiLineOutput")
            val firstLine = listOf(Line(format.regularIndent, firstLineSingleLineCondition(childContents.first.content)))
            val elseLine = listOf(Line(format.regularIndent, multiLineElseToken()))

            firstLine + childContents.second + elseLine + childContents.third
        }}

    private val makeFullMultiLineOutput: (Format) -> (Triple<List<Line>, List<Line>, List<Line>>) -> List<Line>
        get() = { format -> { childContents ->
            println("makeFullMultiLineOutput")
            val firstConditionLine = listOf(
                Line(format.regularIndent, firstLineMultiLineCondition(
                    childContents.first.first().content)
                ))
            val otherConditionLines = separateFirstLine(childContents.first).second
            val thenLine = listOf(Line(format.regularIndent, multiLineThenToken()))
            val thenBranch = childContents.second
            val elseLine = listOf(Line(format.regularIndent, multiLineElseToken()))
            val elseBranch = childContents.third

            firstConditionLine+ otherConditionLines + thenLine + thenBranch + elseLine + elseBranch
        }}

    private val enrichSingleLineFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = true,
                firstLineReservedChars =+ firstLineSingleLineCondition("").length,
                regularIndent = format.regularIndent + 1
            )
        }

    private val enrichCondMultiLineFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = true,
                firstLineReservedChars =+ firstLineMultiLineCondition("").length,
                regularIndent = format.regularIndent + 1
            )
        }

    private val enrichBranchMultiLineFormat: (Format) -> Format
        get() = { format ->
            format.copy(
                continuesFirstLine = false,
                firstLineReservedChars = 0,
                firstLineReservedIndent = 0,
                regularIndent = format.regularIndent + 1
            )
        }

    private val printsAllInSingleLine: (Format) -> (Triple<Pair<LineMode, List<Line>>, Pair<LineMode, List<Line>>, Pair<LineMode, List<Line>>>) -> Boolean
        get() = { format -> { childContent ->
            val parsedToLine = singleLineFullConditional(Triple(
                childContent.first.second.first().content,
                childContent.second.second.first().content,
                childContent.third.second.first().content
            ))
            val occupiedChar = if (format.continuesFirstLine) {
                config.lineWrap - calcRemainingUnoccupiedChars(format)
            } else {
                calcIndentSpace(format.regularIndent)
            }

            childContent.first.first == LineMode.SINGLE
                    && childContent.second.first == LineMode.SINGLE
                    && childContent.third.first == LineMode.SINGLE
                    && fitsLine(occupiedChar) (parsedToLine)
        }}

    private val printsCondInSingleLine: (Format) -> (Pair<LineMode, List<Line>>) -> Boolean
        get() = { format -> { condition ->
            condition.first == LineMode.SINGLE
                    && fitsLine(calcIndentSpace(format.regularIndent)) (condition.second.first().content)
        }}

    private val fitsLine: (Int) -> (String) -> Boolean
        get() = { occupied -> { condition ->
            condition.length + occupied <= config.lineWrap
        }}

    private val singleLineFullConditional: (Triple<String, String, String>) -> String
        get() = { triple ->
            "if ${triple.first} then ${triple.second} else ${triple.third}"
        }

    private val firstLineSingleLineCondition: (String) -> String
        get() = { condition ->
            "if $condition then"
        }

    private val firstLineMultiLineCondition: (String) -> String
        get() = { condition ->
            "if $condition"
        }

    private val multiLineElseToken: () -> String
        get() = { "else" }

    private val multiLineThenToken: () -> String
        get() = { "then" }
}