package app.gatherround.graph

import java.util.TreeSet

class Dijkstra<T> : ShortestPathsFinder<T> {
    private fun calcShortestPathsFVertex(graph: Graph<T>, start: T): Pair<Map<T, Int>, Map<T, T?>> {
        val distances = graph.getVertices().associateWith { Int.MAX_VALUE }.toMutableMap()
        val ancestors = mutableMapOf<T, T?>()
        val prioritySet = TreeSet<Pair<Int, T>>(compareBy { it.first })

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

    override fun getShortestPath(graph: Graph<T>, start: T, finish: T): Pair<Int, List<T>?> {
        val (distances, ancestors) = calcShortestPathsFVertex(graph, start)
        val distance = distances[finish] ?: Int.MAX_VALUE

        if (distance == Int.MAX_VALUE) {
            return Pair(distance, null)
        }

        val path = mutableListOf<T>()
        var current: T? = finish

        while (current != null) {
            path.add(current)
            current = ancestors[current]
        }

        path.reverse()
        return Pair(distance, path)
    }
}