package functions

fun <A, B, C> compose(f: (A) -> B, g: (B) -> C): (A) -> C = { a: A -> g(f(a)) }

infix fun <A, B, C> ((A) -> B).andThen(that: (B) -> C): (A) -> C = compose(this, that)

infix fun <A, B> (A).andThen(that: (A) -> B): B = that(this)

fun <A, B, C> swapArg(f: (A) -> (B) -> C): (B) -> (A) -> C = { b -> { a -> f(a)(b) } }