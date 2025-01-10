package app.gatherround.graph

import java.util.TreeSet

class Dijkstra<Vertex: Comparable<Vertex>> : ShortestPathsFinder<Vertex> {
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

    override fun getShortestPath(graph: Graph<Vertex>,
                                 start: Vertex,
                                 finish: Vertex): Pair<Int, List<Vertex>?> {
        val (distances, ancestors) =
            calcShortestPathsFromVertex(graph, start)
        val distance = distances[finish] ?: Int.MAX_VALUE

        println("distances = ${distances}")


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

    fun getDistances(graph: Graph<Vertex>, start: Vertex): Map<Vertex, Int> {
        val (distances, _) =
            Dijkstra<Vertex>().calcShortestPathsFromVertex(graph, start)
        return distances
    }
}