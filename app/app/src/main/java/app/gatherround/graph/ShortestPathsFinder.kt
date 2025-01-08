package app.gatherround.graph

interface ShortestPathsFinder<Vertex: Comparable<Vertex>> {
    fun getShortestPath(graph: Graph<Vertex>,
                        start: Vertex,
                        finish: Vertex): Pair<Int, List<Vertex>?>
}
