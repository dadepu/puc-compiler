package pretty

import Lexer
import Parser
import pretty.functions.parseExpr
import pretty.functions.parseIndent

fun main() {
    val one = "if 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 == 16 then 3 + 3 + 3 + 3 + 3 + 3 else 2 + 2 + 2 + 2 + 2"

    val two = "\\y -> \\x -> x + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10 + 11 + 12"

    testInput("""
        let x = \x -> 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 in
        let y = \y -> 2 + 2 in
        let z = \z -> 
            let i = 3 in
            z + i in
        3
    """.trimIndent())
}

private fun testInput(input: String) {
    val parser = Parser(Lexer(input))
    val expr = parser.parseExpression()
//    println(expr)
    parseExpr(expr).generateOutput { _ -> Format(true) }
        .second
        .forEach { println( parseIndent(it.indent) + it.content)}
}



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