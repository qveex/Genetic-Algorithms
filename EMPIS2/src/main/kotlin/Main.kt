import java.io.File
import kotlin.math.*
import kotlin.random.Random

const val VARIANT = "\n-cos(x1) * cos(x2) * e^(-((x1-pi)^2 + (x2-pi)^2))\n"
const val IS_MAX_SEARCHING = false
const val FROM = -100.0
const val TO = 100.0
const val P_M = 0.05
const val P_C = 0.5
const val TOURNAMENT_COUNT = 3
const val INIT_POPULATION_COUNT = 750
const val MAX_GENERATION_COUNT = 100
const val MAX_REPEATING_COUNT = 25
const val MAX_I = 2
const val RESULT_FILE = "result.txt"


val population = mutableListOf<MutableList<Double>>()
var generation = 0

fun main() {
    val begin: Long = System.currentTimeMillis()
    File(RESULT_FILE).delete()
    println("VARIANT #14:\n$VARIANT\n")
    population.apply {
        initPopulation()
        saveGeneration()
        println("\nextreme fitness before = $extremeFitness\n")
        evolution()
        println("\nextreme fitness after = $extremeFitness\n")
        saveGeneration()
    }

    val end: Long = System.currentTimeMillis()
    println("\nTime spent: ${ end - begin } ms")

    //Runtime.getRuntime().exec("python3 script.py")
}


fun MutableList<MutableList<Double>>.initPopulation() {
    repeat(INIT_POPULATION_COUNT) {
        add(MutableList(MAX_I) { Random.nextDouble(FROM, TO + 1.0) })
    }
}

fun MutableList<MutableList<Double>>.evolution() {
    var lastFitness = this.extremeFitness
    var fitnessRepeat = 0
    while (generation++ <= MAX_GENERATION_COUNT && fitnessRepeat <= MAX_REPEATING_COUNT) {
        val offspring = selection().apply {
            crossing()
            mutation()
        }
        population.clear()
        population.addAll(offspring)
        if (extremeFitness == lastFitness) fitnessRepeat++
        else lastFitness = extremeFitness.also { fitnessRepeat = 0 }
        print()
    }
    println("GENERATION COUNT = ${generation - 2}")
    println("REPEATING COUNT = $fitnessRepeat")
}

fun MutableList<MutableList<Double>>.selection(): MutableList<MutableList<Double>> {
    val newPopulation = mutableListOf(this.extremeIndividual)
    repeat(INIT_POPULATION_COUNT - 1) {
        var ies = List(TOURNAMENT_COUNT) { (0 until INIT_POPULATION_COUNT).random() }
        while (ies.size != ies.distinct().size) {
            ies = List(TOURNAMENT_COUNT) { (0 until INIT_POPULATION_COUNT).random() }
        }
        val tournamentIndividuals = MutableList(TOURNAMENT_COUNT) { this[ies[it]] }
        newPopulation.add(tournamentIndividuals.extremeIndividual)
    }
    return newPopulation
}

fun MutableList<MutableList<Double>>.crossing() {
    for (i in 0 until INIT_POPULATION_COUNT step 2) {
        if (Random.nextDouble() <= P_C) {

            repeat(2) {
                val n = Random.nextInt(2, 6)
                val u = Random.nextDouble()
                val beta = if (u <= 0.5) (2*u).pow(1 / (n+1)) else (1 / (2*(1-u))).pow(1 / (n+1))

                val h = if (it % 2 == 0)
                    MutableList(MAX_I) { j ->
                        val cj1 = this[i][j]
                        val cj2 = this[i + 1][j]
                        0.5 * ((1 - beta) * cj1 + (1 + beta) * cj2)
                    }
                else
                    MutableList(MAX_I) { j ->
                        val cj1 = this[i][j]
                        val cj2 = this[i + 1][j]
                        0.5 * ((1 + beta) * cj1 + (1 - beta) * cj2)
                    }
                this[i + it] = h
            }
        }
    }
}

fun MutableList<MutableList<Double>>.mutation() {
    repeat(INIT_POPULATION_COUNT) {
        if (Random.nextDouble() <= P_M) {
            val a = Random.nextInt(0, 2)
            val k = Random.nextInt(0, MAX_I)
            val ck = this[it][k]
            val y = abs(ck)
            val r = Random.nextDouble(0.0, 1.0)
            val t = generation
            val T = MAX_GENERATION_COUNT
            val b = 2
            val delta = y * (1 - r.pow((1.0 - t / T).pow(b)))
            val newChromosome = if (a == 0) ck + delta else ck - delta
            this[it][k] = newChromosome
        }
    }
}

val MutableList<MutableList<Double>>.extremeFitness
    get () = if (IS_MAX_SEARCHING) maxFitness else minFitness
val MutableList<MutableList<Double>>.extremeIndividual
    get () = if (IS_MAX_SEARCHING) maxByOrNull { it.fitness }!! else minByOrNull { it.fitness }!!
val MutableList<MutableList<Double>>.maxFitness
    get () = this.maxOf { it.fitness }
val MutableList<MutableList<Double>>.minFitness
    get () = this.minOf { it.fitness }
fun MutableList<MutableList<Double>>.print() {
    val e = extremeIndividual
    println("${e.fitness}\t${e.x1}\t${e.x2}")
}
fun MutableList<MutableList<Double>>.saveGeneration() {
    forEach {
        File(RESULT_FILE).appendText("${it.x1}\t${it.x2}\t${it.fitness}\n")
    }
    File(RESULT_FILE).appendText("\n")
}

val MutableList<Double>.x1: Double get() = this[0]
val MutableList<Double>.x2: Double get() = this[1]
val MutableList<Double>.fitness: Double
    get() = -cos(x1) * cos(x2) * exp(-((x1 - PI).pow(2) + (x2 - PI).pow(2)))