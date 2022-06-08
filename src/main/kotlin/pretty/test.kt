package pretty


/*
    let a = 3 + 3 in
    let b = \x -> if x == 2 then 5 else 10 in
    let c = \x ->
        if x == 2 then
            \y -> 3
        else
            \y -> 2 in

    let d = \format -> \lines -> \continuesInSameLine -> \isMultiLine ->
        \any -> \anymore ->
            let add = \one -> \two -> \three ->
                one + two + three in
            if true then
                \x -> \y -> x + y
            else
                \x -> \y -> x * y in
    3
 */

/*
    # Parent-let breaks line before child-condition does.

        Recreate:   (1) 'let b = if 3 + 3 + 3 + 3 + 3 + 3 + 3 == 9 then 3 else 2 in'

                    (2) 'let d = if 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 == 9 then 3 else 2 in'

 */


fun main() {

    testInput("""
        let a = if true then 1 else 2 in
        
        let b = if 3 + 3 + 3 + 3 + 3 + 3 + 3 == 9 then 3 else 2 in
        
        let c = if 3 + 3 + 3 + 3 + 3 == 9 then 3 + 3 + 3 else 2 in
        
        let d = if 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 + 3 == 9 then 3 else 2 in
        
        1
    """.trimIndent())
}

private fun testInput(input: String) {
    val printer = PrettyPrinter()
    printer.format({ input }, { s -> println(s) })
}
