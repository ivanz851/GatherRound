package app.gatherround.graph

interface ShortestPathsFinder<T> {
    fun getShortestPath(graph: Graph<T>, start: T, finish: T): Pair<Int, List<T>?>
}
