package app.gatherround.test_graph

import app.gatherround.graph.Dijkstra
import app.gatherround.graph.Graph
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestDijkstra {
    private lateinit var graph: Graph<String>
    private lateinit var dijkstra: Dijkstra<String>

    @BeforeEach
    fun setUp() {
        graph = Graph()
        dijkstra = Dijkstra()
    }

    @Test
    fun testShortestPathBetweenConnectedVertices() {
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addVertex("C")
        graph.addEdge("A", "B", 1)
        graph.addEdge("B", "C", 2)

        val (distance, path) = dijkstra.getShortestPath(graph, "A", "C")

        assertEquals(3, distance)
        assertEquals(listOf("A", "B", "C"), path)
    }

    @Test
    fun testShortestPathBetweenTwoVertices() {
        graph.addVertex("X")
        graph.addVertex("Y")
        graph.addEdge("X", "Y", 4)

        val (distance, path) = dijkstra.getShortestPath(graph, "X", "Y")

        assertEquals(4, distance)
        assertEquals(listOf("X", "Y"), path)
    }

    @Test
    fun testNoPathBetweenVertices() {
        graph.addVertex("D")
        graph.addVertex("E")

        val (distance, path) = dijkstra.getShortestPath(graph, "D", "E")

        assertEquals(Int.MAX_VALUE, distance)
        assertNull(path)
    }

    @Test
    fun testMultiplePaths() {
        graph.addVertex("1")
        graph.addVertex("2")
        graph.addVertex("3")
        graph.addEdge("1", "2", 5)
        graph.addEdge("1", "3", 2)
        graph.addEdge("3", "2", 1)

        val (distance, path) = dijkstra.getShortestPath(graph, "1", "2")

        assertEquals(3, distance) // 1 -> 3 -> 2
        assertEquals(listOf("1", "3", "2"), path)
    }

    @Test
    fun testSameVertex() {
        graph.addVertex("A")

        val (distance, path) = dijkstra.getShortestPath(graph, "A", "A")

        assertEquals(0, distance)
        assertEquals(listOf("A"), path)
    }
}
