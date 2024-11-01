package app.gatherround.graph

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GraphTest {
    private lateinit var graph: Graph<String>

    @BeforeEach
    fun setUp() {
        graph = Graph()
    }

    @Test
    fun test_add_vertex() {
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addVertex("C")

        // Проверка на добавление вершин
        val distances = graph.dijkstra("A").first
        assertEquals(0, distances["A"])
        assertEquals(Int.MAX_VALUE, distances["B"])
        assertEquals(Int.MAX_VALUE, distances["C"])
    }

    @Test
    fun `test add edge and shortest path`() {
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addEdge("A", "B", 5)

        // Проверка на добавление рёбер
        val distances = graph.dijkstra("A").first
        assertEquals(0, distances["A"])
        assertEquals(5, distances["B"])

        // Проверка кратчайшего пути
        val (distance, path) = graph.getShortestPath("A", "B")
        assertEquals(5, distance)
        assertEquals(listOf("A", "B"), path)
    }

    @Test
    fun `test shortest path with multiple edges`() {
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addVertex("C")
        graph.addVertex("D")
        graph.addEdge("A", "B", 1)
        graph.addEdge("A", "C", 4)
        graph.addEdge("B", "C", 2)
        graph.addEdge("B", "D", 5)
        graph.addEdge("C", "D", 1)

        // Проверка кратчайшего пути
        val (distance, path) = graph.getShortestPath("A", "D")
        assertEquals(4, distance) // A -> B -> C -> D
        assertEquals(listOf("A", "B", "C", "D"), path)
    }

    @Test
    fun `test unreachable path`() {
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addVertex("C")

        // Проверка недостижимого пути
        val (distance, path) = graph.getShortestPath("A", "C")
        assertEquals(Int.MAX_VALUE, distance)
        assertNull(path)
    }
}
