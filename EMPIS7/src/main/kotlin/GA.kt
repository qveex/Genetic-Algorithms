import java.io.File
import kotlin.random.Random

const val VARIANT = "#14:\n-cos(x1) * cos(x2) * e^(-((x1-pi)^2 + (x2-pi)^2))\n"
const val FROM = -100.0
const val TO = 100.0
const val INIT_POPULATION_SIZE = 100
const val MAX_GENERATION_COUNT = 100
const val RESULT_FILE = "result.txt"

class GA {

    private val particles = MutableList(INIT_POPULATION_SIZE) {
        Particle(
            position = Position(
                x1 = Random.nextDouble(FROM, TO),
                x2 = Random.nextDouble(FROM, TO)
            ),
            speed = mutableMapOf("x1" to Random.nextDouble(FROM, TO), "x2" to Random.nextDouble(FROM, TO))
        )
    }

    private var globalBest = Position(particles.best.x1, particles.best.x2)

    private val MutableList<Particle>.best get() = minBy { it.localBest.fitness }

    private fun correctPosition() {
        particles.forEach {
            it.correctPosition()
        }
    }

    private fun correctSpeed() {
        particles.forEach {
            it.correctSpeed(globalBest)
        }
    }

    private fun calcGlobal() {
        val b = particles.best
        if (globalBest.fitness > b.localBest.fitness) globalBest = b.position.copy()
    }

    fun run() {
        println(VARIANT)
        File(RESULT_FILE).delete()
        particles.savePopulation()

        val begin = System.currentTimeMillis()
        particles.apply {
            repeat(MAX_GENERATION_COUNT) {
                correctPosition()
                correctSpeed()
                calcGlobal()
                println("global = $globalBest \t fitness = ${globalBest.fitness}")
            }
        }
        val end = System.currentTimeMillis()
        println("\nTime spent: ${end - begin} ms")

        particles.savePopulation()
        //Runtime.getRuntime().exec("python3 script.py")
    }

    private fun MutableList<Particle>.savePopulation() {
        forEach {
            File(RESULT_FILE).appendText("${it.localBest.x1}\t${it.localBest.x2}\t${it.localBest.fitness}\n")
        }
        File(RESULT_FILE).appendText("\n")
    }
}