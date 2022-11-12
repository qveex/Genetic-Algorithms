import org.jetbrains.letsPlot.geom.*
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.letsPlot
import kotlin.math.*
import kotlin.random.Random


const val VARIANT = "cos(x - 0.5) / |x|"
const val IS_MAX_SEARCHING = false
const val FROM = -10
const val TO = 10  // [-10,0),(0,10], min
const val P_M = 0.001
const val P_C = 0.5
const val TOURNAMENT_COUNT = 3
const val INIT_POPULATION_COUNT = 100
const val MAX_GENERATION_COUNT = 100
const val BIT_COUNT = 15
const val MIN_FITNESS = -0.411009
const val MAX_FITNESS = 1000.1 // в этом варианте максимума нет _/\_ улетает в небо
val EXTREME_FITNESS = if (IS_MAX_SEARCHING) MAX_FITNESS else MIN_FITNESS


val population = mutableListOf<String>()
val clone = mutableListOf<String>()

fun main() {
    val begin: Long = System.currentTimeMillis()

    println("VARIANT #14:\n$VARIANT\n")
    population.apply {
        initPopulation()
        draw(0)
        println("max fitness before = $maxFitness")
        println("min fitness before = $minFitness")
        evolution()
        println("max fitness after = $maxFitness")
        println("min fitness after = $minFitness")
        draw(MAX_GENERATION_COUNT)
    }

    val end: Long = System.currentTimeMillis()
    println("Time spent: ${ end - begin } ms")
}

fun draw() {
    val size = 2.0.pow(BIT_COUNT).toInt()
    val real: (Int) -> Double = { FROM + it * (TO - FROM) / (size.toDouble() - 1) }

    val xs = List(size) { real(it) }
    val ys = List(size) { cos(real(it) - 0.5) / abs(real(it)) }
    val data = mapOf ("x" to xs, "y" to ys)
    val p = letsPlot(data) + geomLine { x = "x"; y = "y" }
    p.show()
}

fun MutableList<String>.draw(generation: Int) {

    val xs = List(this.size) { this[it].real }
    val xs2 = List(this.size) { clone[it].real }
    val ys = List(this.size) { if (this[it].fitness <= 25) this[it].fitness else 25 }
    val ys2 = List(this.size) { if (clone[it].fitness <= 25) clone[it].fitness else 25 }
    val data = mapOf ("x" to xs, "y" to ys, "fx" to xs2, "fy" to ys2)

    val p = letsPlot(data) +
            geomPoint(size = 3) { x = "x"; y = "y" } +
            geomLine { x = "fx"; y = "fy" } +
            ggtitle("generation = ${ if (generation == MAX_GENERATION_COUNT) "LAST" else if (generation == 0) "FIRST" else generation}")
    p.show()
}

fun MutableList<String>.initPopulation() {
    repeat(INIT_POPULATION_COUNT) {
        var chromosome = ""
        repeat(BIT_COUNT) { chromosome += (0..1).random() }
        this.add(chromosome)
        clone.add(chromosome)
    }
}

fun MutableList<String>.evolution() {
    var generationCounter = 0
    while (generationCounter <= MAX_GENERATION_COUNT && endCondition) {
        val offspring = selection().apply {
            crossing()
            mutation()
        }
        population.clear()
        population.addAll(offspring)
        if (generationCounter == MAX_GENERATION_COUNT / 2) draw(generationCounter)
        generationCounter++
    }
    println("GENERATION COUNT = $generationCounter")
}

fun MutableList<String>.selection(): MutableList<String> {
    val newPopulation = mutableListOf<String>()
    repeat(INIT_POPULATION_COUNT) {
        var ies = List(TOURNAMENT_COUNT) { (0 until INIT_POPULATION_COUNT).random() }
        while (ies.size != ies.distinct().size) {
            ies = List(TOURNAMENT_COUNT) { (0 until INIT_POPULATION_COUNT).random() }
        }
        val tournamentIndividuals = MutableList(TOURNAMENT_COUNT) { this[ies[it]] }
        newPopulation.add(tournamentIndividuals.extremeIndividual)
    }
    return newPopulation
}

fun MutableList<String>.crossing() {
    for (i in 0 until INIT_POPULATION_COUNT step 2) {
        if (Random.nextDouble() <= P_C) {
            val k = (0 until BIT_COUNT).random()
            val crossedA = this[i].substring(0, k) + this[i + 1].substring(k, BIT_COUNT)
            val crossedB = this[i + 1].substring(0, k) + this[i].substring(k, BIT_COUNT)

            this[i] = crossedA
            this[i + 1] = crossedB
        }
    }
}

fun MutableList<String>.mutation() {
    repeat(INIT_POPULATION_COUNT) {
        if (Random.nextDouble() <= P_M) {
            val k = (0 until BIT_COUNT).random()
            val chromosome = this[it]
            val mutatedGen = if (this[it][k] == '0') '1' else '0'
            this[it] = chromosome.substring(0, k) + mutatedGen + chromosome.substring(k + 1, chromosome.length)
        }
    }
}

fun MutableList<String>.print() = println(this)
val MutableList<String>.extremeFitness
    get () = if (IS_MAX_SEARCHING) maxFitness else minFitness
val MutableList<String>.extremeIndividual
    get () = if (IS_MAX_SEARCHING) maxByOrNull { it.fitness }!! else minByOrNull { it.fitness }!!
val MutableList<String>.maxFitness
    get () = this.maxOf { it.fitness }
val MutableList<String>.minFitness
    get () = this.minOf { it.fitness }
val MutableList<String>.endCondition
    get () = if (IS_MAX_SEARCHING) extremeFitness <= EXTREME_FITNESS else extremeFitness >= EXTREME_FITNESS


fun Int.toBinary() = toString(radix = 2)
fun String.fromBinary() = toInt(radix = 2)
val String.real: Double
    get() = FROM + fromBinary() * (TO - FROM) / (2.0.pow(BIT_COUNT.toDouble()) - 1.0)

val String.fitness: Double
    get() =
        if (abs(real) == 0.0)
            if (IS_MAX_SEARCHING)
                Int.MIN_VALUE.toDouble()
            else
                Int.MAX_VALUE.toDouble()
        else
            cos(real - 0.5) / abs(real)

fun String.printData() = println("real = ${real}\nfitness = ${fitness}\n")