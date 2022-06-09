package pretty.expr

import Expr
import pretty.Format
import pretty.Line
import pretty.LineMode
import pretty.Printable
import pretty.functions.parseExpr

/*
    Possible App occurrences:

    # Top Level

        (1)     let add = \x -> \y -> x + y
                add (1) (2)


    # Embedded

        (1)     \x -> \y -> x + y (1) (2)

        (2)     \x ->
                    \y ->
                        x + y
                (1) (2)

        (3)     \x ->
                    \y ->
                        x + y
                (1 + 1 + 1 ...
                    ...) (2)

        (4)     \x -> \y -> x + y (1 + 1 + 1 ...
                    ...) (2)

        (5)     \x -> \y -> x + y ..
                (1) (2)

        (6)     if \x -> 3 + x (1) == 4 then
                    3
                else
                    \x -> x + 1 (1)

        (7)     if \x -> \y -> x + y (1) (2)
                    == 3
                then
                    \x ->
                        \y ->
                            x + y ...
                    (1) (2 + ...
                        ...)
                else
                    3

        (8)     let a = \x ->
                    \y ->
                        let b = \i -> \j -> j + \z -> z + 1 (1)
                        b (1) (2)
                (3) (4)
 */
data class App(

    val content: Expr.App

) : Printable {

    private val exprFunc: Printable = parseExpr(content.func)

    private val exprArg: Printable = parseExpr(content.arg)

    // TODO
    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return Pair(
            LineMode.SINGLE,
            listOf(
                Line(0, "${exprFunc.generateOutput(f).second.first().content} (${exprArg.generateOutput(f).second.first().content})")
            )
        )
    }
}
