package app.gatherround.graph

/** «Бесконечное» расстояние, используемое в алгоритмах */
const val MAX_DIST = Int.MAX_VALUE

/**
 * Класс для представления неориентированного взвешенного графа.
 *
 * @param Vertex тип, которым представлены вершины (должен поддерживать сравнение,
 *               т.к. вершины хранятся в поскольку вершины хранятся в [TreeSet] в алгоритме Дейкстры).
 */
open class Graph<Vertex: Comparable<Vertex>> {

    /* Список смежности графа - для каждой вершины хранится список исходящих рёбер. */
    private val adjacencyList: MutableMap<Vertex, MutableList<Edge>> = mutableMapOf()

    /**
     Ориентированное ребро графа.

     * @property finish конечная вершина
     * @property weight вес ребра
     */
    inner class Edge(val finish: Vertex, val weight: Int = 1)

    /** Добавляет изолированную вершину, если её ещё нет в графе. */
    fun addVertex(vertex: Vertex) {
        adjacencyList.putIfAbsent(vertex, mutableListOf())
    }

    /**
     * Добавляет неориентированное ребро `start — finish` с весом [weight].
     * Если стартовой или финишной вершины нет, она создается.
     */
    fun addEdge(start: Vertex, finish: Vertex, weight: Int = 1) {
        addVertex(start)
        addVertex(finish)

        adjacencyList[start]!!.add(Edge(finish, weight))
        adjacencyList[finish]!!.add(Edge(start, weight))
    }

    /** @return множество всех вершин графа. */
    fun getVertices(): Set<Vertex> {
        return adjacencyList.keys.toSet()
    }

    /** @return список исходящих из вершины рёбер или пустой список, если вершины нет. */
    fun getAdjacentEdges(vertex: Vertex): List<Edge> {
        return adjacencyList[vertex]?.toList() ?: emptyList()
    }

    /** Вывод списка смежности в консоль для отладки. */
    fun printAdjList() {
        for ((vertex, edges) in adjacencyList) {
            val edgeDescriptions = edges.joinToString { "${it.finish} (вес ${it.weight})" }
            print("Вершина $vertex соединена с: ${edgeDescriptions}\n")
        }
    }

    /**
     * @return все рёбра графа в виде пар стартовая вершина — исходящее ребро.
     *        Учитываются оба направления (т.е. каждое неориентированное ребро будет встречаться дважды).
     */
    fun getEdges(): List<Pair<Vertex, Edge>> {
        val res = mutableListOf<Pair<Vertex, Edge>>()

        for ((vertex, edges) in adjacencyList) {
            for (edge in edges) {
                res.add(Pair(vertex, edge))
            }
        }
        return res
    }

    /**
     * Ищет вершину, **минимизирующую максимальное** расстояние до любой вершины
     * из набора [chosenVertexes].
     *
     * @return пару *(оптимальная вершина | null, её максимальное расстояние)*.
     *         Если [chosenVertexes] пуст, возвращает *(null, 0)*.
     */
    fun findOptimalVertex(
        chosenVertexes: Set<Vertex>,
    ): Pair<Vertex?, Int> {
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

    /**
     * Возвращает множество вершин, до которых от **каждой** вершины
     * из словаря **существует путь весом не превышающим bound**.
     */
    private fun getKIntersection(
        dicts: Map<Vertex, Map<Vertex, Int>>,
        bound: Int): Set<Vertex> {
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

    /** Сужает [curIntersection] до тех вершин, которые встречаются в [newDict] с расстоянием не превосходящим [bound]. */
    private fun getIntersection(curIntersection: Set<Vertex>,
                                newDict: Map<Vertex, Int>,
                                bound: Int): MutableSet<Vertex> {
        val result = mutableSetOf<Vertex>()

        for ((vertex, dist) in newDict) {
            if (dist <= bound && vertex in curIntersection) {
                result.add(vertex)
            }
        }

        return result
    }

    /**
     * Вычисляет все кратчайшие расстояния от каждой вершины из [vertices]
     * до остальных, используя алгоритм Дейкстры.
     *
     * @return Map «стартовая вершина → Map(вершина → расстояние от стартовой)»
     */
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

