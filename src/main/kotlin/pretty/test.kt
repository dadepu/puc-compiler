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



fun main() {

    /*
        let add = \firstNumber => \secondNumber => \thirdNumber =>
            \fourthNumber =>
                firstNumber + secondNumber + thirdNumber + fourthNumber
        in

     let add = \firstNumber -> \secondNumber -> \thirdNumber -> \fourthNumber -> firstNumber + secondNumber + thirdNumber + fourthNumber in
        3

if a == cond then
                add
            else
                sub

    3


     */
    testInput("""     
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
    """.trimIndent())
}

private fun testInput(input: String) {
    val printer = PrettyPrinter()
    printer.format({ input }, { s -> println(s) })
}
