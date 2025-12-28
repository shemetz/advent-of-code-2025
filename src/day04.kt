import java.io.File
import kotlin.collections.filter

fun main() {
    val gridAsList = File("src/input.txt").readLines().map { line -> line.map { c -> c == '@' } }
    val gridAsMap = gridAsList.mapIndexed { rowIdx, row ->
        row.mapIndexed { colIdx, trueOrFalse -> Pair(Pair(rowIdx, colIdx), trueOrFalse) }.toMap()
    }.reduce { acc, map -> acc + map }
    val countAccessible = gridAsMap.entries.sumOf { (position, isWall) ->
        val (r, c) = position
        if (!isWall) return@sumOf 0
        val countWallNeighbors = (-1..1).sumOf { rd ->
            (-1..1).count { cd ->
                gridAsMap.getOrDefault(Pair(r + rd, c + cd), false)
            }
        } - 1 // minus the same one, at +0 +0

        if (countWallNeighbors < 4) 1 else 0
    }

    println("Part 1: $countAccessible")  // 1435

    var countRemoved = 0
    val livingGrid = gridAsMap.toMutableMap()
    var latestSize = -1
    while (latestSize != livingGrid.size) {
        latestSize = livingGrid.size
        val toRemove = livingGrid.entries.filter { (position, isWall) ->
            val (r, c) = position
            if (!isWall) return@filter false
            val countWallNeighbors = (-1..1).sumOf { rd ->
                (-1..1).count { cd ->
                    livingGrid.getOrDefault(Pair(r + rd, c + cd), false)
                }
            } - 1 // minus the same one, at +0 +0

            countWallNeighbors < 4
        }.map { it.key }

        countRemoved += toRemove.size
        toRemove.forEach { livingGrid.remove(it) }
    }

    println("Part 2: $countRemoved")  // 8623

    // bonus printout
    for (r in gridAsList.indices) {
        for (c in gridAsList[r].indices) {
            if (livingGrid.getOrDefault(Pair(r, c), false)) {
                print("@")
            } else if (gridAsMap.getOrDefault(Pair(r, c), false)) {
                print("x")
            } else {
                print(".")
            }
        }
        println()
    }
}