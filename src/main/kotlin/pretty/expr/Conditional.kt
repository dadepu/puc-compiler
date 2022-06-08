package pretty.expr

import Expr
import pretty.*
import pretty.functions.*
import pretty.utilities.config

data class Conditional(

    val content: Expr.If

) : Printable {

    private val exprCond: Printable = parseExpr(content.condition)

    private val exprTrue: Printable = parseExpr(content.thenBranch)

    private val exprFalse: Printable = parseExpr(content.elseBranch)


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
    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        val firstLineIndent = f(LineMode.SINGLE).firstLineReservedIndent
        val firstLineChars = f(LineMode.SINGLE).firstLineReservedChars + "if  then  else ".length
        val regularIndent = f(LineMode.MULTI).regularIndent + 1
        val condFormat: (LineMode) -> Format = { mode ->
            when(mode) {
                LineMode.SINGLE -> Format(true, firstLineIndent, firstLineChars, regularIndent)
                LineMode.MULTI -> Format(true, firstLineIndent, firstLineChars, regularIndent)
            }
        }
        val branchFormat: (LineMode) -> Format = { mode ->
            when(mode) {
                LineMode.SINGLE -> Format(true, firstLineIndent, firstLineChars, regularIndent)
                LineMode.MULTI -> Format(false, 0, 0, regularIndent)
            }
        }
        val cond = exprCond.generateOutput(condFormat)
        val trueBranch = exprTrue.generateOutput(branchFormat)
        val falseBranch = exprFalse.generateOutput(branchFormat)

        return if (fitsSingleLine(f(LineMode.SINGLE), cond, trueBranch, falseBranch)) {
            Pair(
                LineMode.SINGLE,
                listOf(generateSingleLine(cond.second.first(), trueBranch.second.first(), falseBranch.second.first(), f(
                    LineMode.SINGLE
                )))
            )
        } else if (fitsSingleLine(f(LineMode.SINGLE), cond)) {
            Pair(
                LineMode.MULTI,
                generateBodyMultiLine(cond.second.first(), trueBranch.second, falseBranch.second, f(LineMode.MULTI))
            )
        } else {
            Pair(
                LineMode.MULTI,
                generateFullMultiLine(cond.second, trueBranch.second, falseBranch.second, f(LineMode.MULTI))
            )
        }
    }

    //TODO pay attention to continues first line boolean!

    private fun generateSingleLine(cond: Line, thenBody: Line, elseBody: Line, parentFormat: Format): Line {
        return Line(parentFormat.firstLineReservedIndent, "if ${cond.content} then ${thenBody.content} else ${elseBody.content}")
    }

    private fun generateBodyMultiLine(cond: Line, thenBody: List<Line>, elseBody: List<Line>, parentFormat: Format): List<Line> {
        return listOf(
            listOf(Line(parentFormat.regularIndent, "if ${cond.content} then")),
            thenBody,
            listOf(Line(parentFormat.regularIndent, "else")),
            elseBody
        ).flatten()
    }

    private fun generateFullMultiLine(cond: List<Line>, thenBody: List<Line>, elseBody: List<Line>, parentFormat: Format): List<Line> {
        val first: (List<Line>) -> Line = { list -> list.first() }
        val exceptFirst: (List<Line>) -> List<Line> = { list -> list.filterIndexed { index, _ -> index != 0 } }
        return listOf(
            listOf(Line(parentFormat.regularIndent, "if ${first(cond).content}")),
            exceptFirst(cond),
            listOf(Line(parentFormat.regularIndent, "then")),
            thenBody,
            listOf(Line(parentFormat.regularIndent, "else")),
            elseBody
        ).flatten()
    }

    private fun fitsSingleLine(parentFormat: Format, cond: Pair<LineMode, List<Line>>, trueBranch: Pair<LineMode, List<Line>>,
                               falseBranch: Pair<LineMode, List<Line>>): Boolean {
        return cond.first == LineMode.SINGLE
                && trueBranch.first == LineMode.SINGLE
                && falseBranch.first == LineMode.SINGLE
                && parentFormat.firstLineReservedIndent * config.indentSize + parentFormat.firstLineReservedChars + "if ${cond.second.first().content} then ${trueBranch.second.first().content} else ${falseBranch.second.first().content}".length <= config.lineWrap
    }

    private fun fitsSingleLine(parentFormat: Format, cond: Pair<LineMode, List<Line>>): Boolean {
        return cond.first == LineMode.SINGLE
                && parentFormat.firstLineReservedIndent * config.indentSize + parentFormat.firstLineReservedChars + "if ${cond.second.first().content} then".length <= config.lineWrap
    }
}