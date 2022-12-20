
fun main() {

    println(VARIANT)
    val begin = System.currentTimeMillis()

    Graph().run()

    val end = System.currentTimeMillis()
    println("Time spent: ${end - begin} ms")

    var dst = 0.0
    for (i in 0 until best.size - 1) {
        dst += euclideanDistance(nodes[best[i]], nodes[best[i + 1]])
    }
    dst += euclideanDistance(nodes[best.last()], nodes[best.first()])
    println("best tour = $dst")
}