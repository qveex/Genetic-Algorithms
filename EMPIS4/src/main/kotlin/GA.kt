import Constants.INIT_POPULATION_SIZE
import Constants.MAX_GENERATION_COUNT
import Constants.REROLL_COUNT
import Constants.TOURNAMENT_SIZE

class GA {

    private val population = MutableList(INIT_POPULATION_SIZE) { Expression().apply { generate() } }
    private val MutableList<Expression>.bestIndividual get() = minBy { it.fitness }
    private val MutableList<Expression>.worstIndividual get() = maxBy { it.fitness }

    private fun selection() {
        val newPopulation = mutableListOf(population.bestIndividual)
        repeat(INIT_POPULATION_SIZE - 1) { i ->
            var ies = List(TOURNAMENT_SIZE) { (0 until INIT_POPULATION_SIZE).random() }
            while (ies.size != ies.distinct().size) {
                ies = List(TOURNAMENT_SIZE) { (0 until INIT_POPULATION_SIZE).random() }
            }
            val tournamentIndividuals = MutableList(TOURNAMENT_SIZE) { population[ies[it]] }
            newPopulation.add(tournamentIndividuals.bestIndividual.clone())
        }
        population.clear()
        population.addAll(newPopulation)
    }

    private fun crossing() {
        for (i in 0 until INIT_POPULATION_SIZE step 2) {
            population[i].crossing(population[i + 1])
        }
    }

    private fun mutation() {
        repeat(INIT_POPULATION_SIZE) {
            population[it].mutation()
        }
    }

    private fun reroll() {
        repeat(REROLL_COUNT) {
            population.remove(population.worstIndividual)
            population.add(Expression().apply { generate() })
        }
    }

    fun evolve() {
        println("best before = ${population.bestIndividual}")
        println("fit = ${population.bestIndividual.fitness}\n")
        var counter = 0
        val begin = System.currentTimeMillis()
        while (++counter <= MAX_GENERATION_COUNT /*|| population.bestIndividual.fitness > 1.0*/) {
            selection()
            crossing()
            mutation()
            reroll()
        }
        val end = System.currentTimeMillis()
        println("best after = ${population.bestIndividual}")
        println("fit = ${population.bestIndividual.fitness}\n")
        println("Time spent: ${ end - begin } ms")
    }

}