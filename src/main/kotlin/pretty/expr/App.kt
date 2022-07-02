package pretty.expr

import compiler.Expr
import functions.andThen
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.*
import pretty.utilities.config

/*

    Possible App occurrences:

    # Top Level

        (1)     let add = \x -> \y -> x + y
                add (1) (2)

        (2)     let add = \x -> \y -> x + y
                add (1) (\x -> x + 1 + 1 ...
                    ... (5))


    # Embedded

        (1)     \x -> \y -> x + y (1) (2)

        (5)     \x -> \y -> x + y ...
                    ... (1) (2)

        (2)     \x ->
                    \y ->
                        x + y (1) (2)

        (3)     \x ->
                    \y ->
                        x + y (1 + 1 + 1 ...
                            ...) (2)

        (6)     if \x -> 3 + x (1) == 4 then
                    3
                else
                    \x -> x + 1 (1)

        (7)     if \x -> \y -> x + y (1) (2)
                    == 3
                then
                    \x ->
                        \y ->
                            x + y (1) (2 + ...
                                ...)
                else
                    3

        (8)     let a = \x ->
                    \y ->
                        let b = \i -> \j -> j + \z -> z + 1 (1)
                        b (1) (2)
                a (3) (4)


    There are two ways in which an App can occur: Top Level and Embedded.

    Embedded:
        The following code will be parsed as follows:
            \x -> x + 3 2

            Lambda [...] Binary(left: x, op: plus, right: App(
                func: 3, arg: 2)

        The App is considered embedded, because the binary-printable is responsible for embedding
            and printing.

    Top-Level:
        Top-level Apps are not bound to a binary. They can occur alone, after a let-expression or
            in conditional-branches.


    This class implements only the top-level variant.

 */
data class App(

    val content: Expr.App

) : Printable {

    private val exprFunc: Printable = parseExpr(content.func)

    private val exprArg: Printable = parseExpr(content.arg)


    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        val func = exprFunc.generateOutput(f)
        val arg = exprArg.generateOutput(formatArgument(func) (f))
        val merged = appendArgument(f) (func) (arg)

        return Pair(lineMode(merged), merged)
    }

    /*
        TODO
     */
    private val formatArgument: (Pair<LineMode, List<Line>>) -> ((LineMode) -> Format) -> ((LineMode) -> Format)
        get() = { func -> { parentFormats ->
            parentFormats // TODO("can be ignored")
        }}

    private val appendArgument: ((LineMode) -> Format) -> (Pair<LineMode, List<Line>>) -> (Pair<LineMode, List<Line>>)
            -> List<Line>
        get() = { formats -> { func -> { arg ->
            val wrappedArg = wrapInParentheses(arg.second)
            val appendFirstToLast: Boolean = argFitsLine(formats) (func.second.size - 1) (func.second.last()) (wrappedArg.first())

            if (appendFirstToLast) {
                val splitFunc = separateLastLine(func.second)
                val splitArg = separateFirstLine(wrappedArg)
                val mergedLine = splitFunc.first.copy(content = "${splitFunc.first.content} ${splitArg.first.content}")
                splitFunc.second + listOf(mergedLine) + splitArg.second
            } else {
                func.second + wrappedArg
            }
        }}}

    private val lineMode: (List<Line>) -> LineMode
        get() = { lines ->
            if (lines.size == 1) LineMode.SINGLE else LineMode.MULTI
        }

    /*
        TODO
     */
    private val argFitsLine: ((LineMode) -> Format) -> (Int) -> (Line) -> (Line) -> Boolean
        get() = { formats -> { lineIndex -> {funcLine -> { argLine ->
            val occupiedChar = 0

            //TODO ("calc occupied chars, otherwise line-wraps will be ignored.")

            removeColor("${funcLine.content} ${argLine.content}").length <= config.lineWrap - occupiedChar
        }}}}

    private val wrapInParentheses: (List<Line>) -> List<Line>
        get() = { lines ->
            (prependTokenToFirstLine(config.colorAppLRParent + "(" + config.colorReset) andThen appendTokenToLastLine(config.colorAppLRParent + ")" + config.colorReset)) (lines)
        }
}
