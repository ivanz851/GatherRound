package app.gatherround.graph

open class Graph<T> {
    private val adjacencyList: MutableMap<T, MutableList<Edge>> = mutableMapOf()

    init {

    }

    inner class Edge(val finish: T, val weight: Int = 1)

    fun addVertex(vertex: T) {
        adjacencyList.putIfAbsent(vertex, mutableListOf())
    }

    fun addEdge(start: T, finish: T, weight: Int = 1) {
        addVertex(start)
        addVertex(finish)

        adjacencyList[start]!!.add(Edge(finish, weight))
        adjacencyList[finish]!!.add(Edge(start, weight))
    }

    fun getVertices(): Set<T> {
        return adjacencyList.keys.toSet()
    }

    fun getAdjacentEdges(vertex: T): List<Edge> {
        return adjacencyList[vertex]?.toList() ?: emptyList()
    }

    fun printAdjList() {
        for ((vertex, edges) in adjacencyList) {
            val edgeDescriptions = edges.joinToString { "${it.finish} (вес ${it.weight})" }
            print("Вершина ${vertex} соединена с: ${edgeDescriptions}\n")
        }
    }
}

