package pretty

/*
   let a = \a -> \b ->
        let add = \x -> \y -> x + y in
        let sub = \x -> \y -> x - y in
        let condition = 3 + 3 == 6 in
        if condition then
            add
        else
            sub
        in
    3
 */
fun main() {
    testInput("""      
        let add = \x -> \y -> \z -> x + y + z in
        let x = \y ->
            let mul = \i -> \j -> i * j in
            mul 1 y
        in
        x 3
        
        
    """.trimIndent())
}

private fun testInput(input: String) {
    val printer = PrettyPrinter()
    printer.format({ input }, { s -> println(s) })
}
