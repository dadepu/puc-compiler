package pretty

data class Config(
    /*
        Specifies how many whitespaces make up an indentation.
     */
    val indentSize: Int = 4,

    /*
        Specifies at which point lines are to be wrapped. Including indentations.
     */
    val lineWrap: Int = 50,

    /*
        Lambda-specific Configurations.
     */
    val lambda: LambdaConfig = LambdaConfig(
        wrappedInParentheses = true
    )

) {
    data class LambdaConfig(

        /*
            Specifies wether lambdas are wrapped in parentheses.
            Eg.: \x -> x + 1, ( \x -> x + 1 )
         */
        val wrappedInParentheses: Boolean,

        /*
            Specifies the connection arrow for lambdas.
         */
        val connectionArrow: String = "->"
    )
}