package pretty

data class Config(

    /*
        Specifies how many whitespaces make up an indentation.
    */
    val colorReset: String = ConsoleColor.RESET,

    val colorAppLRParent: String = ConsoleColor.RED + ConsoleColor.BLUE_BACKGROUND,

    val colorLet: String = ConsoleColor.RED_UNDERLINED,
    val colorIn: String = ConsoleColor.RED_BOLD_BRIGHT,
    val colorLetBinder: String = ConsoleColor.PURPLE,

    val colorLambdaBackslash: String = ConsoleColor.BLUE,
    val colorLambdaBinder: String = ConsoleColor.PURPLE,
    val colorLambdaArrow: String = ConsoleColor.RED,
    val colorLambdaLRParent: String = ConsoleColor.CYAN_BRIGHT,

    val colorIf: String = ConsoleColor.YELLOW_BOLD_BRIGHT,
    val colorThen: String = ConsoleColor.YELLOW_BOLD_BRIGHT,
    val colorElse: String = ConsoleColor.YELLOW_BOLD_BRIGHT,

    val colorBoolLiteral: String = ConsoleColor.YELLOW_BRIGHT,
    val colorIntLiteral: String = ConsoleColor.CYAN,
    val colorVariable: String = ConsoleColor.PURPLE_UNDERLINED,

    val colorOrOperator: String = ConsoleColor.GREEN,
    val colorAndOperator: String = ConsoleColor.BLUE_BOLD + ConsoleColor.RED_BACKGROUND,
    val colorEqualityOperator: String = ConsoleColor.GREEN,
    val colorAddOperator: String = ConsoleColor.GREEN,
    val colorSubtractOperator: String = ConsoleColor.GREEN,
    val colorMultiplyOperator: String = ConsoleColor.GREEN,
    val colorDivideOperator: String = ConsoleColor.GREEN,
    val colorPowerOperator: String = ConsoleColor.GREEN,


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