package pretty

import compiler.Lexer
import compiler.Parser
import pretty.functions.parseExpr
import pretty.functions.parseIndent

class PrettyPrinter {

    fun format(input: () -> String, output: (String) -> Unit) {
        val parser = Parser(Lexer(input()))
        val expr = parser.parseExpression()
        println(expr)

        parseExpr(expr).generateOutput(rootFormat)
            .second
            .forEach { output( parseIndent(it.indent) + it.content) }
    }

    private val rootFormat: (LineMode) -> Format
        get() = { _ -> Format(continuesFirstLine = false) }

}