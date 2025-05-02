package app.gatherround.graph

import java.util.TreeSet

/**
 * Реализация алгоритма Дейкстры для графа с положительными весами ребер.
 *
 * @param Vertex тип, которым представлены вершины. Должен реализовывать [Comparable],
 *               поскольку вершины хранятся в [TreeSet], основанном на бинарном дереве поиска.
 *
 * Реализует интерфейс [ShortestPathsFinder]:
 *  * [getShortestPath] — кратчайший путь между двумя вершинами;
 *  * [getDistances] — кратчайшие расстояния от заданной вершины до всех остальных.
 */
class Dijkstra<Vertex: Comparable<Vertex>> : ShortestPathsFinder<Vertex> {
    /**
     * Считает кратчайшие расстояния от [start] до всех остальных вершин графа и
     * сохраняет предков для восстановления пути.
     *
     * @return *(distances, ancestors)*, где
     * * *distances* — Map «вершина → дистанция» (Int.MAX_VALUE, если недостижима);
     * * *ancestors* — Map «вершина → предыдущая вершина на кратчайшем пути».
     */
    fun calcShortestPathsFromVertex(graph: Graph<Vertex>,
                                    start: Vertex):
            Pair<Map<Vertex, Int>, Map<Vertex, Vertex?>> {
        val distances =
            graph.getVertices().associateWith { Int.MAX_VALUE }.toMutableMap()
        val ancestors = mutableMapOf<Vertex, Vertex?>()

        val prioritySet = TreeSet(
            compareBy<Pair<Int, Vertex>> { it.first }.thenBy { it.second }
        )

        distances[start] = 0
        prioritySet.add(Pair(distances[start]!!, start))

        while (prioritySet.isNotEmpty()) {
            val (_, currentVertex) = prioritySet.pollFirst()

            graph.getAdjacentEdges(currentVertex).forEach { edge ->
                val neighbor = edge.finish
                val newDist = distances[currentVertex]!! + edge.weight

                if (newDist < distances[neighbor]!!) {
                    prioritySet.remove(Pair(distances[neighbor]!!, neighbor))
                    distances[neighbor] = newDist
                    ancestors[neighbor] = currentVertex
                    prioritySet.add(Pair(distances[neighbor]!!, neighbor))
                }
            }
        }

        return Pair(distances, ancestors)
    }

    /**
     * Вычисляет кратчайший путь от [start] до [finish].
     *
     * @return пару *(длина, список вершин в пути)*.
     *         Если вершина [finish] недостижима, длина — `Int.MAX_VALUE`, путь — `null`.
     */
    override fun getShortestPath(graph: Graph<Vertex>,
                                 start: Vertex,
                                 finish: Vertex): Pair<Int, List<Vertex>?> {
        val (distances, ancestors) =
            calcShortestPathsFromVertex(graph, start)
        val distance = distances[finish] ?: Int.MAX_VALUE

        if (distance == Int.MAX_VALUE) {
            return Pair(distance, null)
        }

        val path = mutableListOf<Vertex>()
        var current: Vertex? = finish

        while (current != null) {
            path.add(current)
            current = ancestors[current]
        }

        path.reverse()
        return Pair(distance, path)
    }

    /**
     * Возвращает только кратчайшие расстояния от заданной вершины до остальных вершин графа.
     */
    fun getDistances(graph: Graph<Vertex>, start: Vertex): Map<Vertex, Int> {
        val (distances, _) =
            Dijkstra<Vertex>().calcShortestPathsFromVertex(graph, start)
        return distances
    }
}