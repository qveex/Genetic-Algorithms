import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

const val VARIANT = "#14 \nBerlin52.tsp\n"
const val ALPHA = 3.0 // жадность
const val BETA = 2.0  // стадность
const val RO = 0.5
const val INIT_POPULATION_SIZE = 100
const val MAX_GENERATION_COUNT = 50

val nodes = listOf(
    Node(565, 575),   Node(25, 185),   Node(345, 750),  Node(945, 685),
    Node(845, 655),   Node(880, 660),  Node(25, 230),   Node(525, 1000),
    Node(580, 1175),  Node(650, 1130), Node(1605, 620), Node(1220, 580),
    Node(1465, 200),  Node(1530, 5),   Node(845, 680),  Node(725, 370),
    Node(145, 665),   Node(415, 635),  Node(510, 875),  Node(560, 365),
    Node(300, 465),   Node(520, 585),  Node(480, 415),  Node(835, 625),
    Node(975, 580),   Node(1215, 245), Node(1320, 315), Node(1250, 400),
    Node(660, 180),   Node(410, 250),  Node(420, 555),  Node(575, 665),
    Node(1150, 1160), Node(700, 580),  Node(685, 595),  Node(685, 610),
    Node(770, 610),   Node(795, 645),  Node(720, 635),  Node(760, 650),
    Node(475, 960),   Node(95, 260),   Node(875, 920),  Node(700, 500),
    Node(555, 815),   Node(830, 485),  Node(1170, 65),  Node(830, 610),
    Node(605, 625),   Node(595, 360),  Node(1340, 725), Node(1740, 245)
)

val best = listOf(
    0,  48, 31, 44, 18, 40, 7,  8,  9,  42, 32, 50, 10,
    51, 13, 12, 46, 25, 26, 27, 11, 24, 3,  5,  14, 4,
    23, 47, 37, 36, 39, 38, 35, 34, 33, 43, 45, 15, 28,
    49, 19, 22, 29, 1,  6,  41, 20, 16, 2,  17, 30, 21
)

val pheromones = nodes.associateWith { nodes.associateWith { 1.0 }.toMutableMap() }
val distances = nodes.associateWith { from -> nodes.associateWith { to -> euclideanDistance(from, to) } }
var probabilities = nodes.associateWith { from -> nodes.associateWith { to -> pheromones[from]!![to]!!.pow(ALPHA) * (1 / distances[from]!![to]!!).pow(BETA) } }

data class Node(val x: Int, val y: Int) {
    override fun toString() = "($x, $y)"
}

data class Rib(val from: Node, val to: Node) {
    override fun toString() = "$from -> $to"
}

class Graph {

    private var ants = MutableList(INIT_POPULATION_SIZE) { Ant() }
    private fun repopulate() { ants = MutableList(INIT_POPULATION_SIZE) { Ant() } }

    fun run() {
        ants.apply {
            var best = ants.best
            repeat(MAX_GENERATION_COUNT) {
                paths()
                evaporate()
                calcPheromones()
                println(ants.best.distance)
                best = if (best.distance >= ants.best.distance) ants.best else best
                repopulate()
            }
            println("best solution = ${best.distance}")
            println(best.pathRibs)
            best.draw()
        }
    }

    private fun paths() {
        ants.forEach {
            it.createPath()
        }
    }

    private fun calcPheromones() {
        ants.forEach { ant ->
            ant.pathRibs.forEach {
                pheromones[it.from]!![it.to] = pheromones[it.from]!![it.to]!! + ant.pheromone
                pheromones[it.to]!![it.from] = pheromones[it.to]!![it.from]!! + ant.pheromone
            }
        }
        probabilities = nodes.associateWith { from -> nodes.associateWith { to -> pheromones[from]!![to]!!.pow(ALPHA) * (1 / distances[from]!![to]!!).pow(BETA) } }
    }

    private fun evaporate() {
        pheromones.values.forEach {
            it.forEach { n ->
                it[n.key] = it[n.key]!! * (1 - RO)
            }
        }
    }
}

fun euclideanDistance(from: Node, to: Node) =
    sqrt((from.x - to.x).toDouble().pow(2) + (from.y - to.y).toDouble().pow(2))