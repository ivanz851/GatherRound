package app.gatherround.graph

const val MAX_DIST = Int.MAX_VALUE

open class Graph<Vertex: Comparable<Vertex>> {
    private val adjacencyList: MutableMap<Vertex, MutableList<Edge>> = mutableMapOf()

    inner class Edge(val finish: Vertex, val weight: Int = 1)

    fun addVertex(vertex: Vertex) {
        adjacencyList.putIfAbsent(vertex, mutableListOf())
    }

    fun addEdge(start: Vertex, finish: Vertex, weight: Int = 1) {
        addVertex(start)
        addVertex(finish)

        adjacencyList[start]!!.add(Edge(finish, weight))
        adjacencyList[finish]!!.add(Edge(start, weight))
    }

    fun getVertices(): Set<Vertex> {
        return adjacencyList.keys.toSet()
    }

    fun getAdjacentEdges(vertex: Vertex): List<Edge> {
        return adjacencyList[vertex]?.toList() ?: emptyList()
    }

    fun printAdjList() {
        for ((vertex, edges) in adjacencyList) {
            val edgeDescriptions = edges.joinToString { "${it.finish} (вес ${it.weight})" }
            print("Вершина $vertex соединена с: ${edgeDescriptions}\n")
        }
    }

    fun findOptimalVertex(
        chosenVertexes: Set<Vertex>,
    ): Pair<Vertex?, Int> {
        /*
        Находит такую вершину графа, что максимальное расстояние от нее до вершины из множества
        выбранных вершин минимально.
        Возвращает найденную вершину и максимальное расстояние от нее до вершины из множества
        выбранных вершин.
        */
        if (chosenVertexes.isEmpty()) {
            return Pair(null, 0)
        }

        if (chosenVertexes.size == 1) {
            return Pair(chosenVertexes.first(), 0)
        }

        val distances = precalcSortedDistances(chosenVertexes)

        var left = 0
        var right: Int = MAX_DIST
        var optimalVertex: Vertex? = null

        while (left + 1 < right) {
            val mid: Int = (left + right) / 2
            val intersection = getKIntersection(distances, mid)

            if (intersection.isNotEmpty()) {
                optimalVertex = intersection.first()
                right = mid
            } else {
                left = mid
            }
        }

        val optimalDist = right

        return if (optimalVertex != null) {
            Pair(optimalVertex, optimalDist)
        } else Pair(null, 0)
    }

    private fun getKIntersection(
        dicts: Map<Vertex, Map<Vertex, Int>>,
        bound: Int): Set<Vertex> {
        /*
        Находит общие ключи нескольких хэш-таблиц (вершина, расстояние), при этом значения,
        соответсвующие ключам, не превосходят заданное число bound.
         */
        if (dicts.isEmpty()) {
            return emptySet()
        }

        var intersection = mutableSetOf<Vertex>()
        for ((vertex, dist) in dicts.entries.first().value) {
            if (dist <= bound) {
                intersection.add(vertex)
            }
        }

        for ((_, dict) in dicts) {
            intersection = getIntersection(intersection, dict, bound)
        }

        return intersection
    }

    private fun getIntersection(curIntersection: Set<Vertex>,
                                newDict: Map<Vertex, Int>,
                                bound: Int): MutableSet<Vertex> {
        /*
        Находит ключи словаря newDict, такие что соответсвующие им значения не превосходят
        заданное число bound и содержатся в множестве curIntersection.
        */
        val result = mutableSetOf<Vertex>()

        for ((vertex, dist) in newDict) {
            if (dist <= bound && vertex in curIntersection) {
                result.add(vertex)
            }
        }

        return result
    }

    private fun precalcSortedDistances(vertices: Set<Vertex>):
            Map<Vertex, Map<Vertex, Int>> {
        val result =
            mutableMapOf<Vertex, Map<Vertex, Int>>()

        for (vertex in vertices) {
            val distances =
                Dijkstra<Vertex>().getDistances(this, vertex)
            result[vertex] = distances
        }

        return result
    }
}

