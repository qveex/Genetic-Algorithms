import Context.vars

sealed class Function(
    val value: String,
    val isUnary: Boolean = false,
) {
    override fun toString() = value

    object Sum: Function(
        value = "+",
    )

    object Sub: Function(
        value = "-",
    )

    object Mul: Function(
        value = "*",
    )

    object Div: Function(
        value = "/",
    )

    object Abs: Function(
        value = "abs",
        isUnary = true
    )

    object Cos: Function(
        value = "cos",
        isUnary = true
    )

    object Sin: Function(
        value = "sin",
        isUnary = true
    )

    object Exp: Function(
        value = "exp",
        isUnary = true
    )

    object Pow: Function(
        value = "^"
    )

    data class Var(val name: String): Function(
        value = name
    ) {
        override fun toString() = value
        val get get() = vars[value]!!
    }

    data class Const(val const: Double): Function(
        value = const.toString(),
    ) {
        override fun toString() = value
        val get get() = value.toDouble()
    }
}
