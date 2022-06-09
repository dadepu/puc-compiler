package pretty

/*
    An output has two types. Either the whole output fits in a single line or
        it's spread over multiple lines.
 */
enum class LineMode {

    SINGLE,
    MULTI
}