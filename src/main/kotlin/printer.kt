data class Line(val indent: Int, val out: String)
data class Element(val out: String)


class Pretty {

    private val modifyIndent: (List<Line>) -> (Int) -> List<Line> = { x -> { y ->
        x.map { Line(it.indent + y, it.out) }
    }}

    private val parseIntLiteral: (Expr.IntLiteral) -> Element = { x -> Element(x.num.toString()) }

    private val parseBoolLiteral: (Expr.BoolLiteral) -> Element = { x -> Element(x.bool.toString()) }

    private val parseVar: (Expr.Var) -> Element = { x -> Element(x.name) }

    private val concatElements: (String, Element) -> String = { acc, s -> acc + if (acc.isNotEmpty()) " ${s.out}" else s.out }

    private val parseIndent: (Int) -> String = { x -> IntRange(0, x - 1).fold("") { acc, _ -> "$acc " } }

    private val parseLine: ((Int) -> String) -> (Line) -> String = { p -> { x -> p(x.indent) + x.out} }
}


/*
    Example

    let add = \x -> \y -> x + y in
    let mul = \x -> \y -> x * y in

    if 3 == 3 && 2 == 1 || true then
        add 3 2
    else
        mul 3 2

    let z = \f => (\x => f \v => x x v) (\x => f \v => x x v) in
    let fib = \self => \x ->
        if x == 0 then
            0
        else if x == 1 then
            1
        else
            self (x - 1) + self (x - 2) in
    z fib 10



    IF:
        MULTI-LINE
            if {expr.binary} then
                {expr}
            else
                {expr}

        IF ELSE IF:
            if {expr.binary} then
                {expr}
            else
                if {expr.binary} then
                    ...
                else
                    ...

        SINGLE LINE:        -> if total size < ??
            if {expr.binary} then {expr} else {expr}


    APP:
        SINGLE LINE:
            {expr.app.func} {arg} {arg} {arg} ...

        MULTI LINE:         -> if expr is multiline or size > ??
            \x ->
                ...
            {arg} {arg} {arg}

    LET
        SINGLE LINE:
            let x = \x -> x + 1 in

        MULTI LINE          -> if expr is multiline or size > ??
            let x = \x -> \y ->
                {expr}

    LAMBDA
        SINGLE LINE:
            \x -> {expr}

        MULTI LINE          -> if expr is multiline or size > ??
            \x ->
                {expr}

    BINARY
        SINGLE LINE:
            3 == 3 && 3 + 2 == x || y = x

        MULTI LINE:
            3 ==



    1. requires multiline
    2. get length
    3. get collapsed, get, get multiline


    return List<Line>

    scenarios:
        (1) printable as single line?
                -> requires multiline?
                -> what is die total length?
                    -> calc total length
        (2) get content as lines
                -> raise indent by 1

    Line 0: if {expr.toString} then
    Line 1:     ...
    Line 0: else
    Line 1:     ...

 */