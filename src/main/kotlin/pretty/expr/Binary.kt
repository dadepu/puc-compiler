package pretty.expr

import Expr
import functions.*
import pretty.*
import pretty.functions.*
import pretty.utilities.config

data class Binary(

    val content: Expr.Binary

) : Printable {

    override fun generateOutput(f: (LineMode) -> Format): Pair<LineMode, List<Line>> {
        return if (!isMultiLine(f))
            Pair(LineMode.SINGLE, listOf(generateSingleLineOutput(f)))
        else
            Pair(LineMode.MULTI, generateMultiLineOutput(f))
    }

    private val parseBinary: (Expr) -> List<Pair<ContentType, String>>
        get() = { expr ->
            when(expr) {
                is Expr.Binary      ->
                    parseBinary(expr.left) + listOf(Pair(ContentType.OP, parseOp(expr.op))) + parseBinary(expr.right)
                is Expr.IntLiteral  ->
                    listOf(Pair(ContentType.LIT, parseIntLiteral(expr)))
                is Expr.BoolLiteral ->
                    listOf(Pair(ContentType.LIT, parseBoolLiteral(expr)))
                is Expr.Var         ->
                    listOf(Pair(ContentType.LIT, parseVariable(expr)))
                else -> throw Exception("Found undefined type ${expr.javaClass.simpleName} while parsing binary-bottom.")
            }
        }

    private val mapToString: (List<Pair<ContentType, String>>) -> List<String>
        get() = { x -> x.map { y -> y.second } }

    private val singleLineLength: Int
        get() = (parseBinary andThen mapToString andThen parseToString)(content).length

    private fun isMultiLine(f: (LineMode) -> Format): Boolean {
        val indentLength = f(LineMode.SINGLE).firstLineReservedIndent * config.indentSize
        val reservedLength = f(LineMode.SINGLE).firstLineReservedChars
        val remainingLength: (Int) -> Int = { lineLength -> lineLength - indentLength - reservedLength }
        return singleLineLength > remainingLength(config.lineWrap)
    }

    private fun generateSingleLineOutput(f: (LineMode) -> Format): Line {
        return Line(f(LineMode.SINGLE).regularIndent, (parseBinary andThen mapToString andThen parseToString)(content))
    }

    private fun generateMultiLineOutput(f: (LineMode) -> Format): List<Line> {
        val indent = f(LineMode.MULTI).regularIndent
        val indexToIndentation: (Int) -> Int = { index -> if (index == 0) indent else indent + 1 }
        return formatToLines (listOf()) (parseBinary(content)) (f(LineMode.MULTI))
            .mapIndexed { index, s -> Line(indexToIndentation(index), s) }
    }

    private enum class ContentType {
        OP,
        LIT
    }

    private val formatToLines: (List<String>) -> (List<Pair<ContentType, String>>) -> (Format) -> List<String>
        get() = { output -> { remaining -> { format ->
            if (remaining.isEmpty()) {
                output
            } else if (output.isEmpty()) {
                val newOutput = listOf(remaining.first().second)
                val newRemaining = remaining.drop(1)
                formatToLines(newOutput)(newRemaining) (format)
            } else {
                val op = remaining[0].second
                val lit = remaining[1].second
                val newRemaining = remaining.drop(2)
                val newOutput = appendContent(output) (op) (lit) (format)
                formatToLines(newOutput)(newRemaining) (format)
            }
        }}}

    private val appendContent: (List<String>) -> (String) -> (String) -> (Format) -> List<String>
        get() = { existingContent -> { op -> { lit -> { format ->
            val lastLine = existingContent.last()
            val fitsInLastLine = fitsLine(existingContent.size - 1) ("$lastLine $op $lit") (format)
            if (fitsInLastLine) {
                existingContent.dropLast(1) + listOf("$lastLine $op $lit")
            } else {
                existingContent + listOf("$op $lit")
            }
        }}}}

    private val fitsLine: (Int) -> (String) -> (Format) -> Boolean
        get() = { index -> { appendedString -> { format ->
            config.lineWrap - occupiedChar(index) (format) > appendedString.length
        }}}

    private val occupiedChar: (Int) -> (Format) -> Int
        get() = { lineIndex -> { format ->
            if (lineIndex == 0) {
                format.firstLineReservedIndent * config.indentSize + format.firstLineReservedChars
            } else {
                format.regularIndent * config.indentSize
            }
        }}

    private val parseToString: (List<String>) -> String
        get() = { x -> x.fold("") { acc, s -> if (acc.isBlank()) s else "$acc $s"} }
}