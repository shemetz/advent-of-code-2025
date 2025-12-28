import java.io.File

fun main() {
    val lines = File("src/input.txt").readLines()
    val emptyLineIdx = lines.indexOfFirst { it.isBlank() }
    val ranges = lines.subList(0, emptyLineIdx).map { line ->
        val (start, end) = line.split("-")
        start.toLong()..end.toLong()
    }
    val availableIngs = lines.subList(emptyLineIdx + 1, lines.size).map { it.toLong() }
    val countAvailable = availableIngs.count { ing ->
        ranges.any { range -> ing in range }
    }

    println("Part 1: $countAvailable")  // 694

    val allRangeEdges = ranges.flatMap { listOf(it.first, it.last) }.toSortedSet()
    val mergedRangeSum = allRangeEdges.zipWithNext().sumOf { (start, end) ->
        if (start + 1 == end) {
            return@sumOf 0
        }
        else if (ranges.any { range -> start + 1 in range }) {
            return@sumOf (end - start - 1) // don't count start or end
        } else {
            return@sumOf 0
        }
    } + allRangeEdges.size
    println("Part 2: $mergedRangeSum")  // 352716206375547
}