import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.letsPlot
import kotlin.random.Random

class Ant {

    private val path = mutableListOf<Node>()
    val pathRibs = mutableSetOf<Rib>()

    val distance get() = pathRibs.sumOf { euclideanDistance(it.from, it.to) }
    val pheromone get() = 1 / distance

    private val Node.random: Node get() = (nodes - path.toSet() - this).random()
    private val Node.nearest: Node get() =
        (nodes - path.toSet() - this).minBy { euclideanDistance(this, it) }
    private val Node.lovely: Node get() =
        pheromones[this]!!.filter { !(path + this).contains(it.key) }.maxBy { p -> p.value }.key

    private fun go(from: Node, to: Node) {
        path.add(to)
        pathRibs.add(Rib(from, to))
    }

    fun createPath() {
        val first = nodes.random()
        var from = first
        path.add(from)

        val available = (nodes - path.toSet() - first).toMutableList()

        repeat(available.size) {
            val curProbs = probabilities[from]!!.filter { available.contains(it.key) }.toMutableMap()
            val sum = curProbs.values.sum()
            //println("cur probs = $curProbs")
            curProbs.keys.forEach { curProbs[it] = curProbs[it]!! / sum }

            var r = Random.nextDouble()
            for (to in curProbs) {
                //println("r = $r\tval = ${to.value}")
                if (r < to.value) {
                    go(from, to.key)
                    from = to.key
                    available.remove(to.key)
                    break
                }
                r -= to.value
            }
        }
        go(from, first)
    }

    fun draw() {
        val xs = List(nodes.size) { nodes[it].x }
        val ys = List(nodes.size) { nodes[it].y }
        val data = mapOf ("x" to xs, "y" to ys)

        var p = letsPlot(data)
        pathRibs.forEach {
            val dx = mutableListOf(it.from.x, it.to.x)
            val dy = mutableListOf(it.from.y, it.to.y)
            p += geomLine { x = dx; y = dy }
        }
        p += geomPoint(size = 3) { x = "x"; y = "y" } + ggtitle("distance = $distance")
        p.show()
    }
}

val MutableList<Ant>.best get() = maxBy { it.distance }