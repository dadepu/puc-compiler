package pretty

import pretty.functions.parseExpr
import pretty.functions.parseIndent
import v15project_print_v2.Lexer
import v15project_print_v2.Parser

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