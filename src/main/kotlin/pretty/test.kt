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

//val s:String =
//    TODO("""
//        # Implement Christoph's newest version,
//        # Implement Strings,
//        # Add color-codes,
//        # Create unformatted sample-programs,
//        # Presentation,
//        Optional:
//            # Embedded App, -Lambda and -IF
//    """)

fun main() {
        testInput("""      
        let a = 3 + 2 in
        let b = 2 in
        let c = (\x => a + b * a / b ^ 2) in
        let d = c 2 in
        if d == b && d == a && true == true then a * a * a * a else b + b + b
    """.trimIndent())

    println("-------------------------------")

    testInput("""      
        (\x => \y => \z => x + y + z ^ x - y * z ^ z - y * z ^ z - y * z ^ z) (1) (2) (3)
    """.trimIndent())

    println("-------------------------------")
    testInput("""      
        if let a = 3 in a == 3 then 1 + 1 + 1 + 1 else 2 + 2 + 2 
    """.trimIndent())

        println("-------------------------------")
    testInput("""      
        let a = 3 in
        let b = 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 in
        a + b + b + b + b + b + b + b + b + b + b + b + b + b + b + b + b + b
    """.trimIndent())


    println("-------------------------------")

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
