package app.gatherround.graph

import java.util.TreeSet

class Graph<T> {
    private val adjacencyList: MutableMap<T, MutableSet<Edge>> = mutableMapOf()

    inner class Edge(val finish: T, val weight: Int = 1)

    fun addVertex(vertex: T) {
        adjacencyList.putIfAbsent(vertex, mutableSetOf())
    }

    fun addEdge(start: T, finish: T, weight: Int = 1) {
        addVertex(start)
        addVertex(finish)

        adjacencyList[start]?.add(Edge(finish, weight))
        adjacencyList[finish]?.add(Edge(start, weight))
    }

    fun dijkstra(start: T): Pair<Map<T, Int>, Map<T, T?>> {
        val distances = adjacencyList.keys.associateWith { Int.MAX_VALUE }.toMutableMap()
        val ancestors = mutableMapOf<T, T?>()
        val prioritySet = TreeSet<Pair<Int, T>>(compareBy { it.first })

        distances[start] = 0
        prioritySet.add(Pair(distances[start]!!, start))

        while (prioritySet.isNotEmpty()) {
            val (_, currentVertex) = prioritySet.pollFirst()

            adjacencyList[currentVertex]?.forEach { edge ->
                val neighbor = edge.finish
                val newDist = distances[currentVertex]!! + edge.weight

                if (newDist < distances[neighbor]!!) {
                    prioritySet.remove(Pair(distances[neighbor]!!, neighbor))
                    distances[neighbor] = newDist
                    ancestors[neighbor] = currentVertex
                    prioritySet.add(Pair(newDist, neighbor))
                }
            }
        }

        return Pair(distances, ancestors)
    }

    fun getShortestPath(start: T, finish: T): Pair<Int, List<T>?> {
        val (distances, ancestors) = dijkstra(start)
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

