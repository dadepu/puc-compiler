import kotlinx.collections.immutable.persistentHashMapOf

sealed class Token {

    @Override
    override fun toString(): String {
        return this.javaClass.simpleName
    }

    // Keyword
    object IF : Token()
    object THEN : Token()
    object ELSE : Token()
    object LET : Token()
    object IN : Token()

    // Symbols
    object LPAREN : Token()
    object RPAREN : Token()
    object ARROW : Token()
    object BACKSLASH : Token()
    object EQUALS : Token()

    // Literal
    data class BOOL_LIT(val bool: Boolean) : Token()
    data class INT_LIT(val int: Int) : Token()

    data class IDENT(val ident: String) : Token()

    // Operator
    object PLUS : Token()
    object MINUS : Token()
    object MULTIPLY : Token()
    object DIVIDES : Token()
    object POWER: Token()
    object DOUBLE_EQUALS : Token()
    object AND: Token()
    object OR: Token()

    // Control
    object EOF : Token()
}

class PeekableIterator<T>(val iter: Iterator<T>) {
    var lh: T? = null

    fun peek(): T? {
        lh = next()
        return lh
    }

    fun next(): T? {
        lh?.let { lh = null; return it }
        return if (iter.hasNext()) {
            iter.next()
        } else {
            null
        }
    }
}

class Lexer(val input: String) {

    val iter = PeekableIterator(input.iterator())
    var lh: Token? = null

    fun next(): Token {
        chompWhitespace()
        lh?.let { it -> lh = null; return it }

        return when (val c = iter.next()) {
            null -> Token.EOF
            '(' -> Token.LPAREN
            ')' -> Token.RPAREN
            '+' -> Token.PLUS
            '*' -> Token.MULTIPLY
            '/' -> Token.DIVIDES
            '^' -> Token.POWER
            '\\' -> Token.BACKSLASH
            '-' -> {
                if (iter.peek() == '>') {
                    iter.next()
                    Token.ARROW
                } else {
                    Token.MINUS
                }
            }
            '=' -> {
                if (iter.peek() == '>') {
                    iter.next()
                    Token.ARROW
                } else if (iter.peek() == '=') {
                    iter.next()
                    Token.DOUBLE_EQUALS
                } else {
                    Token.EQUALS
                }
            }
            '&' -> {
                if (iter.peek() == '&') {
                    iter.next()
                    Token.AND
                } else {
                    throw Exception("Missing 2nd & while parsing &&.")
                }
            }
            '|' -> {
                if (iter.peek() == '|') {
                    iter.next()
                    Token.OR
                } else {
                    throw Exception("Missing 2nd | while parsing ||.")
                }
            }
            else -> when {
                c.isJavaIdentifierStart() -> lexIdentifier(c)
                c.isDigit() -> lexInt(c)
                else -> throw Exception("Unexpected $c")
            }
        }
    }

    fun lookahead(): Token {
        lh = next()
        return lh ?: Token.EOF
    }

    private fun lexIdentifier(first: Char): Token {
        var res = first.toString()
        while (iter.peek()?.isJavaIdentifierPart() == true) {
            res += iter.next()
        }
        return when (res) {
            "if" -> Token.IF
            "then" -> Token.THEN
            "else" -> Token.ELSE
            "let" -> Token.LET
            "in" -> Token.IN
            "true" -> Token.BOOL_LIT(true)
            "false" -> Token.BOOL_LIT(false)
            else -> Token.IDENT(res)
        }
    }

    private fun lexInt(first: Char): Token {
        var res = first.toString()
        while (iter.peek()?.isDigit() == true) {
            res += iter.next()
        }
        return Token.INT_LIT(res.toInt())
    }

    private fun chompWhitespace() {
        while (iter.peek()?.isWhitespace() == true) {
            iter.next()
        }
    }
}

class Parser(val lexer: Lexer) {

    fun parseExpression(): Expr {
        return parseBinary(0)
    }

    fun parseBinary(minBindingPower: Int): Expr {
        var lhs = parseApplication()
        while (true) {
            val op = peekOperator() ?: break
            val (leftBp, rightBp) = bindingPowerForOp(op)
            if (minBindingPower > leftBp) break;
            lexer.next()
            val rhs = parseBinary(rightBp)
            lhs = Expr.Binary(lhs, op, rhs)
        }
        return lhs
    }

    fun parseApplication(): Expr {
        var expr = parseAtom() ?: throw Exception("Expected an expression")
        while (true) {
            val arg = parseAtom() ?: break
            expr = Expr.App(expr, arg)
        }
        return expr
    }

    private fun peekOperator(): Operator? {
        return when (lexer.lookahead()) {
            Token.DIVIDES -> Operator.Divide
            Token.DOUBLE_EQUALS -> Operator.Equality
            Token.MINUS -> Operator.Subtract
            Token.MULTIPLY -> Operator.Multiply
            Token.PLUS -> Operator.Add
            Token.POWER -> Operator.Power
            Token.AND -> Operator.And
            Token.OR -> Operator.Or
            else -> null
        }
    }

    private fun bindingPowerForOp(op: Operator): Pair<Int, Int> {
        return when (op) {
            Operator.Or -> 1 to 2
            Operator.And -> 3 to 4
            Operator.Equality -> 6 to 5
            Operator.Add, Operator.Subtract -> 7 to 8
            Operator.Multiply, Operator.Divide -> 9 to 10
            Operator.Power -> 12 to 11
        }
    }

    fun parseAtom(): Expr? {
        return when (lexer.lookahead()) {
            is Token.INT_LIT -> parseInt()
            is Token.BOOL_LIT -> parseBool()
            is Token.BACKSLASH -> parseLambda()
            is Token.IF -> parseIf()
            is Token.IDENT -> parseVar()
            is Token.LET -> parseLet()
            is Token.LPAREN -> {
                expect<Token.LPAREN>("opening paren")
                val inner = parseExpression()
                expect<Token.RPAREN>("closing paren")
                inner
            }
            else -> null
        }
    }

    private fun parseLet(): Expr {
        expect<Token.LET>("let")
        val binder = expect<Token.IDENT>("binder").ident
        expect<Token.EQUALS>("equals")
        val expr = parseExpression()
        expect<Token.IN>("in")
        val body = parseExpression()
        return Expr.Let(binder, expr, body)
    }

    private fun parseVar(): Expr.Var {
        val ident = expect<Token.IDENT>("identifier")
        return Expr.Var(ident.ident)
    }

    private fun parseIf(): Expr.If {
        expect<Token.IF>("if")
        val condition = parseExpression()
        expect<Token.THEN>("then")
        val thenBranch = parseExpression()
        expect<Token.ELSE>("else")
        val elseBranch = parseExpression()
        return Expr.If(condition, thenBranch, elseBranch)
    }

    private fun parseLambda(): Expr.Lambda {
        expect<Token.BACKSLASH>("lambda")
        val binder = expect<Token.IDENT>("binder")
        expect<Token.ARROW>("arrow")
        val body = parseExpression()
        return Expr.Lambda(binder.ident, body)
    }

    private fun parseInt(): Expr.IntLiteral {
        val t = expect<Token.INT_LIT>("integer")
        return Expr.IntLiteral(t.int)
    }

    private fun parseBool(): Expr.BoolLiteral {
        val t = expect<Token.BOOL_LIT>("boolean")
        return Expr.BoolLiteral(t.bool)
    }

    private inline fun <reified T> expect(msg: String): T {
        val tkn = lexer.next()
        return tkn as? T ?: throw Exception("Expected $msg but saw $tkn")
    }
}







fun testLexer(input: String) {
    val lexer = Lexer(input)
    do { println(lexer.next()) } while (lexer.lookahead() != Token.EOF)
}

fun test(input: String) {
    val parser = Parser(Lexer(input))
    val expr = parser.parseExpression()
    print(
        eval(
            persistentHashMapOf(), expr
        )
    )
}

fun testParser(input: String) {
    val parser = Parser(Lexer(input))
    val expr = parser.parseExpression()
    println(expr)
}

fun testPrinter(input: String) {
    val parser = Parser(Lexer(input))
    val expr = parser.parseExpression()
    println(expr)
//    val printer = PrettyPrinter()
//    printer.outputExpr(expr) { x -> println(x) }
}

fun main() {
    testPrinter(
    """
    \x -> x + 1
    """.trimMargin()
    )
}