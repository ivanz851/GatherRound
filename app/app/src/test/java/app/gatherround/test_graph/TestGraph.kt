package app.gatherround.test_graph

import app.gatherround.graph.Graph
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestGraph {
    private lateinit var g: Graph<String>

    @BeforeEach
    fun setUp() {
        g = Graph()
    }

    @Test
    fun `vertex is added only once`() {
        val g = Graph<Int>()
        g.addVertex(1)
        g.addVertex(1)
        assertEquals(setOf(1), g.getVertices())
    }

    @Test
    fun `undirected edge is added in both directions`() {
        val g = Graph<String>()
        g.addEdge("A", "B")
        val aNeighbours = g.getAdjacentEdges("A").map { it.finish }
        val bNeighbours = g.getAdjacentEdges("B").map { it.finish }
        assertAll(
            { assertTrue("B" in aNeighbours) },
            { assertTrue("A" in bNeighbours) }
        )
    }

    @Test
    fun `getEdges returns double count for undirected edges`() {
        val g = Graph<Int>()
        g.addEdge(1, 2)
        assertEquals(2, g.getEdges().size)
    }

    @Test
    fun `findOptimalVertex on empty set returns null`() {
        val g = Graph<Int>()
        val (v, dist) = g.findOptimalVertex(emptySet())
        assertNull(v)
        assertEquals(0, dist)
    }

    @Test
    fun `findOptimalVertex on single vertex returns same vertex with zero distance`() {
        val g = Graph<Int>()
        g.addVertex(42)
        val (v, dist) = g.findOptimalVertex(setOf(42))
        assertEquals(42, v)
        assertEquals(0, dist)
    }
    @Test
    fun `findOptimalVertex picks center of simple path`() {
        /*
           1 --- 2 --- 3
           chosen = {1, 3}
           Версия с единичными весами. Оптимальная вершина 2, макс. расстояние = 1
        */
        val g = Graph<Int>()
        g.addEdge(1, 2)
        g.addEdge(2, 3)

        val (v, dist) = g.findOptimalVertex(setOf(1, 3))
        assertEquals(2, v)
        assertEquals(1, dist)
    }


    @Test
    fun `findOptimalVertex works on triangle graph with equal distances`() {
        /*
          1
         / \
        2---3     chosen = {1,2,3}
        Любая вершина подходит, макс. дистанция = 1
         */
        val g = Graph<Int>()
        g.addEdge(1, 2)
        g.addEdge(2, 3)
        g.addEdge(3, 1)

        val (v, dist) = g.findOptimalVertex(setOf(1, 2, 3))
        assertNotNull(v)
        assertEquals(1, dist)
        assertTrue(v in setOf(1, 2, 3))
    }
}
