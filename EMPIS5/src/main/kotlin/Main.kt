import java.io.File
import kotlin.math.*
import kotlin.random.Random

const val VARIANT = "#14:\n-cos(x1) * cos(x2) * e^(-((x1-pi)^2 + (x2-pi)^2))\n"
const val IS_MAX_SEARCHING = false
const val FROM = -100.0
const val TO = 100.0
const val DEFAULT_DEVIANCE = 0.1
const val INIT_POPULATION_SIZE = 100
const val MAX_GENERATION_COUNT = 100
const val RESULT_FILE = "result.txt"


fun main() {
    File(RESULT_FILE).delete()
    println(VARIANT)
    val population = mutableListOf<Individual>()
    val begin = System.currentTimeMillis()
    population.apply {
        initPopulation()
        savePopulation()

        println("\nextreme fitness before = $extremeFitness\n")
        evolution()
        println("\nextreme fitness after = $extremeFitness\n")
        savePopulation()
    }
    val end = System.currentTimeMillis()
    println("\nTime spent: ${end - begin} ms")

    //Runtime.getRuntime().exec("python3 script.py")
}


fun MutableList<Individual>.initPopulation() {
    repeat(INIT_POPULATION_SIZE) {
        add(
            Individual(
                Random.nextDouble(FROM, TO),
                Random.nextDouble(FROM, TO),
                DEFAULT_DEVIANCE
            )
        )
    }
}

fun MutableList<Individual>.evolution() {
    var counter = 0
    while (++counter < MAX_GENERATION_COUNT) {
        mutation()
        selection()
        printBest()
    }
    println("GENERATION COUNT = $counter")
}

fun MutableList<Individual>.selection() {
    sortByDescending { it.mutations }
    if (IS_MAX_SEARCHING)
        sortByDescending { it.fitness }
    else
        sortBy { it.fitness }

    val newPopulation = subList(0, INIT_POPULATION_SIZE).toList()
    clear()
    addAll(newPopulation)
}

fun MutableList<Individual>.mutation() {
    repeat(INIT_POPULATION_SIZE) {
        add(get(it).mutate())
    }
}

val MutableList<Individual>.extremeFitness
    get() = if (IS_MAX_SEARCHING) maxFitness else minFitness
val MutableList<Individual>.extremeIndividual
    get() = if (IS_MAX_SEARCHING) maxBy { it.fitness } else minBy { it.fitness }
val MutableList<Individual>.maxFitness get() = this.maxOf { it.fitness }
val MutableList<Individual>.minFitness get() = this.minOf { it.fitness }
fun MutableList<Individual>.printBest() {
    with(extremeIndividual) { println("${fitness}\t${x1}\t${x2}") }
}

fun MutableList<Individual>.savePopulation() {
    forEach {
        File(RESULT_FILE).appendText("${it.x1}\t${it.x2}\t${it.fitness}\n")
    }
    File(RESULT_FILE).appendText("\n")
}

data class Individual(
    val x1: Double,
    val x2: Double,
    val param: Double,
    private val successMutation: Int = 0,
    val mutations: Int = 0
) {

    companion object {
        private const val K_SUCCESS = 0.2
        private const val MEAN = 0.0
        private const val C_I = 1.22
        private const val C_D = 0.82
    }

    private val rand = java.util.Random()
    private val fi get() = successMutation.toDouble() / mutations
    val fitness get() = -cos(x1) * cos(x2) * exp(-((x1 - PI).pow(2) + (x2 - PI).pow(2)))
    private fun childFitness(x1: Double, x2: Double) =
        -cos(x1) * cos(x2) * exp(-((x1 - PI).pow(2) + (x2 - PI).pow(2)))

    fun mutate(): Individual {
        val parentFitness = fitness
        val x1Child = x1 + rand.nextGaussian(MEAN, param)
        val x2Child = x2 + rand.nextGaussian(MEAN, param)
        return Individual(
            x1 = x1Child,
            x2 = x2Child,
            param =
                if (fi < K_SUCCESS) param * C_D
                else if (fi > K_SUCCESS) param * C_I
                else param,
            successMutation =
                if (childFitness(x1Child, x2Child) < parentFitness) successMutation + 1
                else successMutation,
            mutations = mutations + 1
        )
    }
}