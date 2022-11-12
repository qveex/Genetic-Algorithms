import Constants.FROM
import Constants.MAX_DEPTH
import Constants.P_C
import Constants.P_M
import Constants.TO
import Context.vars
import Context.constants
import Context.targetFunction
import kotlin.math.*
import kotlin.random.Random

class Expression() {

    constructor(f: Function): this() {
        current = f
    }
    fun clone(): Expression {
        val cloned = Expression(current!!)
        cloned.left = left?.clone()
        cloned.right = right?.clone()
        return cloned
    }

    private var current: Function? = null
    private var left: Expression? = null
    private var right: Expression? = null

    private val isTerminal get() = left == null && right == null
    private val isNotTerminal get() = !isTerminal

    private val result: Double get() =
        if (isNotTerminal)
            when(current) {
                Function.Sum -> left!!.result + right!!.result
                Function.Sub -> left!!.result - right!!.result
                Function.Mul -> left!!.result * right!!.result
                Function.Div -> with (left!!.result / right!!.result) { if (this.isInfinite()) Double.MAX_VALUE else this }
                Function.Pow -> abs(left!!.result).pow(abs(right!!.result))
                Function.Sin -> sin(left!!.result)
                Function.Cos -> cos(left!!.result)
                Function.Abs -> abs(left!!.result)
                Function.Exp -> exp(left!!.result)
                else -> 0.0
            }
        else
            when(current) {
                is Function.Const -> (current as Function.Const).get
                is Function.Var -> (current as Function.Var).get
                else -> 0.0
            }

    fun generate(depth: Int = 0) {
        if (depth == MAX_DEPTH) {
            current = randomTerminal
        } else {
            if ((0..1).random() == 0 && depth > 2) {
                current = randomTerminal
            } else {
                current = randomFunction
                left = Expression().apply { generate(depth + 1) }
                if (!current!!.isUnary)
                    right = Expression().apply { generate(depth + 1) }
            }
        }
    }

    private fun cut(depth: Int = 0) {
        if (depth > MAX_DEPTH) {
            killChilds()
        } else {
            left?.cut(depth + 1)
            right?.cut(depth + 1)
        }
    }

    private fun killChilds() {
        current = when(current) {
            is Function.Const,
            is Function.Var -> current
            else -> randomTerminal
        }
        left = null
        right = null
    }

    fun mutation() {
        if (Random.nextDouble() <= P_M) {
            when ((0..3).random()) {
                // растущая
                0 -> {
                    randomNode.apply {
                        current = randomFunction
                        left = Expression().apply { generate((1..MAX_DEPTH).random()) }
                        right = Expression().apply { generate((1..MAX_DEPTH).random()) }
                    }
                }
                // обмен
                1 -> {
                    randomNode.apply {
                        if (current!!.isUnary)
                            left = Expression().apply { generate((1..MAX_DEPTH).random()) }
                        else
                            left = right.also { right = left }
                    }
                }
                // подмена корня
                2 -> {
                    val old = this.clone()
                    current = randomFunction
                    left = old
                    right = if (current!!.isUnary) null
                    else Expression().apply { generate(1) }
                }
                // смена функции
                else -> {
                    randomNode.apply {
                        val old = this.current
                        this.current = randomFunction

                        if (old!!.isUnary && !this.current!!.isUnary) {
                            this.right = Expression().apply { current = randomTerminal }
                        }
                        else if (this.current!!.isUnary)
                            this.right = null
                    }
                }
            }
            cut()
        }
    }

    fun crossing(recessive: Expression) {
        if (Random.nextDouble() <= P_C) {
            randomNode.apply {
                val recessiveRandomNode = recessive.randomNode.clone()
                this.current = recessiveRandomNode.current
                this.left = recessiveRandomNode.left
                this.right = recessiveRandomNode.right
            }
            recessive.randomNode.apply {
                val dominantRandomNode = this@Expression.randomNode.clone()
                this.current = dominantRandomNode.current
                this.left = dominantRandomNode.left
                this.right = dominantRandomNode.right
            }
            cut()
            recessive.cut()
        }
    }

    private val randomTerminal get() =  if (Random.nextDouble() > 0.5) randomConst else randomVariable
    private val randomVariable get() = Function.Var(vars.keys.random())
    private val randomConst get() = Function.Const(constants.random())
    private val randomUnary get() = when((0..3).random()) {
        0 -> Function.Abs
        1 -> Function.Cos
        2 -> Function.Sin
        3 -> Function.Exp
        else -> Function.Cos
    }
    private val randomFunction get() = when((0..8).random()) {
        0 -> Function.Sum
        1 -> Function.Sub
        2 -> Function.Mul
        3 -> Function.Div
        4 -> Function.Abs
        5 -> Function.Cos
        6 -> Function.Sin
        7 -> Function.Exp
        8 -> Function.Pow
        else -> Function.Sum
    }
    private val randomNode get(): Expression = when ((0..2).random()) {
        0 -> this
        1 ->
            if (left?.isTerminal != false)
                this
            else
                left!!.randomNode
        2 ->
            if (current!!.isUnary) {
                if (left?.isTerminal != false)
                    this
                else
                    left!!.randomNode
            }
            else {
                if (right?.isTerminal != false)
                    this
                else
                    right!!.randomNode
            }
        else -> this
    }
    val fitness get(): Double {
        var dist = 0.0
        vars.keys.forEach { vars[it] = FROM }
        repeat(4) {
            vars.keys.forEach { vars[it] = vars[it]!! + (abs(FROM) + abs(TO)) / 4 }
            dist += abs(result - targetFunction(vars))
        }
        return dist
    }

    override fun toString() =
        if (current!!.isUnary)
            "$current(${left ?: ""})".trim()
        else
            "${left ?: ""} $current ${right ?: ""}".trim()
}