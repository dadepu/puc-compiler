package pretty

data class Config(

    /*
        Specifies in which color tokens are printed.
    */
    val colorReset: String = ConsoleColor.RESET,

    val colorAppLRParent: String = ConsoleColor.RESET,

    val colorLet: String = ConsoleColor.BLUE_BOLD,
    val colorIn: String = ConsoleColor.BLUE_BOLD,
    val colorLetBinder: String = ConsoleColor.RESET,

    val colorLambdaBackslash: String = ConsoleColor.RESET,
    val colorLambdaBinder: String = ConsoleColor.PURPLE,
    val colorLambdaArrow: String = ConsoleColor.RESET,
    val colorLambdaLRParent: String = ConsoleColor.RESET,

    val colorIf: String = ConsoleColor.BLUE_BOLD,
    val colorThen: String = ConsoleColor.BLUE_BOLD,
    val colorElse: String = ConsoleColor.BLUE_BOLD,

    val colorBoolLiteral: String = ConsoleColor.BLUE_BOLD,
    val colorIntLiteral: String = ConsoleColor.BLUE,
    val colorVariable: String = ConsoleColor.PURPLE,

    val colorOrOperator: String = ConsoleColor.RESET,
    val colorAndOperator: String = ConsoleColor.RESET,
    val colorEqualityOperator: String = ConsoleColor.RESET,
    val colorAddOperator: String = ConsoleColor.RESET,
    val colorSubtractOperator: String = ConsoleColor.RESET,
    val colorMultiplyOperator: String = ConsoleColor.RESET,
    val colorDivideOperator: String = ConsoleColor.RESET,
    val colorPowerOperator: String = ConsoleColor.RESET,


    /*
        Specifies how many whitespaces make up an indentation.
     */
    val indentSize: Int = 4,

    /*
        Specifies at which point lines are wrapped. Including indentations.
     */
    val lineWrap: Int = 60,

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
            Eg.: \x -> x + 1 or ( \x -> x + 1 )
         */
        val wrappedInParentheses: Boolean,

        /*
            Specifies the connection arrow.
         */
        val connectionArrow: String = "=>"
    )
}