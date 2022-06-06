package pretty

import Lexer
import Parser

fun main() {
    testInput("""
        let x = \x -> x + 3 +3+3+3+3+3+3 +3 in
        2
    """.trimIndent())
}

/*
        BUG: flatLength should only consider the length of the first line. the output should not be compressed.
            Hence, the name flatLength is misleading.
*/

/*
        Output should take an argument indicating how many empty spaces are leading the first character.
*/

/*
    APP
    (1)     (func) (arg) (arg) ...
    (2)     (looooooong func) \x->
                ..... (arg) (arg) ...
    (3)     (looooooooooooong func)
                (arg) (arg) ....
 */

/*
    TODO:
        App
        Binary-Types: App
        Check: Different multiline conditions may cause bugs
        Refactor Output: Lambda, LetBinding
        Config: Linebreak (Lambda, Let, If), Indentations
        Multiline include indentations
        Linebreak Binaries
        Verify flatLength works as expected
        Single-Line Comments
        Multi-Line Comments
 */

private fun testInput(input: String) {
    val parser = Parser(Lexer(input))
    val expr = parser.parseExpression()
    println(expr)
    parseExpr(0)(expr).output.forEach { println( parseIndent(it.indent) + it.content)}
}

private fun testSimpleLambda() {
    val lambdaExpr = Lambda(0, Expr.Lambda(
        "x", Expr.Var("x")
    ))
    lambdaExpr.output.forEach { println( parseIndent(it.indent) + it.content)}
}

private fun testSimpleCondition() {
    val conditionalExpr = Conditional(0, Expr.If(
        Expr.Binary(Expr.IntLiteral(3), Operator.Equality, Expr.IntLiteral(3)),
        Expr.IntLiteral(5),
        Expr.IntLiteral(6)
    ))
    conditionalExpr.output.forEach { println( parseIndent(it.indent) + it.content)}
}