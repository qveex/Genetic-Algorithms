import java.util.*
import kotlin.math.*

data class Particle(
    var position: Position,
    var speed: MutableMap<String, Double>
) {

    companion object {
        private const val c1 = 2.0 // в локально лучшую
        private const val c2 = 5.0 // в глобально лучшую
        private val COMMON_SPEED = (2.0 * 0.3 / abs(2.0 - (c1 + c2) - sqrt((c1 + c2).pow(2) - 4.0 * (c1 + c2))))
    }

    val x1 get() = position.x1
    val x2 get() = position.x2

    private val random = Random()

    var localBest = position.copy()
        private set

    fun correctPosition() {
        position.x1 += speed["x1"]!!
        position.x2 += speed["x2"]!!

        if (x1 !in (FROM..TO)) position.x1 -= speed["x1"]!!
        if (x2 !in (FROM..TO)) position.x2 -= speed["x2"]!!

        if (position.fitness <= localBest.fitness) localBest = position.copy()
    }

    fun correctSpeed(globalBest: Position) {
        val r1 = random.nextGaussian()
        val r2 = random.nextGaussian()

        speed.keys.forEach {
            speed[it] =
                (speed[it]!! * COMMON_SPEED) +
                (c1*r1 * (localBest[it] - position[it]) * COMMON_SPEED) +
                (c2*r2 * (globalBest[it] - position[it]) * COMMON_SPEED)
        }
    }
}

data class Position(var x1: Double, var x2: Double) {
    operator fun get(x: String) = map[x]!!
    override fun toString() = "($x1, $x2)"
    private val map get() = mapOf("x1" to x1, "x2" to x2)
    val fitness get() = -cos(x1) * cos(x2) * exp(-((x1 - PI).pow(2) + (x2 - PI).pow(2)))
}