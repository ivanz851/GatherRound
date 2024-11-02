package app.gatherround.test_graph

import app.gatherround.graph.Graph
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestGraph {
    private lateinit var graph: Graph<String>

    @BeforeEach
    fun setUp() {
        graph = Graph()
    }

    @Test
    fun testAddVertex() {
        graph.addVertex("A")
        assertTrue(graph.getVertices().contains("A"))
        assertEquals(1, graph.getVertices().size)
    }

    @Test
    fun testAddEdge() {
        graph.addEdge("A", "B", 2)
        assertTrue(graph.getVertices().contains("A"))
        assertTrue(graph.getVertices().contains("B"))

        val edgesA = graph.getAdjacentEdges("A")
        val edgesB = graph.getAdjacentEdges("B")

        assertEquals(1, edgesA.size)
        assertEquals("B", edgesA[0].finish)
        assertEquals(2, edgesA[0].weight)

        assertEquals(1, edgesB.size)
        assertEquals("A", edgesB[0].finish)
        assertEquals(2, edgesB[0].weight)
    }

    @Test
    fun testGetAdjacentEdges() {
        graph.addEdge("A", "B")
        graph.addEdge("A", "C", 3)

        val edgesA = graph.getAdjacentEdges("A")
        assertEquals(2, edgesA.size)

        val finishes = edgesA.map { it.finish }
        assertTrue(finishes.contains("B"))
        assertTrue(finishes.contains("C"))
    }

    @Test
    fun testGetVerticesEmptyGraph() {
        val vertices = graph.getVertices()
        assertTrue(vertices.isEmpty())
    }

    @Test
    fun testPrintAdjList() {
        val output = ByteArrayOutputStream()
        System.setOut(PrintStream(output))

        graph.addEdge("A", "B", 4)
        graph.addEdge("A", "C", 5)
        graph.printAdjList()

        val expectedOutput = """
            Вершина A соединена с: B (вес 4), C (вес 5)
            Вершина B соединена с: A (вес 4)
            Вершина C соединена с: A (вес 5)
        """.trimIndent()

        assertEquals(expectedOutput, output.toString().trim())
    }

    @Test
    fun testAddIntegerVertices() {
        val integerGraph = Graph<Int>()

        integerGraph.addVertex(1)
        integerGraph.addVertex(2)
        integerGraph.addVertex(3)

        assertTrue(integerGraph.getVertices().contains(1))
        assertTrue(integerGraph.getVertices().contains(2))
        assertTrue(integerGraph.getVertices().contains(3))
        assertEquals(3, integerGraph.getVertices().size)

        integerGraph.addEdge(1, 2, 5)
        integerGraph.addEdge(2, 3, 10)

        val edges1 = integerGraph.getAdjacentEdges(1)
        val edges2 = integerGraph.getAdjacentEdges(2)
        val edges3 = integerGraph.getAdjacentEdges(3)

        assertEquals(1, edges1.size)
        assertEquals(5, edges1[0].weight)
        assertEquals(2, edges1[0].finish)

        assertEquals(2, edges2.size)
        assertEquals(5, edges2[0].weight)
        assertEquals(1, edges2[0].finish)
        assertEquals(10, edges2[1].weight)
        assertEquals(3, edges2[1].finish)

        assertEquals(1, edges3.size)
        assertEquals(10, edges3[0].weight)
        assertEquals(2, edges3[0].finish)
    }
}
