package app.gatherround.graph

/**
 * Абстракция «поиска кратчайшего пути» в взвешенном графе.
 *
 * Любой алгоритм (Дейкстра, A*, Беллман–Форд и т. д.), реализующий этот контракт, возвращает
 * **вес** кратчайшего пути и **путь** в виде списка вершин или `null`,
 * если вершина `finish` недостижима из `start`.
 *
 * @param Vertex тип, которым представлены вершины. Должен быть `Comparable`, поскольку
 *               Дейкстра хранит вершины в `TreeSet`.
 */
interface ShortestPathsFinder<Vertex: Comparable<Vertex>> {
    /**
     * Вычисляет кратчайший путь из [start] в [finish] в графе [graph].
     *
     * @return `Pair(длина, путь)`:
     * * **длина** — неотрицательное целое; `Int.MAX_VALUE`, если путь не найден;
     * * **путь**  — список вершин от `start` до `finish` (включительно)
     *               в порядке обхода или `null`, когда путь отсутствует.
     */
    fun getShortestPath(graph: Graph<Vertex>,
                        start: Vertex,
                        finish: Vertex): Pair<Int, List<Vertex>?>
}
