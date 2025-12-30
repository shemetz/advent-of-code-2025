import java.io.File


fun main() {
    val lines = File("src/input.txt").readLines()
    val originalEdges = lines.associate { line ->
        val parts = line.split(":")
        val fromNode = parts[0]
        val neighbors = parts[1].trim().split(" ")
        fromNode to neighbors
    }

    fun countPathsRecur(node: String, edges: Map<String, List<String>>, mapBeingBuilt: MutableMap<String, Long>): Long {
        if (node in mapBeingBuilt) {
            return mapBeingBuilt[node]!!
        }
        if (node !in edges) {
            // this happens in part 2 when we reach a temporarily ignored node
            return 0
        }
        val totalPaths = edges[node]!!.sumOf { node2 ->
            countPathsRecur(node2, edges, mapBeingBuilt)
        }
        mapBeingBuilt[node] = totalPaths
        return totalPaths
    }

    fun countPaths(start: String, end: String, edges: Map<String, List<String>>): Long {
        val mapBeingBuilt = mutableMapOf<String, Long>()
        mapBeingBuilt[end] = 1L
        return countPathsRecur(start, edges, mapBeingBuilt)
    }

    val pathCount1 = countPaths("you", "out", originalEdges)
    println("Part 1: $pathCount1")  // 640

    val step0 = "svr"
    val stepF = "fft"
    val stepD = "dac"
    val step3 = "out"
    val paths0F = countPaths(step0, stepF, originalEdges.filter { it.key !in listOf(stepD) })
    val paths0D = countPaths(step0, stepD, originalEdges.filter { it.key !in listOf(stepF) })
    val pathsDF = countPaths(stepD, stepF, originalEdges)
    val pathsFD = countPaths(stepF, stepD, originalEdges)
    val pathsF3 = countPaths(stepF, step3, originalEdges.filter { it.key !in listOf(stepD) })
    val pathsD3 = countPaths(stepD, step3, originalEdges.filter { it.key !in listOf(stepF) })
    val pathCount2 = paths0F * pathsFD * pathsD3 + paths0D * pathsDF * pathsF3
    println("Part 2: $pathCount2")  // 367579641755680
}