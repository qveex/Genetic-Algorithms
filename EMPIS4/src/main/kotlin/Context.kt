import kotlin.math.pow

object Context {

    /*val targetFunction: (Double, Double) -> Double = { x1, x2 ->
        (1 + (x1 + x2 + 1).pow(2) * (19 - 14 * x1 + 3 * x1.pow(2) - 14 * x2 + 6 * x1 * x2 + 3 * x2.pow(2))) *
        (30 + (2 * x1 - 3 * x2).pow(2) * (18 - 32 * x1 + 12 * x1.pow(2) + 48 * x2 - 36 * x1 * x2 + 27 * x2.pow(2)))
    }*/
    val targetFunction: (Map<String, Double>) -> Double = { vars ->
        (1 + (vars["x1"]!! + vars["x2"]!! + 1).pow(2) * (19 - 14 * vars["x1"]!! + 3 * vars["x1"]!!.pow(2) - 14 * vars["x2"]!! + 6 * vars["x1"]!! * vars["x2"]!! + 3 * vars["x2"]!!.pow(2))) *
        (30 + (2 * vars["x1"]!! - 3 * vars["x2"]!!).pow(2) * (18 - 32 * vars["x1"]!! + 12 * vars["x1"]!!.pow(2) + 48 * vars["x2"]!! - 36 * vars["x1"]!! * vars["x2"]!! + 27 * vars["x2"]!!.pow(2)))
    }

    /*val targetFunction: (Map<String, Double>) -> Double = { vars ->
        vars.keys.sumOf { x -> -vars[x]!! * sin(sqrt(abs(vars[x]!!))) }
    }*/

    val constants = listOf(
        1.0, 1.0, 2.0, 19.0, 14.0, 3.0, 2.0,
        14.0, 6.0, 3.0, 2.0, 30.0, 2.0, 18.0,
        32.0, 12.0, 2.0, 48.0, 36.0, 27.0, 2.0
    )

    val vars = mutableMapOf(
        "x1" to 0.0,
        "x2" to 0.0,
        /*"x3" to 0.0,
        "x4" to 0.0,
        "x5" to 0.0,
        "x6" to 0.0,
        "x7" to 0.0,
        "x8" to 0.0,
        "x9" to 0.0,
        "x10" to 0.0,*/
    )
}