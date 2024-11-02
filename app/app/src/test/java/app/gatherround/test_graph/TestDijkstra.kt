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

    @Test
    fun testBigGraph() {
        graph.addVertex("1")
        graph.addVertex("2")
        graph.addVertex("3")
        graph.addVertex("4")
        graph.addVertex("5")
        graph.addVertex("6")
        graph.addVertex("7")
        graph.addVertex("8")
        graph.addVertex("9")
        graph.addVertex("10")

        graph.addEdge("1", "2", 5)
        graph.addEdge("2", "3", 22)
        graph.addEdge("3", "4", 5)
        graph.addEdge("4", "5", 5)
        graph.addEdge("5", "6", 5)
        graph.addEdge("6", "7", 5)
        graph.addEdge("7", "8", 5)
        graph.addEdge("8", "9", 5)

        graph.addEdge("1", "7", 20)
        graph.addEdge("7", "3", 5)
        graph.addEdge("5", "10", 1)


        val (distance, path) = dijkstra.getShortestPath(graph, "1", "10")

        assertEquals(31, distance) // 1 ->(20) 7 -> (5) 6 ->(5) 5 ->(1) 10
        assertEquals(listOf("1", "7", "6", "5", "10"), path)
    }
}
