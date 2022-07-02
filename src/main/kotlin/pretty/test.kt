package pretty

/*
        - let recursive 'rec'
        - print
        - read
            - read(Int)
            - read(String)
        - strings
        - text-highlighting
 */

fun main() {
        testInput("""      
        let a = 3 + 2 in
        let b = 2 in
        let c = (\x => a + b * a / b ^ 2) in
        let d = c 2 in
        if d == b && d == a && true == true then a * a * a * a else b + b + b
    """.trimIndent())
//
//    println("-------------------------------")
//
//    testInput("""
//        (\x => \y => \z => x + y + z ^ x - y * z ^ z - y * z ^ z - y * z ^ z) (1) (2) (3)
//    """.trimIndent())
//
//    println("-------------------------------")
//    testInput("""
//        if let a = 3 in a == 3 then 1 + 1 + 1 + 1 else 2 + 2 + 2
//    """.trimIndent())
//
//        println("-------------------------------")
//    testInput("""
//        let a = 3 in
//        let b = 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 in
//        a + b + b + b + b + b + b + b + b + b + b + b + b + b + b + b + b + b
//    """.trimIndent())
//
//
//    println("-------------------------------")
//
//    testInput("""
//    let a = (\a -> \b -> \c -> \d -> \e -> a + b + c + d + e) in
//    a 3
//    """.trimIndent())
}

private fun testInput(input: String) {
    val printer = PrettyPrinter()
    printer.format({ input }, { s -> println(s) })
}
